package hyperbase.data;


import hyperbase.meta.HyperMetaStore;
import hyperbase.meta.HyperMetaStoreImpl;
import hyperbase.meta.Meta;
import junit.framework.TestCase;
import org.junit.Test;

public class HyperDataStoreTest extends TestCase {
    HyperMetaStore mstore;

    HyperDataStore store;

    static String name = "test";

    @Override
    public void setUp() {
        mstore = new HyperMetaStoreImpl();
        mstore.add(name);
        Meta meta = mstore.getMeta(name);
        HyperDataStore store = new HyperDataStoreImpl(meta);
    }

    @Override
    public void tearDown() {
        mstore.delete(name);

    }

    @Test
    public void testLoad() {

    }
}
