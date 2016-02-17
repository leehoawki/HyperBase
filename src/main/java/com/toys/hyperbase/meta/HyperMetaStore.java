package com.toys.hyperbase.meta;

import java.util.List;


public interface HyperMetaStore {
    public void add(String name);

    public void add(Meta meta);

    public void delete(String name);

    public List<Meta> getAllMeta();

    public Meta getMeta(String name);
}

