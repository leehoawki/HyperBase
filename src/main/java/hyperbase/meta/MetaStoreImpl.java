package hyperbase.meta;

import hyperbase.exception.TableConflictException;
import hyperbase.exception.TableNotFoundException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MetaStoreImpl implements MetaStore {

    static Logger LOG = Logger.getLogger(MetaStoreImpl.class);

    String filePath;

    PropertiesConfiguration pc;

    Configuration tc;

    Map<String, Meta> metas;

    public MetaStoreImpl() {
        this(MetaStoreImpl.class.getResource("/").getPath() + "/hyper.ctl");
    }

    public MetaStoreImpl(String filePath) {
        LOG.info(String.format("Meta Store initializing from %s", filePath));
        this.filePath = filePath;
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.createNewFile();
            }
            this.pc = new PropertiesConfiguration(f);
            tc = pc.subset("TABLE");
            Iterator<String> keys = tc.getKeys();
            metas = new ConcurrentHashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                metas.put(key, new Meta(key, tc.getString(key)));
            }

        } catch (ConfigurationException | IOException ex) {
            LOG.error(String.format("Meta file initialization failed %s.", filePath), ex);
            throw new IllegalStateException(ex);
        }

        LOG.info(String.format("Meta Store initialized from %s", filePath));
    }

    @Override
    public Meta add(String name) {
        Meta meta = new Meta(name, String.format("/%s/hyper.%s.data", MetaStoreImpl.class.getResource("/").getPath(), name));
        add(meta);
        return meta;
    }

    @Override
    public synchronized void add(Meta meta) {
        String name = meta.getName();
        if (metas.containsKey(name)) {
            LOG.error(String.format("Table %s already exists. Table adding failed.", name));
            throw new TableConflictException(name);
        }

        metas.put(meta.getName(), meta);
        tc.addProperty(meta.getName(), meta.getPath());
        save();
        LOG.info(String.format("Table %s created.", name));
    }

    @Override
    public synchronized void delete(String table) {
        if (!metas.containsKey(table)) {
            LOG.error(String.format("Table %s does not exist. Table adding failed.", table));
            throw new TableNotFoundException(table);
        }
        metas.remove(table);
        tc.clearProperty(table);
        save();
        LOG.info(String.format("Table %s deleted.", table));
    }

    @Override
    public Collection<Meta> getAllMeta() {
        return metas.values();
    }

    @Override
    public Meta getMeta(String table) {
        if (!metas.containsKey(table)) {
            throw new TableNotFoundException(table);
        }
        return metas.get(table);
    }


    synchronized void save() {
        try {
            pc.save();
        } catch (ConfigurationException ex) {
            LOG.error(String.format("Meta file saving failed %s.", filePath), ex);
            throw new IllegalStateException(ex);
        }
    }
}