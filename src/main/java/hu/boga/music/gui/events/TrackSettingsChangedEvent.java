package hu.boga.music.gui.events;

import hu.boga.music.model.Track;

public class TrackSettingsChangedEvent {

    Track track;

    public TrackSettingsChangedEvent(Track track) {
        super();
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

}
