package hyperbase.service;

import hyperbase.data.Data;
import hyperbase.data.DataStore;
import hyperbase.data.DataStoreFactory;
import hyperbase.meta.MetaStore;
import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HyperServiceImpl implements HyperService, InitializingBean {

    static Logger LOG = Logger.getLogger(HyperServiceImpl.class);

    @Autowired
    MetaStore metaStore;

    @Autowired
    DataStoreFactory storeFactory;

    Map<String, DataStore> dataStores;

    public HyperServiceImpl() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataStores = new HashMap<String, DataStore>();
        for (Meta meta : metaStore.getAllMeta()) {
            dataStores.put(meta.getName(), storeFactory.createStore(meta));
        }
    }

    @Override
    public List<Table> getTables() {
        List<Table> tables = new ArrayList<Table>();
        for (Meta m : metaStore.getAllMeta()) {
            Table t = new Table();
            t.setName(m.getName());
            tables.add(t);
        }
        return tables;
    }

    @Override
    public Table getTable(String name) {
        Meta m = metaStore.getMeta(name);
        Table t = new Table();
        t.setName(m.getName());
        return t;
    }

    @Override
    public void createTable(String name) {
        Meta meta = metaStore.add(name);
        dataStores.put(name, storeFactory.createStore(meta));
    }

    @Override
    public void deleteTable(String name) {
        dataStores.remove(name);
        metaStore.delete(name);
    }

    @Override
    public Row get(String table, String key) {
        Data data = dataStores.get(table).get(key);
        Row row = new Row();
        row.setKey(key);
        row.setValue(data.getVal());
        return row;
    }

    @Override
    public void set(String table, String key, String val) {
        Data data = new Data();
        data.setKey(key);
        data.setVal(val);
        DataStore store = dataStores.get(table);
        store.set(data);
    }
}
