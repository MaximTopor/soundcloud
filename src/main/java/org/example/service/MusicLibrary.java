package org.example.service;

import org.example.model.Track;
import java.util.List;

public class MusicLibrary {
    private MySQLDatabaseManager dbManager;

    public MusicLibrary() {
        this.dbManager = new MySQLDatabaseManager();
    }

    public boolean addTrack(Track track) {
        if (dbManager.trackExists(track.getFilePath())) {
            System.out.println("Трек вже є в бібліотеці: " + track.getTitle());
            return false;
        }

        boolean added = dbManager.addTrack(track);
        if (added) {
            System.out.println("Трек додано до MySQL: " + track.getTitle());
        }
        return added;
    }

    public List<Track> getAllTracks() {
        return dbManager.getAllTracks();
    }

    public int getTrackCount() {
        return getAllTracks().size();
    }

    public void updatePlayCount(Track track) {
        dbManager.updatePlayCount(track.getId(), track.getPlayCount());
    }

    // Додаткові методи
    public int getTotalPlayCount() {
        return dbManager.getTotalPlayCount();
    }

    public List<Track> getMostPlayedTracks(int limit) {
        return dbManager.getMostPlayedTracks(limit);
    }
}