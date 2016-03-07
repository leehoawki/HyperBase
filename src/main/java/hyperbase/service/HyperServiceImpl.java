package hyperbase.service;

import hyperbase.data.Data;
import hyperbase.data.DataStore;
import hyperbase.data.DataStoreFactory;
import hyperbase.exception.TableNotFoundException;
import hyperbase.meta.Meta;
import hyperbase.meta.MetaStore;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        LOG.info("DataStores loading.");
        dataStores = new ConcurrentHashMap<>();
        for (Meta meta : metaStore.getAllMeta()) {
            DataStore ds = storeFactory.restoreStore(meta);
            ds.online();
            dataStores.put(meta.getName(), ds);
        }
        LOG.info("DataStores loaded.");
    }

    @Override
    public List<Table> getTables() {
        List<Table> tables = new ArrayList<>();
        for (Meta m : metaStore.getAllMeta()) {
            Table table = new Table();
            table.setName(m.getName());
            tables.add(table);
        }
        return tables;
    }

    @Override
    public Table getTable(String name) {
        Meta m = metaStore.getMeta(name);
        Table table = new Table();
        table.setName(m.getName());
        return table;
    }

    @Override
    public void createTable(String table) {
        Meta meta = metaStore.add(table);
        DataStore ds = storeFactory.createStore(meta);
        ds.online();
        dataStores.put(table, ds);
    }

    @Override
    public void deleteTable(String table) {
        metaStore.delete(table);
        DataStore ds = dataStores.get(table);
        if (ds != null) {
            ds.offline();
            ds.destroy();
            dataStores.remove(table);
        }
    }

    @Override
    public Row get(String table, String key) {
        DataStore store = dataStores.get(table);
        if (store == null) {
            throw new TableNotFoundException(table);
        }
        Data data = store.get(key);
        Row row = new Row();
        row.setKey(key);
        row.setValue(data.getVal());
        return row;
    }

    @Override
    public void set(String table, String key, String val) {
        DataStore store = dataStores.get(table);
        if (store == null) {
            throw new TableNotFoundException(table);
        }
        store.set(key, val);
    }
}
