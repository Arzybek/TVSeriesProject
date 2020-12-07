package com.tvseries.TvSeries.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    private @Id
    @GeneratedValue
    Long id;

    private String name;

    @OneToMany(targetEntity=TvShow.class,  fetch= FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TvShow> watchingShows = new ArrayList<>();
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

    public List<TvShow> getWatchingShows() {
        return watchingShows;
    }

    public void addWatchingShow(TvShow show)
    {
        watchingShows.add(show);
    }

    public boolean deleteWatchingShow(TvShow show)
    {
        return watchingShows.remove(show);
    }

    public void watchEpisode(TvShow show, Episode episode)
    {
        int ind = watchingShows.indexOf(show);
        if (ind==-1) {
            System.out.println("watchEpisodeError: this show not in watching");
            return;
        }
        int indEp = watchingShows.get(ind).getEpisodes().indexOf(episode);
        if (indEp==-1) {
            System.out.println("watchEpisodeError: this show not in episode list");
            return;
        }
        watchingShows.get(ind).getEpisodes().get(indEp).watchEpisode();
    }

    public void unwatchEpisode(TvShow show, Episode episode)
    {
        int ind = watchingShows.indexOf(show);
        if (ind==-1) {
            System.out.println("unwatchEpisodeError: this show not in watching");
            return;
        }
        int indEp = watchingShows.get(ind).getEpisodes().indexOf(episode);
        if (indEp==-1) {
            System.out.println("unwatchEpisodeError: this show not in episode list");
            return;
        }
        watchingShows.get(ind).getEpisodes().get(indEp).unwatchEpisode();
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

    public User(String name, String login, String passwordHash) {
        this.name = name;
        this.login = login;
        this.passwordHash = passwordHash;
        this.photoLink = "https://i.imgur.com/oCVNMVX.jpg";
    }
}
