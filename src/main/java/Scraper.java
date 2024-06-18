import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {

    private String team;
    private String score;

    public String toString(){
        return team + score;

    }



    public static void main(String[] args) {

        Document document;
        try {
            document = Jsoup.connect("https://www.flashscore.pl/tabela/ABkrguJ9/EcpQtcVi/#/EcpQtcVi/table").get();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Scraper> products = new ArrayList<>();
        Elements matches = document.select(".tableWrapper");

        for (Element productElement : matches) {
            Scraper product = new Scraper();




        }
    }
    }
