package cm.rc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        log.info("bootstrap类启动中");
        SpringApplication.run(Bootstrap.class, args);
    }
}
