package nsu.graphics.fifthlab.windows;

import nsu.graphics.fifthlab.panels.parameters.ParametersListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class RenderSettings extends JFrame implements ParametersListener, ComponentListener {
    private static final int numberOfSegmentsPerInterval = 2;
    //private final ParametersPanel parametersPanel;
    private final TemplateWindowListener listener;

    public RenderSettings(TemplateWindowListener listener, int templateWindowWidth, int templateWindowHeight, int pointsColor, int splineColor) {
        super("Object Template");
        setSize(templateWindowWidth, templateWindowHeight);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ///setAlwaysOnTop(true);
        this.listener = listener;
        try {
            //parametersPanel = new ParametersPanel(this, numberOfSegmentsPerInterval);
            //add(parametersPanel);
            addComponentListener(this);
            setBackground(Color.WHITE);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disposeTemplateWindow() {
        //this.setVisible(false);
        this.dispose();
    }

    @Override
    public void acceptSettings(int N, int K, int m, int M) {
        //listener.aetSettings(pointsPanel.getSplinePoints(), N, K, m, M);
        disposeTemplateWindow();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        //pointsPanel.componentResized();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
