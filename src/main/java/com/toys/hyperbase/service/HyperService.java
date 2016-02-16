package com.toys.hyperbase.service;

import com.toys.hyperbase.meta.MetaStore;
import com.toys.hyperbase.service.model.Row;
import com.toys.hyperbase.service.model.Table;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HyperService {

    static Logger LOG = Logger.getLogger(HyperService.class);

    @Autowired
    MetaStore metaStore;

    public HyperService() {

    }

    public List<Table> getTables() {
        //TODO
        return null;
    }

    public Table getTable(String name) {
        //TODO
        return null;
    }

    public void createTable(String name) {
        //TODO
    }

    public void deleteTable(String name) {
        //TODO
    }

    public Row get(String table, String key) {
        //TODO
        return null;
    }

    public void set(String table, String key, String val) {
        //TODO
    }
}
