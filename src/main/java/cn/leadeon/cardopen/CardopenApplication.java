package cn.leadeon.cardopen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.leadeon"})
@MapperScan("cn.leadeon.cardopen.mapper")
public class CardopenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardopenApplication.class, args);
    }
}
