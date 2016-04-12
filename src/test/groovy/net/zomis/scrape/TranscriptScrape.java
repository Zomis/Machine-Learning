package net.zomis.scrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranscriptScrape {

    public static void main(String[] args) throws IOException {
        System.out.println("Enter chat message id or URL");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        Matcher matcher = Pattern.compile("\\d+$").matcher(line.trim());
        if (!matcher.find()) {
            System.out.println("No match found");
            return;
        }
        long messageId = Long.parseLong(matcher.group());
        String url = "http://chat.stackexchange.com/transcript/message/" +
            messageId + "#" + messageId;
        System.out.println("Fetching URL " + url);
        Document doc = Jsoup.connect(url).get();
        List<String> texts = Scraping.texts(doc);
        texts.forEach(System.out::println);
    }

}
