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

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:48:36
 */
public class DownloadPages {

	public static void main(String[] args) throws MalformedURLException, IOException {
		String site;
		List<HtmlPattern> htmlPatterns = new ArrayList<>();
		HtmlPattern htmlPatternDiv;
		HtmlPattern htmlPatternParagrafo;

//		
		site = "g1.globo.com";

		htmlPatternDiv = new HtmlPattern(".*", "div.materia-conteudo");
		htmlPatternParagrafo = new HtmlPattern(".*\\..*", "p");

		htmlPatterns.add(htmlPatternDiv);
		htmlPatterns.add(htmlPatternParagrafo);

		String startURL = "http://" + site;
		Pattern urlPattern = new Pattern("http.*" + site + ".*");

		FileHandler file = new FileHandler("resultados/", "resultados.txt", false);
		int depth = 1;
		WebCrawler webCrawler = new WebCrawler(startURL, urlPattern, htmlPatterns, file, depth, false	);

		String startTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		System.out.println("Time: " + startTime);

		long timeStart = System.nanoTime();
		webCrawler.startCrawler();
		long timeEnd = System.nanoTime();

		String endTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		System.out.println("Start time: " + startTime + "\nEnd time: " + endTime);

		System.out.println("Tempo de duração: " + ((timeEnd - timeStart) / 1000000) + "ms");

	}
}
