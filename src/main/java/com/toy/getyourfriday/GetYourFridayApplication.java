package com.toy.getyourfriday;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GetYourFridayApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GetYourFridayApplication.class).run(args);
    }
}
