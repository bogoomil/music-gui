package hu.boga.music.gui.track;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import hu.boga.music.enums.ChordType;
import hu.boga.music.enums.NoteLength;
import hu.boga.music.gui.controls.*;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Piece;
import hu.boga.music.model.Track;
import hu.boga.music.model.TrackSettings;
import hu.boga.music.theory.Scale;

public class TrackEditorFrame extends JInternalFrame {

    private int origHeight = 300;
    private TrackEditorPanel trackEditorPanel;
    private Piece piece;
    private Point dragStart;
    private Track track;

    NoteNameCombo noteNameCombo = new NoteNameCombo();
    ToneCombo toneCombo = new ToneCombo();
    InstrumentCombo instrCombo = new InstrumentCombo();
    NoteLengthCombo noteLengthCombo = new NoteLengthCombo();
    TempoSlider tempoSlider = new TempoSlider();
    JComboBox cbChannel = new JComboBox(new DefaultComboBoxModel(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"}));
    VolumeSlider volumeSlider = new VolumeSlider();

    public TrackEditorFrame() {
        super("Trackeditor", true, false, true, false);
        createTrackEditorPanel();
        createTrackSettingsPanel();
        this.addInternalFrameListener(new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {

            }

            @Override
            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {

            }

            @Override
            public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
                try {
                    TrackEditorFrame.this.setClosed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
                TrackEditorFrame.this.getDesktopIcon().setLocation(TrackEditorFrame.this.getLocation().x, TrackEditorFrame.this.getLocation().y);

            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {

            }

            @Override
            public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {

            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {

            }
        });
        this.setPreferredSize(new Dimension(1500, 542));
        this.setVisible(true);
        this.pack();
    }

    public void setTrack(Track track) {
        this.setTitle(track.getId().toString().substring(0,10));
        this.track = track;
        this.trackEditorPanel.setTrack(track);

        this.initSettingsControls(track);
    }

    private void initSettingsControls(Track track) {
        TrackSettings trackSettings = track.getSettings();
        this.cbChannel.setSelectedIndex(trackSettings.midiChannel);
        this.instrCombo.setProgram(trackSettings.program);
        this.volumeSlider.setValue(trackSettings.volume);


    }

    private void createTrackEditorPanel() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        trackEditorPanel = new TrackEditorPanel();
        scrollPane.setViewportView(trackEditorPanel);
        trackEditorPanel.currentScale = Scale.getScale(noteNameCombo.getSelectedNoteName(), toneCombo.getSelectedTone());

        MidiEngine.addMidiEventListener(trackEditorPanel);
    }

    private void createTrackSettingsPanel() {
        Box verticalBox = Box.createVerticalBox();
        this.add(verticalBox, BorderLayout.NORTH);

        verticalBox.add(createGeneralControlsPanel());

        verticalBox.add(createChordSelectorPanel());

        verticalBox.add(createTrackSpecificControls());

    }

    private JPanel createGeneralControlsPanel() {
        JPanel generalControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("Play");
        generalControlsPanel.add(btn);
        btn.addActionListener(l -> {
            tryPlayTrack();

        });

        btn = new JButton("Stop");
        generalControlsPanel.add(btn);
        btn.addActionListener(l -> {
            MidiEngine.getSequencer().stop();
        });


        generalControlsPanel.add(new JLabel("Root"));
        generalControlsPanel.add(noteNameCombo);
        noteNameCombo.addActionListener(l -> {
            changeTrackEditorScale();
        });
        generalControlsPanel.add(new JLabel("Tone"));
        generalControlsPanel.add(toneCombo);
        toneCombo.addActionListener(l -> {
            changeTrackEditorScale();
        });
        generalControlsPanel.add(new JLabel("Note length"));
        generalControlsPanel.add(noteLengthCombo);
        noteLengthCombo.setSelectedNoteLength(NoteLength.NEGYED);
        noteLengthCombo.addActionListener(l -> trackEditorPanel.setCurrentNoteLength(noteLengthCombo.getSelectedNoteLength()));

        generalControlsPanel.add(tempoSlider);

        return generalControlsPanel;
    }

    private JPanel createChordSelectorPanel() {
        JPanel chordSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Chords");
        chordSelectorPanel.add(label);
        ButtonGroup buttonGroupChords = new ButtonGroup();

        JRadioButton jRadioButton = new JRadioButton();
        jRadioButton.addActionListener(l -> trackEditorPanel.setCurrentChordType(null));
        buttonGroupChords.add(jRadioButton);
        jRadioButton.setText("None");
        chordSelectorPanel.add(jRadioButton);
        jRadioButton.setSelected(true);

        Arrays.stream(ChordType.values()).sequential().forEach(ct -> {
            JRadioButton rb = new JRadioButton();
            rb.addActionListener(l -> trackEditorPanel.setCurrentChordType(ct));
            buttonGroupChords.add(rb);
            chordSelectorPanel.add(rb);
            rb.setText(ct.name());
        });
        return chordSelectorPanel;
    }

    private JPanel createTrackSpecificControls() {
        JPanel trackSpecificControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        trackSpecificControlsPanel.add(new JLabel("Instrument"));
        trackSpecificControlsPanel.add(instrCombo);
        instrCombo.addActionListener(l -> track.getSettings().program = instrCombo.getProgram());
        trackSpecificControlsPanel.add(new JLabel("Channel"));
        trackSpecificControlsPanel.add(cbChannel);
        cbChannel.addActionListener(l -> track.getSettings().midiChannel = cbChannel.getSelectedIndex());
        trackSpecificControlsPanel.add(new JLabel("Volume"));
        trackSpecificControlsPanel.add(volumeSlider);
        volumeSlider.addChangeListener(l -> track.getSettings().volume = volumeSlider.getValue());
        return trackSpecificControlsPanel;
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
        track.getSettings().program = instrCombo.getProgram();
        track.getSettings().midiChannel = cbChannel.getSelectedIndex();
        track.getSettings().volume = volumeSlider.getValue();
    }

}
