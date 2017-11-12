package test.completableFuture;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import Config.MyThreadFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class myControllerTest {

	private static final int MAX_THREAD_POOL_SIZE = 100;
	private static final String HOST = "http://localhost:";
	private static final String PATH = "/";
	private static final int tpkListSize = 40;
	private final Logger logger = LoggerFactory.getLogger(myControllerTest.class);
	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
	}

	@Test
	public void CompletableFuturePerformances() throws InterruptedException, ExecutionException {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();

		// create a Thread Pool exclusively meant for the rest call execution
		final Executor executor = Executors.newFixedThreadPool(getProperPoolSize(), new MyThreadFactory());
		// foreach call we have a Thread executing the rest call
		List<CompletableFuture<String>> futures = IntStream.range(1, tpkListSize)
				.mapToObj(i -> CompletableFuture
						.supplyAsync(() -> restTemplate.getForObject(HOST + port + PATH, String.class), executor))
				.collect(Collectors.toList());

		// synchronization point (important feature when, at a certain point, we need
		// to use the results of the remote call)
		futures.forEach(CompletableFuture::join);

		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("Async version: job completed after " + between.getSeconds() + " seconds");
	}

	private int getProperPoolSize() {
		return Math.min(tpkListSize, MAX_THREAD_POOL_SIZE);
	}

	@Test
	public void CompletableFutureParallelPerformances() throws InterruptedException, ExecutionException {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();
		List<CompletableFuture<String>> res = new ArrayList<>();
		IntStream.range(1, tpkListSize).parallel().forEach(number -> res
				.add(CompletableFuture.supplyAsync(() -> restTemplate.getForObject(HOST + port + PATH, String.class))));
		// catches the async calls results
		res.stream().forEach(string -> {
			try {
				string.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("Parallel Async version: job completed after " + between.getSeconds() + " seconds");
	}

	@Test
	@Ignore
	public void RestPerformances() {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();
		for (int i = 0; i < 20; i++) {
			restTemplate.getForObject(HOST + port + PATH, String.class);
		}
		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("Sync version: job completed after " + between.getSeconds() + " seconds");
	}

	@Test
	public void RestParallelPerformances() {
		LocalDateTime startTime = LocalDateTime.now();
		RestTemplate restTemplate = new RestTemplate();
		IntStream.range(1, tpkListSize).parallel()
				.forEach(number -> restTemplate.getForObject(HOST + port + PATH, String.class));
		LocalDateTime endTime = LocalDateTime.now();
		Duration between = Duration.between(startTime, endTime);
		logger.info("Parallel version: job completed after " + between.getSeconds() + " seconds");
	}
}
