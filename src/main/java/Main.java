import items.NewsItem;
import items.UrlItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webwork.Parser;
import webwork.Requester;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static final String URL = "https://histrf.ru/read/articles";

    public static void main(String[] args) {
        try {
            // Получение основного документа
            Document doc = Requester.getRequest(URL);
            if(doc == null) return;
            
            Element main_div = doc.select("div.flex.flex-wrap").last();
            Elements divs = main_div.select("div.w-full")
                                    .not(".relative")
                                    .not(".aspect-wrap")
                                    .not(".flex")
                                    .not("overflow-hidden");

//            // Создание объекта URL
//            UrlItem item = Parser.parseNote(main_div);
//            System.out.println(item);
//
//            // Получение новости
//            doc = Requester.getRequest(item.getUrl());
//            if(doc == null) return;
//
//            // Парсинг страницы
//            NewsItem news = Parser.parseNews(item, doc);
//            System.out.println(news);

        }
        catch (IOException /*| NoSuchAlgorithmException */ e) {
            e.printStackTrace();
        }
    }
}