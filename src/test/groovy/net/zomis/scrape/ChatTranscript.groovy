package net.zomis.scrape

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.time.LocalDate

class ChatTranscript {

    static class ChatUser {
        String id
        List<String> knownNames = []
    }

    static class ChatMessage {
        ChatUser user
        String message
        int stars
        boolean pinned
        int reply
    }

    public static void main(String[] args) {
        def transcript = scrape(8595, LocalDate.of(2016, 1, 6), 15, 16)

    }

    static ChatTranscript scrape(int roomId, LocalDate date, int fromHour, int toHour) {
        Document document = Jsoup.connect("http://chat.stackexchange.com/transcript/$roomId/" +
                "$date.year/$date.monthValue/$date.dayOfMonth/$fromHour-$toHour").get()
    }

}
