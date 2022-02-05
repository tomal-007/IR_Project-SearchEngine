
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


public class Crawler {

    public void writeToFile(String filename, String temp) {
        FileWriter writer;
        try {
            writer = new FileWriter(filename);
            try {
                //save to file
                writer.write(temp);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        //Document doc;
        try {

            // need http protocol
            /*
            doc = Jsoup.connect("http://google.com").get();

            // get page title
            String title = doc.title();
            System.out.println("title : " + title);

            // get all links
            Elements links = doc.select("a[href]");
            Elements x= doc.outerHtml();
            String temp= x.text();
            for (Element link : links) {

                // get the value from href attribute
                System.out.println("\nlink : " + link.attr("href"));
                System.out.println("text : " + link.text());

            }
            FileWriter writer;
            String filename="abcd.txt";
            writer = new FileWriter(filename);
            try {
                writer.write(temp);
                writer.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            */
            final Connection.Response response = Jsoup.connect("https://en.wikipedia.org/wiki/World_War_II").execute();
            final Document doc = response.parse();

            final File f = new File("filename.html");
            FileUtils.writeStringToFile(f, doc.outerHtml(), StandardCharsets.UTF_8);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
