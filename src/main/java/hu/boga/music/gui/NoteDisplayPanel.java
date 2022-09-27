package hu.boga.music.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLayeredPane;

import com.google.common.eventbus.Subscribe;

import hu.boga.music.App;
import hu.boga.music.enums.NoteName;
import hu.boga.music.gui.events.TrackNotesChangedEvent;
import hu.boga.music.gui.events.TrackSettingsChangedEvent;

public abstract class NoteDisplayPanel extends JLayeredPane {
    protected final NoteName[] NOTE_NAMES = new NoteName[] { NoteName.C, NoteName.D, NoteName.E, NoteName.F, NoteName.G, NoteName.A, NoteName.B };
    protected final NoteName[] HALF_NOTE_NAMES = new NoteName[] { NoteName.Cs, NoteName.Eb, NoteName.Fs, NoteName.Ab, NoteName.Bb };

    public NoteDisplayPanel() {
        super();
        App.EVENT_BUS.register(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                addLabels();
            }
        });
    }

    protected float getKeyWidth() {
        return this.getOctaveWidth() / 7f;
    }

    protected int getOctaveWidth() {
        return this.getWidth() / 8;
    }

    protected abstract void addLabels();

    @Subscribe
    protected abstract void handleTrackSettingsEvent(TrackSettingsChangedEvent ev);

    @Subscribe
    protected abstract void handleTrackNotesChangedEvent(TrackNotesChangedEvent ev);

    protected int computeXByOctaveAndNoteName(int octave, NoteName nn) {
        int offset = this.getOctaveWidth() * octave;

        int keyWidth = Math.round(getKeyWidth());
        int shift = Math.round(this.getKeyWidth() / 2);
        final NoteName[] nns = new NoteName[] { NoteName.C, NoteName.D, NoteName.E, NoteName.F, NoteName.G, NoteName.A, NoteName.B };

        switch (nn) {
        case Cs:
            return offset + shift;
        case Eb:
            return offset + keyWidth + shift;
        case Fs:
            return offset + keyWidth * 3 + shift;
        case Ab:
            return offset + keyWidth * 4 + shift;
        case Bb:
            return offset + keyWidth * 5 + shift;
        default:
            for (int i = 0; i < 7; i++) {
                if (nns[i] == nn) {
                    return offset + i * keyWidth;
                }
            }
        }
        return 0;
    }
}
