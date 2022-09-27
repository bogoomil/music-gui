package hu.boga.music.gui.trackeditor;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import com.google.common.eventbus.Subscribe;

import hu.boga.music.App;
import hu.boga.music.gui.events.NoteLabelBeingDraggedEvent;
import hu.boga.music.gui.events.NoteLabelDragEvent;
import hu.boga.music.gui.events.NoteLabelLengthChangedEvent;
import hu.boga.music.model.Note;

public class NoteLabel_Old extends JLabel {
    Note note;
    int startDragX, startDragY;
    Color origColor;

    boolean active;

    public static final Color ACTIVE_COLOR = Color.GREEN;
    public static final Color SELECTED_COLOR = Color.PINK;

    public NoteLabel_Old(Note note, boolean active, Color origColor) {
        super();

        App.EVENT_BUS.register(this);

        setBorder(new LineBorder(new Color(0, 0, 0)));
        this.note = note;
        this.setText(note.getPitch().getName().name());
        this.active = active;
        this.origColor = origColor;

        setBackground(origColor);

        this.setOpaque(true);
        if (active && note.isSelected()) {
            this.setBackground(SELECTED_COLOR);
        } else if (active) {
            this.setBackground(origColor);
        } else {
            Color c = new Color(origColor.getRed(), origColor.getGreen(), origColor.getBlue(), 70);
            this.setBackground(c);
        }

        if (active) {
            this.addMouseWheelListener(new MouseAdapter() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() > 0) {
                        note.decrementLength();
                    } else {
                        note.incrementLength();
                    }
                    App.EVENT_BUS.post(new NoteLabelLengthChangedEvent());
                }
            });
            this.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    // int x = getX();
                    // x += e.getX() - startDragX;
                    int y = getY();
                    int offset = e.getY() - startDragY;
                    y += offset;

                    NoteLabel_Old.this.setBounds(getX(), y, getWidth(), getHeight());
                    App.EVENT_BUS.post(new NoteLabelBeingDraggedEvent(NoteLabel_Old.this, offset));
                    note.setSelected(true);
                }

            });
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (active) {
                        App.EVENT_BUS.post(new NoteLabelDragEvent(note, NoteLabel_Old.this.getLocation()));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                    // note.toggleSelection();
                    startDragX = e.getX();
                    startDragY = e.getY();
                }
            });
        }

    }

    public Note getNote() {
        return note;
    }

    @Subscribe
    void handleNoteLabelBeingDraggedEvent(NoteLabelBeingDraggedEvent ev) {
        if (this.note.isSelected() && this.active && ev.getNoteLabel() != this) {
            int y = getY();
            y += ev.getOffset();
            NoteLabel_Old.this.setBounds(getX(), y, getWidth(), getHeight());
            // App.EVENT_BUS.post(new NoteLabelDragEvent(note,
            // NoteLabel.this.getLocation()));

        }
    }

    public boolean isSelectedAndActive() {
        return this.active && note.isSelected();
    }

}
