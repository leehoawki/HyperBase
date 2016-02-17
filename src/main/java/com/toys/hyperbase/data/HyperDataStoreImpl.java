package com.toys.hyperbase.data;


import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class HyperDataStoreImpl implements HyperDataStore {

    ConcurrentHashMap<String, Data> hashMap;

    public HyperDataStoreImpl(String name) {

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
        hashMap.put(data.getKey(), data);
    }

    @Override
    public Data get(String key) {
        return hashMap.get(key);
    }
}
