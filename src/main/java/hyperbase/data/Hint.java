package hyperbase.data;


public class Hint {

    String key;

    String fileName;

    long offset;

    public Hint(String key, String name, long offset) {
        this.key = key;
        this.fileName = name;
        this.offset = offset;
    }
}
