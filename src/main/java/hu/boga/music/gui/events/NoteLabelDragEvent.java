package hu.boga.music.gui.events;

import java.awt.Point;

import hu.boga.music.model.Note;

public class NoteLabelDragEvent {
    private Note n;
    Point p;

    public NoteLabelDragEvent(Note n, Point p) {
        super();
        this.n = n;
        this.p = p;
    }

    public Note getN() {
        return n;
    }

    public Point getP() {
        return p;
    }

}
