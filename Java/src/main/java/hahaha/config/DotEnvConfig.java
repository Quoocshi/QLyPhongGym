package hahaha.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to load .env or .env.local file into Spring Environment
 */
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Try to load .env.local first, then .env
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env.local")
                    .ignoreIfMissing()
                    .load();
            
            // If .env.local not found or empty, try .env
            if (dotenv.entries().isEmpty()) {
                dotenv = Dotenv.configure()
                        .filename(".env")
                        .ignoreIfMissing()
                        .load();
            }
            
            // Add all env variables to Spring Environment
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded from .env: " + entry.getKey() + " = " + 
                    (entry.getKey().contains("PASSWORD") || entry.getKey().contains("SECRET") 
                        ? "***HIDDEN***" 
                        : entry.getValue()));
            });
            
            if (!dotenvMap.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
                System.out.println("✅ Successfully loaded .env file into Spring Environment");
            } else {
                System.out.println("⚠️ No .env file found or it's empty");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load .env file - " + e.getMessage());
        }
    }
}




