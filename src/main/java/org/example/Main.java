package org.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RedisConfig.class);
        AutoCompleteGenerator autoCompleteGenerator = applicationContext.getBean("autoCompleteGenerator", AutoCompleteGenerator.class);

        autoCompleteGenerator.start();

    }

}