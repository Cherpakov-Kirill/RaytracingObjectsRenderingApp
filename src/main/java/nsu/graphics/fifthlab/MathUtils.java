package nsu.graphics.fifthlab;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

public class MathUtils {
    public static boolean isNumeric(String string) {
        double value;
        if (string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            value = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Numeric type.");
        }
        return false;
    }

    public static double[][] initRotateXOperator(double deg) {
        double[][] rotateX = new double[4][4];
        rotateX[0][0] = 1;
        rotateX[1][1] = cosDeg(deg);
        rotateX[2][2] = rotateX[1][1];
        rotateX[2][1] = sinDeg(deg);
        rotateX[1][2] = (-1.0) * rotateX[2][1];
        rotateX[3][3] = 1;
        return rotateX;
    }

    public static double[][] initRotateYOperator(double deg) {
        double[][] rotateY = new double[4][4];
        rotateY[0][0] = cosDeg(deg);
        rotateY[0][2] = sinDeg(deg);
        rotateY[1][1] = 1;
        rotateY[2][2] = rotateY[0][0];
        rotateY[2][0] = (-1.0) * rotateY[0][2];
        rotateY[3][3] = 1;
        return rotateY;
    }

    public static double[][] initRotateZOperator(double deg) {
        double[][] rotateZ = new double[4][4];
        rotateZ[0][0] = cosDeg(deg);
        rotateZ[1][1] = rotateZ[0][0];
        rotateZ[1][0] = sinDeg(deg);
        rotateZ[0][1] = (-1.0) * rotateZ[1][0];
        rotateZ[2][2] = 1;
        rotateZ[3][3] = 1;
        return rotateZ;
    }

    public static Point3D rotatePointAroundAxis(double[][] rotateMatrixAroundAxis, Point3D point) {
        double[][] result = matrixMultiplying(rotateMatrixAroundAxis, new double[][]{{point.x()}, {point.y()}, {point.z()}, {1}});
        return new Point3D(result[0][0], result[1][0], result[2][0]);
    }

    public static double[][] matrixMultiplying(double[][] A, double[][] B) {
        int m = A.length;
        int n = B[0].length;
        int o = B.length;
        double[][] res = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < o; k++) {
                    res[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return res;
    }

    public static double cosDeg(double degrees) {
        return cos(toRadians(degrees));
    }

    public static double sinDeg(double degrees) {
        return sin(toRadians(degrees));
    }
}
