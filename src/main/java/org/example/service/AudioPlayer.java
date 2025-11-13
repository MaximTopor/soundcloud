package org.example.service;

import org.example.model.Track;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlayer {
    private Player player;
    private Track currentTrack;
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Thread playThread;
    private MusicLibrary library;
    private AudioPlayerListener listener;
    private boolean repeatAll = false; // 🔄 НОВЕ ПОЛЕ
    private List<Track> currentPlaylist;
    private int currentTrackIndex = -1;

    public interface AudioPlayerListener {
        void onPlay(Track track);
        void onStop(Track track);
        void onComplete(Track track);
        void onError(Track track, String error);
        void onNextTrack(Track nextTrack); // 🆕 НОВИЙ МЕТОД
    }

    // 🆕 МЕТОД ДЛЯ ВІДТВОРЕННЯ ПЛЕЙЛИСТУ
    public void playPlaylist(List<Track> playlist, int startIndex) {
        this.currentPlaylist = playlist;
        this.currentTrackIndex = startIndex;

        if (playlist != null && !playlist.isEmpty() && startIndex < playlist.size()) {
            play(playlist.get(startIndex));
        }
    }

    // 🆕 МЕТОД ДЛЯ НАСТУПНОГО ТРЕКУ
    public void playNext() {
        if (currentPlaylist != null && currentPlaylist.size() > 1) {
            currentTrackIndex = (currentTrackIndex + 1) % currentPlaylist.size();
            Track nextTrack = currentPlaylist.get(currentTrackIndex);
            play(nextTrack);

            if (listener != null) {
                listener.onNextTrack(nextTrack);
            }
        }
    }

    // 🆕 МЕТОД ДЛЯ ПОПЕРЕДНЬОГО ТРЕКУ
    public void playPrevious() {
        if (currentPlaylist != null && currentPlaylist.size() > 1) {
            currentTrackIndex = (currentTrackIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
            Track prevTrack = currentPlaylist.get(currentTrackIndex);
            play(prevTrack);
        }
    }

    // ОНОВЛЕНИЙ МЕТОД PLAY
    public void play(Track track) {
        try {
            stop();

            currentTrack = track;
            System.out.println("🎵 Відтворення: " + track.getTitle());

            // Оновлення лічильника
            track.incrementPlayCount();
            if (library != null) {
                library.updatePlayCount(track);
            }

            FileInputStream fis = new FileInputStream(track.getFilePath());
            player = new Player(fis);

            isPlaying.set(true);

            if (listener != null) {
                listener.onPlay(track);
            }

            playThread = new Thread(() -> {
                try {
                    player.play();
                    isPlaying.set(false);

                    // 🔄 АВТОМАТИЧНО НАСТУПНИЙ ТРЕК ПІСЛЯ ЗАВЕРШЕННЯ
                    if (listener != null) {
                        listener.onComplete(track);
                    }

                    // 🆕 ЯКЩО УВІМКНУТО REPEAT ALL - ГРАТИ НАСТУПНИЙ
                    if (repeatAll && currentPlaylist != null) {
                        System.out.println("🔂 Автоматичне відтворення наступного треку");
                        playNext();
                    }

                } catch (Exception e) {
                    isPlaying.set(false);
                    if (listener != null) {
                        listener.onError(track, e.getMessage());
                    }
                }
            });

            playThread.setDaemon(true);
            playThread.start();

        } catch (Exception e) {
            System.out.println("❌ Не вдалося відтворити файл: " + e.getMessage());
            if (listener != null) {
                listener.onError(track, e.getMessage());
            }
        }
    }

    // 🆕 МЕТОД ДЛЯ УВІМКНЕННЯ/ВИМКНЕННЯ ПОВТОРУ
    public void setRepeatAll(boolean repeat) {
        this.repeatAll = repeat;
        System.out.println("🔂 Repeat All: " + (repeat ? "УВІМКНЕНО" : "ВИМКНЕНО"));
    }

    public boolean isRepeatAll() {
        return repeatAll;
    }

    // Гетери та сетери
    public void setMusicLibrary(MusicLibrary library) {
        this.library = library;
    }

    public void setListener(AudioPlayerListener listener) {
        this.listener = listener;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
        isPlaying.set(false);

        if (listener != null && currentTrack != null) {
            listener.onStop(currentTrack);
        }
    }
}