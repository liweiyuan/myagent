package suishen.wade.agent.test.springbootagent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import suishen.wade.agent.test.springbootagent.service.HelloService;

/**
 * @author :lwy
 * @date 2018/7/31 16:22
 */
@RestController
public class HelloController {


    @Autowired
    private HelloService helloService;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "index";
    }


    @RequestMapping("/hello")
    @ResponseBody
    public String hello() throws InterruptedException {
        Thread.sleep(1000);
        return "hello";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/execute", String.class);
        return responseEntity.toString();
    }


    @RequestMapping("/execute")
    @ResponseBody
    public String execute() {
        return "service";
    }


    @RequestMapping("/to")
    @ResponseBody
    public String to() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/from", String.class);
        return "service";
    }

    @RequestMapping("/from")
    @ResponseBody
    public String from() {
        return "from";
    }


    @RequestMapping("/hello/service")
    @ResponseBody
    public String helloService() throws InterruptedException {
        return helloService.getHelloService();
    }
}
