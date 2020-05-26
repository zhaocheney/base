package com.zhaoch.base.code.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Launcher
 *
 * @author Zhaochen
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {"com.zhaoch.mybatisplus"})
public class CodeAutoGeneratorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CodeAutoGeneratorApplication.class, args);
        CodeAutoGenerator codeAutoGenerator = (CodeAutoGenerator) run.getBean("codeAutoGenerator");
        codeAutoGenerator.generate();
    }

}
