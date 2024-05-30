package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Vector;


public class Requester {
    public static Document getRequest(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        return doc;
    }

    public static String parse(Document doc) {
//        Elements news = doc.select("[^data-v-2fb9c850]");
        Element main_div = doc.select("div.flex.flex-wrap").last();
        Elements links = main_div.select("a");
        Vector<String> hrefs = new Vector<String>();
        for (Element element : links) {
            hrefs.addElement(element.attr("href"));
        }

        return hrefs.toString();
    }
}
