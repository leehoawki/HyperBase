package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.commons.lang.ArrayUtils;
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

    final File hintsFile;

    DataOutputStream dos;

    ExecutorService es;

    volatile int curr;

    volatile boolean online;

    public DataStoreImpl(Meta meta) {
        this.meta = meta;
        this.hints = new ConcurrentHashMap<>();
        this.fileNamePrefix = String.format("hyper.data.%s", meta.getName());
        this.hintsFile = new File(String.format("%s/hyper.hints.%s", meta.getDir(), meta.getName()));
    }

    @Override
    public synchronized void online() {
        if (!online) {
            try {
                es = Executors.newSingleThreadExecutor();
                dos = new DataOutputStream(getFilePath(fileNamePrefix, meta.getDir(), curr), true);
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
                dos.close();
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
        File dir = new File(meta.getDir());
        int to = -1;
        if (hintsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(hintsFile))) {
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
                filter(file -> getFileSeq(file.getName()) > t).collect(Collectors.toList())) {
            try (FileInputStream fis = new FileInputStream(f)) {
                int offset = 0;
                while (true) {
                    byte[] bytes = readData(fis);
                    if (bytes == null) {
                        break;
                    }
                    Data data = Data.deserialize(bytes);
                    hints.put(data.key, new Hint(data.key, f.getAbsolutePath(), offset, data.timestamp));
                    offset += INT_SZ + bytes.length;
                }
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex);
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
        if (!new File(getFilePath(fileNamePrefix, meta.getDir(), to)).exists()) {
            return;
        }

        File dir = new File(meta.getDir());
        Map<String, Hint> nhints = new HashMap<>();
        int count = 1;
        DataOutputStream ndos = null;
        try {
            List<File> files = Arrays.stream(dir.listFiles(x -> x.getName().
                    startsWith(fileNamePrefix))).filter(file -> getFileSeq(file.getName()) <= to).collect(Collectors.toList());
            List<File> toDeleteList = new ArrayList<>();
            ndos = new DataOutputStream(getFilePath(fileNamePrefix, meta.getDir(), to, count));
            int offset = 0;

            for (File f : files) {
                toDeleteList.add(f);
                try (FileInputStream fis = new FileInputStream(f)) {
                    if (ndos.file.length() > FILE_SZ) {
                        ndos.switchTo(getFilePath(fileNamePrefix, meta.getDir(), to, ++count));
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
                            nhints.put(data.key, new Hint(data.key, ndos.file.getAbsolutePath(), offset, data.timestamp));
                            ndos.write(intToBytes(bytes.length));
                            ndos.write(bytes);
                            offset += INT_SZ + bytes.length;
                        }
                    }
                }
            }
            ndos.close();
            hints.putAll(nhints);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hintsFile))) {
                oos.writeInt(to);
                oos.writeObject(nhints);
            } catch (IOException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            }

            toDeleteList.forEach(File::delete);
        } catch (IOException | ClassNotFoundException ex) {
            if (ndos != null) {
                ndos.clean();
            }
            LOG.error(ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s merging completed.", meta.getName()));
    }

    @Override
    public synchronized void destroy() {
        LOG.info(String.format("Table %s destroy in progress...", meta.getName()));
        File dir = new File(meta.getDir());
        for (File f : dir.listFiles(x -> x.getName().startsWith(fileNamePrefix))) {
            f.delete();
        }
        hintsFile.delete();
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
            try {
                if (dos.file.length() > FILE_SZ) {
                    synchronized (this) {
                        if (dos.file.length() > FILE_SZ) {
                            archive();
                        }
                    }
                }
                byte[] cell = Data.serialize(data);
                hints.put(data.key, new Hint(data.key, dos.file.getAbsolutePath(), dos.file.length(), data.timestamp));
                dos.write(ArrayUtils.addAll(intToBytes(cell.length), cell));
            } catch (IOException ex) {
                LOG.error(ex);
                throw new IllegalStateException(ex);
            }
        });
    }

    synchronized void archive() {
        LOG.info(String.format("Table %s archiving in progress...", meta.getName()));
        try {
            dos.switchTo(getFilePath(fileNamePrefix, meta.getDir(), ++curr));
        } catch (IOException ex) {
            LOG.error(ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s archiving completed.", meta.getName()));
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
            LOG.error(ex);
            throw new IllegalStateException(ex);
        }
    }

    static String getFilePath(String fileNamePrefix, String dir, int n) {
        return getFilePath(fileNamePrefix, dir, n, 0);
    }

    static String getFilePath(String fileNamePrefix, String dir, int to, int arch) {
        return String.format("%s/%s.%03d%03d", dir, fileNamePrefix, to, arch);
    }

    static int getFileSeq(String fileName) {
        String[] a = StringUtils.split(fileName, '.');
        return Integer.parseInt(a[a.length - 1]);
    }

    static byte[] readData(FileInputStream fis) throws IOException {
        byte[] bsz = new byte[INT_SZ];
        int re = fis.read(bsz);
        if (re != INT_SZ) {
            return null;
        }
        int sz = bytesToInt(bsz);
        byte[] bytes = new byte[sz];
        fis.read(bytes);
        return bytes;
    }

    static final int INT_SZ = 4;

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

    static final class Hint implements Serializable {

        final String key;

        final String fileName;

        final long offset;

        final long timestamp;

        public Hint(String key, String name, long offset, long timestamp) {
            this.key = key;
            this.fileName = name;
            this.offset = offset;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("Key:%s, File:%s, Offset:%s, TS:%s", key, fileName, offset, timestamp);
        }
    }

    static class DataOutputStream {

        FileOutputStream fos;

        File file;

        String path;

        boolean append;

        List<File> files = new ArrayList<>();

        public DataOutputStream(String path) throws IOException {
            this(path, false);
        }

        public DataOutputStream(String path, boolean append) throws IOException {
            this.path = path;
            this.file = new File(path);
            this.fos = new FileOutputStream(path, append);
            this.append = append;
            this.files.add(file);
        }

        public void write(byte[] bytes) throws IOException {
            fos.write(bytes);
        }

        public void close() throws IOException {
            fos.close();
        }

        public void switchTo(String newPath) throws IOException {
            this.path = newPath;
            this.file = new File(newPath);
            this.fos.close();
            this.fos = new FileOutputStream(path, append);
            this.files.add(file);
        }

        public void clean() {
            files.forEach(File::delete);
        }
    }
}