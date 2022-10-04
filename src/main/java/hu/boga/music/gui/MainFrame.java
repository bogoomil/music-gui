package hu.boga.music.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.google.common.eventbus.Subscribe;
import hu.boga.music.App;
import hu.boga.music.gui.events.ProjectChangedEvent;
import hu.boga.music.gui.events.TrackOpenEvent;
import hu.boga.music.gui.projecteditor.Project;
import hu.boga.music.gui.projecteditor.ProjectEditor;
import hu.boga.music.gui.track.TrackEditorFrame;
import hu.boga.music.midi.MidiConverter;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Track;

public class MainFrame extends JFrame {

    public static final String DEFAULT_PROJECT_NAME = "defaultProject";
    private Project project;
    private TrackEditorFrame trackEditorFrame;
    private JMenu tracksMenuItem;
    private JFileChooser fileChooser;

    public MainFrame() {
        App.EVENT_BUS.register(this);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setPreferredSize(new Dimension(800, 600));
        this.setMinimumSize(new Dimension(600, 400));
        getContentPane().setLayout(null);

        createMainMenu();

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
        Rectangle rectangle = new Rectangle(getContentPane().getWidth() - editor.getWidth(), 0, editor.getWidth(), getContentPane().getHeight());
        editor.setBounds(rectangle);
        getContentPane().add(editor);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".mid");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        fileChooser.setCurrentDirectory(new File("."));

    }

    private void createMainMenu(){
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem item = new JMenuItem("Import midi");
        menu.add(item);

        item.addActionListener(l -> {
            importMidi();
        });

        item = new JMenuItem("Export midi");
        menu.add(item);
        item.addActionListener(l -> {
            exportMidi();

        });
    }

    private void exportMidi() {
        if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            try {
                MidiEngine.exportMidi(project.getTracks(), project.getTempo(), file.getAbsolutePath());
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void importMidi() {
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            MidiConverter converter = new MidiConverter(file);
            try {
                List<Track> trackList = converter.convertMidiFileToTracks();
                project.setTracks(trackList);
                App.EVENT_BUS.post(new ProjectChangedEvent());
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ;
    }

    @Subscribe
    private void handleTrackopenEvent(TrackOpenEvent event){
        if(trackEditorFrame == null){
            trackEditorFrame = new TrackEditorFrame();
            getContentPane().add(trackEditorFrame);
        }
        trackEditorFrame.setTrack(event.track);
    }

}
