package suishen.wade.agent.test.springbootagent.service;

import org.springframework.stereotype.Service;

/**
 * @author :lwy
 * @date 2018/8/6 19:17
 */
@Service
public class HelloService {


    public String getHelloService() throws InterruptedException {
        Thread.sleep(1000);
        return "hello,service";
    }
}
