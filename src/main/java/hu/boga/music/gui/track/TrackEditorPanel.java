package hu.boga.music.gui.track;

import com.google.common.collect.Lists;
import hu.boga.music.App;
import hu.boga.music.enums.ChordType;
import hu.boga.music.enums.NoteLength;
import hu.boga.music.enums.NoteName;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.midi.MidiEventListener;
import hu.boga.music.model.Note;
import hu.boga.music.model.Track;
import hu.boga.music.theory.Chord;
import hu.boga.music.theory.Pitch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrackEditorPanel extends JLayeredPane implements MidiEventListener {

    private static final int OCTAVES = 8;
    private int barCount = 10;
    private static final int TICK_COUNT_IN_BAR = 32;
    private static final int LINE_HEIGHT = 20;
    private static final int KEYBOARD_OFFSET = 40;
    private static final Color KEY_BLACK_COLOR = new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
    private static final Color KEY_WHITE_COLOR = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
    private static final Color HIGHLIGHT_COLOR = new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 80);

    Track track;
    TrackEditorFrame editor;
    Point dragStart;
    private Map<Integer, List<Note>> copiedNotes;
    private int currentTick;
    List<NoteName> currentScale;

    private int currentMouseTickPosition;
    private Pitch currentMousePitchPosition = new Pitch();

    private NoteLength currentNoteLength = NoteLength.NEGYED;
    private ChordType currentChordType;

    public TrackEditorPanel() {
//        this.editor = editor;
        App.EVENT_BUS.register(this);
        this.setLayout(null);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(getTickCount() * 10, LINE_HEIGHT * NoteName.values().length * OCTAVES));

        MouseAdapter adapter = prepareMouseAdapterForTrackEditorPanel();
        this.addMouseWheelListener(adapter);
        this.addMouseMotionListener(adapter);
        this.addMouseListener(adapter);

        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(e.getKeyCode());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                super.keyPressed(e);
            }
        });
    }


    public void setTrack(Track track) {
        this.track = track;
        displayNotes();
    }

    private void handleMouseWheelEventOnTrackEditor(MouseWheelEvent mouseWheelEvent) {
        int newWidth = this.getWidth() + (mouseWheelEvent.getWheelRotation() * 500);
        if (newWidth >= getParent().getWidth()) {
            Dimension dimension = new Dimension(newWidth, getHeight());
            this.setPreferredSize(dimension);
            this.setSize(dimension);
            this.revalidate();
            this.displayNotes();
        }
    }

    private void handleMouseClickedOnTrackEditor(MouseEvent e) {
        if (e.getX() < KEYBOARD_OFFSET) {
            Note note = new Note();
            note.setPitch(getPitchByY(e.getY()));
            note.setLength(NoteLength.NEGYED);
            MidiEngine.playNotes(track.getSettings().program, 120, note);
//            MidiEngine.playNote(120, track.getSettings().midiChannel, note);
        } else {
            if (e.getButton() == MouseEvent.BUTTON3) {
                showPopupMenu(e.getLocationOnScreen());
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                addNoteToTrack(e.getPoint());
            }
        }

    }

    private void addNoteToTrack(Point point) {

        Note note = new Note();
        note.setPitch(getPitchByY(point.y));
        note.setLength(currentNoteLength);
        note.setVelocity(track.getSettings().volume);
        int tickByX = getTickByX(point.x);
//        this.track.getNotesAtTick(tickByX).add(note);

        if (currentChordType != null) {
            Chord chord = Chord.getChord(currentMousePitchPosition, currentChordType);
            Arrays.stream(chord.getPitches()).forEach(pitch -> {
                Note n = new Note();
                n.setPitch(pitch);
                n.setLength(currentNoteLength);
                n.setVelocity(track.getSettings().volume);
                if(!track.getNotesAtTick(tickByX).contains(n)){
                    this.track.getNotesAtTick(tickByX).add(n);
                }
            });
        }

        displayNotes();
        this.repaint();
    }

    public void addBar(int barsToAdd) {
        this.barCount += barsToAdd;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintVerticalLines(g);
        paintHorizontalLines(g);

        if (currentTick != 0) {
            paintTickPointer(g);
        }

        paintHighlightedChord(g);

    }

    private void paintHighlightedChord(Graphics g) {
        g.setColor(HIGHLIGHT_COLOR);
        int tickWidth = getTickWidth();
        int x = getXByTick(currentMouseTickPosition, tickWidth);
        int y = getYByPitch(currentMousePitchPosition.getMidiCode());
        int width = currentNoteLength.getErtek() * tickWidth;
        g.fillRect(x, y, width, LINE_HEIGHT);

        if (currentChordType != null) {
            Chord chord = Chord.getChord(currentMousePitchPosition, currentChordType);
            Arrays.stream(chord.getPitches()).forEach(pitch -> {
                int y2 = getYByPitch(pitch.getMidiCode());
                g.fillRect(x, y2, width, LINE_HEIGHT);
            });
        }
    }

    private void paintTickPointer(Graphics g) {
        int x = getXByTick(currentTick, getTickWidth());
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        g.setColor(Color.MAGENTA);
        g.drawLine(x, 0, x, getHeight());

    }

    private void displayNotes() {
        removeAll();
        int tickWidth = getTickWidth();
        this.track.getTrackMap().forEach((tick, list) -> {
            list.forEach(note -> {
                displayOneNote(tickWidth, tick, note);
            });
        });
        revalidate();
        repaint();
    }

    private void displayOneNote(int tickWidth, Integer tick, Note note) {
        NoteLabel noteLabel = new NoteLabel(note);
        Rectangle bounds = new Rectangle(
                getXByTick(tick, tickWidth),
                getYByPitch(note.getMidiCode()),
                tickWidth * note.getLength().getErtek(),
                LINE_HEIGHT);
        noteLabel.setBounds(bounds);
        MouseAdapter mouseAdapter = prepareMouseAdapterForNoteLabel(note, noteLabel);
        noteLabel.addMouseListener(mouseAdapter);
        noteLabel.addMouseMotionListener(mouseAdapter);
        noteLabel.addMouseWheelListener(mouseAdapter);
        add(noteLabel);
    }

    private MouseAdapter prepareMouseAdapterForTrackEditorPanel() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleMouseWheelEventOnTrackEditor(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleasedOnTrackEditor(e);

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDraggedOnTrackEditor(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMovedOnTrackEditor(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClickedOnTrackEditor(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseExitedOnTrackEditor();
            }
        };
        return mouseAdapter;
    }

    private void handleMouseExitedOnTrackEditor() {
        currentMousePitchPosition = new Pitch();
        currentMouseTickPosition = 0;
        repaint();
        revalidate();
    }

    private void handleMouseMovedOnTrackEditor(MouseEvent e) {
        int newMouseTickPosition = getTickByX(e.getX());
        Pitch newMousePitchPosition = getPitchByY(e.getY());
        if (newMousePitchPosition.getMidiCode() != currentMousePitchPosition.getMidiCode() || newMouseTickPosition != currentMouseTickPosition) {
            currentMouseTickPosition = newMouseTickPosition;
            currentMousePitchPosition = newMousePitchPosition;
            repaint();
            revalidate();
        }
    }

    private void handleMouseDraggedOnTrackEditor(MouseEvent e) {
        if (dragStart == null) {
            dragStart = e.getPoint();
        }
    }

    private void handleMouseReleasedOnTrackEditor(MouseEvent e) {
        if (dragStart != null) {
            selectNotes(dragStart, e.getPoint());
            dragStart = null;
        }
    }

    private MouseAdapter prepareMouseAdapterForNoteLabel(Note note, NoteLabel noteLabel) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int startDragX;

            @Override
            public void mouseClicked(MouseEvent e) {
                note.toggleSelection();
                displayNotes();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                NoteLabel source = (NoteLabel) e.getSource();
                if (source.note.isSelected()) {
                    if (e.getWheelRotation() > 0) {
                        getSelectedNoteLabels().forEach(nl -> nl.note.decrementLength());
                    } else {
                        getSelectedNoteLabels().forEach(nl -> nl.note.incrementLength());
                    }
                }
                displayNotes();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NoteLabel> selectedNoteLabels = getSelectedNoteLabels();
                        selectedNoteLabels.forEach(noteLabel -> {
                            int tick = getTickByX(noteLabel.getX());
                            track.moveNoteToTick(noteLabel.note, tick);
                        });
                        displayNotes();
                        repaint();
                    }
                });
                th.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startDragX = e.getX();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                NoteLabel source = (NoteLabel) e.getSource();
                if (source.note.isSelected()) {
                    List<NoteLabel> selectedNoteLabels = getSelectedNoteLabels();
                    selectedNoteLabels.forEach(noteLabel -> {
                        int x = noteLabel.getX();
                        int elmozdulas = e.getX() - startDragX;
                        noteLabel.setBounds(x + elmozdulas, noteLabel.getY(), noteLabel.getWidth(), noteLabel.getHeight());
                    });
                }
            }
        };
        return mouseAdapter;
    }

    private void paintVerticalLines(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        int tickWidth = getTickWidth();
        for (int i = 0; i < getTickCount(); i += 1) {
            if (i % 16 == 0) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(getXByTick(i, tickWidth), 0, getXByTick(i, tickWidth) + (tickWidth * 8), this.getHeight());
            } else if (i % 8 == 0) {
                g.setColor(Color.GRAY);
                g.fillRect(getXByTick(i, tickWidth), 0, getXByTick(i, tickWidth) + (tickWidth * 8), this.getHeight());
            }
            if (i % TICK_COUNT_IN_BAR == 0) {
                g.setColor(Color.RED);
                graphics2D.setStroke(new BasicStroke(2));
                if (i > 0) {
                    g.drawString("" + (i / TICK_COUNT_IN_BAR), getXByTick(i, tickWidth) + 5, this.getVisibleRect().y + 10);
                }
            } else {
                graphics2D.setStroke(new BasicStroke(1));
                g.setColor(Color.BLACK);
            }
            g.drawLine(getXByTick(i, tickWidth), 0, getXByTick(i, tickWidth), this.getHeight());
        }
    }

    private Pitch getPitchByY(int y) {
        int pitch = (this.getHeight() / LINE_HEIGHT - 1) - (y / LINE_HEIGHT);
        return new Pitch(pitch);
    }

    private int getTickByX(int x) {
        int tickWidth = getTickWidth();
        int tick = (x - KEYBOARD_OFFSET) / tickWidth;
        return tick;
    }

    private int getXByTick(int tick, int tickWidth) {
        return tick * tickWidth + KEYBOARD_OFFSET;
    }

    private int getYByPitch(int midiCode) {
        return (OCTAVES * 12 - 1 - midiCode) * LINE_HEIGHT;
    }

    private int getTickWidth() {
        int tickWidth = (this.getWidth() - KEYBOARD_OFFSET) / getTickCount();
        return tickWidth;
    }

    private void paintHorizontalLines(Graphics g) {
        g.setColor(Color.BLACK);
        List<NoteName> noteNameList = Arrays.asList(NoteName.values());
        noteNameList = Lists.reverse(noteNameList);
        for (int i = 0; i < this.getHeight() / LINE_HEIGHT; i++) {
            paintOneHorizontalLine(g, noteNameList, i);
        }
    }

    private void paintOneHorizontalLine(Graphics g, List<NoteName> noteNameList, int i) {
        NoteName noteName = noteNameList.get(i % 12);
        drawKeyboardKey(g, noteName, i);
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        Stroke dashed = new BasicStroke(3);
        g2d.setStroke(dashed);
        g.drawLine(0, i * LINE_HEIGHT, this.getWidth(), i * LINE_HEIGHT);
        dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{1}, 0);
        g2d.setStroke(dashed);
        if (this.currentScale.contains(noteName)) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.drawLine(KEYBOARD_OFFSET, i * LINE_HEIGHT + (LINE_HEIGHT / 2), this.getWidth(), i * LINE_HEIGHT + (LINE_HEIGHT / 2));
    }

    private void drawKeyboardKey(Graphics g, NoteName noteName, int i) {
        Color[] keyColors = getKeyColorsByNoteName(noteName);
        g.setColor(keyColors[0]);
        g.fillRect(this.getVisibleRect().x, i * LINE_HEIGHT, KEYBOARD_OFFSET, LINE_HEIGHT);
        g.setColor(keyColors[1]);
        g.drawString(noteName.name() + "(" + (OCTAVES - (i / 12)) + ")", this.getVisibleRect().x, i * LINE_HEIGHT + 15);
    }

    private Color[] getKeyColorsByNoteName(NoteName noteName) {
        if (noteName == NoteName.Ab || noteName == NoteName.Bb || noteName == NoteName.Cs || noteName == NoteName.Eb || noteName == NoteName.Fs) {
            return new Color[]{KEY_BLACK_COLOR, Color.GREEN};
        } else {
            return new Color[]{KEY_WHITE_COLOR, Color.BLACK};
        }
    }

    private int getTickCount() {
        return TICK_COUNT_IN_BAR * barCount;
    }

    private void showPopupMenu(Point clickPoint) {
        JPopupMenu popupMenu = new JPopupMenu("Menu");
        JMenuItem mi = new JMenuItem("Add bar");
        mi.addActionListener(l -> {
            addBar(1);
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Select all");
        mi.addActionListener(l -> {
            selectAllNotes();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Deselect all");
        mi.addActionListener(l -> {
            deselectAllNotes();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Delete selected");
        mi.addActionListener(l -> {
            deleteSelectedNotes();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Copy selected");
        mi.addActionListener(l -> {
            copySelectedNotes();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Paste notes");
        mi.addActionListener(l -> {
            paste();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Shift selected down");
        mi.addActionListener(l -> {
            shiftSelectedDown();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        mi = new JMenuItem("Shift selected up");
        mi.addActionListener(l -> {
            shiftSelectedUp();
            popupMenu.setVisible(false);
        });
        popupMenu.add(mi);

        popupMenu.setLocation(clickPoint);
        popupMenu.setVisible(true);
    }

    private void deselectAllNotes() {
        this.getSelectedNoteLabels().forEach(nl -> {
            nl.note.setSelected(false);
        });
        displayNotes();
    }

    private void copySelectedNotes() {
        track.copy();
        this.getSelectedNoteLabels().forEach(nl -> {
            nl.note.setSelected(false);
        });
    }

    private void paste() {
        track.paste();
        displayNotes();
    }

    private void selectAllNotes() {
        track.selectAll();
        displayNotes();
    }

    private void shiftSelectedUp() {
        track.shiftPitch(1);
        displayNotes();
    }

    private void shiftSelectedDown() {
        track.shiftPitch(-1);
        displayNotes();
    }

    private void deleteSelectedNotes() {
        List<NoteLabel> noteLabels = getSelectedNoteLabels();
        track.removeSelected();
        displayNotes();

    }

    private void selectNotes(Point from, Point to) {
        this.findAllBetween(from, to).forEach(nl -> nl.getNote().toggleSelection());
        displayNotes();
    }

    private List<NoteLabel> findAllBetween(Point start, Point end) {
        List<NoteLabel> retVal = new ArrayList<>();
        int width = end.x - start.x;
        int height = end.y - start.y;
        Rectangle r = new Rectangle(start, new Dimension(width, height));
        for (Component c : getComponents()) {
            if (c instanceof NoteLabel) {
                NoteLabel nl = (NoteLabel) c;
                if (nl.getBounds().intersects(r)) {
                    retVal.add(nl);
                }
            }
        }
        return retVal;
    }

    private List<NoteLabel> getSelectedNoteLabels() {
        List<NoteLabel> retVal = new ArrayList<>();
        for (Component c : getComponents()) {
            if (c instanceof NoteLabel) {
                NoteLabel nl = (NoteLabel) c;
                if (nl.note.isSelected()) {
                    retVal.add(nl);
                }
            }
        }
        return retVal;
    }

    @Override
    public void processTickEvent(int tick) {
        currentTick = tick;
        this.repaint();
    }

    @Override
    public void processMeasureEvent(int measure) {

    }

    public void setCurrentNoteLength(NoteLength currentNoteLength) {
        this.currentNoteLength = currentNoteLength;
    }

    public void setCurrentChordType(ChordType currentChordType) {
        this.currentChordType = currentChordType;
    }
}
