package hyperbase.data;


import java.io.*;

public class Data implements Serializable {

    String key;

    String val;

    long timestamp;

    public Data(String key, String val, long timestamp) {
        this.key = key;
        this.val = val;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }

    public long getTimestamp() {
        return timestamp;
    }

    static Data deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis);) {
            return (Data) ois.readObject();
        }
    }

    static byte[] serialize(Data data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(data);
            oos.flush();
            return bos.toByteArray();
        }
    }
}
