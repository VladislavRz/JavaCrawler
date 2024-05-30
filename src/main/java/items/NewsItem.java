package items;

public class NewsItem {
    private String body;
    private String date;
    private String hash;
    private String time;
    private String title;
    private String url;

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "url='" + url + "'" +
                ", title='" + title + "'" +
                ", body='" + body + "'" +
                ", date='" + date + "'" +
                ", time='" + time + "'" +
                ", hash='" + hash + "'" +
                "}";
    }
}
