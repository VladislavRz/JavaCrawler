package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;


public class Requester {
    public static Document getRequest(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        return doc;
    }

}
