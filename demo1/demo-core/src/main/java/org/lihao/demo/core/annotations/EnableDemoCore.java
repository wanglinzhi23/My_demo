package org.lihao.demo.core.annotations;


import org.lihao.demo.core.configuration.DemoCoreConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Import({DemoCoreConfiguration.class})
public @interface EnableDemoCore {
}
