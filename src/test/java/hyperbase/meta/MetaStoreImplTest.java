package hyperbase.meta;

import junit.framework.TestCase;
import org.junit.Test;

public class MetaStoreImplTest extends TestCase {

    @Override
    public void setUp() {

    }

    @Override
    public void tearDown() {

    }

    @Test
    public void test() {
        MetaStore store = new MetaStoreImpl();
        assertEquals(store.getAllMeta().size(), 0);
        store.add("MetaStoreTest1");

        MetaStore store2 = new MetaStoreImpl();
        assertNotNull(store2.getMeta("MetaStoreTest1"));
        store2.delete("MetaStoreTest1");

        MetaStore store3 = new MetaStoreImpl();
        assertEquals(store3.getAllMeta().size(), 0);
    }
}
