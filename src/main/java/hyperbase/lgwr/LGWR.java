package hyperbase.lgwr;


import hyperbase.service.HyperService;


public interface LGWR {
    public void append(Redo redo);

    public void restore(HyperService service);
}
