package threadpool.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	private static final int PoolSize = 3;
	private static final int QueueSize = PoolSize * 2;
	private static final long Time2RetrievalMillis = 500L;
	private static final int NumRetrieved = 10;
	private static final long Time2ProcessMillis = 800L;
	private static final int NumLoop = 4;
	
	
	public static void main(String[] args) throws Exception {
		new Main().run();
	}
	
	public void run() throws Exception {
		ExecutorService executor = createThreadPool(PoolSize, QueueSize);

		for(int i=0; i< NumLoop; i++) {
			retrieveAndExecute(executor);
		}
		
		executor.shutdown();
	}
	
	private void retrieveAndExecute(ExecutorService executor) throws InterruptedException {
		retrieve().forEach ( i -> {
			log("run() before execute() ...");
			executor.execute(createRunnable(i));
			log("run() after  execute() ...");
		});
	}

	private Runnable createRunnable(Integer i) {
		return () -> {try {
			log("createRunnable() before sleep ...");
			Thread.sleep(Time2ProcessMillis);
			log("createRunnable() wake up from sleeping");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}};
	}

	private List<Integer> retrieve() throws InterruptedException  {
		log("retrieve() before sleep ...");
		Thread.sleep(Time2RetrievalMillis);
		log("retrieve() wake up from sleeping");

		return Collections.nCopies(NumRetrieved, new Integer(3));
	}
	
	private ExecutorService createThreadPool(int poolSize, int queueSize) {
		return new ThreadPoolExecutor(
			poolSize, poolSize, 
			0L, TimeUnit.MILLISECONDS, 
			new LinkedBlockingQueue<Runnable>(queueSize), 
			new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	private static void log(String ptn, Object... objects) {
		String dateTimeSec = LocalDateTime.now().format(formatter);
		String threadName = String.format(" [%-15s] ", Thread.currentThread().getName());
		
		System.out.println(dateTimeSec + threadName + String.format(ptn, objects));
	}
}