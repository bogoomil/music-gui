package hu.boga.music.gui.keyboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;

import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import com.google.common.eventbus.Subscribe;

import hu.boga.music.App;
import hu.boga.music.gui.events.ChordHighLightedEvent;
import hu.boga.music.gui.trackeditor.TrackPanel;

public class Keyboard extends JInternalFrame {

    // public static final JCheckBox CBX_CHORD_MODE = new JCheckBox("Chord
    // mode", false);
    private JLabel lbChord;
    public static final JRadioButton ALL_CHORDS_RADIO_BTN = new JRadioButton("All chords");
    public static final JRadioButton IN_SCALE_RADIO_BTN = new JRadioButton("In scale");
    private final JPanel keyboardPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final JPanel mainPanelCenter = new JPanel();
    private final TrackPanel trackPanel = new TrackPanel();
    private final KeyPanel keyPanel = new KeyPanel();
    private final JSlider sliderScroll = new JSlider();
    private final JPanel panel = new JPanel();
    private final JSlider sliderZoom = new JSlider();

    public Keyboard() {
        super("Keyboard", true, true, false, true);
        App.EVENT_BUS.register(this);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new BorderLayout(0, 0));

        mainPanel.add(mainPanelCenter, BorderLayout.CENTER);
        mainPanelCenter.setLayout(new BorderLayout(0, 0));

        // getContentPane().add(trackPanel, BorderLayout.CENTER);
        // keyboardPanel.setPreferredSize(new Dimension(10, 250));

        // getContentPane().add(keyboardPanel, BorderLayout.NORTH);
        keyboardPanel.setLayout(new BorderLayout());
        keyboardPanel.add(getTopPanel(), BorderLayout.NORTH);
        keyboardPanel.add(keyPanel, BorderLayout.CENTER);
        keyboardPanel.setPreferredSize(new Dimension(10, 250));

        mainPanelCenter.add(keyboardPanel, BorderLayout.NORTH);

        JPanel trackPanelWrapper = new JPanel(null);

        mainPanelCenter.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        panel.setBounds(5, 5, 1400, 300);
        trackPanel.setBounds(0, 0, 1400, 300);
        panel.add(trackPanel);
        sliderScroll.setMaximum(100);
        sliderScroll.setSnapToTicks(true);
        sliderScroll.setPaintTicks(true);
        sliderScroll.setValue(100);
        sliderScroll.setOrientation(SwingConstants.VERTICAL);

        // trackPanelWrapper.add(trackPanel);

        mainPanel.add(sliderScroll, BorderLayout.EAST);

        sliderScroll.setMaximum(100);
        sliderZoom.setValue(20);
        sliderZoom.setMaximum(30);

        mainPanel.add(sliderZoom, BorderLayout.SOUTH);
        sliderScroll.addChangeListener(l -> {
            double percent = trackPanel.getHeight() * ((100 - sliderScroll.getValue()) / 100d);

            System.out.println(" :: " + sliderScroll.getValue());
            Point p = trackPanel.getLocation();
            p.y = (int) (-1 * Math.round(percent));
            trackPanel.setLocation(p);

        });

        sliderZoom.addChangeListener(l -> {
            trackPanel.setTickHeight(sliderZoom.getValue());
            trackPanel.repaint();
        });

        this.pack();
        this.setVisible(true);
    }

    private JPanel getTopPanel() {
        JPanel p = new JPanel();
        keyboardPanel.add(p, BorderLayout.NORTH);
        FlowLayout flowLayout = (FlowLayout) p.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        p.add(IN_SCALE_RADIO_BTN);
        p.add(ALL_CHORDS_RADIO_BTN);
        IN_SCALE_RADIO_BTN.setFont(App.DEFAULT_FONT);
        IN_SCALE_RADIO_BTN.setSelected(true);
        ALL_CHORDS_RADIO_BTN.setFont(App.DEFAULT_FONT);

        ButtonGroup group = new ButtonGroup();
        group.add(ALL_CHORDS_RADIO_BTN);
        group.add(IN_SCALE_RADIO_BTN);
        lbChord = new JLabel("");
        lbChord.setFont(App.DEFAULT_FONT);
        p.add(lbChord);

        return p;
    }

    @Subscribe
    private void handleChordHighLightedEvent(ChordHighLightedEvent ev) {
        this.lbChord.setText(ev.getChord() != null ? ev.getPointer() + ". " + ev.getChord() : "");
    }
}
