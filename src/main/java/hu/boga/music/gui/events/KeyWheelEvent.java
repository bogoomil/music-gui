package hu.boga.music.gui.events;

import hu.boga.music.theory.Pitch;

public class KeyWheelEvent {
    private Pitch pitch;
    private int wheelRotation;

    public KeyWheelEvent(Pitch pitch, int wheelRotation) {
        super();
        this.pitch = pitch;
        this.wheelRotation = wheelRotation;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public int getWheelRotation() {
        return wheelRotation;
    }

}
