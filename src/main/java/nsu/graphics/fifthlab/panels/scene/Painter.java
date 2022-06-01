package nsu.graphics.fifthlab.panels.scene;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.render.Render;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter {
    public static final int screenIncreaseSize = 1;
    private final BufferedImage bufferedImage;
    private final Render render;
    private final Graphics2D graphics;
    private final Dimension imSize;

    public Painter(BufferedImage img, Color objectColor, Render render) {
        graphics = img.createGraphics();
        this.bufferedImage = img;
        graphics.setColor(objectColor);
        imSize = new Dimension(img.getWidth(), img.getHeight());
        this.render = render;
    }

    public void drawLine(Point start, Point finish) {
        int startX = getXOnImage(start.x);
        int startY = getYOnImage(start.y);
        int finishX = getXOnImage(finish.x);
        int finishY = getYOnImage(finish.y);
        graphics.drawLine(startX, startY, finishX, finishY);
    }

    public void setRenderRGB(Color color, int x, int y) {
        int xScreen = getXOnImage(x);
        int yScreen = getYOnImage(y);
        try {
            bufferedImage.setRGB(xScreen, yScreen, color.getRGB());
        } catch (Exception e) {
            System.err.println(xScreen + " " + yScreen);
            throw e;
        }

    }

    public void drawRenderOnScreen() {
        /*graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(renderImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics.dispose();*/
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public Point3D getCamPosition() {
        return render.getCamPosition();
    }

    public Vector getUpCamVector() {
        return render.getUpVector();
    }

    public Vector getViewVector() {
        return render.getViewVector();
    }

    public double getDistanceToProjection() {
        return render.getDistanceToProjection();
    }

    private int getXOnImage(int x) {
        return imSize.width / 2 - x;
    }

    private int getYOnImage(int y) {
        return imSize.height / 2 - y;
    }
}
