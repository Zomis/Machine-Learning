package net.zomis.machlearn.images;

public class ZRect {

    public int left;
    public int top;
    public int right;
    public int bottom;

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
