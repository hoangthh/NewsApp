package tranhoang202204.gmail.com.newsapp;

public class News {
    private String imageUrl;
    private String title;
    private String description;
    private String tag;
    private String date;
    private String comment;

    public News(String imageUrl, String title, String description, String tag, String date) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.date = date;
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
}
