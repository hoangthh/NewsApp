package tranhoang202204.gmail.com.newsapp;

public class VideoShort {
    private String videoUrl, title, desc;

    public VideoShort(String videoUrl, String title, String desc) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.desc = desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}
