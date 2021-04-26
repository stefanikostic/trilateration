import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Point {
    public double x;
    public double y;
    public double r;
    public boolean anchorFlag;
    public int stepen;
    List<Point> anchors;

    public Point (double x, double y) {
        this.x = x;
        this.y = y;
        this.stepen = 0;
        anchors = new ArrayList<>();
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point node = (Point) o;
        return Double.compare(node.x, x) == 0 &&
                Double.compare(node.y, y) == 0;
    }

    @Override
    public int hashCode () {
        return Objects.hash(x, y);
    }

    public int getStepen () {
        return stepen;
    }

    public double getR () {
        return r;
    }

    public int getAnchorsSize () {
        return anchors.size();
    }
}
