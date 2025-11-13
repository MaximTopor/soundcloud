package org.example.service;

import org.example.model.Track;
import org.example.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabaseManager {
    private DatabaseConfig config;

    public MySQLDatabaseManager() {
        this.config = new DatabaseConfig();
        if (initializeDatabase()) {
            System.out.println("✅ MySQL підключено успішно!");
        } else {
            System.out.println("❌ Не вдалося підключитися до MySQL");
            System.out.println("📋 Переконайся, що:");
            System.out.println("   - MySQL сервер запущений");
            System.out.println("   - Користувач та база даних створені");
            System.out.println("   - config.properties містить правильні дані");
        }
    }

    private boolean initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS tracks (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "title VARCHAR(255) NOT NULL," +
                    "artist VARCHAR(255)," +
                    "album VARCHAR(255)," +
                    "genre VARCHAR(100)," +
                    "file_path VARCHAR(500) UNIQUE NOT NULL," +
                    "duration INT," +
                    "play_count INT DEFAULT 0," +
                    "date_added DATETIME" +
                    ")";

            stmt.execute(sql);
            System.out.println("✅ Таблиця tracks створена/перевірена");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Помилка ініціалізації MySQL: " + e.getMessage());
            return false;
        }
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getUrl(), config.getUsername(), config.getPassword());
    }

    public boolean addTrack(Track track) {
        String sql = "INSERT INTO tracks (title, artist, album, genre, file_path, duration, date_added) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, track.getTitle());
            pstmt.setString(2, track.getArtist());
            pstmt.setString(3, track.getAlbum());
            pstmt.setString(4, track.getGenre());
            pstmt.setString(5, track.getFilePath());
            pstmt.setInt(6, track.getDuration());
            pstmt.setTimestamp(7, Timestamp.valueOf(track.getDateAdded()));

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("⚠️ Трек вже є в базі: " + track.getFilePath());
            } else {
                System.out.println("❌ Помилка додавання треку: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY title";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Track track = new Track(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre"),
                        rs.getString("file_path"),
                        rs.getInt("duration"),
                        rs.getInt("play_count"),
                        rs.getTimestamp("date_added").toLocalDateTime()
                );
                tracks.add(track);
            }
            System.out.println("✅ Завантажено " + tracks.size() + " треків з MySQL");

        } catch (SQLException e) {
            System.out.println("❌ Помилка завантаження треків: " + e.getMessage());
        }

        return tracks;
    }

    public void updatePlayCount(int trackId, int playCount) {
        String sql = "UPDATE tracks SET play_count = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playCount);
            pstmt.setInt(2, trackId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Помилка оновлення лічильника: " + e.getMessage());
        }
    }

    public boolean trackExists(String filePath) {
        String sql = "SELECT COUNT(*) FROM tracks WHERE file_path = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, filePath);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.out.println("Помилка перевірки треку: " + e.getMessage());
            return false;
        }
    }

    public int getTotalPlayCount() {
        String sql = "SELECT SUM(play_count) FROM tracks";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.out.println("Помилка отримання статистики: " + e.getMessage());
            return 0;
        }
    }

    public List<Track> getMostPlayedTracks(int limit) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY play_count DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Track track = new Track(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre"),
                        rs.getString("file_path"),
                        rs.getInt("duration"),
                        rs.getInt("play_count"),
                        rs.getTimestamp("date_added").toLocalDateTime()
                );
                tracks.add(track);
            }

        } catch (SQLException e) {
            System.out.println("Помилка отримання популярних треків: " + e.getMessage());
        }

        return tracks;
    }
}