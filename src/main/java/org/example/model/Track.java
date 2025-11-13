package org.example.model;

import java.time.LocalDateTime;

public class Track {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String filePath;
    private int duration; // в секундах
    private int playCount;
    private LocalDateTime dateAdded;

    // Конструктор для нового треку
    public Track(String title, String artist, String album, String genre,
                 String filePath, int duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.filePath = filePath;
        this.duration = duration;
        this.playCount = 0;
        this.dateAdded = LocalDateTime.now();
    }

    // Конструктор для завантаження з БД
    public Track(int id, String title, String artist, String album, String genre,
                 String filePath, int duration, int playCount, LocalDateTime dateAdded) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.filePath = filePath;
        this.duration = duration;
        this.playCount = playCount;
        this.dateAdded = dateAdded;
    }

    // Гетери
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public String getFilePath() { return filePath; }
    public int getDuration() { return duration; }
    public int getPlayCount() { return playCount; }
    public LocalDateTime getDateAdded() { return dateAdded; }

    // Сетери
    public void setPlayCount(int playCount) { this.playCount = playCount; }
    public void incrementPlayCount() { this.playCount++; }

    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + getFormattedDuration() + ")";
    }
}