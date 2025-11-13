package org.example.ui;

import org.example.model.Track;
import org.example.service.MetadataReader;
import org.example.service.MusicLibrary;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;

public class TrackListPanel extends JPanel {
    private MusicLibrary library;
    private JList<Track> trackList;
    private DefaultListModel<Track> listModel;
    private TrackSelectionListener selectionListener;

    public interface TrackSelectionListener {
        void onTrackSelected(Track track);
    }

    public void setSelectedTrack(Track track) {
        // Шукаємо трек в списку за шляхом файлу (унікальний ідентифікатор)
        for (int i = 0; i < listModel.getSize(); i++) {
            Track currentTrack = listModel.getElementAt(i);
            if (currentTrack.getFilePath().equals(track.getFilePath())) {
                trackList.setSelectedIndex(i);
                trackList.ensureIndexIsVisible(i); // Прокручуємо до вибраного
                break;
            }
        }
    }

    public java.util.List<Track> getAllTracks() {
        java.util.List<Track> tracks = new java.util.ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            tracks.add(listModel.getElementAt(i));
        }
        return tracks;
    }

    public TrackListPanel(MusicLibrary library) {
        this.library = library;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Список треків"));

        // Модель і список
        listModel = new DefaultListModel<>();
        trackList = new JList<>(listModel);
        trackList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Кнопки
        JButton addButton = new JButton("Додати трек");

        addButton.addActionListener(e -> addNewTrack());

        // Подвійний клік по треку
        trackList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Track selected = trackList.getSelectedValue();
                    if (selected != null && selectionListener != null) {
                        selectionListener.onTrackSelected(selected);
                    }
                }
            }
        });

        // Додаємо компоненти
        add(new JScrollPane(trackList), BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);

        // Завантажуємо треки
        loadTracks();
    }



    private void addNewTrack() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "MP3 файли", "mp3"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Створюємо трек з метаданими
            Track newTrack = MetadataReader.createTrackFromFile(filePath);

            boolean added = library.addTrack(newTrack);
            if (added) {
                listModel.addElement(newTrack);
                JOptionPane.showMessageDialog(this,
                        "Трек додано: " + newTrack.getTitle(), "Успіх",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Цей трек вже є в бібліотеці: " + newTrack.getTitle(),
                        "Увага", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void loadTracks() {
        // Завантажуємо треки з бібліотеки
        library.getAllTracks().forEach(listModel::addElement);
    }

    public void setSelectionListener(TrackSelectionListener listener) {
        this.selectionListener = listener;
    }

    public Track getSelectedTrack() {
        return trackList.getSelectedValue();
    }
}