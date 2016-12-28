package webcrawler;

import webcrawler.fileHandler.FileHandler;
import webcrawler.pattern.HtmlPattern;
import webcrawler.pattern.Pattern;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:48:36
 */
public class DownloadPages {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Deve-se passar somente 1 parâmetro!");
            return;
        }
        String startTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        WebCrawler webCrawler = null;
        try {
            String site;
            List<HtmlPattern> htmlPatterns = new ArrayList<>();
            HtmlPattern htmlPatternDiv;
            HtmlPattern htmlPatternParagrafo;

            //
            site = "g1.globo.com";

            //Jsoup tag for "document.select(YOUR_TAG_HERE)"
            htmlPatternDiv = new HtmlPattern(".*", "div.materia-conteudo");
            htmlPatternParagrafo = new HtmlPattern(".*\\..*", "p");

            htmlPatterns.add(htmlPatternDiv);
            htmlPatterns.add(htmlPatternParagrafo);

            String startURL = "http://" + site;
            Pattern urlPattern = new Pattern("http.*" + site + ".*");

            FileHandler file = new FileHandler("resultados/", "resultados.txt", false);
            int depth = Integer.valueOf(args[0]);
            webCrawler = new WebCrawler(startURL, urlPattern, htmlPatterns, file, depth, false);

            System.out.println("Time: " + startTime);

            long timeStart = System.nanoTime();
            webCrawler.startCrawler();
            long timeEnd = System.nanoTime();

            String endTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            System.out.println("Start time: " + startTime + "\nEnd time: " + endTime);

            System.out.println("Tempo de duração: " + ((timeEnd - timeStart) / 1000000) + "ms");
        } catch (MalformedURLException ex) {
            printStackTrace(ex, webCrawler, startTime);
        } catch (IOException ex) {
            printStackTrace(ex, webCrawler, startTime);
        }
    }

    public static void printStackTrace(Exception ex, WebCrawler webCrawler, String startTime) {
        ex.printStackTrace();
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        if (webCrawler != null) {
            System.out.println("Links Verificados: " + webCrawler.getLinksVerificados());
        }
    }
}
