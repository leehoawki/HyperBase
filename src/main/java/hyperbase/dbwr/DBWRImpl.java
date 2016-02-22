package hyperbase.dbwr;

import hyperbase.data.DataStore;
import org.springframework.stereotype.Component;

@Component
public class DBWRImpl implements DBWR {

    @Override
    public void write(DataStore store) {
        store.dump();
    }
}

