package hyperbase.data;


public interface DataStore {
    public void set(String key, String val);

    public void set(Data data);

    public Data get(String key);

    public void dump();

    public void load();
}
