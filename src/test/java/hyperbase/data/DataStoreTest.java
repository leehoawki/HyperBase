package hyperbase.data;


import hyperbase.meta.MetaStore;
import hyperbase.meta.MetaStoreImpl;
import hyperbase.meta.Meta;
import junit.framework.TestCase;
import org.junit.Test;

public class DataStoreTest extends TestCase {
    MetaStore mstore;

    DataStore store;

    DataStoreFactory factory;

    static String name = "test";

    @Override
    public void setUp() {
        mstore = new MetaStoreImpl();
        factory = new DataStoreFactoryImpl();
        mstore.add(name);
        Meta meta = mstore.getMeta(name);
        store = factory.createStore(meta);
    }

    @Override
    public void tearDown() {
        mstore.delete(name);
    }

    @Test
    public void test() {

    }
}
