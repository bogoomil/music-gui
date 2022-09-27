package hu.boga.music.gui.events;

import hu.boga.music.theory.Chord;

public class ChordHighLightedEvent {

    private Chord chord;
    private int pointer;

    public ChordHighLightedEvent(Chord ch, int pointer) {
        this.chord = ch;
        this.pointer = pointer;
    }

    public Chord getChord() {
        return chord;
    }

    public int getPointer() {
        return pointer;
    }

}
