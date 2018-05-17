package intellif.guice;

import com.google.inject.AbstractModule;
import intellif.service.ServerServiceItf;
import intellif.service.TaskServiceItf;
import intellif.service.impl.ServerServiceImpl;
import intellif.service.impl.TaskServiceImpl;

/**
 * Created by yangboz on 12/26/15.
 */
public class TaskResumeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskServiceItf.class).to(TaskServiceImpl.class);
        bind(ServerServiceItf.class).to(ServerServiceImpl.class);
//        bind(PersonDetailServiceItf.class).to(PersonDetailServiceImpl.class);
    }
}
