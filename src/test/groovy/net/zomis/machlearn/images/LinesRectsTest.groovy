package net.zomis.machlearn.images

import org.junit.Test

class LinesRectsTest {

    @Test
    void testRectsFromLines() {
        def xLines = [47, 285, 286, 287, 320, 321, 322, 354, 388, 422, 455, 456, 489, 490, 1296]
        def yLines = [72, 73, 74, 107, 108, 141, 175, 209, 242, 243, 276]
        def rects = BineroScan.createRectsFromLines(xLines, yLines);
        assert rects != null;
        assert rects.length == 6;
        for (int y = 0; y < rects.length; y++) {
            assert rects[y][0].left == 287;
            assert rects[y][0].right == 320;

            assert rects[y][1].left == 322;
            assert rects[y][1].right == 354;

            assert rects[y][2].left == 354;
            assert rects[y][2].right == 388;

            assert rects[y][3].left == 388;
            assert rects[y][3].right == 422;

            assert rects[y][4].left == 422;
            assert rects[y][4].right == 455;

            assert rects[y][5].left == 456;
            assert rects[y][5].right == 489;
        }
        for (int x = 0; x < rects.length; x++) {
            assert rects[x].length == 6;

            assert rects[0][x].top == 74;
            assert rects[0][x].bottom == 107;

            assert rects[1][x].top == 108;
            assert rects[1][x].bottom == 141;

            assert rects[2][x].top == 141;
            assert rects[2][x].bottom == 175;

            assert rects[3][x].top == 175;
            assert rects[3][x].bottom == 209;

            assert rects[4][x].top == 209;
            assert rects[4][x].bottom == 242;

            assert rects[5][x].top == 243;
            assert rects[5][x].bottom == 276;

        }
    }

}
