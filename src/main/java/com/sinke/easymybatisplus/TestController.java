package com.sinke.easymybatisplus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
     @GetMapping("test")
    public  void test(){
      int d=  DbHelper.getTableDao(Student.class).count();
         System.out.println(d);
    }
}
