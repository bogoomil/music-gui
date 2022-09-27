package hu.boga.music.gui.track;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import hu.boga.music.App;
import hu.boga.music.enums.NoteLength;
import hu.boga.music.gui.controls.*;
import hu.boga.music.gui.projecteditor.TesztForm;
import hu.boga.music.gui.track.TrackEditorPanel;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Piece;
import hu.boga.music.model.Track;
import hu.boga.music.theory.Scale;

public class TrackEditor extends JInternalFrame {

    private int origHeight = 300;
    private TrackEditorPanel trackEditorPanel;
    private Piece piece;
    private Point dragStart;
    private Track track;

    Box verticalBox = Box.createVerticalBox();

    NoteNameCombo noteNameCombo = new NoteNameCombo();
    ToneCombo toneCombo = new ToneCombo();
    InstrumentCombo instrCombo = new InstrumentCombo();
    NoteLengthCombo noteLengthCombo = new NoteLengthCombo();
    NoteLengthCombo gapLengthCombo = new NoteLengthCombo();
    TempoSlider tempoSlider = new TempoSlider();
    JComboBox cbChannel = new JComboBox(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
    VolumeSlider volumeSlider = new VolumeSlider();

    public TrackEditor(Track track) {
        super("Trackeditor", true, false, true, true);
        this.track = track;
        createTrackEditorPanel();
        createTrackSettingsPanel();
        this.setPreferredSize(new Dimension(1500, 450));
        this.pack();
        this.setVisible(true);

    }

    private void createTrackEditorPanel() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        trackEditorPanel = new TrackEditorPanel(track, this);
        scrollPane.setViewportView(trackEditorPanel);
        trackEditorPanel.currentScale = Scale.getScale(noteNameCombo.getSelectedNoteName(), toneCombo.getSelectedTone());

        MidiEngine.addMidiEventListener(trackEditorPanel);
    }

    private void createTrackSettingsPanel() {
        JPanel panelNorth = new JPanel();
        panelNorth.add(verticalBox);

        JPanel panel1 = new JPanel();
        verticalBox.add(panel1);

        JPanel panel2 = new JPanel();
        verticalBox.add(panel2);

        JPanel panel3 = new JPanel();
        verticalBox.add(panel3);

        this.add(verticalBox, BorderLayout.NORTH);

        JButton btn = new JButton("Play");
        panel1.add(btn);
        btn.addActionListener(l -> {
            tryPlayTrack();

        });

        btn = new JButton("Stop");
        panel1.add(btn);
        btn.addActionListener(l -> {
            MidiEngine.getSequencer().stop();
        });


        panel1.add(new JLabel("Instrument"));
        panel1.add(instrCombo);
        panel1.add(new JLabel("Root"));
        panel1.add(noteNameCombo);
        noteNameCombo.addActionListener(l -> {
            changeTrackEditorScale();
        });
        panel1.add(new JLabel("Tone"));
        panel1.add(toneCombo);
        toneCombo.addActionListener(l -> {
            changeTrackEditorScale();
        });
        panel1.add(new JLabel("Note length"));
        panel1.add(noteLengthCombo);
        noteLengthCombo.setSelectedNoteLength(NoteLength.NEGYED);

        panel1.add(tempoSlider);
        panel1.add(new JLabel("Channel"));
        panel1.add(cbChannel);
        panel1.add(new JLabel("Volume"));
        panel1.add(volumeSlider);


        JLabel label = new JLabel("Chords in scale");
        panel2.add(label);

        label = new JLabel("All chords");
        panel3.add(label);
    }

    private void changeTrackEditorScale() {
        trackEditorPanel.currentScale = Scale.getScale(noteNameCombo.getSelectedNoteName(), toneCombo.getSelectedTone());
        trackEditorPanel.repaint();
        trackEditorPanel.revalidate();
    }

    private void tryPlayTrack() {
        try {
            playTrack();
        } catch (InvalidMidiDataException | MidiUnavailableException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private void playTrack() throws InvalidMidiDataException, MidiUnavailableException, IOException {
        initTrackSettings();
        MidiEngine.playTracks(Arrays.asList(track), tempoSlider.getValue(), 0);
    }

    private void initTrackSettings() {
        track.getSettings().setProgram(instrCombo.getProgram());
        track.getSettings().setMidiChannel(cbChannel.getSelectedIndex());
        track.getSettings().setVolume(volumeSlider.getValue());
    }

    public NoteLength getNoteLength(){
        return this.noteLengthCombo.getSelectedNoteLength();
    }

}
