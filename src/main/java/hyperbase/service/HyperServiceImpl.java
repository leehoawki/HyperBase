package hyperbase.service;

import hyperbase.meta.HyperMetaStore;
import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HyperServiceImpl implements HyperService {

    static Logger LOG = Logger.getLogger(HyperServiceImpl.class);

    @Autowired
    HyperMetaStore metaStore;

    Map<String, HyperMetaStore> dataStores;

    public HyperServiceImpl() {

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
        //TODO
    }

    @Override
    public void deleteTable(String name) {
        //TODO
    }

    @Override
    public Row get(String table, String key) {
        //TODO
        return null;
    }

    @Override
    public void set(String table, String key, String val) {
        //TODO
    }
}
