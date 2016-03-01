package hyperbase.data;


import java.io.Serializable;
import java.util.zip.CRC32;

public class Data implements Serializable {

    String key;

    String val;

    public Data(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public Data(String cell) {
        String crc = cell.substring(0, 10);
        int ksz = Integer.valueOf(cell.substring(10, 16));
        this.key = cell.substring(16, 16 + ksz);
        int vsz = Integer.valueOf(cell.substring(16 + ksz, 22 + ksz));
        this.val = cell.substring(22 + ksz, 22 + ksz + vsz);
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

    @Override
    public String toString() {
        CRC32 crc32 = new CRC32();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(SZ_FORMAT, key.length()));
        sb.append(key);
        sb.append(String.format(SZ_FORMAT, val.length()));
        sb.append(val);
        String re = sb.toString();
        crc32.update(re.getBytes());

        return String.valueOf(crc32.getValue()) + re;
    }

    static final String SZ_FORMAT = "%06d";
}
