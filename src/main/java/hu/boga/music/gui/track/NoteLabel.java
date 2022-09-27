package hu.boga.music.gui.track;

import com.google.common.eventbus.Subscribe;
import hu.boga.music.App;
import hu.boga.music.gui.events.NoteLabelBeingDraggedEvent;
import hu.boga.music.model.Note;
import hu.boga.music.model.Track;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class NoteLabel extends JLabel {
    Note note;
    int startDragX;

//    public static final Color SELECTED_COLOR = Color.GREEN;
//    private static final Color ORIG_COLOR = Color.ORANGE;
    public static final Color SELECTED_COLOR = new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 90);
    private static final Color ORIG_COLOR = new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 90);

    public NoteLabel(Note note) {
        super();
        this.note = note;
        setBorder(new LineBorder(new Color(0, 0, 0)));
        setBackground(ORIG_COLOR);
        setOpaque(true);

        if (note.isSelected()) {
            this.setBackground(SELECTED_COLOR);
        } else {
            this.setBackground(ORIG_COLOR);
        }
    }


    public Note getNote() {
        return note;
    }
}
