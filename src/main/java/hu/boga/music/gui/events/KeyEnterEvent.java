package hu.boga.music.gui.events;

import hu.boga.music.enums.NoteName;

public class KeyEnterEvent {
    private int octave;
    private NoteName noteName;
    private int degree;

    public KeyEnterEvent(int octave, NoteName noteName, int degree) {
        super();
        this.octave = octave;
        this.noteName = noteName;
        this.degree = degree;
    }

    public int getOctave() {
        return octave;
    }

    public NoteName getNoteName() {
        return noteName;
    }

    public int getDegree() {
        return degree;
    }

}