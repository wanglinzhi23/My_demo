package intellif.guice;

import com.google.inject.AbstractModule;
import intellif.jobs.CronJobFactory;
import intellif.service.TaskServiceItf;
import intellif.service.impl.TaskServiceImpl;
import org.quartz.spi.JobFactory;

/**
 * Created by yangboz on 10/27/15.
 */
public class QuartJobModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JobFactory.class).to(CronJobFactory.class);
        bind(TaskServiceItf.class).to(TaskServiceImpl.class);
//        bind(new TypeLiteral<Repository<TaskInfo, Long>>() {
//        }).to(TaskInfoDao.class);
    }
}
