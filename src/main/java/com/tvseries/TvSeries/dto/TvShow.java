package com.tvseries.TvSeries.dto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TvShow {

    private @Id @GeneratedValue Long id;
    private String name;
    private String category;
    private int year;
    //private Image image;
    private String description;
    private int watcherCount;

    //public void setImage(Image image) {
   //     this.image = image;
    //}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWatcherCount(int watcherCount) {
        this.watcherCount = watcherCount;
    }

    //public Image getImage() {
    //    return image;
    //}

    public String getDescription() {
        return description;
    }

    public int getWatcherCount() {
        return watcherCount;
    }

   // public List<Comment> getComments() {
    //    return comments;
   // }

    //private List<Comment> comments = new ArrayList();

    TvShow() {}

    public TvShow(String name, String category, int year) {

        this.name = name;
        this.category = category;
        this.year = year;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TvShow))
            return false;
        TvShow another = (TvShow) o;
        return Objects.equals(this.id, another.id) && Objects.equals(this.name, another.name)
                && Objects.equals(this.category, another.category) && Objects.equals(this.year, another.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.category, this.year);
    }

    @Override
    public String toString() {
        return "TvShow{" + "id=" + this.id + ", name='" + this.name + '\'' + ", category='" + this.category + '\'' + '}' +
                ", year: " + this.year;
    }
}
