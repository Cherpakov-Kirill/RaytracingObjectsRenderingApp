package nsu.graphics.fifthlab;


import java.awt.*;

public class RayColor {
    private double reb;
    private double green;
    private double blue;

    public RayColor(double reb, double green, double blue) {
        this.reb = reb;
        this.green = green;
        this.blue = blue;
    }

    public RayColor(RayColor rayColor){
        this.reb = rayColor.reb;
        this.green = rayColor.green;
        this.blue = rayColor.blue;
    }

    public void mulByCoefficient(double coefficient) {
        reb *= coefficient;
        green *= coefficient;
        blue *= coefficient;
    }

    public void addColor(RayColor rayColor) {
        reb += rayColor.reb;
        green += rayColor.green;
        blue += rayColor.blue;
    }

    public void scalarProd(RayColor rayColor) {
        reb *= rayColor.reb;
        green *= rayColor.green;
        blue *= rayColor.blue;
    }

    public Color toColor() {
        return new Color((int) Math.round(reb),
                (int) Math.round(green),
                (int) Math.round(blue));
    }


    public int getIntRed() {
        return (int) Math.round(reb);
    }

    public int getIntGreen() {
        return (int) Math.round(green);
    }

    public int getIntBlue() {
        return (int) Math.round(blue);
    }
}
