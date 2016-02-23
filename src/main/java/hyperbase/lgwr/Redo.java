package hyperbase.lgwr;


import org.apache.commons.lang.ArrayUtils;

public class Redo {

    String action;

    String[] data;

    public Redo(String action, String... data) {
        this.action = action;
        this.data = data;
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


