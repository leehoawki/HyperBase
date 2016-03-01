package hyperbase.data;


public class Hint {

    String key;

    String fileName;

    long offset;

    int size;


    public Hint(String key, String name, long offset, int size) {
        this.key = key;
        this.fileName = name;
        this.offset = offset;
        this.size = size;
    }
}
