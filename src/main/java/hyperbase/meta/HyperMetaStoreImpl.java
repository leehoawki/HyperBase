package hyperbase.meta;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HyperMetaStoreImpl implements HyperMetaStore {

    static Logger LOG = Logger.getLogger(HyperMetaStoreImpl.class);

    String dir;

    public HyperMetaStoreImpl() {
        this.dir = HyperMetaStoreImpl.class.getResource("/").getPath() + "/data";
        File dir = new File(this.dir);
        if (!dir.exists()) {
            LOG.info(String.format("Data dir %s initializing. HyperBase metadata initialized.", this.dir));
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            LOG.error(String.format("Data dir %s conflicts. HyperBase metadata initialization failed.", this.dir));
            throw new IllegalStateException();
        } else {
            LOG.info(String.format("Data dir %s loaded. HyperBase metadata initialized.", this.dir));
        }
    }

    @Override
    public synchronized void add(String name) {
        Meta meta = new Meta(name, dir + "/" + name);
        add(meta);
    }

    @Override
    public synchronized void add(Meta meta) {
        String name = meta.getName();
        File f = new File(dir + "/" + name);
        if (f.exists()) {
            LOG.error(String.format("Table %s already exists. Table adding failed.", name));
            throw new IllegalArgumentException(name);
        }
        try {
            f.createNewFile();
        } catch (IOException ex) {
            LOG.error(String.format("Datafile %s creation failed. Table adding failed.", name), ex);
            throw new IllegalStateException(ex);
        }
        LOG.info(String.format("Table %s created.", name));
    }

    @Override
    public synchronized void delete(String name) {
        File f = new File(dir + "/" + name);
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
        File dir = new File(this.dir);
        for (File f : dir.listFiles()) {
            String name = f.getName();
            list.add(new Meta(name, dir + "/" + name));
        }
        return list;
    }

    @Override
    public Meta getMeta(String name) {
        String path = dir + "/" + name;
        File f = new File(path);
        if (!f.exists()) {
            LOG.error(String.format("Table %s does not exist. Table adding failed.", name));
            throw new IllegalArgumentException(name);
        }
        return new Meta(name, path);
    }
}
