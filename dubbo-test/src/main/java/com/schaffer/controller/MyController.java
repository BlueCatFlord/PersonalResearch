package com.schaffer.controller;


import com.schaffer.service.DemoHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
 * Created by schaffer on 2016/9/1.
 */
@Controller
public class MyController {

    @Resource(name = "demoService")
    private DemoHelloService demoHelloService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String callService(Model model) {
        String result = demoHelloService.sayHello("schaffer");
        model.addAttribute("name", result);
        return "hello";
    }
}
