package intellif.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by yangboz on 12/18/15.
 */
@Component
public class PropertiesBean {

    @Value("${isJar}")//是否以Jar文件运行
    private String isJar;

    @Override
    public String toString() {
        return "PropertiesBean{" +
                "isJar='" + isJar + '\'' +
                '}';
    }

    public Boolean getIsJar() {
        return Boolean.valueOf(isJar);
    }

    public void setIsJar(String isJar) {
        this.isJar = isJar;
    }
}
