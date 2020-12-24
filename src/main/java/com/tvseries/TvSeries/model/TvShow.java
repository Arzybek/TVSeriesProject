package com.tvseries.TvSeries.model;

import org.hibernate.annotations.Cascade;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

@Entity
public class TvShow implements Serializable {

    private @Id @GeneratedValue Long id;
    private String name;
    private String category;
    private int year;
    private String imgLink;
    @Lob
    private String description;
    private int watcherCount;
    private Boolean isUserShow = false;
    private Long authorID;

    @OneToMany(targetEntity=Episode.class,  fetch= FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Episode> episodes = new ArrayList<>();


    @Column( length = 100000 )
    private HashMap<Long, Float> ratings = new HashMap<>();

    @Column( length = 100000 )
    private HashMap<Long, String> reviews = new HashMap<>();

    @Column( length = 100000 )
    private ArrayList<Long> reviewsAuthors = new ArrayList<>();

    private Float rating = 0F;

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

    public TvShow() {
    }

    public TvShow(String name, String category, int year) {
        this.name = name;
        this.category = category;
        this.year = year;
    }

    public TvShow(String name, String category, int year, String imgLink) {
        this.name = name;
        this.category = category;
        this.year = year;
        this.imgLink = imgLink;
    }

    public TvShow(String name, String category, int year, String imgLink, String description) {
        this.name = name;
        this.category = category;
        this.year = year;
        this.imgLink = imgLink;
        this.description = description;
    }

    public TvShow(String name, String category, int year, Long authorID) {

        this.name = name;
        this.category = category;
        this.year = year;
        this.isUserShow = true;
        this.authorID = authorID;
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

    public void addEpisode(Episode ep)
    {
        episodes.add(ep);
    }

    public void deleteEpisode(Episode ep)
    {
        episodes.remove(ep);
    }

    public void setImgLink(String imgLink)
    {
        this.imgLink = imgLink;
    }

    public String getImgLink() {return this.imgLink;}

    public boolean isUserShow()
    {
        return isUserShow;
    }

    public void setisUserShow(boolean isUserShow)
    {
        this.isUserShow = isUserShow;
    }

    public void setAuthorID(Long ID)
    {
        this.authorID = ID;
    }

    public Long getAuthorID()
    {
        return this.authorID;
    }


    public List<Episode> getEpisodes()
    {
        return episodes;
    }

    public void addRating(Long userID, Float rating)
    {
        ratings.put(userID, rating);
        var summ = 0F;
        for (Float rait:ratings.values()) {
            summ+=rait;
        }
        if (ratings.size()==0)
            this.rating = 0F;
        else
            this.rating = summ/ratings.size();
    }

    public Float getRating()
    {
        return rating;
    }

    public void deleteRating(Long userID)
    {
        ratings.remove(userID);
    }

    public void addReview(Long authorID, String review)
    {
        this.reviews.put(authorID, review);
        this.reviewsAuthors.add(authorID);
    }

    public ArrayList<String> getNRandomReviews(int showAmount)
    {
        var alreadyGot = new HashSet<Long>();
        if (showAmount>reviews.size())
            showAmount = reviews.size();
        var output = new ArrayList<String>();
        Random random = new Random();
        int i=0;
        while (i<showAmount)
        {
            var author = reviewsAuthors.get(random.nextInt(reviewsAuthors.size()));
            if (alreadyGot.contains(author))
                continue;
            output.add(reviews.get(author));
            alreadyGot.add(author);
            i++;
        }
        return output;
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
