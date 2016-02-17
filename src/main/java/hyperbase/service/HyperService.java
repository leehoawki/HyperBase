package hyperbase.service;

import java.util.List;

public interface HyperService {
    public List<Table> getTables();

    public Table getTable(String name);

    public Row get(String table, String key);

    public void createTable(String name);

    public void deleteTable(String name);

    public void set(String table, String key, String val);
}
