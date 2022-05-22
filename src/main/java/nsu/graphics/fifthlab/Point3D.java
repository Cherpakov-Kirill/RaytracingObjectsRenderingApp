package nsu.graphics.fifthlab;

public class Point3D {
    private double x;
    private double y;
    private double z;

    public Point3D(Point3D point3D) {
        this.x = point3D.x();
        this.y = point3D.y();
        this.z = point3D.z();
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(Point3D point1, Point3D point2) {
        this.x = point1.x() - point2.x();
        this.y = point1.y() - point2.y();
        this.z = point1.z() - point2.z();
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public void mulCoordinates(double mul) {
        this.x *= mul;
        this.y *= mul;
        this.z *= mul;
    }

    public void addDelta(Point3D delta) {
        this.x += delta.x;
        this.y += delta.y;
        this.z += delta.z;
    }

    public void addDelta(Vector vector) {
        Point3D delta = vector.getVertex();
        this.x += delta.x;
        this.y += delta.y;
        this.z += delta.z;
    }

    public void subDelta(Point3D delta) {
        this.x -= delta.x;
        this.y -= delta.y;
        this.z -= delta.z;
    }

    public void subDelta(Vector vector) {
        Point3D delta = vector.getVertex();
        this.x -= delta.x;
        this.y -= delta.y;
        this.z -= delta.z;
    }

    @Override
    public String toString(){
        return "{"+x+","+y+","+z+"}";
    }

    public static Point3D getDelta(Point3D point1, Point3D point2) {
        if (point1 == null && point2 != null) {
            return point2;
        }
        if (point1 != null && point2 == null) {
            return point1;
        }
        if (point1 == null && point2 == null) {
            return null;
        }
        return new Point3D(Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y), Math.abs(point1.z - point2.z));
    }

    public static Point3D minOnAllCoordinates(Point3D point1, Point3D point2) {
        if (point1 == null && point2 != null) {
            return point2;
        }
        if (point1 != null && point2 == null) {
            return point1;
        }
        if (point1 == null && point2 == null) {
            return null;
        }
        return new Point3D(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y), Math.min(point1.z, point2.z));
    }

    public static Point3D maxOnAllCoordinates(Point3D point1, Point3D point2) {
        if (point1 == null && point2 != null) {
            return point2;
        }
        if (point1 != null && point2 == null) {
            return point1;
        }
        if (point1 == null && point2 == null) {
            return null;
        }
        return new Point3D(Math.max(point1.x, point2.x), Math.max(point1.y, point2.y), Math.max(point1.z, point2.z));
    }
}
