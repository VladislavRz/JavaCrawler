import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import parser.Requester;

import java.io.IOException;

public class Main {
    public static final String URL = "https://histrf.ru/read/articles";

    public static void main(String[] args) {
        try {
            Document doc = Requester.getRequest(URL);
            String news = Requester.parse(doc);
            System.out.println(news);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}