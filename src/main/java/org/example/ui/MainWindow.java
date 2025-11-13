package org.example.ui;

import org.example.model.Track;
import org.example.service.MusicLibrary;
import org.example.service.AudioPlayer;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private MusicLibrary library;
    private AudioPlayer player;
    private TrackListPanel trackListPanel;
    private PlayerPanel playerPanel;

    public MainWindow() {
        library = new MusicLibrary();
        player = new AudioPlayer();


        player.setMusicLibrary(library);

        initializeWindow();
        connectComponents();
        System.out.println("✅ MainWindow створено, MusicLibrary передано в AudioPlayer");
    }

    private void initializeWindow() {
        setTitle("Мій Музичний Плеєр");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Створюємо панелі
        trackListPanel = new TrackListPanel(library);
        playerPanel = new PlayerPanel(player);

        // Додаємо панелі на вікно
        add(trackListPanel, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.SOUTH);
    }

    private void connectComponents() {
        // Коли вибирають трек в списку
        trackListPanel.setSelectionListener(new TrackListPanel.TrackSelectionListener() {
            @Override
            public void onTrackSelected(Track track) {
                playerPanel.setNowPlaying(track);
            }
        });

        // Коли хочуть відтворити трек
        playerPanel.setControlListener(new PlayerPanel.PlayerControlListener() {
            @Override
            public void onPlayRequested() {
                Track selectedTrack = trackListPanel.getSelectedTrack();
                if (selectedTrack != null) {
                    java.util.List<Track> allTracks = trackListPanel.getAllTracks();
                    int currentIndex = allTracks.indexOf(selectedTrack);
                    player.playPlaylist(allTracks, currentIndex);
                    playerPanel.setNowPlaying(selectedTrack);
                } else {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Оберіть трек зі списку", "Увага",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

            @Override
            public void onStopRequested() {
                player.stop();
            }

            @Override
            public void onNextRequested() {
                // 🆕 РЕАЛІЗАЦІЯ НАСТУПНОГО ТРЕКУ
                player.playNext();
                Track currentTrack = player.getCurrentTrack();
                if (currentTrack != null) {
                    playerPanel.setNowPlaying(currentTrack);

                    // 🆕 ОНОВЛЮЄМО ВИБІР У СПИСКУ
                    trackListPanel.setSelectedTrack(currentTrack);
                }
            }

            @Override
            public void onPrevRequested() {
                // 🆕 РЕАЛІЗАЦІЯ ПОПЕРЕДНЬОГО ТРЕКУ
                player.playPrevious();
                Track currentTrack = player.getCurrentTrack();
                if (currentTrack != null) {
                    playerPanel.setNowPlaying(currentTrack);

                    // 🆕 ОНОВЛЮЄМО ВИБІР У СПИСКУ
                    trackListPanel.setSelectedTrack(currentTrack);
                }
            }
        });
    }
}