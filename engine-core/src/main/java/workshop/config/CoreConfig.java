package workshop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan({"workshop.dao", "workshop.service"})
@EnableMongoRepositories("workshop.dao")
public class CoreConfig {
}
