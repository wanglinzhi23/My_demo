package intellif;

import com.google.inject.Guice;
import com.google.inject.Injector;
import intellif.audit.AuditAdvice;
import intellif.configs.PropertiesInitializer;
import intellif.enums.MqttTopicNames;
import intellif.guice.TaskResumeModule;
import intellif.mqtt.EventBusHelper;
import intellif.mqtt.MqttEngRptEBSubscriber;
import intellif.service.MqttMessageServiceItf;
import intellif.service.impl.MqttMessageServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.rmi.RemoteException;

@ImportResource({"classpath*:simplesm-context.xml", "memcached.xml"})
@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing//@see: http://blog.countableset.ch/
public class Application extends SpringBootServletInitializer {

    private static Logger LOG = LogManager.getLogger(Application.class);
    
    //
    private static Class<Application> applicationClass = Application.class;

    public static void main(String[] args) throws TException, RemoteException {
        // SpringApplication.run(Application.class, args);
        //
        ConfigurableApplicationContext context = new SpringApplicationBuilder(applicationClass)
                .initializers(new PropertiesInitializer()).run(args);
        LOG.info("ApplicationContext:" + context.getDisplayName() + context.getStartupDate());
        //Guice module inject here,@see: https://github.com/google/guice/wiki/GettingStarted
        Injector task_injector = Guice.createInjector(new TaskResumeModule());
        //Global Task status monitor system start-up.
        /*
         * @see:http://stackoverflow.com/questions/310271/injecting-beans-into-a-class-outside-the-spring-managed-context
		 * https://code.google.com/p/spring-eventbus/ AlarmServiceItf
		 */
        EventBusHelper eventBusHelper = new EventBusHelper();
        context.getAutowireCapableBeanFactory().autowireBeanProperties(eventBusHelper, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT,true);
        MqttMessageServiceItf mqttMessageServiceItf = new MqttMessageServiceImpl();
        context.getAutowireCapableBeanFactory().autowireBeanProperties(mqttMessageServiceItf, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, true);
        mqttMessageServiceItf.setup(MqttTopicNames.EngineReport.getValue());
        
        //EventBus register
        MqttEngRptEBSubscriber mqttEngRptEBSubscriber = new MqttEngRptEBSubscriber(task_injector); //
        eventBusHelper.registerSubscriber(mqttEngRptEBSubscriber); //
    }
    
    //
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //
        return application.sources(applicationClass);
    }

    // @see:
    // http://stackoverflow.com/questions/26425067/resolvedspring-boot-access-to-entitymanager
    @Bean
    public PersistenceAnnotationBeanPostProcessor persistenceBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    //
    @Bean
    public AuditAdvice auditAdvice() {
        return new AuditAdvice();
    }
    
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }
    
    @Profile("jetty")
    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(
            JettyServerCustomizer jettyServerCustomizer) {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.addServerCustomizers(jettyServerCustomizer);
        return factory;
    }


    @Bean
    public JettyServerCustomizer jettyServerCustomizer() {
        return server -> {
            // Tweak the connection config used by Jetty to handle incoming HTTP
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMaxThreads(500);
            threadPool.setMinThreads(50);
        };
    }
}
