package net.zomis.machlearn.text.duga

import net.zomis.machlearn.text.BagOfWords
import org.junit.Test

import java.util.stream.Collectors

class ProgrammersCommentTest {

    @Test
    void commentLearning() {
        String source = getClass().getClassLoader().getResource('trainingset-programmers-comments.txt').text
        def lines = source.split('\n')
        BagOfWords bowYes = new BagOfWords()
        BagOfWords bowNo  = new BagOfWords()
        BagOfWords bowAll = new BagOfWords()
        for (String str : lines) {
            if (!str.startsWith('0 ') && !str.startsWith('1 ')) {
                continue
            }
            boolean expected = str.startsWith('1')
            String text = str.substring(2)
            BagOfWords bow = expected ? bowYes : bowNo
            bow.addText(text)
            bowAll.addText(text)
            // println text
        }
        println bowAll.data
        println '-------------'
        println bowYes.data
        println '-------------'
        println bowNo.data
        println '-------------'

//        List<Map.Entry<String, Integer>> stream = bowAll.getData().entrySet().stream()
//            .sorted(Comparator.comparing({it.value}))
//            .collect(Collectors.toList())
////        stream.forEach({System.out.println(it)})
//        println 'Count ' + stream.size()
    }

}
