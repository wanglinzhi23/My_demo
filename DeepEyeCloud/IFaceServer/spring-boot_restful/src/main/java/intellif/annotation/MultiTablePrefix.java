package intellif.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface MultiTablePrefix
{
    String shortName();
    String schema();
}