package hyperbase.meta;

import java.util.List;


public interface MetaStore {
    public Meta add(String name);

    public void add(Meta meta);

    public void delete(String name);

    public List<Meta> getAllMeta();

    public Meta getMeta(String name);
}

