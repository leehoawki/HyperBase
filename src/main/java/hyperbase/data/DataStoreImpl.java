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
                byte[] crc = new byte[10];
                int re = fr.read(crc);
                if (re != 10) {
                    break;
                }
                byte[] ksz = new byte[6];
                fr.read(ksz);
                byte[] key = new byte[Integer.valueOf(new String(ksz))];
                fr.read(key);

                byte[] vsz = new byte[6];
                fr.read(vsz);
                byte[] val = new byte[Integer.valueOf(new String(vsz))];
                fr.read(val);

                int length = 22 + Integer.valueOf(new String(ksz)) + Integer.valueOf(new String(vsz));
                hints.put(new String(key), new Hint(new String(key), meta.getPath(), offset, length));
                offset += length;
            }
        } catch (IOException ex) {
            LOG.error("", ex);
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
            String cell = data.toString();
            hints.put(data.key, new Hint(data.key, meta.getPath(), file.length(), cell.length()));
            file.seek(file.length());
            file.write(cell.getBytes());
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
        byte[] bytes = new byte[hint.size];
        try (RandomAccessFile file = new RandomAccessFile(hint.fileName, "rw")) {
            file.seek(hint.offset);
            file.read(bytes);
            Data data = new Data(new String(bytes));
            return data;
        } catch (IOException ex) {
            LOG.error(String.format("Error getting %s from file %s", hint.key, hint.fileName), ex);
            throw new IllegalStateException(ex);
        }
    }
}
