package hu.boga.music.gui.projecteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.eventbus.Subscribe;
import hu.boga.music.App;
import hu.boga.music.gui.events.ProjectChangedEvent;
import hu.boga.music.gui.events.TrackOpenEvent;
import hu.boga.music.gui.controls.TempoSlider;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Track;

public class ProjectEditor extends JInternalFrame {

    public static final String DEFAULT_TRACK_NAME = "default track name";
    private Track currentTrack = App.CONTROLLER.getCurrentTrack();
    private JPanel tracksPanel;
    public static final TempoSlider TEMPO_SLIDER = new TempoSlider();
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField projectNameTxtField;
    public static final JToggleButton BTN_REC = new JToggleButton("REC");
    private JFileChooser fileChooser = new JFileChooser();


    Project project;

    public ProjectEditor(Project project) {

        super("Project editor", true, false, false, false);
        this.project = project;
        App.EVENT_BUS.register(this);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel tracksWrapper = new JPanel();
        scrollPane.setViewportView(tracksWrapper);

        tracksPanel = new JPanel();
        tracksWrapper.add(tracksPanel);
        tracksPanel.setLayout(new BoxLayout(tracksPanel, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel pnButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnButtons.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel.add(pnButtons);

        BTN_REC.setBackground(Color.RED);
        BTN_REC.setForeground(Color.WHITE);
        pnButtons.add(BTN_REC);
        JButton btnPlay = new JButton("Play");
        btnPlay.addActionListener(e -> {
            try {
                MidiEngine.playTracks(project.getTracks(), TEMPO_SLIDER.getValue(), 0);
            } catch (InvalidMidiDataException | MidiUnavailableException | IOException e1) {
                e1.printStackTrace();
            }
        });
        pnButtons.add(btnPlay);
        JButton btnStop = new JButton("Stop");
        pnButtons.add(btnStop);

        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        panel.add(panel_1);

        projectNameTxtField = new JTextField();
        projectNameTxtField.setBorder(new TitledBorder(null, "Title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.add(projectNameTxtField);
        projectNameTxtField.setColumns(10);

        panel_1.add(TEMPO_SLIDER);

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
        flowLayout_2.setAlignment(FlowLayout.LEFT);
        panel.add(panel_2);

        JButton btnAddTrack = new JButton("+track");
        panel_2.add(btnAddTrack);
        btnAddTrack.addActionListener(e -> {
            addTrack(project);
        });

        JButton btnDelTrack = new JButton("-track");
        panel_2.add(btnDelTrack);

        JButton btnDuplicateTrack = new JButton("X2 track");
        panel_2.add(btnDuplicateTrack);

        JPanel panel_3 = new JPanel();
        getContentPane().add(panel_3, BorderLayout.SOUTH);

        TEMPO_SLIDER.setValue(project.getTempo());
        TEMPO_SLIDER.addChangeListener(l -> {
            project.setTempo(TEMPO_SLIDER.getValue());
        });

        this.pack();
        this.setVisible(true);
    }

    private void addTrack(Project project) {
        Track newTrack = new Track();
        project.getTracks().add(newTrack);
        this.updateTrackButtons();
        App.EVENT_BUS.post(new TrackOpenEvent(newTrack));
    }

    private void updateTrackButtons() {
        tracksPanel.removeAll();
        this.project.getTracks().forEach(t -> {
            JButton jButton = new JButton(t.getId().toString().substring(0,10));
            jButton.addActionListener(l -> {
                App.EVENT_BUS.post(new TrackOpenEvent(t));
            });
            tracksPanel.add(jButton);
        });
        tracksPanel.revalidate();
    }

    @Subscribe
    void handleProjectChangedEvent(ProjectChangedEvent event){
        updateTrackButtons();
    }

}
