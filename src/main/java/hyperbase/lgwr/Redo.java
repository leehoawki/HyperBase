package hyperbase.lgwr;


import org.apache.commons.lang.StringUtils;

public class Redo {
    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";
    String action;
    String data;
    public Redo(String action, String... args) {
        this.action = action;
        this.data = StringUtils.join(args, ',');
    }
}


