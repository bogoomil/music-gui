package hu.boga.music.gui.trackeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import hu.boga.music.App;
import hu.boga.music.gui.NoteDisplayPanel;
import hu.boga.music.gui.events.NoteLabelDragEvent;
import hu.boga.music.gui.events.NoteLabelLengthChangedEvent;
import hu.boga.music.gui.events.TrackNotesChangedEvent;
import hu.boga.music.gui.events.TrackSettingsChangedEvent;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.midi.MidiEventListener;
import hu.boga.music.model.Note;
import hu.boga.music.model.Track;

public class TrackPanel extends NoteDisplayPanel implements MidiEventListener {

    Track currentTrack = App.CONTROLLER.getCurrentTrack();

    private static int tickHeight = 20;

    Point dragStart;
    private int currentTick;

    public TrackPanel() {
        super();
        App.EVENT_BUS.register(this);
        MidiEngine.addMidiEventListener(this);
        this.setLayout(null);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(25 * 56, 300));

        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart == null) {
                    dragStart = e.getPoint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragStart != null) {
                    selectNotes(dragStart, e.getPoint());
                    dragStart = null;
                }

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int tick = getTickAtYPosition(e.getY());
     //           currentTrack.getSettings().setPointer(tick);
                repaint();
            }
        });

    }

    public static int getTickAtYPosition(int y) {
        return y / tickHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentTrack != null) {
            paintVerticalLines(g);
            paintHorizontalLines(g);

            if (currentTick != 0) {
                paintTickPointer(g);
            }
        }
    }

    private void paintTickPointer(Graphics g) {
        g.fillRect(0, currentTick * tickHeight, getWidth(), 5);

    }

    private void paintHorizontalLines(Graphics g) {
        int firstEmptyTick = currentTrack.getFirstEmptyTick() + 64;
        for (int i = 0; i <= firstEmptyTick; i++) {
            if (i % 32 == 0) {
                g.setColor(Color.RED);
            } else if (i % 8 == 0) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.BLACK);
            }
            int y = i * this.tickHeight;
            g.drawLine(0, y, this.getWidth(), y);

//            if (i == currentTrack.getSettings().getPointer()) {
//                g.setColor(Color.PINK);
//                g.fillRect(0, y, this.getWidth(), tickHeight);
//            }
        }
        Rectangle r = this.getBounds();
        r.height = App.CONTROLLER.getPiece().getMaxTick() * TrackPanel.tickHeight + 500;
        this.setBounds(r);
    }

    private void paintVerticalLines(Graphics g) {
        for (int i = 0; i < 8; i++) {
            g.setColor(Color.BLACK);
            for (int j = 0; j < 7; j++) {
                int x = this.computeXByOctaveAndNoteName(i, NOTE_NAMES[j]);
                g.drawLine(x, 0, x, getHeight());
            }
        }
    }

    @Override
    protected void addLabels() {
        this.removeAll();
        currentTrack.getTrackMap().forEach((tick, list) -> {
            createLabelsFromNotes(tick, list, true, currentTrack.getSettings().getColor());
        });
        addLabelsOfAllTracks();
        this.repaint();
        this.revalidate();
    }

    private void addLabelsOfAllTracks() {
        App.CONTROLLER.getPiece().getTracks().forEach(t -> {
            t.getTrackMap().forEach((tick, list) -> {
                if (!t.equals(currentTrack)) {
                    createLabelsFromNotes(tick, list, false, t.getSettings().getColor());
                }
            });
        });
    }

    private void createLabelsFromNotes(Integer tick, List<Note> list, boolean active, Color labelColor) {

        int y = tickHeight * tick;
        list.forEach(n -> {
            int x = this.computeXByOctaveAndNoteName(n.getPitch().getOctave() - currentTrack.getSettings().getOctave(), n.getPitch().getName());
            NoteLabel_Old nl = new NoteLabel_Old(n, active, labelColor);
            nl.setBounds(new Rectangle(x, y, Math.round(this.getKeyWidth()), n.getLength().getErtek() * TrackPanel.tickHeight));
            this.add(nl);
        });
    }

    public int getTickHeight() {
        return tickHeight;
    }

    public void setTickHeight(int tickHeight) {
        TrackPanel.tickHeight = tickHeight;
    }

    public Track getTrack() {
        return currentTrack;
    }

    @Override
    protected void handleTrackSettingsEvent(TrackSettingsChangedEvent ev) {
        this.currentTrack = ev.getTrack();
        this.addLabels();
    }

    @Override
    protected void handleTrackNotesChangedEvent(TrackNotesChangedEvent ev) {
        this.currentTrack = ev.getTrack();
        addLabels();

    }

    @Subscribe
    private void handleNoteLabelDragEvent(NoteLabelDragEvent ev) {

        this.getSelectedAndActiveLabels().forEach(nl -> {
            int tick = TrackPanel.getTickAtYPosition(nl.getLocation().y);
            currentTrack.moveNoteToTick(nl.getNote(), tick);

        });

        addLabels();
    }

    private List<NoteLabel_Old> getSelectedAndActiveLabels() {
        List<NoteLabel_Old> retVal = new ArrayList<>();
        for (Component c : this.getComponents()) {
            if (c instanceof NoteLabel_Old) {
                NoteLabel_Old nl = (NoteLabel_Old) c;
                if (nl.isSelectedAndActive()) {
                    retVal.add(nl);
                }
            }
        }
        return retVal;
    }

    @Subscribe
    private void handleNoteLabelLengthChangedEvent(NoteLabelLengthChangedEvent ev) {
        addLabels();
    }

    private void selectNotes(Point from, Point to) {
        this.findAllBetween(from, to).forEach(nl -> nl.getNote().toggleSelection());
        this.addLabels();
    }

    private List<NoteLabel_Old> findAllBetween(Point start, Point end) {
        List<NoteLabel_Old> retVal = new ArrayList<>();
        int width = end.x - start.x;
        int height = end.y - start.y;
        Rectangle r = new Rectangle(start, new Dimension(width, height));
        for (Component c : getComponents()) {
            if (c instanceof NoteLabel_Old) {
                NoteLabel_Old nl = (NoteLabel_Old) c;
                if (nl.getBounds().intersects(r)) {
                    retVal.add(nl);
                }
            }
        }
        return retVal;
    }

    @Override
    public void processTickEvent(int tick) {
        System.out.println("TICK EVEN: " + tick);
        currentTick = tick;
        this.repaint();

    }

    @Override
    public void processMeasureEvent(int measure) {
        System.out.println("MEASURE EVEN: " + measure);
        currentTick = 0;
    }

}
