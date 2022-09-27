package hu.boga.music.gui.projecteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import hu.boga.music.App;
import hu.boga.music.gui.controls.InstrumentCombo;
import hu.boga.music.gui.controls.NoteLengthCombo;
import hu.boga.music.gui.controls.NoteNameCombo;
import hu.boga.music.gui.controls.ToneCombo;
import hu.boga.music.gui.controls.VolumeSlider;
import hu.boga.music.gui.events.TrackSettingsChangedEvent;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Note;
import hu.boga.music.model.Track;
import hu.boga.music.model.TrackSettings;

public class TrackSettingsPanel extends JPanel {

    private Track track;
    private int octave;
    private JRadioButton rdbtnTrackSelected = new JRadioButton();
    private NoteNameCombo noteNameCombo = new NoteNameCombo();
    private ToneCombo toneCombo = new ToneCombo();
    private InstrumentCombo instrCombo = new InstrumentCombo();
    private NoteLengthCombo noteLengthCombo = new NoteLengthCombo();
    private NoteLengthCombo gapLengthCombo = new NoteLengthCombo();

    private final JPanel pnCenter = new JPanel();
    private final JPanel panel_1 = new JPanel();
    private JComboBox cbChannel = new JComboBox(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
    private final JButton btnPlayTrack = new JButton("Play track");
    private final VolumeSlider volumeSlider = new VolumeSlider();
    private final JButton btnClear = new JButton("Clear notes");
    private final JButton btnSelectall = new JButton("SelectAll");
    private final JButton btnDeselectall = new JButton("DeselectAll");
    private final JButton btnDeleteSelected = new JButton("Delete selected");
    private JPanel pnNorth;
    private final Box verticalBox = Box.createVerticalBox();
    private final Box verticalBox_2 = Box.createVerticalBox();
    private final JPanel panel_2 = new JPanel();
    private final JButton btnShiftoctavePlus = new JButton("shift octave +");
    private final JButton btnShiftoctaveMinus = new JButton("shift octave -");
    private final JButton btnBtncopy = new JButton("Copy");
    private final JButton btnBtnpaste = new JButton("Paste");
    private final JPanel panel = new JPanel();
    private final JPanel panel_3 = new JPanel();
    private static Map<Integer, List<Note>> NOTES_TO_PASTE;
    private JSlider loopSlider = new JSlider();
    private final JPanel pnSliders = new JPanel();
    private final Box verticalBox_1 = Box.createVerticalBox();
    private final JPanel panel_4 = new JPanel();
    private final JPanel panel_5 = new JPanel();
    private final Box verticalBox_3 = Box.createVerticalBox();
    private final JPanel panel_6 = new JPanel();
    private final JPanel panel_7 = new JPanel();
    private final JCheckBox chckbxPattern = new JCheckBox("Pattern");

    public TrackSettingsPanel(Track track) {
        super();
        setBackground(track.getSettings().getColor());
        // setPreferredSize(new Dimension(393, 240));
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        this.track = track;
        setLayout(new BorderLayout(0, 0));
        this.add(getNorthPanel(), BorderLayout.NORTH);
        FlowLayout fl_pnCenter = (FlowLayout) pnCenter.getLayout();
        fl_pnCenter.setAlignment(FlowLayout.LEFT);
        panel_4.add(noteLengthCombo);
        panel_5.setBorder(new TitledBorder(null, "Gap length", TitledBorder.LEADING, TitledBorder.TOP, App.DEFAULT_FONT, null));

        verticalBox_1.add(panel_5);
        panel_5.add(gapLengthCombo);
        gapLengthCombo.setSelectedNoteLength(track.getSettings().getGapLength());

        verticalBox_1.add(chckbxPattern);
        chckbxPattern.setSelected(track.getSettings().isPattern());
        noteLengthCombo.addActionListener(e -> this.broadCastTrackChangedEvent());
        rdbtnTrackSelected.setOpaque(false);

        toneCombo.addActionListener(e -> this.broadCastTrackChangedEvent());
        noteNameCombo.addActionListener(e -> this.broadCastTrackChangedEvent());

        instrCombo.addActionListener(e -> this.broadCastTrackChangedEvent());
        rdbtnTrackSelected.addActionListener(e -> {
            if (rdbtnTrackSelected.isSelected()) {
                broadCastTrackChangedEvent();
            }
        });
        gapLengthCombo.addActionListener(l -> this.broadCastTrackChangedEvent());
        chckbxPattern.addActionListener(l -> this.broadCastTrackChangedEvent());

        this.octave = track.getSettings().getOctave();
        pnCenter.setOpaque(false);
        add(pnCenter);
        panel_7.add(cbChannel);
        cbChannel.setPreferredSize(new Dimension(100, 40));
        cbChannel.setBorder(
                new TitledBorder(new LineBorder(new Color(184, 207, 229)), "MIDI Channel", TitledBorder.LEADING, TitledBorder.TOP, App.DEFAULT_FONT, new Color(51, 51, 51)));
        cbChannel.addActionListener(e -> this.broadCastTrackChangedEvent());
        cbChannel.setFont(App.DEFAULT_FONT);

        pnCenter.add(pnSliders);
        pnSliders.setLayout(new GridLayout(0, 1, 0, 0));
        pnSliders.add(volumeSlider);

        final TitledBorder tb = new TitledBorder(null, "Loop count", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        loopSlider.setMaximum(25);
        loopSlider.setMinimum(0);
        loopSlider.setSnapToTicks(true);
        loopSlider.setPaintTicks(true);
        loopSlider.setPaintLabels(true);
        loopSlider.setMajorTickSpacing(5);
        loopSlider.setMinorTickSpacing(1);
        loopSlider.setBorder(tb);
        loopSlider.setValue(0);

        pnSliders.add(loopSlider);
        volumeSlider.addChangeListener(l -> this.broadCastTrackChangedEvent());
        volumeSlider.setValue(track.getSettings().getVolume());

        pnCenter.add(panel);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        panel.add(btnPlayTrack);
        btnPlayTrack.setFont(App.DEFAULT_FONT);
        panel.add(btnClear);
        btnClear.setFont(App.DEFAULT_FONT);
        panel.add(btnDeleteSelected);
        btnDeleteSelected.setFont(App.DEFAULT_FONT);
        btnDeleteSelected.addActionListener(l -> {
            this.track.removeSelected();
            broadCastTrackChangedEvent();
        });
        panel.add(btnShiftoctavePlus);
        btnShiftoctavePlus.setFont(App.DEFAULT_FONT);
        panel.add(btnShiftoctaveMinus);
        btnShiftoctaveMinus.setFont(App.DEFAULT_FONT);
        btnShiftoctaveMinus.addActionListener(l -> {
            this.track.shiftSelected(-1);
            broadCastTrackChangedEvent();
        });
        btnShiftoctavePlus.addActionListener(l -> {
            this.track.shiftSelected(1);
            broadCastTrackChangedEvent();
        });
        btnClear.addActionListener(l -> {
            this.track.clearAllNotes();
//            this.track.getSettings().setPointer(0);
            this.broadCastTrackChangedEvent();
        });
        btnPlayTrack.addActionListener(e -> {
            try {
                MidiEngine.playTracks(Arrays.asList(track),
                        ProjectEditor.TEMPO_SLIDER.getValue(), loopSlider.getValue());
            } catch (InvalidMidiDataException | MidiUnavailableException | IOException e1) {
                e1.printStackTrace();
            }
        });

        pnCenter.add(panel_3);
        panel_3.setLayout(new GridLayout(0, 1, 0, 0));
        panel_3.add(btnSelectall);
        btnSelectall.setFont(App.DEFAULT_FONT);
        btnSelectall.addActionListener(l -> {
            this.track.selectAll();
            broadCastTrackChangedEvent();
        });
        panel_3.add(btnDeselectall);
        btnDeselectall.setFont(App.DEFAULT_FONT);
        btnDeselectall.addActionListener(l -> {
            this.track.unSelectAll();
            broadCastTrackChangedEvent();
        });
        panel_3.add(btnBtncopy);
        btnBtncopy.setFont(App.DEFAULT_FONT);
        panel_3.add(btnBtnpaste);
        btnBtnpaste.setFont(App.DEFAULT_FONT);

        btnBtncopy.addActionListener(l -> {
            NOTES_TO_PASTE = track.copy();
        });

        btnBtnpaste.addActionListener(l -> {
            if (NOTES_TO_PASTE != null) {
//                track.paste(NOTES_TO_PASTE, track.getSettings().getPointer());
            }

            broadCastTrackChangedEvent();
        });
    }

    private JPanel getNorthPanel() {
        pnNorth = new JPanel();
        pnNorth.setOpaque(false);
        FlowLayout fl_pnNorth = (FlowLayout) pnNorth.getLayout();
        fl_pnNorth.setAlignment(FlowLayout.LEFT);

        pnNorth.add(rdbtnTrackSelected);

        pnNorth.add(verticalBox_2);
        verticalBox_2.add(instrCombo);

        verticalBox_2.add(panel_2);
        panel_2.add(noteNameCombo);
        panel_2.add(toneCombo);

        instrCombo.setProgram(track.getSettings().getProgram());
        noteNameCombo.setSelectedNoteName(track.getSettings().getScaleRoot());
        toneCombo.setSelectedTone(track.getSettings().getScaleTone());

        pnNorth.add(verticalBox_1);
        panel_4.setBorder(new TitledBorder(null, "Notelength", TitledBorder.LEADING, TitledBorder.TOP, App.DEFAULT_FONT, null));

        verticalBox_1.add(panel_4);
        noteLengthCombo.setSelectedNoteLength(track.getSettings().getLength());

        pnNorth.add(verticalBox_3);
        panel_6.setBorder(new TitledBorder(null, "Octave", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        verticalBox_3.add(panel_6);
        JButton btnOctaveMinus = new JButton("<");
        panel_6.add(btnOctaveMinus);
        btnOctaveMinus.setMargin(new Insets(0, 0, 0, 0));

        JLabel lblOctave = new JLabel("" + track.getSettings().getOctave());
        panel_6.add(lblOctave);
        lblOctave.setFont(App.DEFAULT_FONT);

        JButton btnOctavePlus = new JButton(">");
        panel_6.add(btnOctavePlus);
        btnOctavePlus.setMargin(new Insets(0, 0, 0, 0));

        verticalBox_3.add(panel_7);
        cbChannel.setSelectedIndex(track.getSettings().getMidiChannel());
        btnOctavePlus.addActionListener(e -> {
            this.octave++;
            this.broadCastTrackChangedEvent();
            lblOctave.setText("" + octave);
        });
        btnOctaveMinus.addActionListener(e -> {
            this.octave--;
            this.broadCastTrackChangedEvent();
            lblOctave.setText("" + octave);
        });

        pnNorth.add(verticalBox);

        pnNorth.add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
        // panel_1.add(cbChannel);

        return pnNorth;
    }

    public JRadioButton getRadioButton() {
        return this.rdbtnTrackSelected;
    }

    private void broadCastTrackChangedEvent() {
        selectTrack();
        TrackSettings ts = this.track.getSettings();

        ts.setProgram(instrCombo.getProgram());
        ts.setLength(noteLengthCombo.getSelectedNoteLength());
        ts.setGapLength(gapLengthCombo.getSelectedNoteLength());
        ts.setMidiChannel(Integer.parseInt("" + cbChannel.getItemAt(cbChannel.getSelectedIndex())));
        ts.setOctave(octave);
        ts.setScaleRoot(noteNameCombo.getSelectedNoteName());
        ts.setScaleTone(toneCombo.getSelectedTone());
        ts.setVolume(volumeSlider.getValue());
        ts.setPattern(chckbxPattern.isSelected());

        App.EVENT_BUS.post(new TrackSettingsChangedEvent(this.track));
    }

    private void selectTrack() {
        App.CONTROLLER.setCurrentTrack(track);
        this.rdbtnTrackSelected.setSelected(true);
    }

    public Track getTrack() {
        return track;
    }

}
