package edu.njucm.retrievejava.controllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.njucm.retrievejava.controller.DocumentController;
import edu.njucm.retrievejava.es.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Search {
    @Autowired
    SearchService searchService;

    @Test
    void Test1() throws JsonProcessingException {
        System.out.println(searchService.search("title","bigtable"));;
    }
}
