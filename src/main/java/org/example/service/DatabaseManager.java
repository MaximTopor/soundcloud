package org.example.service;

import org.example.model.Track;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:music_library.db";
    private MusicLibrary library;

    public DatabaseManager() {
        initializeDatabase();
    }
    public void setMusicLibrary(MusicLibrary library) {
        this.library = library;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Змінюємо назву таблиці з tracks на music_tracks
            String sql = "CREATE TABLE IF NOT EXISTS music_tracks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "artist TEXT," +
                    "album TEXT," +
                    "genre TEXT," +
                    "file_path TEXT UNIQUE NOT NULL," +
                    "duration INTEGER," +
                    "play_count INTEGER DEFAULT 0," +
                    "date_added TEXT" +
                    ")";

            stmt.execute(sql);
            System.out.println("База даних ініціалізована");

        } catch (SQLException e) {
            System.out.println("Помилка ініціалізації БД: " + e.getMessage());
        }
    }

    public boolean addTrack(Track track) {
        // Змінюємо на music_tracks
        String sql = "INSERT INTO music_tracks (title, artist, album, genre, file_path, duration, date_added) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, track.getTitle());
            pstmt.setString(2, track.getArtist());
            pstmt.setString(3, track.getAlbum());
            pstmt.setString(4, track.getGenre());
            pstmt.setString(5, track.getFilePath());
            pstmt.setInt(6, track.getDuration());
            pstmt.setString(7, track.getDateAdded().toString());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Трек вже є в базі: " + track.getFilePath());
            } else {
                System.out.println("Помилка додавання треку: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        // Змінюємо на music_tracks
        String sql = "SELECT * FROM music_tracks ORDER BY title";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDateTime dateAdded = LocalDateTime.parse(rs.getString("date_added"));

                Track track = new Track(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre"),
                        rs.getString("file_path"),
                        rs.getInt("duration"),
                        rs.getInt("play_count"),
                        dateAdded
                );
                tracks.add(track);
            }

        } catch (SQLException e) {
            System.out.println("Помилка завантаження треків: " + e.getMessage());
        }

        return tracks;
    }

    public void updatePlayCount(int trackId, int playCount) {
        // Змінюємо на music_tracks
        String sql = "UPDATE music_tracks SET play_count = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playCount);
            pstmt.setInt(2, trackId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Помилка оновлення лічильника: " + e.getMessage());
        }
    }

    public boolean trackExists(String filePath) {
        // Змінюємо на music_tracks
        String sql = "SELECT COUNT(*) FROM music_tracks WHERE file_path = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, filePath);
            ResultSet rs = pstmt.executeQuery();

            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.out.println("Помилка перевірки треку: " + e.getMessage());
            return false;
        }
    }
}