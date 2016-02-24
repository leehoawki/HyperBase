package hyperbase.meta;

import java.util.Collection;

public interface MetaStore {
    public Meta add(String name);

    public void add(Meta meta);

    public void delete(String name);

    public Collection<Meta> getAllMeta();

    public Meta getMeta(String name);

    public long getSCN();
}

