package hu.boga.music.gui.projecteditor;

import hu.boga.music.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Project {
    private String name;

    public Project(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<Track> tracks = new ArrayList<>();

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Optional<Track> getTrackById(int id){
        return tracks.stream().filter(track -> track.getId() == id).findFirst();
    }
}
