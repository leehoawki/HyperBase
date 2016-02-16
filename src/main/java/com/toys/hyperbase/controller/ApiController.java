package com.toys.hyperbase.controller;

import com.toys.hyperbase.service.HyperService;
import com.toys.hyperbase.service.model.Row;
import com.toys.hyperbase.service.model.Table;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class ApiController {

    static Logger logger = Logger.getLogger(ApiController.class);

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
        //TODO
        return null;
    }

    @RequestMapping(value = "{table}", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateTable(@RequestBody String action) {
        //TODO
        return null;
    }

    @RequestMapping(value = "{table}/{key}", method = RequestMethod.POST)
    public
    @ResponseBody
    String update(@RequestBody String value) {
        //TODO
        return null;
    }
}