package hu.boga.music.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import hu.boga.music.App;
import hu.boga.music.gui.keyboard.Keyboard;
import hu.boga.music.gui.projecteditor.ProjectEditor;
import hu.boga.music.gui.track.TrackEditor;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Track;

public class MainFrame extends JFrame {
    public MainFrame() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setPreferredSize(new Dimension(800, 600));
        this.setMinimumSize(new Dimension(600, 400));
        getContentPane().setLayout(null);

//        Keyboard keyboard = new Keyboard();
//        getContentPane().add(keyboard);

         TrackEditor trackEditor = new TrackEditor(new Track());
         getContentPane().add(trackEditor);
         trackEditor.setLocation(0, 0);

//        ProjectEditor pe = new ProjectEditor();
//        pe.setSize(500, 800);
//
//        pe.setPiece(App.CONTROLLER.getPiece());
//
//        getContentPane().add(pe);
//        pe.setLocation(keyboard.getWidth() + 5, 0);
        // pe.setBounds(new Rectangle(keyboard.getWidth(), 0, 500, 500));
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MidiEngine.getSynth().close();
                MidiEngine.getSequencer().close();
                System.exit(0);
            }
        });
    }
}
