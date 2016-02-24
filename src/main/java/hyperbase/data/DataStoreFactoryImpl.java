package hyperbase.data;

import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class DataStoreFactoryImpl implements DataStoreFactory {

    static Logger LOG = Logger.getLogger(DataStoreFactoryImpl.class);

    @Override
    public DataStore createStore(Meta meta) {
        try {
            new File(meta.getPath()).createNewFile();
        } catch (IOException ex) {
            LOG.error(String.format("Datafile %s:%s creation failed. Table adding failed.", meta.getName(), meta.getPath()), ex);
            throw new IllegalStateException(ex);
        }
        DataStore store = new DataStoreImpl(meta);
        return store;
    }
}
