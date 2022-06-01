package nsu.graphics.fifthlab.render;

import nsu.graphics.fifthlab.MathUtils;
import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.panels.scene.Painter;
import nsu.graphics.fifthlab.scene.LightSource;
import nsu.graphics.fifthlab.scene.Scene;
import nsu.graphics.fifthlab.scene.primitives.AbstractPrimitive;
import nsu.graphics.fifthlab.RayColor;
import org.json.JSONObject;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Render {
    private static final Vector xAxis = new Vector(1, 0, 0);
    private static final Vector yAxis = new Vector(0, 1, 0);
    private static final Vector zAxis = new Vector(0, 0, 1);
    private final RenderListener listener;
    private final RayColor backgroundColor;
    private final double gamma;
    private int depth;
    private final Quality quality;
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
    private Vector horizontalCamMatrix;
    private Vector verticalCamMatrix;
    private Scene scene;

    //Rotate
    private double[][] rotateX;
    private double[][] rotateY;
    private double[][] rotateZ;

    //Rotations
    private void initRotateXOperator(double deg) {
        rotateX = MathUtils.initRotateXOperator(deg);
    }

    private void initRotateYOperator(double deg) {
        rotateY = MathUtils.initRotateYOperator(deg);
    }

    private void initRotateZOperator(double deg) {
        rotateZ = MathUtils.initRotateZOperator(deg);
    }

    private Point3D rotatePointAroundX(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateX, point);
    }

    private Point3D rotatePointAroundY(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateY, point);
    }

    private Point3D rotatePointAroundZ(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateZ, point);
    }

    private void initRotationBeforeCamCorrection() {
        up.normalize();
        Point3D axisPoint = up.getVertex();
        double d = Math.sqrt(axisPoint.y() * axisPoint.y() + axisPoint.z() * axisPoint.z());
        double cos = axisPoint.z() / d;

        double alpha = Math.toDegrees(Math.acos(cos));
        initRotateXOperator(alpha);
        Vector rotatedXUp = new Vector(rotatePointAroundX(axisPoint));
        double angleX = rotatedXUp.getDegreeBetween(yAxis);
        if (Math.round(angleX) != 90 || Vector.getScalarMul(rotatedXUp, zAxis) < 0) {
            alpha *= -1;
            initRotateXOperator(alpha);
        }

        double betta = Math.toDegrees(Math.acos(d));
        initRotateYOperator(betta);
        Vector rotatedYUp = new Vector(rotatePointAroundY(rotatePointAroundX(axisPoint)));
        double angleY = rotatedYUp.getDegreeBetween(zAxis);
        if (Math.round(angleY) != 0) {
            betta *= -1;
            initRotateYOperator(betta);
        }

        Vector newView = new Vector(rotatePointAroundY(rotatePointAroundX(viewVector.getVertex())));
        double gamma = newView.getDegreeBetween(xAxis);
        initRotateZOperator(gamma);
        Vector rotatedZ = new Vector(rotatePointAroundZ(newView.getVertex()));
        double angleZ = rotatedZ.getDegreeBetween(xAxis);
        if (Math.round(angleZ) != 0) {
            gamma *= -1;
            initRotateZOperator(gamma);
        }
    }

    private void initCamParameters() {
        this.normalizedViewVector = new Vector(viewVector);
        this.normalizedViewVector.normalize();
        camShift = new Vector(normalizedViewVector);
        camShift.mulCoordinates(5);
        horizontalCamMatrix = Vector.vectorMultiply(viewVector, up);
        horizontalCamMatrix.normalize();
        verticalCamMatrix = new Vector(up);
        verticalCamMatrix.normalize();
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
        viewVector = new Vector(viewPoint, camPosition);
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
        this.backgroundColor = new RayColor(150, 150, 150);
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
            this.backgroundColor = new RayColor(Br, Bg, Bb);
            this.gamma = sampleObject.getDouble("GAMMA");
            this.depth = sampleObject.getInt("DEPTH");
            if (depth > 3) depth = 3;
            this.quality = Quality.getQuality(sampleObject.getString("QUALITY"));
            this.camPosition = parsePoint(sampleObject, "EYE");
            this.viewPoint = parsePoint(sampleObject, "VIEW");

            this.up = new Vector(parsePoint(sampleObject, "UP"));
            this.viewVector = new Vector(viewPoint, camPosition);

            Vector right = Vector.vectorMultiply(viewVector, up);
            this.up = Vector.vectorMultiply(right, viewVector);
            initCamParameters();

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
        jsonObject.put("Br", backgroundColor.getIntRed());
        jsonObject.put("Bg", backgroundColor.getIntGreen());
        jsonObject.put("Bb", backgroundColor.getIntBlue());
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

    public void renderScene(Painter painter) {
        initRotationBeforeCamCorrection();
        Vector camMatrixPointOnView = Vector.vectorSum((new Vector(camPosition)), Vector.mulVectorOnCoefficient(normalizedViewVector, Zn));
        Vector startMatrix = Vector.vectorSubtraction(camMatrixPointOnView, Vector.mulVectorOnCoefficient(horizontalCamMatrix, widthCamMatrix / 2));
        startMatrix = Vector.vectorSubtraction(startMatrix, Vector.mulVectorOnCoefficient(verticalCamMatrix, heightCamMatrix / 2));
        int x = (int) (camPosition.x() + Zn);
        for (int row = 0; row < heightCamMatrix; row++) { //z
            Vector startRow = Vector.vectorSum(startMatrix, Vector.mulVectorOnCoefficient(verticalCamMatrix, row));
            for (int column = 0; column < widthCamMatrix; column++) { //y
                Vector pos = Vector.vectorSum(startRow, Vector.mulVectorOnCoefficient(horizontalCamMatrix, column));
                Point3D pixel = pos.getVertex();
                Vector rayDirection = new Vector(pixel, camPosition);
                rayDirection.normalize();
                Ray ray = new Ray(pixel, rayDirection);
                Color pixelColor = makeTraceRay(ray).toColor();
                Point3D localPixelPos = Vector.vectorSubtraction(pos, camMatrixPointOnView).getVertex();
                Point3D rotatedX = rotatePointAroundX(localPixelPos);
                Point3D rotatedY = rotatePointAroundY(rotatedX);
                Point3D rotatedZ = rotatePointAroundZ(rotatedY);
                painter.setRenderRGB(pixelColor, (int) Math.round(rotatedZ.y()), (int) Math.round(rotatedZ.z()));
                //todo: make progress statusBar
            }
        }
    }

    private RayColor makeTraceRay(Ray ray) {
        if (ray.getDepth() > depth) {
            return new RayColor(0, 0, 0);
        }

        double t = makeIntersection(ray, null);

        if (t == Double.MAX_VALUE) return new RayColor(backgroundColor);
        else {
            ray.computeAndSetIntersectPoint(t);
            Vector normal = ray.getIntersectionNormal();
            Vector v = Vector.getNegateVector(ray.getDirection());

            //AMBIENT COLOR
            AbstractPrimitive rayPrimitive = ray.getIntersectionWithPrimitive();
            RayColor pixelColor = scene.getAmbientLight();
            pixelColor.scalarProd(rayPrimitive.getDiffuseReflectionCoefficient());

            //For each light sources
            for (LightSource lightSource : scene.getLightSources()) {
                Vector l = Vector.pointsSubtraction(lightSource.getPosition(), ray.getIntersectPoint());
                double distanceToLight = l.getLength();
                l.normalize();
                //SHADOW
                //Checking that between light source and primitive has not other primitives
                Ray rayToLight = new Ray(ray.getIntersectPoint(), l);
                double tLight = makeIntersection(rayToLight, rayPrimitive);
                if (tLight < Double.MAX_VALUE && tLight > 10E-14 && tLight < distanceToLight) continue;

                RayColor lightColor = lightSource.getColor();
                //DIFFUSE REFLECTION
                double scalarNL = Vector.getScalarMul(normal, l);
                if (scalarNL > 0) { //I want to light only front-side of primitive
                    RayColor diffuse = rayPrimitive.getDiffuseReflectionCoefficient();
                    diffuse.mulByCoefficient(scalarNL);
                    diffuse.scalarProd(lightColor);
                    pixelColor.addColor(diffuse);
                }

                //SPECULAR REFLECTION
                Vector r = Vector.vectorSubtraction(
                        Vector.mulVectorOnCoefficient(normal, 2 * scalarNL),
                        l);
                double scalarRV = Vector.getScalarMul(r, v);
                if (scalarRV > 0 && scalarNL > 0) { //I want to light only front-side of primitive
                    double powScalarRV = Math.pow(scalarRV, rayPrimitive.getPower());
                    RayColor diffuse = rayPrimitive.getSpecularReflectionCoefficient();
                    diffuse.mulByCoefficient(powScalarRV);
                    diffuse.scalarProd(lightColor);
                    pixelColor.addColor(diffuse);
                }
            }

            //REFLECTION
            double scalarNV = Vector.getScalarMul(v, normal);
            Vector reflection = Vector.vectorSubtraction(
                    Vector.mulVectorOnCoefficient(normal, 2 * scalarNV),
                    v);
            Ray reflectedRay = new Ray(ray.getIntersectPoint(), reflection, ray.getDepth() + 1);
            RayColor reflectedColor = makeTraceRay(reflectedRay);
            reflectedColor.scalarProd(rayPrimitive.getSpecularReflectionCoefficient());
            pixelColor.addColor(reflectedColor);

            return pixelColor;
        }
    }

    private double makeIntersection(Ray ray, AbstractPrimitive excludePrimitive) {
        double t = Double.MAX_VALUE;
        List<AbstractPrimitive> primitives = scene.getPrimitives();
        for (AbstractPrimitive primitive : primitives) {
            if (primitive.equals(excludePrimitive)) continue;
            double result = primitive.intersect(ray);
            if (result < t && result > 10E-10) {
                t = result;
                ray.setIntersectionWithPrimitive(primitive);
            }
        }
        return t;
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
        if (wheelRotation == 1) Zn *= 1.05;
        if (wheelRotation == -1) Zn *= 0.95;
    }

    public void zoom(int wheelRotation) {
        if (wheelRotation == 1) camPosition.addDelta(camShift);
        if (wheelRotation == -1) camPosition.subDelta(camShift);
    }

    public void moveRight() {
        camPosition.addDelta(yAxis);
        viewVector = new Vector(viewPoint, camPosition);
        initCamParameters();
    }

    public void moveLeft() {
        camPosition.subDelta(yAxis);
        viewVector = new Vector(viewPoint, camPosition);
        initCamParameters();
    }

    public void moveUp() {
        camPosition.addDelta(zAxis);
        viewVector = new Vector(viewPoint, camPosition);
        initCamParameters();
    }

    public void moveDown() {
        camPosition.subDelta(zAxis);
        viewVector = new Vector(viewPoint, camPosition);
        initCamParameters();
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
