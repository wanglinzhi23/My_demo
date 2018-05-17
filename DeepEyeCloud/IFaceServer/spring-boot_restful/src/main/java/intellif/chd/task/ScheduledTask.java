package intellif.chd.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import intellif.chd.dto.FaceQuery;
import intellif.chd.service.TimesClusterPersonItf;

@Component
public class ScheduledTask {

	private static final Logger LOG = LogManager.getLogger(ScheduledTask.class);
	@Autowired
	private TimesClusterPersonItf timesClusterPersonItf;

	@Scheduled(cron = "${scheduler.cron}")
	public void fixedRunningTask() {
		try {
			FaceQuery faceQuery = new FaceQuery();
			timesClusterPersonItf.start(faceQuery);
		} catch (Throwable e) {
			LOG.info("fail to start task ,{} catch exception: ", e);
		}
	}

}
