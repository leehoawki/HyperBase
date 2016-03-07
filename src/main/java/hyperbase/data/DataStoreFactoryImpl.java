package hyperbase.data;

import hyperbase.meta.Meta;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class DataStoreFactoryImpl implements DataStoreFactory {

    static Logger LOG = Logger.getLogger(DataStoreFactoryImpl.class);

    @Override
    public DataStore createStore(Meta meta) {
        DataStore store = new DataStoreImpl(meta);
        return store;
    }

    @Override
    public DataStore restoreStore(Meta meta) {
        DataStore store = new DataStoreImpl(meta);
        store.restore();
        return store;
    }
}
