package hyperbase.data;


public class Hint {

    String key;

    String fileName;

    long offset;

    long timestamp;

    public Hint(String key, String name, long offset, long timestamp) {
        this.key = key;
        this.fileName = name;
        this.offset = offset;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Key:%s, File:%s, Offset:%s, TS:%s", key, fileName, offset, timestamp);
    }
}