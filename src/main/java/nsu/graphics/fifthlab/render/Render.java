package nsu.graphics.fifthlab.render;

import nsu.graphics.fifthlab.MathUtils;
import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.scene.Scene;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Render {
    private RenderListener listener;
    private Color backgroundColor;
    private double gamma;
    private int depth;
    private Quality quality;
    private Point3D camPosition;
    private Point3D viewPoint;
    private Vector viewVector;
    private Vector normalizedViewVector;
    private Vector camShift;
    private Vector up;
    private double Zn;
    private double Zf;
    private double widthCamMatrix;
    private double heightCamMatrix;
    private Scene scene;

    private void initCamParameters() {
        this.normalizedViewVector = new Vector(viewVector);
        this.normalizedViewVector.normalize();
        camShift = new Vector(normalizedViewVector);
        camShift.mulCoordinates(5);
    }

    public void initCam(Scene scene) {
        up = new Vector(0, 0, 1);
        Point3D minPoint = scene.getMinPoint();
        //minPoint.mulCoordinates(1.025);
        Point3D maxPoint = scene.getMaxPoint();
        //maxPoint.mulCoordinates(1.025);
        Point3D delta = Point3D.getDelta(minPoint, maxPoint);
        delta.mulCoordinates(0.5);
        viewPoint = new Point3D(minPoint);
        viewPoint.addDelta(delta);
        double camX = minPoint.x() - delta.z() / (Math.tan(Math.toRadians(15)));
        camPosition = new Point3D(camX, viewPoint.y(), viewPoint.z());
        this.viewVector = new Vector(viewPoint, camPosition);
        initCamParameters();

        Zn = (minPoint.x() - camPosition.x()) / 2.0;
        Zf = maxPoint.x() - camPosition.x() + delta.x() / 2.0;
        Dimension userPanelSize = listener.getUserPanelSize();
        heightCamMatrix = delta.y(); //because delta - delta from centre (1/2)
        double ratio = userPanelSize.getWidth() / userPanelSize.getHeight();
        widthCamMatrix = heightCamMatrix * ratio;
    }

    public Render(RenderListener listener, Scene scene) {
        this.scene = scene;
        this.listener = listener;
        this.backgroundColor = new Color(0, 0, 0);
        this.gamma = 1.5;
        this.depth = 3;
        this.quality = Quality.NORMAL;
        initCam(scene);
    }

    private Point3D parsePoint(JSONObject jsonObject, String name) {
        double POINT1x = jsonObject.getDouble(name + "x");
        double POINT1y = jsonObject.getDouble(name + "y");
        double POINT1z = jsonObject.getDouble(name + "z");
        return new Point3D(POINT1x, POINT1y, POINT1z);
    }

    private void addPointToJSON(JSONObject jsonObject, String name, Point3D point) {
        jsonObject.put(name + "x", point.x());
        jsonObject.put(name + "y", point.y());
        jsonObject.put(name + "z", point.z());
    }

    public Render(RenderListener listener, File file, Scene scene) {
        this.scene = scene;
        this.listener = listener;
        byte[] encodedBytes;
        try {
            encodedBytes = Files.readAllBytes(file.toPath());
            JSONObject sampleObject = new JSONObject(new String(encodedBytes, StandardCharsets.UTF_8));

            int Br = sampleObject.getInt("Br");
            int Bg = sampleObject.getInt("Br");
            int Bb = sampleObject.getInt("Br");
            this.backgroundColor = new Color(Br, Bg, Bb);
            this.gamma = sampleObject.getDouble("GAMMA");
            this.depth = sampleObject.getInt("DEPTH");
            if (depth > 3) depth = 3;
            this.quality = Quality.getQuality(sampleObject.getString("QUALITY"));
            this.camPosition = parsePoint(sampleObject, "EYE");
            this.viewPoint = parsePoint(sampleObject, "VIEW");

            this.up = new Vector(parsePoint(sampleObject, "UP"));
            this.viewVector = new Vector(viewPoint, camPosition);
            initCamParameters();

            Vector right = Vector.vectorMultiply(viewVector, up);
            this.up = Vector.vectorMultiply(right, viewVector);

            this.Zn = sampleObject.getInt("ZN");
            this.Zf = sampleObject.getInt("ZF");
            this.widthCamMatrix = sampleObject.getInt("SW");
            this.heightCamMatrix = sampleObject.getInt("SH");
            Dimension userPanelSize = listener.getUserPanelSize();
            double ratio = userPanelSize.getWidth() / userPanelSize.getHeight();
            widthCamMatrix = heightCamMatrix * ratio;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSettings(File file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Br", backgroundColor.getRed());
        jsonObject.put("Bg", backgroundColor.getGreen());
        jsonObject.put("Bb", backgroundColor.getBlue());
        jsonObject.put("GAMMA", gamma);
        jsonObject.put("DEPTH", depth);
        jsonObject.put("QUALITY", quality.toString().toLowerCase());
        addPointToJSON(jsonObject, "EYE", camPosition);
        addPointToJSON(jsonObject, "VIEW", viewPoint);
        addPointToJSON(jsonObject, "UP", up.getVertex());
        jsonObject.put("ZN", Zn);
        jsonObject.put("ZF", Zf);
        jsonObject.put("SW", widthCamMatrix);
        jsonObject.put("SH", heightCamMatrix);

        System.out.println(jsonObject);
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            fOut.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Point3D getCamPosition() {
        return new Point3D(camPosition);
    }

    public Vector getUpVector() {
        return new Vector(up);
    }

    public double getDistanceToProjection() {
        return Zn;
    }

    public Vector getViewVector() {
        return new Vector(viewVector);
    }

    public void mulDistanceToProjection(int wheelRotation) {
        //System.out.println("Changing Zn");
        if (wheelRotation == 1) Zn *= 1.05;
        if (wheelRotation == -1) Zn *= 0.95;
        /*System.out.println(Zn);
        System.out.println(camPosition);*/
    }

    public void moveCamera(int wheelRotation) {
        //System.out.println("MoveCamera");
        if (wheelRotation == 1) camPosition.addDelta(camShift);
        if (wheelRotation == -1) camPosition.subDelta(camShift);
        /*System.out.println(Zn);
        System.out.println(camPosition);*/
    }

    public void rotateCam(int degreeZ, int degreeY) {
        viewVector.rotateAroundZAxis(degreeZ);
        viewVector.rotateAroundYAxis(degreeY);
        up.rotateAroundZAxis(degreeZ);
        up.rotateAroundYAxis(degreeY);
        Vector newVectorToCam = Vector.vectorSubtraction(new Vector(viewPoint), viewVector);
        camPosition = newVectorToCam.getVertex();
        initCamParameters();
    }
}
