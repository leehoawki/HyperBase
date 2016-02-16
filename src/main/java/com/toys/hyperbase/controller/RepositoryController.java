package com.toys.hyperbase.controller;

import com.toys.hyperbase.model.Repository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/repository")
public class RepositoryController {

    @RequestMapping(value = "{name}", method = RequestMethod.GET)
    public
    @ResponseBody
    Repository getShopInJSON(@PathVariable String name) {
        Repository repository = new Repository();
        repository.setName(name);

        return repository;
    }
}