package net.zomis.machlearn.images;

public class ZRect {

    public int left;
    public int top;
    public int right;
    public int bottom;

    public ZRect() {
        this(0, 0, 0, 0);
    }

    public ZRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "ZRect{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

    public int width() {
        return this.right - this.left;
    }

    public int height() {
        return this.bottom - this.top;
    }
}
