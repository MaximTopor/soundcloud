package org.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;

    public DatabaseConfig() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();
        try {
            // Спробуємо завантажити з файлу
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            System.out.println("❌ Файл config.properties не знайдено, використовуються значення за замовчуванням");
            // Значення за замовчуванням
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/music_player");
            props.setProperty("db.username", "music_user");
            props.setProperty("db.password", "root");
        }

        this.url = props.getProperty("db.url");
        this.username = props.getProperty("db.username");
        this.password = props.getProperty("db.password");

        System.out.println("🔧 Конфігурація БД:");
        System.out.println("   URL: " + url);
        System.out.println("   User: " + username);
        System.out.println("   Password: " + (password != null ? "***" : "null"));
    }

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}