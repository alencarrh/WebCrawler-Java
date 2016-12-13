package webcrawler;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import webcrawler.tree.BTree;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:45:53
 */
public class Page implements Comparable<Page> {

	private final int nivel;
	private final Link link;
	private final Page pageOrigem;
	private BTree<Page> childPages;
	private Document html;
	private boolean downloaded;
	private boolean problem;
	private boolean showStackTraceOnErrors;

	public Page(int nivel, Link link, Page pageOrigem) {
		this.nivel = nivel;
		this.link = link;
		this.pageOrigem = pageOrigem;
		this.showStackTraceOnErrors = false;
	}

	public Page(int nivel, Link link, Page pageOrigem, boolean showStackTraceOnErrors) {
		this.nivel = nivel;
		this.link = link;
		this.pageOrigem = pageOrigem;
		this.showStackTraceOnErrors = showStackTraceOnErrors;
	}

	public void downloadPage() {
		try {
			html = Jsoup.connect(link.getStringURL()).get();
		} catch (IOException ex) {
			downloaded = false;
			problem = true;
			if (showStackTraceOnErrors) {
				ex.printStackTrace();
			}
		}

	}

	public String getContentToSave() {
		
	}

	public void createChildPages() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int compareTo(Page o) {
		return this.link.compareTo(o.getLink());
	}

	public void cleanPage() {
		html = null;
	}

	public int getNivel() {
		return nivel;
	}

	public Link getLink() {
		return link;
	}

	public Page getPageOrigem() {
		return pageOrigem;
	}

	public BTree<Page> getChildPages() {
		return childPages;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public boolean isProblem() {
		return problem;
	}

}
