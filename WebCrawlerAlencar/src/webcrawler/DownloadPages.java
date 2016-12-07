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
//        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
//            showHelp();
//            return;
//        }
//
//        if (args.length < 2) {
//            System.err.println("Erro: São necessários 2 parâmtros para a execução.['help' para ajuda]");
//            System.exit(1);
//        }

		String site;
		List<HtmlPattern> htmlPatterns = new ArrayList<>();
		HtmlPattern htmlPatternDiv;
		HtmlPattern htmlPatternParagrafo;

		String opcao = args[0];
		switch (opcao) {
			case "1":
				site = "unisinos.br";
				htmlPatternDiv = new HtmlPattern(".*<p>.*</p>.*", "<div class=\"article\">", "<div class=\"share-bar\">");
				htmlPatternParagrafo = new HtmlPattern(".*\\..*", "<p>", "</p>");
				break;
			case "2":
				site = "g1.globo.com";
				htmlPatternDiv = new HtmlPattern(".*<p>.*</p>.*", "class=\"materia-conteudo entry-content clearfix\" id=\"materia-letra\">", "<div class=\"lista-de-entidades\">");
				htmlPatternParagrafo = new HtmlPattern(".*\\..*", "<p>", "</p>");
				break;
			default:
				site = opcao;
				htmlPatternDiv = new HtmlPattern(".*", "", "");
				htmlPatternParagrafo = new HtmlPattern(".*", "", "");
				break;
		}

		htmlPatterns.add(htmlPatternDiv);
		htmlPatterns.add(htmlPatternParagrafo);

		String startURL = "http://" + site;
		Pattern urlPattern = new Pattern("http.*" + site + ".*");
		FileHandler file = new FileHandler(args.length >= 3 ? args[2] : "resultados.txt", false);

		WebCrawler webCrawler = new WebCrawler(startURL, urlPattern, htmlPatterns, file, Integer.valueOf(args[1]));

		String startTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		System.out.println("Time: " + startTime);
		long timeStart = System.nanoTime();

		webCrawler.startCrawler();

		long timeEnd = System.nanoTime();
		String endTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		System.out.println("Start time: " + startTime + "\nEnd time: " + endTime);

		System.out.println("Tempo de duração: " + ((timeEnd - timeStart) / 1000000) + "ms");

	}

	private static void showHelp() {
		System.out.println("HELP");
	}

}
