package hu.boga.music.gui.controls;

import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import hu.boga.music.App;
import hu.boga.music.enums.Tone;

public class ToneCombo extends JComboBox<Tone> {

    private static final List<Tone> list = Arrays.asList(Tone.values());

    public ToneCombo() {
        super();
        setFont(App.DEFAULT_FONT);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        list.forEach(i -> {
            model.addElement(i);
        });
        this.setModel(model);
    }

    public Tone getSelectedTone() {
        return this.getItemAt(this.getSelectedIndex());
    }

    public void setSelectedTone(Tone scaleRoot) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == scaleRoot) {
                this.setSelectedIndex(i);
            }
        }

    }

}
