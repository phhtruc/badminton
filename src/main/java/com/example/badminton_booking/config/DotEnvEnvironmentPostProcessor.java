package com.example.badminton_booking.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.Ordered;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Try to load .env file from project root
        Resource resource = new FileSystemResource(".env");

        if (!resource.exists()) {
            // Try classpath
            resource = new ClassPathResource(".env");
        }

        if (resource.exists()) {
            try {
                Map<String, Object> properties = loadEnvFile(resource);
                environment.getPropertySources()
                        .addLast(new MapPropertySource("dotenv", properties));
                System.out.println("✅ Loaded " + properties.size() + " properties from .env file");
            } catch (IOException e) {
                System.err.println("⚠️ Could not load .env file: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ No .env file found, using default configuration");
        }
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER + 1;
    }

    private Map<String, Object> loadEnvFile(Resource resource) throws IOException {
        Map<String, Object> properties = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse key=value
                int index = line.indexOf('=');
                if (index > 0) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();

                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    properties.put(key, value);
                }
            }
        }

        return properties;
    }
}
