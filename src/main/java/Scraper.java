import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;

public class Scraper {

    public static void main(String[] args){
        String url = "https://www.flashscore.pl";

        try {
            Document document = Jsoup.connect(url).get();
            Elements matches = document.select("#g_1_Q9YPy46c");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
