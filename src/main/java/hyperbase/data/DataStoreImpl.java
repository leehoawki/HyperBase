package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DataStoreImpl implements DataStore {

    static Logger LOG = Logger.getLogger(DataStoreImpl.class);

    final Map<String, Hint> hints;

    final Meta meta;

    final String fileNamePrefix;

    final String hintsFilePath;

    FileOutputStream fos;

    ExecutorService es;

    volatile int curr;

    volatile boolean online;

    public DataStoreImpl(Meta meta) {
        this.meta = meta;
        this.hints = new ConcurrentHashMap<>();
        this.fileNamePrefix = String.format("hyper.data.%s", meta.getName());
        this.hintsFilePath = String.format("%s/hyper.hints.%s", meta.getPath(), meta.getName());
    }

    @Override
    public synchronized void online() {
        if (!online) {
            try {
                es = Executors.newSingleThreadExecutor();
                fos = new FileOutputStream(getPath(), true);
            } catch (IOException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            }
            online = true;
        }
    }

    @Override
    public synchronized void offline() {
        if (online) {
            try {
                es.shutdown();
                es.awaitTermination(10, TimeUnit.SECONDS);
                fos.close();
            } catch (IOException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            online = false;
        }
    }


    @Override
    public synchronized void restore() {
        LOG.info(String.format("Table %s loading in progress...", meta.getName()));
        File dir = new File(meta.getPath());
        File hf = new File(hintsFilePath);
        int to = 0;
        if (hf.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hf))) {
                to = ois.readInt();
                Map<String, Hint> map = (Map<String, Hint>) ois.readObject();
                hints.putAll(map);
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            }
        }

        final int t = to;

        for (File f : Arrays.stream(dir.listFiles(x -> x.getName().
                startsWith(fileNamePrefix))).sorted((o1, o2) -> getFileSeq(o1.getName()) - getFileSeq(o2.getName())).
                filter(file -> getFileSeq(file.getName()) >= t).collect(Collectors.toList())) {
            try (FileInputStream fis = new FileInputStream(f)) {
                int offset = 0;
                while (true) {
                    byte[] bytes = readData(fis);
                    if (bytes == null) {
                        break;
                    }
                    Data data = Data.deserialize(bytes);
                    hints.put(data.key, new Hint(data.key, f.getAbsolutePath(), offset, data.timestamp));
                    offset += 4 + bytes.length;
                }
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(String.format("Table %s loading failed.", meta.getName()), ex);
                throw new IllegalStateException(ex);
            }
        }

        LOG.info(String.format("Table %s loading completed.", meta.getName()));
    }

    @Override
    public synchronized void merge() {
        LOG.info(String.format("Table %s merging in progress...", meta.getName()));
        if (curr == 0) {
            return;
        }
        int to = curr - 1;
        if (!new File(getArchivePath(to)).exists()) {
            return;
        }

        File dir = new File(meta.getPath());
        Map<String, Hint> nhints = new HashMap<>();
        int count = 1;
        try {
            File nf = new File(getMergePath(to, count));
            List<File> files = Arrays.stream(dir.listFiles(x -> x.getName().
                    startsWith(fileNamePrefix))).filter(file -> getFileSeq(file.getName()) <= to).collect(Collectors.toList());
            List<File> toDeleteList = new ArrayList<>();
            FileOutputStream nfos = new FileOutputStream(nf);
            int offset = 0;

            for (File f : files) {
                toDeleteList.add(f);
                FileInputStream fis = new FileInputStream(f);
                if (nf.length() > FILE_SZ) {
                    count += 1;
                    nf = new File(getMergePath(to, count));
                    nfos.close();
                    nfos = new FileOutputStream(nf);
                    offset = 0;
                }
                while (true) {
                    byte[] bytes = readData(fis);
                    if (bytes == null) {
                        break;
                    }
                    Data data = Data.deserialize(bytes);
                    Hint h = hints.get(data.key);
                    if (h.timestamp == data.timestamp && h.fileName.equals(f.getAbsolutePath())) {
                        nhints.put(data.key, new Hint(data.key, nf.getAbsolutePath(), offset, data.timestamp));
                        nfos.write(intToBytes(bytes.length));
                        nfos.write(bytes);
                        offset += 4 + bytes.length;
                    }
                }
                fis.close();
            }
            nfos.close();
            hints.putAll(nhints);
            File hf = new File(hintsFilePath);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hf))) {
                oos.writeInt(to);
                oos.writeObject(nhints);
            } catch (IOException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            }

            for (File f : toDeleteList) {
                f.delete();
            }
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error(ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s merging completed.", meta.getName()));
    }

    @Override
    public synchronized void destroy() {
        LOG.info(String.format("Table %s destroy in progress...", meta.getName()));
        File dir = new File(meta.getPath());
        for (File f : dir.listFiles(x -> x.getName().startsWith(fileNamePrefix))) {
            f.delete();
        }
        new File(hintsFilePath).delete();
        LOG.info(String.format("Table %s destroy completed.", meta.getName()));
    }

    @Override
    public void set(String key, String val) {
        long timestamp = new Date().getTime();
        Data d = new Data(key, val, timestamp);
        set(d);
    }

    @Override
    public void set(Data data) {
        es.execute(() -> {
            File f = new File(getPath());
            if (f.length() > FILE_SZ) {
                synchronized (this) {
                    f = new File(getPath());
                    if (f.length() > FILE_SZ) {
                        archive();
                        f = new File(getPath());
                    }
                }
            }
            try {
                byte[] cell = Data.serialize(data);
                hints.put(data.key, new Hint(data.key, f.getAbsolutePath(), f.length(), data.timestamp));
                fos.write(intToBytes(cell.length));
                fos.write(cell);
            } catch (IOException ex) {
                LOG.error(String.format("Error setting %s to file %s", data.key, meta.getPath()), ex);
                throw new IllegalStateException(ex);
            }
        });
    }

    @Override
    public Data get(String key) {
        Hint hint = hints.get(key);
        if (hint == null) {
            return new Data(key, null, 0L);
        }

        try (RandomAccessFile file = new RandomAccessFile(hint.fileName, "rw")) {
            file.seek(hint.offset);
            byte[] sz = new byte[4];
            file.read(sz);
            byte[] data = new byte[bytesToInt(sz)];
            file.read(data);
            return Data.deserialize(data);
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error(String.format("Error getting %s from file %s", hint.key, hint.fileName), ex);
            throw new IllegalStateException(ex);
        }
    }

    synchronized void archive() {
        LOG.info(String.format("Table %s archiving in progress...", meta.getName()));
        curr += 1;
        try {
            fos.close();
            fos = new FileOutputStream(getPath(), true);
        } catch (IOException ex) {
            LOG.error(ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s archiving completed.", meta.getName()));
    }

    String getPath() {
        return String.format("%s/%s.%03d000", meta.getPath(), fileNamePrefix, curr);
    }

    String getArchivePath(int arch) {
        return String.format("%s/%s.%03d000", meta.getPath(), fileNamePrefix, arch);
    }

    String getMergePath(int to, int arch) {
        return String.format("%s/%s.%03d%03d", meta.getPath(), fileNamePrefix, to, arch);
    }

    static int getFileSeq(String fileName) {
        String[] a = StringUtils.split(fileName, '.');
        return Integer.valueOf(a[a.length - 1]);
    }

    static byte[] readData(FileInputStream fis) throws IOException {
        byte[] bsz = new byte[4];
        int re = fis.read(bsz);
        if (re != 4) {
            return null;
        }
        int sz = bytesToInt(bsz);
        byte[] bytes = new byte[sz];
        fis.read(bytes);
        return bytes;
    }

    static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF) << 16)
                | ((src[2] & 0xFF) << 8)
                | (src[3] & 0xFF);
        return value;
    }


    static final int FILE_SZ = 20 * 1024 * 1024;
}
