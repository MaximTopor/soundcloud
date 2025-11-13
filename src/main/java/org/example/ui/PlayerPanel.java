package org.example.ui;

import org.example.model.Track;
import org.example.service.AudioPlayer;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private AudioPlayer player;
    private JLabel nowPlayingLabel;
    private JButton playButton, stopButton, nextButton, prevButton, repeatButton;
    private PlayerControlListener controlListener;
    private boolean repeatAll = false;

    public interface PlayerControlListener {
        void onPlayRequested();
        void onStopRequested();
        void onNextRequested(); // 🆕
        void onPrevRequested(); // 🆕
    }

    public PlayerPanel(AudioPlayer player) {
        this.player = player;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Управління"));

        // Лейбл поточного треку
        nowPlayingLabel = new JLabel("Оберіть трек");
        nowPlayingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Кнопки
        playButton = new JButton("▶ Play");
        stopButton = new JButton("⏹ Stop");
        nextButton = new JButton("⏭ Next");
        prevButton = new JButton("⏮ Prev");
        repeatButton = new JButton("🔁 Repeat");

        stopButton.setEnabled(false);
        nextButton.setEnabled(false);
        prevButton.setEnabled(false);

        // Обробники кнопок
        playButton.addActionListener(e -> {
            if (controlListener != null) {
                controlListener.onPlayRequested();
            }
        });

        stopButton.addActionListener(e -> {
            player.stop();
            updateUIState(false);
        });

        nextButton.addActionListener(e -> {
            if (controlListener != null) {
                controlListener.onNextRequested();
            }
        });

        prevButton.addActionListener(e -> {
            if (controlListener != null) {
                controlListener.onPrevRequested();
            }
        });

        repeatButton.addActionListener(e -> {
            repeatAll = !repeatAll;
            player.setRepeatAll(repeatAll);
            repeatButton.setText(repeatAll ? "🔂 Repeat" : "🔁 Repeat");
        });

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(prevButton);
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(repeatButton);

        // Додаємо все на панель
        add(nowPlayingLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public void setNowPlaying(Track track) {
        nowPlayingLabel.setText("▶ " + track.getTitle());
        updateUIState(true);
    }

    public void updateUIState(boolean isPlaying) {
        playButton.setEnabled(!isPlaying);
        stopButton.setEnabled(isPlaying);
        nextButton.setEnabled(isPlaying);
        prevButton.setEnabled(isPlaying);
    }

    public void setControlListener(PlayerControlListener listener) {
        this.controlListener = listener;
    }
}