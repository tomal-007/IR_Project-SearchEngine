
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {
    private static final int MAX_DEPTH = 1;
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

    private static void downloadImage(String strImageURL, String title, int imageCounter) {

        //get file name from image path
        String strImageName =
                strImageURL.substring(strImageURL.lastIndexOf("/") + 1);

        System.out.println("Saving: " + strImageName + ", from: " + strImageURL);

        try {

            //open the stream from URL
            URL urlImage = new URL(strImageURL);
            InputStream in = urlImage.openStream();

            byte[] buffer = new byte[4096];
            int n = -1;

            OutputStream os =
                    new FileOutputStream("crawledPages/" + title + "_" + imageCounter + "-4321-" + strImageName);

            //write bytes to the output stream
            while ((n = in.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }

            //close the stream
            os.close();

            System.out.println("Image saved");

        } catch (IOException e) {
            e.printStackTrace();
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

                if (fileCount > 100000) {
                    return;
                }
                // text file
                Elements paragraphs = doc.select("p");
                writeToFile("crawledPages/" + title + ".txt", title, paragraphs);

                // download the images
                //select all img tags
                Elements imageElements = doc.select("img");

                int imageCounter = 1;
                //iterate over each image
                for (Element imageElement : imageElements) {

                    //make sure to get the absolute URL using abs: prefix
                    String strImageURL = imageElement.attr("abs:src");

                    //download image one by one
                    downloadImage(strImageURL, title, imageCounter);
                    imageCounter++;
                }

                fileCount++;
                //Document document = Jsoup.connect(URL).get(); //create a file for each page

                Elements linksOnPage = doc.select("p a[href]");
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