package com.toys.hyperbase.meta;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class HyperMetaStore implements MetaStore {

    static Logger LOG = Logger.getLogger(HyperMetaStore.class);

    String path;

    public HyperMetaStore() {
        path = "data";
        File dir = new File(path);
        if (!dir.exists()) {
            LOG.info("");
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            LOG.error("");
            throw new IllegalStateException("");
        } else {
            LOG.info("");
        }
    }

    @Override
    public synchronized void add(String name) {
        long timestamp = new Date().getTime();
        Meta meta = new Meta(name);
        add(meta);
    }

    @Override
    public synchronized void add(Meta meta) {
        File f = new File(path + "/" + meta.getName());
        if (f.exists()) {
            throw new IllegalArgumentException("");
        }
        try {
            f.createNewFile();
        } catch (IOException ex) {
            LOG.error("");
            throw new IllegalStateException("", ex);
        }
    }

    @Override
    public synchronized void delete(String name) {
        File f = new File(path + "/" + name);
        if (!f.exists()) {
            throw new IllegalArgumentException("");
        }
        f.delete();
    }

    @Override
    public synchronized List<Meta> getAllMeta() {
        List<Meta> list = new ArrayList<Meta>();
        File dir = new File(path);
        for (File f : dir.listFiles()) {
            list.add(new Meta(f.getName()));
        }
        return list;
    }

    @Override
    public Meta getMeta(String name) {
        File f = new File(path + "/" + name);
        if (!f.exists()) {
            throw new IllegalArgumentException("");
        }
        return new Meta(f.getName());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
