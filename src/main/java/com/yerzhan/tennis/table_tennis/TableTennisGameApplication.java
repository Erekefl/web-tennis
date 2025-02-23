package com.yerzhan.tennis.table_tennis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.yerzhan.tennis.table_tennis",
	"com.yerzhan.tennis.table_tennis.service",
	"com.yerzhan.tennis.table_tennis.service.impl",
	"com.yerzhan.tennis.table_tennis.controller",
	"com.yerzhan.tennis.table_tennis.config"
})
public class TableTennisGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(TableTennisGameApplication.class, args);
	}

}
