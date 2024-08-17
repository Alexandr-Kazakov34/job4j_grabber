package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int PAGE = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

/*    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse(parse -> LocalDateTime.now());
        String startLink = String.format("%s%s%s", SOURCE_LINK, PREFIX, SUFFIX);
        List<Post> posts = habrCareerParse.list(startLink);
        posts.forEach(System.out::println);
    }*/

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element desc = document.select(".basic-section--appearance-vacancy-description").first();
        return desc != null ? desc.text() : "Описание не найдено";
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> rsl = new ArrayList<>();
        for (int i = PAGE; i > 0; i--) {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                String fullLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                rsl.add(new Post(1, titleElement.text(),
                        fullLink,
                        retrieveDescription(fullLink),
                        dateTimeParser.parse(dateElement.child(0).attr("datetime"))));
            }
        }
        return rsl;
    }
}