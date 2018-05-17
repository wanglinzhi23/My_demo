package com.intellif.bankmatch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.intellif.bankmatch.service.DataFetch;

public class TopFaceMatch {
	
	public static Logger logger = Logger.getLogger(TopFaceMatch.class);
	public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	public static Long lastPersonId = (long) 0;
	public static Boolean taskFinished = false;
	public static Boolean timeToStop = false;
	
	public static void main(String[] args) {
//		long delay = computeNextDelay(19, 05, 0);
//		long delaystop = 5 * 60;
		long delay = computeNextDelay(3, 0, 0);
		long delaystop = (7 - 3) * 60 * 60;
		executorService.scheduleAtFixedRate(() -> {
			logger.info("It's a good day! Task starting.. ");
			timeToStop = false;
			executorService.schedule(() -> {
				logger.info("It's time to have a break... Let's continue tomorrow.");
				timeToStop = true;
				if (taskFinished) stop();
			}, delaystop, TimeUnit.SECONDS);
			DataFetch dataFetch = new DataFetch();
			dataFetch.fetchPersonInfo();
//		}, delay, 60 * 7, TimeUnit.SECONDS);
		}, delay, 24 * 60 * 60, TimeUnit.SECONDS);
	}
	
	private static long computeNextDelay(int targetHour, int targetMin, int targetSec) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) > 0) {
            zonedNextTarget = zonedNextTarget.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }
	
	public static void stop() {
		logger.info("ScheduledExecutorService going to shutdown...");
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
        	ex.printStackTrace();
        }
    }
}
