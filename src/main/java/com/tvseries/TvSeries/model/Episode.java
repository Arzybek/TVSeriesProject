package com.tvseries.TvSeries.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Episode {

    private @Id
    @GeneratedValue
    Long id;
    private String episodeName;
    private int index;
    private String show;
    private long showID;
    private String description;
    private int watcherCount;
    private boolean isWatched;


    public void setDescription(String description) {
        this.description = description;
    }

    public void setWatcherCount(int watcherCount) {
        this.watcherCount = watcherCount;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public void watchEpisode()
    {
        isWatched = true;
    }

    public void unwatchEpisode()
    {
        isWatched = false;
    }

    public String getDescription() {
        return description;
    }

    public int getWatcherCount() {
        return watcherCount;
    }

    public Integer getEpisodeIndex()
    {
        return this.index;
    }


    //private List<Comment> comments = new ArrayList();

    public Episode ()
    {

    }

    public Episode(String show, long showID, int index) {

        this.show = show;
        this.showID = showID;
        this.index = index;
        this.isWatched = false;
    }

    public Long getId() {
        return this.id;
    }

    public String getEpisodeName() {
        return this.episodeName;
    }

    public String getShow() {
        return this.show;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShow(String show) {
        this.show = show;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Episode))
            return false;
        Episode another = (Episode) o;
        return Objects.equals(this.id, another.id) && Objects.equals(this.episodeName, another.episodeName)
                 && Objects.equals(this.index, another.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.episodeName, this.index);
    }

    @Override
    public String toString() {
        return "Episode{" + "id=" + this.id + ", name='" + this.episodeName + '\'' + '}';
    }

}
