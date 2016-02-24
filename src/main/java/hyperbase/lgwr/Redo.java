package hyperbase.lgwr;


public class Redo {
    long scn;

    String action;

    String[] data;

    public Redo(String action, String... data) {
        this.scn = scn;
        this.action = action;
        this.data = data;
    }

    public long getScn() {
        return scn;
    }

    public String getAction() {
        return action;
    }

    public String[] getData() {
        return data;
    }

    public static final String CREATE = "CREATE";

    public static final String DELETE = "DELETE";

    public static final String UPDATE = "UPDATE";
}


