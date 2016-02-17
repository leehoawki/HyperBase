package com.toys.hyperbase.data;


public interface HyperDataStore {
    public void set(String key, String val);

    public void set(Data data);

    public Data get(String key);
}
