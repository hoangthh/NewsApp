package tranhoang202204.gmail.com.newsapp;

import java.io.Serializable;

public class News implements Serializable {
    private String id;
    private String imageUrl;
    private String title;
    private String description;
    private String tag;
    private String date;
    private String link;
    private String comment;
    private String bookmarked;
    private String content;

    public News(String id, String imageUrl, String title, String description, String tag, String date, String bookmarked, String link) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.date = date;
        this.bookmarked = bookmarked;
        this.link = link;
    }

    public News(String id, String imageUrl, String title, String description, String tag, String date, String bookmarked, String link, String content) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.date = date;
        this.bookmarked = bookmarked;
        this.link = link;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter và setter cho bookmark
    public String getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(String bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
