package hyperbase.meta;


public final class Meta {
    final String name;

    final String dir;

    public Meta(String name, String dir) {
        this.name = name;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }


    public String getDir() {
        return dir;
    }
}
