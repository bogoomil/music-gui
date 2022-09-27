package hu.boga.music.gui.keyboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JLabel;
import javax.swing.border.MatteBorder;

import hu.boga.music.App;
import hu.boga.music.enums.NoteName;
import hu.boga.music.gui.events.KeyClickedEvent;
import hu.boga.music.gui.events.KeyEnterEvent;
import hu.boga.music.gui.events.KeyExitEvent;
import hu.boga.music.gui.events.KeyWheelEvent;
import hu.boga.music.theory.Pitch;

public class KeyLabel extends JLabel {

    private int octave;
    private NoteName noteName;
    private int degree;
    private boolean highLighted = false;

    private Color origColor;
    private static final Color DISABLED_COLOR = Color.LIGHT_GRAY;
    private static final Color HIGH_LIGHT_COLOR = Color.CYAN;

    private static final String[] degrees = new String[] { "I", "II", "III", "IV", "V", "VI", "VII" };

    public KeyLabel(int octave, NoteName noteName, Color origColor, int degree) {
        setBorder(new MatteBorder(0, 0, 1, 1, new Color(0, 0, 0)));
        this.octave = octave;
        this.noteName = noteName;
        this.origColor = origColor;
        this.degree = degree;
        setBackground(origColor);
        setOpaque(true);

        this.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

            }
        });

        this.addMouseWheelListener(e -> App.EVENT_BUS.post(new KeyWheelEvent(getPitch(), e.getWheelRotation())));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    App.EVENT_BUS.post(new KeyExitEvent(octave, noteName, degree));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    App.EVENT_BUS.post(new KeyEnterEvent(octave, noteName, degree));
                }

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    App.EVENT_BUS.post(new KeyClickedEvent(octave, noteName, degree));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                super.mouseReleased(e);
            }

        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            origColor = DISABLED_COLOR;
        }
        this.setBackground(enabled ? origColor : DISABLED_COLOR);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.origColor == Color.BLACK) {
            g.setColor(Color.WHITE);
        }
        if (this.isEnabled()) {
            if (degree == 0) {
                g.setColor(Color.RED);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
            } else {
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
            }
            g.drawString(degrees[this.degree], 2, this.getHeight() - 20);
            g.drawString(this.noteName.name() + " " + this.octave, 2, this.getHeight() - 10);

        }
    }

    public void setHighLightedOn() {
        this.setBackground(HIGH_LIGHT_COLOR);
        this.setHighLighted(true);
        this.revalidate();
    }

    public void setHighLightedOff() {
        this.setBackground(origColor);
        this.setHighLighted(false);
        this.revalidate();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((noteName == null) ? 0 : noteName.hashCode());
        result = prime * result + octave;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KeyLabel other = (KeyLabel) obj;
        if (noteName != other.noteName) {
            return false;
        }
        if (octave != other.octave) {
            return false;
        }
        return true;
    }

    public boolean isHighLighted() {
        return highLighted;
    }

    public void setHighLighted(boolean highLighted) {
        this.highLighted = highLighted;
    }

    public Pitch getPitch() {
        return new Pitch(this.noteName.ordinal() + (this.octave * 12));
    }

}
