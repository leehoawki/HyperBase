package hyperbase.data;

import hyperbase.meta.Meta;
import org.springframework.stereotype.Component;


@Component
public class DataStoreFactoryImpl implements DataStoreFactory {

    @Override
    public DataStore createStore(Meta meta) {
        DataStore store = new DataStoreImpl(meta);
        return store;
    }
}
