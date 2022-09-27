package hu.boga.music.gui.events;

import hu.boga.music.model.Track;

public class TrackNotesChangedEvent {

    Track track;

    public TrackNotesChangedEvent(Track track) {
        super();
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

}
