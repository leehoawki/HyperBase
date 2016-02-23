package hyperbase.service;

import hyperbase.data.Data;
import hyperbase.data.DataStore;
import hyperbase.data.DataStoreFactory;
import hyperbase.dbwr.DBWR;
import hyperbase.exception.TableNotFoundException;
import hyperbase.lgwr.LGWR;
import hyperbase.lgwr.Redo;
import hyperbase.meta.Meta;
import hyperbase.meta.MetaStore;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HyperServiceImpl implements HyperService, InitializingBean {

    static Logger LOG = Logger.getLogger(HyperServiceImpl.class);

    @Autowired
    MetaStore metaStore;

    @Autowired
    LGWR logWriter;

    @Autowired
    DBWR dbWriter;

    @Autowired
    DataStoreFactory storeFactory;

    Map<String, DataStore> dataStores;

    ScheduledExecutorService ses;

    public HyperServiceImpl() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("DataStores loading.");
        dataStores = new HashMap<String, DataStore>();
        for (Meta meta : metaStore.getAllMeta()) {
            dataStores.put(meta.getName(), storeFactory.createStore(meta));
        }
        LOG.info("DataStores loaded.");

        LOG.info("DataStores restoring.");
        restore();
        LOG.info("DataStores restored.");

        ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (DataStore store : dataStores.values()) {
                    dbWriter.write(store);
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    void restore() {
        Iterator<Redo> redos = logWriter.read();
        while (redos.hasNext()) {
            Redo redo = redos.next();
            String action = redo.getAction();
            String[] args = redo.getData();
            if (Redo.CREATE.equals(action)) {
                createTable(args[0]);
            } else if (Redo.DELETE.equals(action)) {
                deleteTable(args[0]);
            } else if (Redo.UPDATE.equals(action)) {
                set(args[0], args[1], args[2]);
            } else {
                LOG.error(String.format("Instance Restore failed. Unrecognized data %s in redo log.", args[0]));
                throw new IllegalArgumentException(args[0]);
            }
        }
    }


    @Override
    public List<Table> getTables() {
        List<Table> tables = new ArrayList<Table>();
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
        Redo redo = new Redo(Redo.CREATE, table);
        logWriter.append(redo);

        Meta meta = metaStore.add(table);
        dataStores.put(table, storeFactory.createStore(meta));
    }

    @Override
    public void deleteTable(String table) {
        Redo redo = new Redo(Redo.DELETE, table);
        logWriter.append(redo);

        dataStores.remove(table);
        metaStore.delete(table);
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
        if (data != null) {
            row.setValue(data.getVal());
        }
        return row;
    }

    @Override
    public void set(String table, String key, String val) {
        DataStore store = dataStores.get(table);
        if (store == null) {
            throw new TableNotFoundException(table);
        }
        Redo redo = new Redo(Redo.UPDATE, table, key, val);
        logWriter.append(redo);

        Data data = new Data();
        data.setKey(key);
        data.setVal(val);
        store.set(data);
    }
}
