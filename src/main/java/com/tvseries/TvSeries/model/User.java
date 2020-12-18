package com.tvseries.TvSeries.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Entity
public class User {
    private @Id
    @GeneratedValue
    Long id;

    private String name;

    @Column( length = 100000 )
    private HashMap<Long, Boolean[]> watchingShows = new HashMap<>();

    @Column( length = 100000 )
    private HashMap<Long, Float> showRatings = new HashMap<>();

    @Column( length = 100000 )
    private HashMap<Long, String> showReviews = new HashMap<>();
    //private List<TvShow> likedShows = new ArrayList<>();
    //private List<TvShow> featureShows = new ArrayList<>();
    //private List<Comment> comments = new ArrayList<>();

    //public List<Comment> getComments() {
    //    return comments;
    //}

    private String description;
    private int age;
    private String login;
    private String photoLink;

    public User(){}

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getWatchingShowsIDs() {
        return watchingShows.keySet();
    }

    public void addWatchingShow(TvShow show)
    {
        var episodesCounter = show.getEpisodes().size();
        var epWatch = new Boolean[episodesCounter];
        for (int i=0;i<episodesCounter;i++)
            epWatch[i] = false;
        watchingShows.put(show.getId(),epWatch);
    }

    public void deleteWatchingShow(Long showID)
    {
        watchingShows.remove(showID);
    }

    public void watchEpisode(Long showID, Long episodeID)
    {
        if (!watchingShows.containsKey(showID))
            return;
        if (watchingShows.get(showID).length<episodeID)
            return;
        watchingShows.get(showID)[episodeID.intValue()] = true;
    }

    public void unwatchEpisode(Long showID, Long episodeID)
    {
        if (!watchingShows.containsKey(showID))
            return;
        if (watchingShows.get(showID).length<episodeID)
            return;
        watchingShows.get(showID)[episodeID.intValue()] = false;
    }

    public Boolean isWatchedEpisode(Long showID, Long episodeID)
    {
        if (!watchingShows.containsKey(showID))
            return false;
        if (watchingShows.get(showID).length<episodeID)
            return false;
        return watchingShows.get(showID)[episodeID.intValue()];
    }


    public Boolean[] getWatchedEpisodes(Long showID)
    {
        if (!watchingShows.containsKey(showID))
            return new Boolean[0];
        return watchingShows.get(showID);
    }

    public void addRating(long showID, Float rating)
    {
        showRatings.put(showID, rating);
    }

    public Float getRating(long showID)
    {
        if (!showRatings.containsKey(showID))
            return 0F;
        return showRatings.get(showID);
    }


    public void addReview(long showID, String review)
    {
        showReviews.put(showID, review);
    }

    public String getReview(long showID)
    {
        if (!showRatings.containsKey(showID))
            return "no review";
        return showReviews.get(showID);
    }

    /*public void setWatchingShows(List<TvShow> watchingShows) {
        this.watchingShows = watchingShows;
    }

    public List<TvShow> getLikedShows() {
        return likedShows;
    }

    public void setLikedShows(List<TvShow> likedShows) {
        this.likedShows = likedShows;
    }*/

    //public List<TvShow> getFeatureShows() {
    //    return featureShows;
    //}

    //public void setFeatureShows(List<TvShow> featureShows) {
    //    this.featureShows = featureShows;
    //}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    private String passwordHash;

    public String getPhotoLink(){
        return this.photoLink;
    }


    public User(String login, String passwordHash) {
        this.name = "logged anon";
        this.login = login;
        this.passwordHash = passwordHash;
        this.photoLink = "https://i.imgur.com/oCVNMVX.jpg";
    }

    public User(String name, String login, String passwordHash) {
        this.name = name;
        this.login = login;
        this.passwordHash = passwordHash;
        this.photoLink = "https://i.imgur.com/oCVNMVX.jpg";
    }
}
