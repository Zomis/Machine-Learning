package net.zomis.scrape

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

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

    static def process(SearchScraping search, Scanner scanner, PrintWriter writer) {
        Document doc = search.getDocument();
        def results = doc.select('.message .content .quote')

        // Remove time stamp and comment link
        results.select('span.relativetime').parents().remove()

        // Remove user name and link
        for (def el : results) {
            el.select('a').last().remove()
        }

        for (Element element : results) {
            String text = element.html().replace("\n", "");
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
