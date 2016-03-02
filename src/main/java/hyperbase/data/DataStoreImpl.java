package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStoreImpl implements DataStore {

    static Logger LOG = Logger.getLogger(DataStoreImpl.class);

    Map<String, Hint> hints;

    Meta meta;

    public DataStoreImpl(Meta meta) {
        this.meta = meta;
        this.hints = new ConcurrentHashMap<String, Hint>();
    }

    @Override
    public void restore() {
        LOG.info(String.format("Table %s loading in progress...", meta.getName()));
        try (FileInputStream fr = new FileInputStream(meta.getPath())) {
            int offset = 0;
            while (true) {
                byte[] bsz = new byte[4];
                int re = fr.read(bsz);
                if (re != 4) {
                    break;
                }
                int sz = bytesToInt(bsz);
                byte[] data = new byte[sz];
                fr.read(data);
                String key = Data.deserialize(data).key;
                hints.put(key, new Hint(key, meta.getPath(), offset));
                offset += 4 + sz;
            }
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error(String.format("Table %s loading completed.", meta.getName()), ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s loading completed.", meta.getName()));
    }

    @Override
    public void merge() {
        LOG.info(String.format("Table %s merging in progress...", meta.getName()));
        //TODO
        LOG.info(String.format("Table %s merging completed.", meta.getName()));
    }

    @Override
    public void set(String key, String val) {
        Data d = new Data(key, val);
        set(d);
    }

    @Override
    public void set(Data data) {
        try (RandomAccessFile file = new RandomAccessFile(meta.getPath(), "rw")) {
            byte[] cell = Data.serialize(data);
            hints.put(data.key, new Hint(data.key, meta.getPath(), file.length()));
            file.seek(file.length());
            file.write(intToBytes(cell.length));
            file.write(cell);
        } catch (IOException ex) {
            LOG.error(String.format("Error setting %s to file %s", data.key, meta.getPath()), ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Data get(String key) {
        Hint hint = hints.get(key);
        if (hint == null) {
            return new Data(key, null);
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
        value = (int) (((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF) << 16)
                | ((src[2] & 0xFF) << 8)
                | (src[3] & 0xFF));
        return value;
    }
}
