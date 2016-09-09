package com.schaffer.service;


import org.springframework.stereotype.Component;

/**
 * Created by schaffer on 2016/8/28.
 */
@Component("demoService")
public class DemoHelloServiceImpl implements DemoHelloService {

    @Override
    public String sayHello(String name) {
        return "not hello" + name;
    }
}
