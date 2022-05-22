package nsu.graphics.fifthlab;

public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void setPoint(Point3D point){
        x = point.x();
        y = point.y();
        z = point.z();
    }

    public Vector(Vector vector) {
        setPoint(vector.getVertex());
    }

    public Vector(Point3D point3D) {
        setPoint(point3D);
    }

    public Vector(Point3D finish, Point3D start) {
        this.x = finish.x() - start.x();
        this.y = finish.y() - start.y();
        this.z = finish.z() - start.z();
    }

    private void rotate(double[][] rotateMatrix){
        Point3D point = MathUtils.rotatePointAroundAxis(rotateMatrix,getVertex());
        setPoint(point);
    }

    public void rotateAroundXAxis(double deg) {
        rotate(MathUtils.initRotateXOperator(deg));
    }

    public void rotateAroundYAxis(double deg) {
        rotate(MathUtils.initRotateYOperator(deg));
    }

    public void rotateAroundZAxis(double deg) {
        rotate(MathUtils.initRotateZOperator(deg));
    }

    public void mulCoordinates(double mul) {
        this.x *= mul;
        this.y *= mul;
        this.z *= mul;
    }

    public Point3D getVertex() {
        return new Point3D(x, y, z);
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double getDegreeBetween(Vector vector) {
        double scalarMul = getScalarMul(this, vector);
        double cos = scalarMul / (getLength() * vector.getLength());
        return Math.toDegrees(Math.acos(cos));
    }

    @Override
    public String toString(){
        return "{"+x+","+y+","+z+"}";
    }

    public void normalize() {
        mulCoordinates(1 / getLength());
    }

    public static double getScalarMul(Vector vector1, Vector vector2) {
        Point3D last1 = vector1.getVertex();
        Point3D last2 = vector2.getVertex();
        double mulX = last1.x() * last2.x();
        double mulY = last1.y() * last2.y();
        double mulZ = last1.z() * last2.z();
        return mulX + mulY + mulZ;
    }

    public static Vector vectorMultiply(Vector vector1, Vector vector2) {
        double x = vector1.y * vector2.z - vector1.z * vector2.y;
        double y = vector1.z * vector2.x - vector1.x * vector2.z;
        double z = vector1.x * vector2.y - vector1.y * vector2.x;
        return new Vector(x, y, z);
    }

    public static Vector vectorSubtraction(Vector reduced, Vector deductible) {
        return new Vector(reduced.x - deductible.x, reduced.y - deductible.y, reduced.z - deductible.z);
    }
}
