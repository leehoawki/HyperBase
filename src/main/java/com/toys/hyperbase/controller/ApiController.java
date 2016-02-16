package com.toys.hyperbase.controller;

import com.toys.hyperbase.service.HyperService;
import com.toys.hyperbase.service.model.Row;
import com.toys.hyperbase.service.model.Table;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class ApiController {

    static Logger LOG = Logger.getLogger(ApiController.class);

    @Autowired
    public HyperService service;

    @RequestMapping(value = "tables", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Table> getTables() {
        return service.getTables();
    }

    @RequestMapping(value = "{table}", method = RequestMethod.GET)
    public
    @ResponseBody
    Table getTable(@PathVariable String table) {
        return service.getTable(table);
    }

    @RequestMapping(value = "{table}/{key}", method = RequestMethod.GET)
    public
    @ResponseBody
    Row get(@PathVariable String table, @PathVariable String key) {
        return service.get(table, key);
    }

    @RequestMapping(value = "{table}", method = RequestMethod.POST)
    public
    @ResponseBody
    void updateTable(@PathVariable String table, @RequestBody String action) {
        if ("create".equals(action)) {
            service.createTable(table);
        } else if ("delete".equals(action)) {
            service.deleteTable(table);
        } else {
            throw new IllegalArgumentException(String.format("Illegal Action : %s. Only create/delete are supported.", action));
        }
    }

    @RequestMapping(value = "{table}/{key}", method = RequestMethod.POST)
    public
    @ResponseBody
    void set(@PathVariable String table, @PathVariable String key, @RequestBody String value) {
        service.set(table, key, value);
    }
}