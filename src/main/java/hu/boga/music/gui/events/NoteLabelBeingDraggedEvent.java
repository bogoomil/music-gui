package hu.boga.music.gui.events;

import hu.boga.music.gui.trackeditor.NoteLabel_Old;

public class NoteLabelBeingDraggedEvent {
    NoteLabel_Old noteLabel;
    int offset;

    public NoteLabelBeingDraggedEvent(NoteLabel_Old noteLabel, int offset) {
        super();
        this.noteLabel = noteLabel;
        this.offset = offset;
    }

    public NoteLabel_Old getNoteLabel() {
        return noteLabel;
    }

    public int getOffset() {
        return offset;
    }

}
