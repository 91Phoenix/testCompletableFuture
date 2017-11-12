package test.completableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
    	System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
        SpringApplication.run(Main.class, args);
    }
}
