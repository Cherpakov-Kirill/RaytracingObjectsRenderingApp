package nsu.graphics.fifthlab.panels.scene;

import nsu.graphics.fifthlab.render.Render;
import nsu.graphics.fifthlab.render.RenderListener;
import nsu.graphics.fifthlab.scene.Scene;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ScenePanel extends JPanel implements RenderListener, MouseWheelListener, MouseListener, MouseMotionListener, KeyListener {
    private final Dimension panelSize;          // visible image size
    private BufferedImage img;                  // image to view
    private Painter painter;                  // image to view
    private final JScrollPane spIm;
    private int lastX = 0, lastY = 0;           // last captured mouse coordinates
    private Scene scene;
    private Render render;
    private Color objectColor;

    private boolean ctrlIsPressed;

    /**
     * Creates default Object viewer panel.
     * Visible space will be painted in black.
     * <p>
     *
     * @param scrollPane  - JScrollPane to add a new Image-viewer
     * @param width       - start width of panel
     * @param height      - start height of panel
     * @param objectColor - color of an object on screen
     */
    public ScenePanel(JScrollPane scrollPane, int width, int height, int objectColor) {
        spIm = scrollPane;
        spIm.setWheelScrollingEnabled(false);
        spIm.setDoubleBuffered(true);
        spIm.setViewportView(this);
        panelSize = new Dimension(width, height);
        spIm.validate();
        img = createEmptyImage(width, height);
        this.objectColor = new Color(objectColor);
        //openSceneFile(new File("/home/kirill/EngineeringGraphics/EG_lab5/build/classes/java/Test1.scene"));
        //setDefaultRender();

        ctrlIsPressed = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (img != null) g2d.drawImage(img, 0, 0, panelSize.width, panelSize.height, null);
    }

    public void openSceneFile(File file) {
        scene = new Scene(file);
    }

    public void setDefaultRender() {
        render = new Render(this, scene);
        drawWireframeObjects();
    }

    public void openRenderFile(File file) {
        render = new Render(this, file, scene);
        drawWireframeObjects();
    }

    public void saveImage(File file) {
        try {
            String filename = file.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            System.out.println(extension);
            ImageIO.write(img, extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRenderSettings(File file) {
        render.saveSettings(file);
    }

    public void setObjectColor(Color color) {
        objectColor = color;
        drawWireframeObjects();
    }

    public void normalizeObjectPosition() {
        render.initCam(scene);
        selectView();
    }

    public void selectView() {
        drawWireframeObjects();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void renderObjects() {
        img = createEmptyImage(panelSize.width, panelSize.height);
        render.renderScene(painter);
        repaint();
        revalidate();
        spIm.validate();
        spIm.repaint();
        removeMouseListener(this);
        removeMouseMotionListener(this);
        removeMouseWheelListener(this);
    }

    private void drawWireframeObjects() {
        img = createEmptyImage(panelSize.width, panelSize.height);
        scene.drawPrimitives(painter);
        repaint();
        revalidate();
        spIm.validate();
        spIm.repaint();
    }

    private BufferedImage createEmptyImage(int width, int height) {
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        //g.setColor(objectColor);
        g.dispose();
        painter = new Painter(newImg, objectColor, render);
        repaint();
        return newImg;
    }

    public void componentResized() {
        int width = getWidth();
        int height = getHeight();
        int newImgWidth = Math.max(width, panelSize.width);
        int newImgHeight = Math.max(height, panelSize.height);
        if (newImgWidth > panelSize.width || newImgHeight > panelSize.height) {
            BufferedImage newResizedImage = new BufferedImage(newImgWidth, newImgHeight, BufferedImage.TYPE_INT_ARGB);
            setPanelSize(newImgWidth, newImgHeight);
            Graphics2D g = newResizedImage.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(img, (newImgWidth - img.getWidth()) / 2, (newImgHeight - img.getHeight()) / 2, img.getWidth(), img.getHeight(), null);
            g.dispose();
            img = newResizedImage;
            repaint();
        }
        setScrollOnMiddle();
    }

    private void setScrollOnMiddle() {
        Dimension bounds = getVisibleRectSize();

        JScrollBar horizontal = spIm.getHorizontalScrollBar();
        horizontal.setValue((horizontal.getMaximum() - bounds.width) / 2);

        JScrollBar vertical = spIm.getVerticalScrollBar();
        vertical.setValue((vertical.getMaximum() - bounds.height) / 2);
        spIm.repaint();
    }

    ///SIZE OF PANEL
    private void setPanelSize(int width, int height) {
        panelSize.width = width;
        panelSize.height = height;
    }

    private Dimension getVisibleRectSize() {
        // maximum size for panel without scrolling (inner border of the ScrollPane)
        Dimension viewportSize = spIm.getViewport().getSize();
        if (viewportSize.height == 0) return new Dimension(spIm.getWidth() - 3, spIm.getHeight() - 3);
        else return viewportSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return panelSize;
    }

    @Override
    public Dimension getUserPanelSize() {
        return getPreferredSize();
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (ctrlIsPressed) render.zoom(e.getWheelRotation());
        else render.mulDistanceToProjection(e.getWheelRotation());
        drawWireframeObjects();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("Pressed: " + e.getKeyCode());
        if (e.getKeyCode() == 17) {
            ctrlIsPressed = true;
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            render.moveRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            render.moveLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            render.moveUp();
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            render.moveDown();
        }
        drawWireframeObjects();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Released: " + e.getKeyCode());
        if (e.getKeyCode() == 17) {
            ctrlIsPressed = false;
        }
    }

    private void updateMousePoint(Point point) {
        lastX = (int) point.getX();
        lastY = (int) point.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        updateMousePoint(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int currDegreeZ = lastX - e.getX();
        int currDegreeY = e.getY() - lastY;
        render.rotateCam(currDegreeZ, currDegreeY);
        updateMousePoint(e.getPoint());
        drawWireframeObjects();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }


    ///NOT USED
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}