package hu.boga.music.gui.controls;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import hu.boga.music.midi.MidiEngine;

public class InstrumentCombo extends JComboBox {

    private static final List<Instrument> instruments = Arrays.asList(MidiEngine.getSynth().getAvailableInstruments());

    public InstrumentCombo() {
        super();
        // setFont(App.DEFAULT_FONT);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        instruments.sort(Comparator.comparing(Instrument::getName));
        instruments.forEach(i -> {
            model.addElement(i.getName());
        });
        this.setModel(model);
    }

    public int getProgram() {
        return instruments.get(this.getSelectedIndex()).getPatch().getProgram();
    }

    public void setProgram(int program) {
        for (int i = 0; i < this.instruments.size(); i++) {
            if (instruments.get(i).getPatch().getProgram() == program) {
                this.setSelectedIndex(i);
                break;
            }
        }
    }
}
