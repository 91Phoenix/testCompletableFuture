package test.completableFuture;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class myControllerTest {

	private static final String HOST = "http://localhost:";
	private static final String PATH = "/";
	private final Logger logger = LoggerFactory.getLogger(myControllerTest.class);
	@LocalServerPort
	private int port;

	@Test
	public void CompletableFuturePerformances() throws InterruptedException, ExecutionException {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();
		List<CompletableFuture<String>> res = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			res.add(CompletableFuture.supplyAsync(() -> restTemplate.getForObject(HOST + port + PATH, String.class)));
		}
		res.stream().forEach(string -> {
			try {
				logger.info(string.get());
			} catch (Exception e) {
				e.printStackTrace();
			}});
		
		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("job completed after " + between.getSeconds() + " seconds");
	}

	@Test
	public void RestPerformances() {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();
		for (int i = 0; i < 5; i++) {
			restTemplate.getForObject(HOST + port + PATH, String.class);
		}
		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("job completed after " + between.getSeconds() + " seconds");
	}
}
