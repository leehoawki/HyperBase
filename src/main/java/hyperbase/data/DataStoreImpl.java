package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

public class DataStoreImpl implements DataStore {

    static Logger LOG = Logger.getLogger(DataStoreImpl.class);

    ConcurrentHashMap<String, Data> map;

    Meta meta;

    public DataStoreImpl(Meta meta) {
        this.meta = meta;
        this.load();
    }

    void load() {
        LOG.info(String.format("Table %s loading from %s in progress...", meta.getName(), meta.getPath()));
        // TODO
        LOG.info(String.format("Table %s loaded from %s.", meta.getName(), meta.getPath()));
    }

    @Override
    public synchronized void merge() {
        //TODO
    }

    @Override
    public void set(String key, String val) {
        Data d = new Data();
        d.setKey(key);
        d.setVal(val);
        set(d);
    }

    @Override
    public void set(Data data) {
        map.put(data.getKey(), data);
    }

    @Override
    public Data get(String key) {
        return map.get(key);
    }
}
