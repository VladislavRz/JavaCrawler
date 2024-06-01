import items.NewsItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import parser.Parser;
import parser.Requester;

import java.io.IOException;

public class Main {
    public static final String URL = "https://histrf.ru/read/articles";
    public static final String DOMAIN = "https://histrf.ru";

    public static void main(String[] args) {
        try {
            // Получение основного документа
            Document doc = Requester.getRequest(URL);
            Element main_div = doc.select("div.flex.flex-wrap").last();
            String link = main_div.select("a").first().attr("href");

            // Получение новости
            doc = Requester.getRequest(DOMAIN + link);

            // Парсинг страницы
            NewsItem news = Parser.parse(DOMAIN + link, doc);
            System.out.println(news.toString());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}