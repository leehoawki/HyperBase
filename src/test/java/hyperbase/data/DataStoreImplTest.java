package hyperbase.data;


import hyperbase.meta.Meta;
import hyperbase.meta.MetaStore;
import hyperbase.meta.MetaStoreImpl;
import junit.framework.TestCase;
import org.junit.Test;

public class DataStoreImplTest extends TestCase {
    MetaStore mstore;

    DataStoreImpl store;

    Meta meta;

    String name = "test";

    @Override
    public void setUp() {
        mstore = new MetaStoreImpl();
        mstore.add(name);
        meta = mstore.getMeta(name);
        store = new DataStoreImpl(meta);
    }

    @Override
    public void tearDown() {
        mstore.delete(name);
        store.destroy();
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

        DataStore store2 = new DataStoreImpl(meta);
        store2.restore();

        assertEquals(store2.get("1").getVal(), "A");
        assertEquals(store2.get("2").getVal(), "B");
        assertEquals(store2.get("3").getVal(), null);
    }

    @Test
    public void testArchive() {
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "C");
        store.curr += 1;
        store.set("1", "D");
        store.set("2", "E");
        store.set("4", "F");
        store.curr += 1;
        store.set("5", "G");
        store.set("6", "H");
        store.set("7", "I");

        DataStore store2 = new DataStoreImpl(meta);
        store2.restore();
        assertEquals(store2.get("1").getVal(), "D");
        assertEquals(store2.get("2").getVal(), "E");
        assertEquals(store2.get("3").getVal(), "C");
        assertEquals(store2.get("4").getVal(), "F");
        assertEquals(store2.get("5").getVal(), "G");
        assertEquals(store2.get("6").getVal(), "H");
        assertEquals(store2.get("7").getVal(), "I");
    }
}
