package hyperbase.lgwr;


import java.util.Iterator;


public interface LGWR {
    public void append(Redo redo);

    public Iterator<Redo> read();
}
