package items;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewsItem {
    private String body;
    private String date;
    private String hash;
    private String title;
    private String url;

    public NewsItem() {}

    public NewsItem(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        this.url = node.get("url").asText();
        this.title = node.get("title").asText();
        this.hash = node.get("hash").asText();
        this.date = node.get("date").asText();
        this.body = node.get("body").asText();
    }

    // Setters
    public void setBody(String body) {
        this.body = body;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    // Getters
    public String getBody() {
        return body;
    }
    public String getDate() {
        return date;
    }
    public String getHash() {
        return hash;
    }
    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "url='" + url + "'" +
                ", title='" + title + "'" +
                ", body='" + body + "'" +
                ", date='" + date + "'" +
                ", hash='" + hash + "'" +
                "}";
    }

    public String obj2json() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }
}
