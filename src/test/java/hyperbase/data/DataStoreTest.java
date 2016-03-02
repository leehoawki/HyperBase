package hyperbase.data;


import hyperbase.meta.Meta;
import hyperbase.meta.MetaStore;
import hyperbase.meta.MetaStoreImpl;
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
    public void testGetAndSet() {
        store.set("1", "A");
        store.set("1", "B");
        store.set("1", "A");
        store.set("2", "B");
        store.set("2", "BBBBBBBBBB");
        store.set("3", "A");
        store.set("3", null);
        store.set("3", "");
        store.set("", "A");

        assertEquals(store.get("1").getVal(), "A");
        assertEquals(store.get("2").getVal(), "BBBBBBBBBB");
        assertEquals(store.get("3").getVal(), "");
        assertEquals(store.get("").getVal(), "A");
    }

    @Test
    public void testRestore() {
        store.set("1", "A");
        store.set("1", "B");
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "A");
        store.set("3", null);

        DataStore store2 = factory.createStore(mstore.getMeta(name));
        store2.restore();

        assertEquals(store2.get("1").getVal(), "A");
        assertEquals(store2.get("2").getVal(), "B");
        assertEquals(store2.get("3").getVal(), null);
    }
}
