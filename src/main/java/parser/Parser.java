package parser;

import items.NewsItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
    public static NewsItem parse(String url, Document doc) {
        NewsItem item = new NewsItem();
        String text = "";
        String title;
        String date;
        Element body;
        Elements nodes;

        // Получение заголовка
        title = doc.select("h1").first().text();

        // Получение текста публикации
        body = doc.select("div.page-body").first();
        nodes = body.select("p");
        for(Element node : nodes) {
            text = text + node.text() + "\n";
        }

        // Получение даты публикации
        date = doc.select("div.absolute").last()
                  .select("div.absolute").last().text();

        // Инициализация полей новости
        item.setUrl(url);
        item.setBody(text);
        item.setTitle(title);
        item.setDate(date);

        return item;
    }
}
