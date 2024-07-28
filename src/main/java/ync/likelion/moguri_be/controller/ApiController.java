package ync.likelion.moguri_be.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ync.likelion.moguri_be.model.TestTable;
import ync.likelion.moguri_be.repository.TestTableRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TestTableRepository testTableRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/success")
    public String success() {
        return "Success!";
    }

    @GetMapping("/dbtest")
    public List<TestTable> dbTest() {
        return testTableRepository.findAll();
    }
}
