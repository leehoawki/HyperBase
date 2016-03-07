package hyperbase.data;


import hyperbase.meta.Meta;
import hyperbase.meta.MetaStore;
import hyperbase.meta.MetaStoreImpl;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class DataStoreImplTest extends TestCase {

    DataStoreImpl store;

    Meta meta;

    String name = "test";

    MetaStore mstore = new MetaStoreImpl();

    DataStoreFactory sdf = new DataStoreFactoryImpl();

    @Override
    public void setUp() {
        mstore.add(name);
        meta = mstore.getMeta(name);
        store = (DataStoreImpl) sdf.createStore(meta);
        store.online();
    }

    @Override
    public void tearDown() {
        store.offline();
        store.destroy();
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
        store.offline();
        assertEquals(store.get("1").getVal(), "A");
        assertEquals(store.get("2").getVal(), "BBBBBBBBBB");
        assertEquals(store.get("3").getVal(), "");
        assertEquals(store.get("").getVal(), "A");
    }

    @Test
    public void testRestore() throws IOException {
        store.set("1", "A");
        store.set("1", "B");
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "A");
        store.set("3", null);
        store.offline();
        sdf.restoreStore(meta);
        DataStore store2 = sdf.restoreStore(meta);
        assertEquals(store2.get("1").getVal(), "A");
        assertEquals(store2.get("2").getVal(), "B");
        assertEquals(store2.get("3").getVal(), null);
        store2.offline();
        store2.destroy();
    }

    @Test
    public void testArchive() {
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "C");
        store.offline();
        store.online();
        store.archive();
        store.set("1", "D");
        store.set("2", "E");
        store.set("4", "F");
        store.offline();
        store.online();
        store.archive();
        store.set("5", "G");
        store.set("6", "H");
        store.set("7", "I");
        store.offline();

        DataStore store2 = sdf.restoreStore(meta);
        assertEquals(store2.get("1").getVal(), "D");
        assertEquals(store2.get("2").getVal(), "E");
        assertEquals(store2.get("3").getVal(), "C");
        assertEquals(store2.get("4").getVal(), "F");
        assertEquals(store2.get("5").getVal(), "G");
        assertEquals(store2.get("6").getVal(), "H");
        assertEquals(store2.get("7").getVal(), "I");
        store2.offline();
        store2.destroy();
    }

    @Test
    public void testMerge() {
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "C");
        store.offline();
        store.online();
        store.archive();
        store.set("1", "D");
        store.set("2", "E");
        store.set("4", "F");
        store.offline();
        store.online();
        store.archive();
        store.set("5", "G");
        store.set("6", "H");
        store.set("7", "I");
        store.offline();
        store.merge();

        assertEquals(store.get("1").getVal(), "D");
        assertEquals(store.get("2").getVal(), "E");
        assertEquals(store.get("3").getVal(), "C");
        assertEquals(store.get("4").getVal(), "F");
        assertEquals(store.get("5").getVal(), "G");
        assertEquals(store.get("6").getVal(), "H");
        assertEquals(store.get("7").getVal(), "I");

        store.online();
        store.set("1", "A");
        store.set("2", "B");
        store.set("3", "C");
        store.offline();
        store.archive();
        store.merge();

        assertEquals(store.get("1").getVal(), "A");
        assertEquals(store.get("2").getVal(), "B");
        assertEquals(store.get("3").getVal(), "C");
        assertEquals(store.get("4").getVal(), "F");
        assertEquals(store.get("5").getVal(), "G");
        assertEquals(store.get("6").getVal(), "H");
        assertEquals(store.get("7").getVal(), "I");
    }
}
