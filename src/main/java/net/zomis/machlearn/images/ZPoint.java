package net.zomis.machlearn.images;

public class ZPoint {

    private final int x;
    private final int y;

    public ZPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isInside(int left, int top, int right, int bottom) {
        return (this.x >= left && this.x <= right && this.y >= top && this.y <= bottom);
    }

    @Override
    public String toString() {
        return "Point{" + "x=" + x + ", y=" + y + '}';
    }
}
