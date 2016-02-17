package com.toys.hyperbase.meta;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HyperMetaStoreImpl implements HyperMetaStore {

    static Logger LOG = Logger.getLogger(HyperMetaStoreImpl.class);

    String path;

    public HyperMetaStoreImpl() {
        this.path = HyperMetaStoreImpl.class.getResource("/").getPath() + "data";
        File dir = new File(path);
        if (!dir.exists()) {
            LOG.info(String.format("Data dir %s initializing. HyperBase metadata initialized.", path));
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            LOG.error(String.format("Data dir %s conflicts. HyperBase metadata initialization failed.", path));
            throw new IllegalStateException();
        } else {
            LOG.info(String.format("Data dir %s loaded. HyperBase metadata initialized.", path));
        }
    }

    @Override
    public synchronized void add(String name) {
        Meta meta = new Meta(name);
        add(meta);
    }

    @Override
    public synchronized void add(Meta meta) {
        String name = meta.getName();
        File f = new File(path + "/" + name);
        if (f.exists()) {
            LOG.error(String.format("Table %s already exists. Table adding failed.", name));
            throw new IllegalArgumentException(name);
        }
        try {
            f.createNewFile();
        } catch (IOException ex) {
            LOG.error(String.format("Datafile %s creation failed. Table adding failed.", name));
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s created.", name));
    }

    @Override
    public synchronized void delete(String name) {
        File f = new File(path + "/" + name);
        if (!f.exists()) {
            LOG.error(String.format("Table %s does not exist. Table adding failed.", name));
            throw new IllegalArgumentException(name);
        }
        f.delete();
        LOG.info(String.format("Table %s deleted.", name));
    }

    @Override
    public List<Meta> getAllMeta() {
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
            LOG.error(String.format("Table %s does not exist. Table adding failed.", name));
            throw new IllegalArgumentException(name);
        }
        return new Meta(f.getName());
    }
}
