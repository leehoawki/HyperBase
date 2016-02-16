package com.toys.hyperbase.controller;

import com.toys.hyperbase.service.model.Row;
import com.toys.hyperbase.service.model.Table;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/")
public class ApiController {


    @RequestMapping(value = "tables", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Table> getTables() {
        List<Table> list = new ArrayList<Table>();
        for (int i = 0; i < 3; i++) {
            Table t = new Table();
            t.setName(String.valueOf(i));
            list.add(t);
        }
        return list;
    }


    @RequestMapping(value = "{table}", method = RequestMethod.GET)
    public
    @ResponseBody
    Table getTable(@PathVariable String table) {
        Table t = new Table();
        t.setName(table);
        return t;
    }


    @RequestMapping(value = "{table}/{key}", method = RequestMethod.GET)
    public
    @ResponseBody
    Row get(@PathVariable String table, @PathVariable String key) {
        Row r = new Row();
        r.setKey(key);
        r.setValue("0");
        return r;
    }


//
//    @RequestMapping(value = "{table}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Repository updateTable(@RequestBody Action action) {
//
//        return null;
//    }
//
//    @RequestMapping(value = "{table}/{key}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Repository update(@RequestBody Action action) {
//
//        return null;
//    }


}