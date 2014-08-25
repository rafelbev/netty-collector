package workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import workshop.config.DefaultConfig;

public class EngineReceiver {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DefaultConfig.class, args);
    }
}
