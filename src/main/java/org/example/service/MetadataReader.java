package org.example.service;

import org.example.model.Track;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class MetadataReader {

    public static Track createTrackFromFile(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName().replace(".mp3", "");

        // Простий парсинг назви файлу (можна покращити)
        String title = fileName;
        String artist = "Невідомий виконавець";
        String album = "Невідомий альбом";
        String genre = "Невизначено";

        // Спроба отримати тривалість (спрощено)
        int duration = getEstimatedDuration(file);

        return new Track(title, artist, album, genre, filePath, duration);
    }

    private static int getEstimatedDuration(File file) {
        // Спрощена оцінка: ~1MB ≈ 1 хвилина для MP3
        long fileSizeMB = file.length() / (1024 * 1024);
        return (int) fileSizeMB * 60;
    }
}