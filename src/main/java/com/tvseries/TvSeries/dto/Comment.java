package com.tvseries.TvSeries.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Comment {
    private @Id
    @GeneratedValue
    Long id;

    public Comment(String title, String data, byte mark) {
        this.title = title;
        this.data = data;
        this.mark = mark;
        //this.author = author;
        //this.show = show;
    }

    private String title;
    private String data;
    private byte mark;
    //private User author;
    //private TvShow show;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte getMark() {
        return mark;
    }

    public void setMark(byte mark) {
        this.mark = (byte)(mark & 5);
    }

    //public User getAuthor() {
    //    return author;
    //}

    //public void setAuthor(User author) {
    //    this.author = author;
    //}

    //public TvShow getShow() {
    //    return show;
    //}

    //public void setShow(TvShow show) {
    //    this.show = show;
    //}
}
