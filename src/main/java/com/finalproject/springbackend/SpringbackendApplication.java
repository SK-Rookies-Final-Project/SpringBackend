package com.finalproject.springbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication
public class SpringbackendApplication {

	public static void main(String[] args) {
		// 1. .env 파일 찾아 메모리에 로드
		Dotenv dotenv = Dotenv.load();
		// 2. .env 파일에 있는 모든 줄(KEY=VALUE)을 읽어 자바 시스템 속성으로 등록
		dotenv.entries().forEach(entry ->
				System.setProperty(
						entry.getKey(),
						entry.getValue()
				)
		);
		//3. 설정 준비 후 Spring Boot 시작
		SpringApplication.run(SpringbackendApplication.class, args);
	}

}
