package intellif.jobs;

import intellif.consts.GlobalConsts;
import intellif.service.PersonDetailServiceItf;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshPersonStatusJob extends Thread {

	@Autowired
	private PersonDetailServiceItf personDetailService;

	 @Scheduled(cron="0 0 0 * * ?")
	public void run()
	{
		 if(!GlobalConsts.run){
			 return;
		 }
		Date now = new Date();
		try {
			personDetailService.refreshPerson();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished refresh person status.");
		System.out.println("Refresh person status cost time："+(new Date().getTime()-now.getTime()));
	}

}
