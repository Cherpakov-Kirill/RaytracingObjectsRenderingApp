package nsu.graphics.fifthlab.render;

import java.awt.*;

public class ColorUtils {
    public static Color mulColor(Color color, double coefficient) {
        return new Color(Math.round(color.getRed() * coefficient),
                Math.round(color.getBlue() * coefficient),
                Math.round(color.getGreen() * coefficient));
    }

    public static Color mulColor(Color color, Color coefficients) {
        return new Color(Math.round(color.getRed() * coefficients.getRed()),
                Math.round(color.getBlue() * coefficients.getBlue()),
                Math.round(color.getGreen() * coefficients.getGreen()));
    }

    public static Color addColor(Color color1, Color color2) {
        return new Color(Math.round(color1.getRed() + color2.getRed()),
                Math.round(color1.getBlue() + color2.getBlue()),
                Math.round(color1.getGreen() + color2.getGreen()));
    }
}
