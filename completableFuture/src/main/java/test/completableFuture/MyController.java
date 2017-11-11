package test.completableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Simple Spring boot application
 */

@RestController
public class MyController {
	
	private final Logger logger = LoggerFactory.getLogger(MyController.class);

    @RequestMapping(path="/",method=RequestMethod.GET)
    @ResponseBody
    String home() throws InterruptedException {
    	
    	logger.info("processing request");
    	Thread.sleep(1000);
        return "Hello World!";
    }
}