package com.toys.hyperbase.data;


import com.toys.hyperbase.meta.Meta;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.server.ExportException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HyperDataStoreImpl implements HyperDataStore {

    static Logger LOG = Logger.getLogger(HyperDataStoreImpl.class);

    ConcurrentHashMap<String, Data> map;

    ExecutorService queue = Executors.newSingleThreadExecutor();

    public HyperDataStoreImpl(Meta meta) {
        try {
            FileInputStream fis = new FileInputStream(meta.getPath());
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (ConcurrentHashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            LOG.error("");
            throw new IllegalStateException();
        } catch (ClassNotFoundException c) {
            LOG.error("");
            throw new IllegalStateException();
        }
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
        map.put(data.getKey(), data);
    }

    @Override
    public Data get(String key) {
        return map.get(key);
    }
}
