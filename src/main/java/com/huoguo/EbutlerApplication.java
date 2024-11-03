package com.huoguo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class EbutlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbutlerApplication.class, args);
    }
}
