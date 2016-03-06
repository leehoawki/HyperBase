package hyperbase.data;


import hyperbase.meta.Meta;

public interface DataStoreFactory {
    public DataStore createStore(Meta meta);

    public DataStore restoreStore(Meta meta);
}
