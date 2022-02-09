
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {
    private static final int MAX_DEPTH = 5;
    private HashSet<String> links;
    int fileCount;

    public Crawler() {
        links = new HashSet<>();
        fileCount = 0;
    }


    public void writeToFile(String filename, String title, Elements paragraphs) {

        try {
            FileWriter writer = new FileWriter(filename);

            writer.write(title);
            writer.write("\n");
            for (Element p : paragraphs) {
                writer.write(p.text());
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void getPageLinks(String URL, int depth) {
        try {
            final Connection.Response response = Jsoup.connect(URL).execute();
            final Document doc = response.parse();
            String title = doc.title();
            //System.out.println(title);

            /*
            Elements wikiDataItems = doc.select("a[href^=\"https://www.wikidata.org/wiki/Special:EntityPage/\"]");
            String uniqueURL = "";
            for (Element item : wikiDataItems) {
                uniqueURL = item.attr("abs:href").split("#")[0];
                //System.out.println("URL: "+" [" + URL + "], "+"UniqueURL: "+uniqueURL);
                break;
            }

             */
            if ((!links.contains(title) && (depth <= MAX_DEPTH))) {
                links.add(title);
                System.out.println(depth);
                //String body = doc.body().text();
                Elements paragraphs = doc.select("p");
                if (fileCount > 100000) {
                    return;
                }
                writeToFile("crawledPages/" + title + ".txt", title, paragraphs);

                fileCount++;
                //Document document = Jsoup.connect(URL).get(); //create a file for each page

                Elements linksOnPage =doc.select("p a[href]");
                depth++;
                for (Element page : linksOnPage) {
                    String matchedURL = page.attr("abs:href");
                    //if (page.text().matches("^.*?(Java 8|java 8|JAVA 8).*$"))
                    Pattern pattern = Pattern.compile("^https://en.wikipedia.org", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(matchedURL);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            matchedURL = matchedURL.split("#")[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getPageLinks(matchedURL, depth);
                    }

                }

            }
        } catch (Exception e) {
            //System.err.println("For '" + URL + "': " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Crawler().getPageLinks("https://en.wikipedia.org/wiki/Novel", 1);
    }
}