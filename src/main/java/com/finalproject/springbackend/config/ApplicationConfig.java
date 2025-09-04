package com.finalproject.springbackend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import jakarta.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class ApplicationConfig {

    @PostConstruct
    public void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./") // 프로젝트 루트 디렉토리
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // 환경변수를 시스템 프로퍼티로 설정
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });

            System.out.println(".env file loaded successfully");
            System.out.println("KAFKA_TOPIC_AUDIT_LOG: " + System.getProperty("KAFKA_TOPIC_AUDIT_LOG"));
            System.out.println("KAFKA_TOPIC_UNAUTHORIZED: " + System.getProperty("KAFKA_TOPIC_UNAUTHORIZED"));

        } catch (Exception e) {
            System.err.println("❌ Error loading .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}