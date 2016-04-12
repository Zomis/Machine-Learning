package net.zomis.scrape

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.stream.Stream

class Scraping {

    public static void main(String[] args) {
        SearchScraping search = new SearchScraping("programmers", "20298", "125580", 1);

        Scanner scanner = new Scanner(System.in);
        FileOutputStream stream = new FileOutputStream("classifications.txt", true);
        PrintWriter writer = new PrintWriter(stream);
        while (search.hasNextPage()) {
            boolean cont = process(search, scanner, writer);
            if (!cont) {
                println "Terminated on " + search.getURL();
                return;
            }
            search.nextPage()
        }
    }

    static List<String> texts(Document doc) {
        Elements results = doc.select('.message .content .quote')

        // Remove time stamp and comment link
        results.select('span.relativetime').parents().remove()

        List<String> result = new ArrayList<>(results.size());
        // Remove user name and link
        for (def el : results) {
            def rem = el.select('a').last();
            rem?.remove();
            result.add(el.html().replace("\n", ""));
        }
        return result;
    }

    static def process(SearchScraping search, Scanner scanner, PrintWriter writer) {
        Document doc = search.getDocument();
        List<String> results = texts(doc);

        for (String text : results) {
            println text;
            String input = scanner.nextLine();
            int classification = getClassification(input);
            if (classification == -1) {
                return false;
            }
            writer.append(String.format("%d %s%n", classification, text));
            writer.flush()
        }
        return true;
    }

    static int getClassification(String s) {
        if (s.equals("1") || s.equalsIgnoreCase("y")) {
            return 1;
        }
        if (s.equals("0") || s.equalsIgnoreCase("n")) {
            return 0;
        }
        if (s.equals("q")) {
            return -1;
        }
        return 2;
    }
}
