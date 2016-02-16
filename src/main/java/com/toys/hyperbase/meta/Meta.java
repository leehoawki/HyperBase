package com.toys.hyperbase.meta;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class Meta {

    static Logger LOG = Logger.getLogger(Meta.class);

    String path;

    PropertiesConfiguration pc;

    List<String> tableNames;

    long timestamp;

    public Meta() {
        this("Hyper.ctl");
    }

    public Meta(String path) {
        try {
            this.path = path;
            pc = new PropertiesConfiguration(path);
            tableNames = pc.getList("tables");
            timestamp = pc.getLong("timestamp");
        } catch (ConfigurationException ex) {
            LOG.error(String.format("Control file %s not existed. HyperBase shutdown.", path));
            throw new IllegalStateException(ex);
        }
    }

    public synchronized void sync() {
        try {
            pc.save();
        } catch (ConfigurationException ex) {
            LOG.error(String.format("Control file %s saving failed. HyperBase shutdown.", path), ex);
            throw new IllegalStateException(ex);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public synchronized void addTable(String name) {
        for (String n : tableNames) {
            if (n.equals(name)) {
                LOG.error("");
                throw new IllegalStateException();
            }
        }
        this.tableNames.add(name);
    }

    public synchronized void removeTable(String name) {
        boolean r = tableNames.remove(name);
        if (!r) {
            LOG.error("");
            throw new IllegalStateException();
        }
    }
}
