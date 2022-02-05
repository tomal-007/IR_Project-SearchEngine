import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class WebCrawlerWithDepth {
    private static final int MAX_DEPTH = 2;
    private HashSet<String> links;

    public WebCrawlerWithDepth() {
        links = new HashSet<>();
    }

    public void getPageLinks(String URL, int depth) {
        if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
            System.out.println(">> Depth: " + depth + " [" + URL + "]");
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");
                //Elements otherLinks = document.select("a[href^=\"http://www.mkyong.com/page/\"]");

                depth++;
                for (Element page : linksOnPage) {
                    //if (page.text().matches("^.*?(Java 8|java 8|JAVA 8).*$"))
                    getPageLinks(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }



    public static void main(String[] args) {
        new WebCrawlerWithDepth().getPageLinks("https://en.wikipedia.org/wiki/Main_Page", 0);
    }
}