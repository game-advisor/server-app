package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private HelloService helloService;

    @GetMapping("/")
    public String hello(){
        return helloService.hello();
    }
}
