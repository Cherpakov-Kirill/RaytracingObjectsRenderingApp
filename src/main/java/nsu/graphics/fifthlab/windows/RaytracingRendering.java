package nsu.graphics.fifthlab.windows;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.*;

import nsu.graphics.fifthlab.PropertyList;
import nsu.graphics.fifthlab.panels.scene.ScenePanel;
import ru.nsu.cg.MainFrame;


public class RaytracingRendering extends MainFrame implements ComponentListener, TemplateWindowListener {
    private final ScenePanel scenePanel;

    private final String[] imageExtensions;
    private final String[] sceneExtensions;
    private final String[] renderExtensions;
    PropertyList propertyList;

    public RaytracingRendering(PropertyList propertyList) {
        super(propertyList.wireframeObjectWindowWidth, propertyList.wireframeObjectWindowHeight, "Raytracing objects rendering App");
        this.propertyList = propertyList;
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/Open a scene", "Open a scene file", KeyEvent.VK_O, "/Open.png", "openSceneFile");
            addMenuItem("File/Save image", "Save your picture as file", KeyEvent.VK_S, "/Save.png", "saveImage");
            addMenuItem("File/Load render settings", "Load render settings", KeyEvent.VK_L, "/LoadRenderSettings.png", "loadRenderSettings");
            addMenuItem("File/Save render settings", "Save render settings", KeyEvent.VK_S, "/SaveRenderSettings.png", "saveRenderSettings");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "/Exit.png", "exit");

