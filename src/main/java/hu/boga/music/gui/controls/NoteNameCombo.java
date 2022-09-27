package hu.boga.music.gui.controls;

import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import hu.boga.music.App;
import hu.boga.music.enums.NoteName;

public class NoteNameCombo extends JComboBox<NoteName> {

    private static final List<NoteName> list = Arrays.asList(NoteName.values());

    public NoteNameCombo() {
        super();
        setFont(App.DEFAULT_FONT);
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        list.forEach(i -> {
            model.addElement(i);
        });
        this.setModel(model);
        this.setSelectedIndex(0);
    }

    public NoteName getSelectedNoteName() {
        return this.getItemAt(this.getSelectedIndex());
    }

    public void setSelectedNoteName(NoteName scaleRoot) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == scaleRoot) {
                setSelectedIndex(i);
            }
        }
    }
}
