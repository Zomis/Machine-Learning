package net.zomis.scrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class SearchScraping {

    private String nextLink;
    private Document document;

    public SearchScraping(String query, String room, String user) throws IOException {
        String url = "/search?q=" + query;
        if (room != null) {
            url += "&Room=" + room;
        }
        if (user != null) {
            url += "&User=" + user;
        }
        url += "&pagesize=50&sort=newest";

        fetch(url);
    }

    private void fetch(String url) throws IOException {
        url = "http://chat.stackexchange.com" + url;

        this.document = Jsoup.connect(url).get();

        Element els = document.select(".pager a").last();
        String nextLink = els.attr("href");
        String linkName = els.select("span").html().trim();
        this.nextLink = linkName.equals("next") ? nextLink : null;
    }

    public boolean nextPage() throws IOException {
        if (nextLink == null) {
            return false;
        }
        fetch(nextLink);
        return true;
    }

    public Document getDocument() {
        return document;
    }

}
