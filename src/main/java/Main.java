import items.NewsItem;
import items.UrlItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import webwork.Parser;
import webwork.Requester;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static final String URL = "https://histrf.ru/read/articles";
    public static final String DOMAIN = "https://histrf.ru";

    public static void main(String[] args) {
        try {
            // Получение основного документа
            Document doc = Requester.getRequest(URL);
            if(doc == null) return;

            Element main_div = doc.select("div.flex.flex-wrap").last();
            String title = main_div.select("h2").first().text();
            String uri = main_div.select("a").first().attr("href");

            // Создание объекта URL
            UrlItem item = new UrlItem();
            item.setUrl(DOMAIN + uri);
            item.setTitle(title);
            item.setHash(UrlItem.calcHash(item.getUrl() + ":" + item.getTitle()));
            System.out.println(item);

            // Получение новости
            doc = Requester.getRequest(item.getUrl());
            if(doc == null) return;

            // Парсинг страницы
            NewsItem news = Parser.parse(item, doc);
            System.out.println(news);

        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}