            addSubMenu("Rendering", KeyEvent.VK_R);
            addMenuItem("Rendering/Init", "Normalize object position", KeyEvent.VK_F, "/FitImage.png", "init");
            addMenuItem("Rendering/Settings", "Rendering settings", KeyEvent.VK_S, "/Settings.png", "renderingSettings");
            addMenuItem("Rendering/Palette", "Choose wireframe objects color", KeyEvent.VK_C, "/Palette.png", "chooseObjectColor");
            addRadioMenuItem("Rendering/Select view", "Select view", KeyEvent.VK_V, "/WireframeObjects.png", "selectView");
            addRadioMenuItem("Rendering/Render objects", "Render objects", KeyEvent.VK_R, "/Render.png", "renderObjects");

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "/About.png", "showAbout");
            addMenuItem("Help/Usage", "Shows program usage information", KeyEvent.VK_U, "/Usage.png", "showUsage");

            addToolBarButton("File/Open a scene");
            addToolBarButton("File/Save image");
            addToolBarButton("File/Load render settings");
            addToolBarButton("File/Save render settings");
            addToolBarSeparator();
            addToolBarButton("Rendering/Init");
            addToolBarButton("Rendering/Settings");
            addToolBarButton("Rendering/Palette");
            addToolBarSeparator();
            addToolBarToggleButton("Rendering/Select view");
            addToolBarToggleButton("Rendering/Render objects");

            JScrollPane scrollPane = new JScrollPane();
            scenePanel = new ScenePanel(scrollPane, propertyList.wireframeObjectWindowWidth-20, propertyList.wireframeObjectWindowHeight-100, propertyList.objectColor);
            scrollPane.setViewportView(scenePanel);
            add(scrollPane);
            addComponentListener(this);
            setBackground(Color.WHITE);
            String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            System.out.println(path);
            path = path.substring(0,path.lastIndexOf('/'));
            path+="/Data/Default/default.scene";
            File file = new File(path);
            if(file.exists()){
                scenePanel.openSceneFile(file);
                System.out.println("Opened scene file " + file.getAbsolutePath());
                tryToFindAndOpenRenderSettings(file.getAbsolutePath());
                selectView();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        changeSelectedButtonForView("Rendering/Select view");
        imageExtensions = new String[4];
        imageExtensions[0] = "png";
        imageExtensions[1] = "jpeg";
        imageExtensions[2] = "bmp";
        imageExtensions[3] = "gif";
        sceneExtensions = new String[1];
        sceneExtensions[0] = "scene";
        renderExtensions = new String[1];
        renderExtensions[0] = "render";
        setFocusable(true);
        addKeyListener(scenePanel);
    }

    @Override
    public void setSettings(List<Point> splinePoints, int N, int K, int m, int M) {
        //objectsPanel.setTemplate(splinePoints, N, K, m, M);
    }

    public void setObjectColorToProp(int rgb) {
        propertyList.setObjectColor(rgb);
    }

    //File/Open - opens any scene .scene file
    public void openSceneFile() {
        File file = getOpenFileName(sceneExtensions);
        if (file == null) return;
        scenePanel.openSceneFile(file);
        System.out.println("Opened scene file " + file.getAbsolutePath());
        tryToFindAndOpenRenderSettings(file.getAbsolutePath());
        selectView();
    }

    //File/Save image - saves image .scene file
    public void saveImage() {
        File file = getSaveFileName(imageExtensions);
        if (file == null) return;
        if (menuViewMap.get("Rendering/Select view").isSelected()) {
            System.out.println("Saving image with Wireframe objects");
        }
        if (menuViewMap.get("Rendering/Render objects").isSelected()) {
            System.out.println("Saving image with Render objects");
        }
        scenePanel.saveImage(file);
        System.out.println("Saving image file to " + file.getAbsolutePath());
    }

    private void tryToFindAndOpenRenderSettings(String scenePath) {
        String renderPath = scenePath.substring(0, scenePath.lastIndexOf('.')+1);
        renderPath += "render";
        File file = new File(renderPath);
        if (file.exists()){
            System.out.println("Render settings file exists");
            scenePanel.openRenderFile(file);
            showMessage("Opened render settings file:\n" + file.getAbsolutePath(), "Render settings file exists");
            System.out.println("Opened render settings file " + file.getAbsolutePath());
        }
        else {
            scenePanel.setDefaultRender();
            System.out.println("Render settings file not exists");
        }
    }

    //File/Load render settings - loads render settings from any .render file
    public void loadRenderSettings() {
        File file = getOpenFileName(renderExtensions);
        if (file == null) return;
        scenePanel.openRenderFile(file);
        System.out.println("Opened render settings file " + file.getAbsolutePath());
    }

    //File/Save render settings - saves render settings in any .render file
    public void saveRenderSettings() {
        File file = getSaveFileName(renderExtensions);
        if (file == null) return;
        scenePanel.saveRenderSettings(file);
        System.out.println("Saved render settings file to " + file.getAbsolutePath());
    }


    //View/Init
    public void init() {
        scenePanel.normalizeObjectPosition();
    }

    //View/Settings
    public void renderingSettings() {
        scenePanel.draw();
    }

    //View/Palette
    public void chooseObjectColor() {
        Color color = JColorChooser.showDialog(this,
                "Choose color", Color.BLACK);
        if (color != null) {
            scenePanel.setObjectColor(color);
            setObjectColorToProp(color.getRGB());
        }
    }

    //View/Select view
    public void selectView() {
        changeSelectedButtonForView("Rendering/Select view");
    }

    //View/Render objects
    public void renderObjects() {
        changeSelectedButtonForView("Rendering/Render objects");
    }

    //File/Exit - exits application
    public void exit() {
        System.exit(0);
    }

    //Help/About... - shows program version and copyright information
    public void showAbout() {
        JOptionPane.showMessageDialog(this, "Raytracing objects rendering App. ver. 1.0\nCopyright 2022 Cherpakov Kirill, FIT, group 19201\nProgram for raytracing rendering of scene.", "About Raytracing objects rendering App", JOptionPane.INFORMATION_MESSAGE);
    }

    //Help/Usage - shows program usage information
    public void showUsage() {
        JOptionPane.showMessageDialog(this, "Open scene-file to start working with it.\nPush on the Open File button or find the same menu item in the menu \"File\"", "Usage", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String str, String title) {
        JOptionPane.showMessageDialog(this, str, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void changeSelectedButtonForView(String title) {
        JRadioButtonMenuItem item = menuViewMap.get(title);
        item.setSelected(true);
        JToggleButton button = menuToolbarViewMap.get(title);
        button.setSelected(true);
    }

    public static void main(String[] args) {
        RaytracingRendering mainFrame = new RaytracingRendering(new PropertyList());
        mainFrame.setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        scenePanel.componentResized();
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
