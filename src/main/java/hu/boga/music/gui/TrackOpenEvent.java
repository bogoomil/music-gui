package hu.boga.music.gui;

import hu.boga.music.model.Track;

public class TrackOpenEvent {
    public Track track;
    public TrackOpenEvent(Track t) {
        this.track = t;
    }
}
