package hu.boga.music.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.google.common.eventbus.Subscribe;
import hu.boga.music.App;
import hu.boga.music.gui.projecteditor.Project;
import hu.boga.music.gui.projecteditor.ProjectEditor;
import hu.boga.music.gui.track.TrackEditor;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Track;

public class MainFrame extends JFrame {

    public static final String DEFAULT_PROJECT_NAME = "defaultProject";
    private Project project;
    private TrackEditor trackEditor;
    private JMenu tracksMenuItem;

    public MainFrame() {
        App.EVENT_BUS.register(this);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setPreferredSize(new Dimension(800, 600));
        this.setMinimumSize(new Dimension(600, 400));
        getContentPane().setLayout(null);

        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MidiEngine.getSynth().close();
                MidiEngine.getSequencer().close();
                System.exit(0);
            }
        });
        this.project = new Project(DEFAULT_PROJECT_NAME);

        ProjectEditor editor = new ProjectEditor(project);
        getContentPane().add(editor);

    }

    @Subscribe
    private void handleTrackopenEvent(TrackOpenEvent event){
        if(trackEditor == null){
            trackEditor = new TrackEditor();
            getContentPane().add(trackEditor);
        }
        trackEditor.setTrack(event.track);
    }


}
