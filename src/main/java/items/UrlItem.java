package items;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class UrlItem {
    private String hash;
    private String title;
    private String url;

    public UrlItem(){}

    public UrlItem(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        this.url = node.get("url").asText();
        this.title = node.get("title").asText();
        this.hash = node.get("hash").asText();
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    // Getters
    public String getUrl() {
        return url;
    }
    public String getTitle() {
        return title;
    }
    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "UrlItem{" +
                "url='" + url + "'" +
                ", title='" + title + "'" +
                ", hash='" + hash + "'" +
                "}";
    }

    public String obj2json() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }
}
