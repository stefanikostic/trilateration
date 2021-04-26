import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Point3D {
    public double x;
    public double y;
    public double z;
    public double r;
    public boolean anchorFlag;
    public int stepen;
    List<Point3D> anchors;

    public Point3D (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stepen = 0;
        anchors = new ArrayList<>();
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D node = (Point3D) o;
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
