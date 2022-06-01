package nsu.graphics.fifthlab;


import java.awt.*;

public class RayColor {
    private double red;
    private double green;
    private double blue;

    public RayColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RayColor(RayColor rayColor) {
        this.red = rayColor.red;
        this.green = rayColor.green;
        this.blue = rayColor.blue;
    }

    public void mulByCoefficient(double coefficient) {
        red *= coefficient;
        green *= coefficient;
        blue *= coefficient;
    }

    public void addColor(RayColor rayColor) {
        red += rayColor.red;
        green += rayColor.green;
        blue += rayColor.blue;
    }

    public void scalarProd(RayColor rayColor) {
        red *= rayColor.red;
        green *= rayColor.green;
        blue *= rayColor.blue;
    }

    public Color toColor() {
        int r = Math.min((int) Math.round(red), 255);
        int g = Math.min((int) Math.round(green), 255);
        int b = Math.min((int) Math.round(blue), 255);
        return new Color(r, g, b);
    }


    public int getIntRed() {
        return (int) Math.round(red);
    }

    public int getIntGreen() {
        return (int) Math.round(green);
    }

    public int getIntBlue() {
        return (int) Math.round(blue);
    }
}
