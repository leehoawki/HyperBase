package com.toys.hyperbase.service;

import com.toys.hyperbase.meta.HyperMetaStore;
import com.toys.hyperbase.service.model.Row;
import com.toys.hyperbase.service.model.Table;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HyperServiceImpl implements HyperService {

    static Logger LOG = Logger.getLogger(HyperServiceImpl.class);

    @Autowired
    HyperMetaStore metaStore;

    public HyperServiceImpl() {

    }

    @Override
    public List<Table> getTables() {
        //TODO
        return null;
    }

    @Override
    public Table getTable(String name) {
        //TODO
        return null;
    }

    @Override
    public void createTable(String name) {
        //TODO
    }

    @Override
    public void deleteTable(String name) {
        //TODO
    }

    @Override
    public Row get(String table, String key) {
        //TODO
        return null;
    }

    @Override
    public void set(String table, String key, String val) {
        //TODO
    }
}
