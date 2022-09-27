package hu.boga.music;

import java.awt.Font;

import com.google.common.eventbus.EventBus;

import hu.boga.music.controller.AppController;
import hu.boga.music.gui.MainFrame;

public class App {

    public static final EventBus EVENT_BUS = new EventBus();

    public static final AppController CONTROLLER = new AppController();

    public static final Font DEFAULT_FONT = App.DEFAULT_FONT;

    public static void main(String[] args) {
        new MainFrame();
    }

}
