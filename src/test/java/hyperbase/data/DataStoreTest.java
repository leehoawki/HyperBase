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
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "C");
        store.set("4", "D");
        store.set("2", "E");
        store.set("3", "F");
        store.dump();
        DataStore store2 = new DataStoreImpl(mstore.getMeta(name));
        assertEquals("A", store2.get("1").getVal());
        assertEquals("E", store2.get("2").getVal());
        assertEquals("F", store2.get("3").getVal());
        assertEquals("D", store2.get("4").getVal());
    }
}
