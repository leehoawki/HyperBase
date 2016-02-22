package hyperbase.data;


import hyperbase.meta.Meta;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStoreImpl implements DataStore {

    static Logger LOG = Logger.getLogger(DataStoreImpl.class);

    ConcurrentHashMap<String, Data> map;

    Meta meta;

    public DataStoreImpl(Meta meta) {
        this.meta = meta;
        this.load();
    }

    @Override
    public void load() {
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
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s loaded from %s.", meta.getName(), meta.getPath()));
    }

    @Override
    public void dump() {
        LOG.info(String.format("Table %s dumping to %s in progress...", meta.getName(), meta.getPath()));
        try {
            String tmpPath = meta.getPath() + ".tmp";
            BufferedWriter bw = new BufferedWriter(new FileWriter(tmpPath));
            for (Data data : map.values()) {
                bw.write(String.format("%s:%s", data.getKey(), data.getVal()));
                bw.write("\n");
            }
            bw.close();
            FileUtils.moveFile(new File(tmpPath), new File(meta.getPath()));
        } catch (IOException ex) {
            LOG.error(String.format("Table %s dumping error.", meta.getName()), ex);
            throw new IllegalStateException(ex);
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
