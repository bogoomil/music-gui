package hu.boga.music.gui.projecteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

import hu.boga.music.App;
import hu.boga.music.gui.controls.TempoSlider;
import hu.boga.music.midi.MidiEngine;
import hu.boga.music.model.Piece;
import hu.boga.music.model.Track;

public class ProjectEditor extends JInternalFrame {

    private Piece piece = App.CONTROLLER.getPiece();
    private Track currentTrack = App.CONTROLLER.getCurrentTrack();
    private JPanel tracks;
    public static final TempoSlider TEMPO_SLIDER = new TempoSlider();
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField tfPieceName;
    public static final JToggleButton BTN_REC = new JToggleButton("REC");
    private JFileChooser fileChooser = new JFileChooser();

    public ProjectEditor() {

        super("Project editor", true, false, false, false);
        App.EVENT_BUS.register(this);

        TEMPO_SLIDER.addChangeListener(l -> this.piece.setTempo(TEMPO_SLIDER.getValue()));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel tracksWrapper = new JPanel();
        scrollPane.setViewportView(tracksWrapper);

        tracks = new JPanel();
        tracksWrapper.add(tracks);
        tracks.setLayout(new BoxLayout(tracks, BoxLayout.Y_AXIS));

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
                MidiEngine.playTracks(App.CONTROLLER.getPiece().getTracks(), TEMPO_SLIDER.getValue(), 0);
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

        tfPieceName = new JTextField();
        tfPieceName.setBorder(new TitledBorder(null, "Title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.add(tfPieceName);
        tfPieceName.setColumns(10);

        panel_1.add(TEMPO_SLIDER);

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
        flowLayout_2.setAlignment(FlowLayout.LEFT);
        panel.add(panel_2);

        JButton btnAddTrack = new JButton("+track");
        panel_2.add(btnAddTrack);
        btnAddTrack.addActionListener(e -> {
            Track newTrack = new Track();
            App.CONTROLLER.getPiece().addTrack(newTrack);
            this.rebuildGui();
        });

        JButton btnDelTrack = new JButton("-track");
        panel_2.add(btnDelTrack);
//        btnDelTrack.addActionListener(e -> {
//            this.getSelectedPanel().ifPresent(tsp -> {
//                piece.removeTrack(tsp.getTrack());
//                this.rebuildGui();
//            });
//        });

        JButton btnDuplicateTrack = new JButton("X2 track");
        panel_2.add(btnDuplicateTrack);
//        btnDuplicateTrack.addActionListener(e -> {
//            this.getSelectedPanel().ifPresent(tsp -> {
//                Track clone = tsp.getTrack().clone();
//                piece.addTrack(clone);
//                this.rebuildGui();
//            });
//        });

        JPanel panel_3 = new JPanel();
        getContentPane().add(panel_3, BorderLayout.SOUTH);

        this.pack();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmOpen = new JMenuItem("Open...");
        mnFile.add(mntmOpen);
        mntmOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(ProjectEditor.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        open(file);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

        JMenuItem mntmSave = new JMenuItem("Save...");
        mnFile.add(mntmSave);
        mntmSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(ProjectEditor.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        save(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        this.setVisible(true);
    }

    private void open(File file) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Piece p = om.readValue(file, Piece.class);
        this.setPiece(p);

    }

    private void save(File file) throws IOException {
        piece.setName(this.tfPieceName.getText());

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(piece);
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.flush();
        writer.close();

    }

    public void setPiece(Piece p) {
        this.piece = p;
        App.CONTROLLER.setPiece(piece);
        tfPieceName.setText(piece.getName());
        rebuildGui();
    }

    private void rebuildGui() {
        tfPieceName.setText(this.piece.getName());
        TEMPO_SLIDER.setValue(piece.getTempo());
        tracks.removeAll();
        this.piece.getTracks().forEach(t -> {
//            TrackSettingsPanel tsp = new TrackSettingsPanel(t);
//            this.buttonGroup.add(tsp.getRadioButton());
//            tracks.add(tsp);
        });
        tracks.revalidate();
    }

//    private Optional<TrackSettingsPanel> getSelectedPanel() {
//        for (int i = 0; i < this.tracks.getComponents().length; i++) {
//            TrackSettingsPanel tsp = (TrackSettingsPanel) tracks.getComponent(i);
//            if (tsp.getRadioButton().isSelected()) {
//                return Optional.of(tsp);
//            }
//
//        }
//        return Optional.empty();
//    }

}
