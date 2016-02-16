package com.toys.hyperbase.controller;

import com.toys.hyperbase.model.Repository;
import com.toys.hyperbase.model.Table;
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

    @RequestMapping(value = "repositories", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Repository> getRepositories() {
        List<Repository> list = new ArrayList<Repository>();
        for (int i = 0; i < 3; i++) {
            Repository repository = new Repository();
            repository.setName(String.valueOf(i));
            list.add(repository);
        }
        return list;
    }

    @RequestMapping(value = "{repository}", method = RequestMethod.GET)
    public
    @ResponseBody
    Repository getRepository(@PathVariable String repository) {
        Repository r = new Repository();
        r.setName(repository);

        return r;
    }

    @RequestMapping(value = "{repository}/tables", method = RequestMethod.GET)
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


    @RequestMapping(value = "{repository}/{table}", method = RequestMethod.GET)
    public
    @ResponseBody
    Table getTable(@PathVariable String repository, @PathVariable String table) {
        Table t = new Table();
        t.setName(table);
        return t;
    }


    @RequestMapping(value = "{repository}/{table}/{key}", method = RequestMethod.GET)
    public
    @ResponseBody
    Table get(@PathVariable String repository, @PathVariable String table, @PathVariable String key) {
        Table t = new Table();
        t.setName(table);
        return t;
    }


//    @RequestMapping(value = "{repository}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Repository updateRepository(@RequestBody Action action) {
//
//        return null;
//    }
//
//    @RequestMapping(value = "{repository}/{table}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Repository updateTable(@RequestBody Action action) {
//
//        return null;
//    }
//
//    @RequestMapping(value = "{repository}/{table}/{key}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Repository update(@RequestBody Action action) {
//
//        return null;
//    }


}