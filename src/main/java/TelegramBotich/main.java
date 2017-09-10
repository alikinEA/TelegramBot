package TelegramBotich;

import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Alikin E.A. on 22.04.17.
 */
@ComponentScan("TelegramBotich")
@EnableAutoConfiguration
@Configuration
public class main {

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(main.class, args);
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(5);
    }

}
