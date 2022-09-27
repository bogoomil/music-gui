package hu.boga.music.gui.controls;

import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import hu.boga.music.App;
import hu.boga.music.enums.NoteLength;

public class NoteLengthCombo extends JComboBox<NoteLength> {

    private static final List<NoteLength> list = Arrays.asList(NoteLength.values());

    public NoteLengthCombo() {
        super();
        setFont(App.DEFAULT_FONT);
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        list.forEach(i -> {
            model.addElement(i);
        });
        this.setModel(model);
        this.setSelectedIndex(3);
    }

    public NoteLength getSelectedNoteLength() {
        return this.getItemAt(this.getSelectedIndex());
    }

    public void setSelectedNoteLength(NoteLength length) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == length) {
                this.setSelectedIndex(i);
            }
        }

    }
}
