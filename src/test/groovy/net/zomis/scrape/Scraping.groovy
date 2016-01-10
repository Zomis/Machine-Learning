package net.zomis.scrape

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Scraping {

    public static void main(String[] args) {
        String search = "programmers"
        Document doc = Jsoup.connect("http://chat.stackexchange.com/search?q=" + search +
                "&Room=20298&User=125580&pagesize=50&sort=newest").get();
        def results = doc.select('.message .content .quote')

        // Remove time stamp and comment link
        results.select('span.relativetime').parents().remove()

        // Remove user name and link
        for (def el : results) {
            el.select('a').last().remove()
        }

        println '------'
        def first = results.first()
        println first.html()
    }

}
