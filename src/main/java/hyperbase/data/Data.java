package hyperbase.data;


import java.io.Serializable;

public class Data implements Serializable {

    long timestamp;

    String key;

    String val;

    public Data() {

    }

    public Data(String key, String val, long timestamp) {
        this.key = key;
        this.val = val;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
