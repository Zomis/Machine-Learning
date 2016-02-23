package net.zomis.scrape

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Scraping {

    public static void main(String[] args) {
        SearchScraping search = new SearchScraping("programmers", "20298", "125580");

        Document doc = search.getDocument();
        def results = doc.select('.message .content .quote')

        // Remove time stamp and comment link
        results.select('span.relativetime').parents().remove()

        // Remove user name and link
        for (def el : results) {
            el.select('a').last().remove()
        }

        for (Element element : results) {
            println element.html().replace("\n", "")
        }

    }

}
