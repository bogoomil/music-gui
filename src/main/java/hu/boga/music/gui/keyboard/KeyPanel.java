package hu.boga.music.gui.keyboard;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JLayeredPane;

import com.google.common.eventbus.Subscribe;

import hu.boga.music.App;
import hu.boga.music.enums.NoteName;
import hu.boga.music.gui.NoteDisplayPanel;
import hu.boga.music.gui.events.ChordHighLightedEvent;
import hu.boga.music.gui.events.KeyClickedEvent;
import hu.boga.music.gui.events.KeyEnterEvent;
import hu.boga.music.gui.events.KeyExitEvent;
import hu.boga.music.gui.events.KeyWheelEvent;
import hu.boga.music.gui.events.TrackNotesChangedEvent;
import hu.boga.music.gui.events.TrackSettingsChangedEvent;
import hu.boga.music.gui.projecteditor.ProjectEditor;
import hu.boga.music.model.Note;
import hu.boga.music.model.TrackSettings;
import hu.boga.music.theory.Chord;
import hu.boga.music.theory.Pitch;
import hu.boga.music.theory.Scale;

public class KeyPanel extends NoteDisplayPanel {

    // int initialKeyWidth = 25;
    int pointer = -1;

    private TrackSettings settings = App.CONTROLLER.getCurrentTrack().getSettings();

    public KeyPanel() {
        super();
        this.setLayout(null);
        // this.setBounds(0, 0, initialKeyWidth * 56, 200);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.addLabels();
    }

    @Override
    protected void addLabels() {
        removeAll();
        for (int i = 0; i < 8; i++) {
            addOctave(i);
        }
        this.revalidate();
    }

    private void addOctave(int octave) {

        List<NoteName> scale = Scale.getScale(settings.getScaleRoot(), settings.getScaleTone());

        int keyWidth = Math.round(getKeyWidth());

        for (int i = 0; i < 5; i++) {
            int x = this.computeXByOctaveAndNoteName(octave, HALF_NOTE_NAMES[i]);
            KeyLabel kl = new KeyLabel(octave + settings.getOctave(), HALF_NOTE_NAMES[i], Color.BLACK, scale.indexOf(HALF_NOTE_NAMES[i]));
            kl.setBounds(x, 0, keyWidth - 2, this.getHeight() - this.getHeight() / 3);
            kl.setEnabled(scale.contains(HALF_NOTE_NAMES[i]));
            add(kl, JLayeredPane.PALETTE_LAYER);

        }

        for (int i = 0; i < 7; i++) {
            int x = this.computeXByOctaveAndNoteName(octave, NOTE_NAMES[i]);
            KeyLabel kl = new KeyLabel(octave + settings.getOctave(), NOTE_NAMES[i], Color.WHITE, scale.indexOf(NOTE_NAMES[i]));
            kl.setBounds(x, 0, keyWidth, getHeight());
            kl.setEnabled(scale.contains(NOTE_NAMES[i]));

            add(kl, JLayeredPane.DEFAULT_LAYER);
        }
    }

    @Override
    @Subscribe
    protected void handleTrackSettingsEvent(TrackSettingsChangedEvent ev) {
        this.settings = ev.getTrack().getSettings();
        this.addLabels();
    }

    @Subscribe
    private void handleKeyEnterEvent(KeyEnterEvent ev) {
        highLightKey(ev.getNoteName(), ev.getOctave());
    }

    @Subscribe
    private void handleKeyExitEvent(KeyExitEvent ev) {
        this.highLightOffKeys();
        App.EVENT_BUS.post(new ChordHighLightedEvent(null, 0));
        pointer = -1;

    }

    @Subscribe
    private void handleKeyClickedEvent(KeyClickedEvent ev) {
        List<Note> notes = this.getHighLightedNotes();
        App.CONTROLLER.playNotes(notes, settings.getProgram(), ProjectEditor.TEMPO_SLIDER.getValue());
        if (ProjectEditor.BTN_REC.isSelected()) {
            // notes.forEach(n -> n.setSelected(true));
//            App.CONTROLLER.addNotesToCurrentTrack(notes);
            App.EVENT_BUS.post(new TrackNotesChangedEvent(App.CONTROLLER.getCurrentTrack()));
        }
    }

    private List<Note> getHighLightedNotes() {
        List<Pitch> ps = this.getHighLightedKeys();
        List<Note> notes = new ArrayList<>();
        ps.forEach(p -> {
            Note note = new Note();
            note.setPitch(p);
            note.setLength(settings.getLength());
            notes.add(note);
        });
        return notes;
    }

    @Subscribe
    private void handleKeyWheelEvent(KeyWheelEvent ev) {
        pointer += ev.getWheelRotation() * -1;

        List<Chord> chs = null;
        if (Keyboard.ALL_CHORDS_RADIO_BTN.isSelected()) {
            chs = Chord.getChordsOfPitch(ev.getPitch());
        } else {
            chs = Chord.getChordsOfPitchInScale(ev.getPitch(), settings.getScaleRoot(), settings.getScaleTone());
        }
        this.highLightOffKeys();
        if (pointer < 0) {
            pointer = 0;
        }
        if (pointer > chs.size() - 1) {
            pointer = chs.size() - 1;
        }
        this.highLightChord(chs.get(pointer));
        App.EVENT_BUS.post(new ChordHighLightedEvent(chs.get(pointer), pointer));
    }

    private void highLightKey(NoteName nn, int octave) {
        highLightOnKey(nn, octave);
    }

    private void highLightChord(Chord ch) {
        for (Pitch p : ch.getPitches()) {
            highLightOnKey(p.getName(), p.getOctave());
        }
    }

    private void highLightOnKey(NoteName nn, int octave) {
        this.getKeyLabel(octave, nn).ifPresent(KeyLabel::setHighLightedOn);
    }

    private void highLightOffKeys() {
        for (Component c : this.getComponents()) {
            KeyLabel kl = (KeyLabel) c;
            if (kl.isHighLighted()) {
                kl.setHighLightedOff();
            }
        }
    }

    private Optional<KeyLabel> getKeyLabel(int octave, NoteName nn) {
        KeyLabel retVal = new KeyLabel(octave, nn, null, 0);
        for (Component c : this.getComponents()) {
            if (c.equals(retVal)) {
                return Optional.of((KeyLabel) c);
            }
        }
        return Optional.empty();
    }

    private List<Pitch> getHighLightedKeys() {
        List<Pitch> retVal = new ArrayList<>();
        for (Component c : this.getComponents()) {
            KeyLabel kl = (KeyLabel) c;
            if (kl.isHighLighted()) {
                retVal.add(kl.getPitch());
            }
        }
        return retVal;
    }

    @Override
    protected void handleTrackNotesChangedEvent(TrackNotesChangedEvent ev) {
        // TODO Auto-generated method stub

    }

}
