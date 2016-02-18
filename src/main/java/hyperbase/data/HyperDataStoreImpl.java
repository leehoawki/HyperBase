package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;

public class HyperDataStoreImpl implements HyperDataStore {

    static Logger LOG = Logger.getLogger(HyperDataStoreImpl.class);

    ConcurrentHashMap<String, Data> map;

    Meta meta;

    public HyperDataStoreImpl(Meta meta) {
        this.meta = meta;
        load();
    }

    public synchronized void load() {
        LOG.info(String.format("Table %s loading from %s in progress...", meta.getName(), meta.getPath()));
        try {
            BufferedReader br = new BufferedReader(new FileReader(meta.getPath()));
            map = new ConcurrentHashMap<String, Data>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] pair = StringUtils.split(line, ":");
                map.put(pair[0], new Data(pair[0], pair[1]));
            }
            br.close();
        } catch (Exception ex) {
            LOG.error(String.format("Table %s loading error.", meta.getName()), ex);
            throw new IllegalStateException(String.format("Table %s loading error."), ex);
        }
        LOG.info(String.format("Table %s loaded from %s.", meta.getName(), meta.getPath()));
    }


    public synchronized void dump() {
        LOG.info(String.format("Table %s dumping to %s in progress...", meta.getName(), meta.getPath()));
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(meta.getPath()));
            for (Data data : map.values()) {
                bw.write(String.format("%s:%s", data.getKey(), data.getVal()));
                bw.write("\n");
            }
            bw.close();
        } catch (Exception ex) {
            LOG.error(String.format("Table %s dumping error.", meta.getName()), ex);
            throw new IllegalStateException(String.format("Table %s dumping error."), ex);
        }
        LOG.info(String.format("Table %s dumped to %s.", meta.getName(), meta.getPath()));
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
