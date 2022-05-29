package nsu.graphics.fifthlab.scene;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.panels.scene.Painter;
import nsu.graphics.fifthlab.scene.primitives.*;
import nsu.graphics.fifthlab.scene.primitives.Rectangle;
import nsu.graphics.fifthlab.RayColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class Scene {
    private RayColor ambientLight;
    private final List<LightSource> lightSources;
    private final List<AbstractPrimitive> primitives;
    private final List<Box> boxes;
    private final List<Rectangle> rectangles;
    private final List<Sphere> spheres;
    private final List<Triangle> triangles;

    private void parseAmbientLight(JSONObject ambientLight) {
        int Ar = ambientLight.getInt("Ar");
        int Ag = ambientLight.getInt("Ag");
        int Ab = ambientLight.getInt("Ab");
        this.ambientLight = new RayColor(Ar, Ag, Ab);
    }

    private void parseLightSource(JSONObject lightSource) {
        int Lx = lightSource.getInt("Lx");
        int Ly = lightSource.getInt("Ly");
        int Lz = lightSource.getInt("Lz");
        int Lr = lightSource.getInt("Lr");
        int Lg = lightSource.getInt("Lg");
        int Lb = lightSource.getInt("Lb");
        this.lightSources.add(new LightSource(Lx, Ly, Lz, Lr, Lg, Lb));
    }

    private void parseLightSources(JSONObject lightSources) {
        int NL = lightSources.getInt("NL");
        JSONArray lights = lightSources.getJSONArray("Lights");
        for (int i = 0; i < NL; i++) {
            parseLightSource(lights.getJSONObject(i));
        }
    }

    private RayColor parseDiffuseReflection(JSONObject jsonObject) {
        double KDr = jsonObject.getDouble("KDr");
        double KDg = jsonObject.getDouble("KDg");
        double KDb = jsonObject.getDouble("KDb");
        return new RayColor(KDr, KDg, KDb);
    }

    private RayColor parseSpecularReflection(JSONObject jsonObject) {
        double KSr = jsonObject.getDouble("KSr");
        double KSg = jsonObject.getDouble("KSg");
        double KSb = jsonObject.getDouble("KSb");
        return new RayColor(KSr, KSg, KSb);
    }

    private Point3D parsePoint(JSONObject jsonObject, String name) {
        int POINT1x = jsonObject.getInt(name + "x");
        int POINT1y = jsonObject.getInt(name + "y");
        int POINT1z = jsonObject.getInt(name + "z");
        return new Point3D(POINT1x, POINT1y, POINT1z);
    }

    private void parseBoxes(JSONArray boxes) {
        for (int i = 0; i < boxes.length(); i++) {
            JSONObject box = boxes.getJSONObject(i);
            int MINx = box.getInt("MINx");
            int MINy = box.getInt("MINy");
            int MINz = box.getInt("MINz");
            int MAXx = box.getInt("MAXx");
            int MAXy = box.getInt("MAXy");
            int MAXz = box.getInt("MAXz");
            RayColor diffuseReflection = parseDiffuseReflection(box);
            RayColor specularReflection = parseSpecularReflection(box);
            double power = box.getDouble("Power");
            this.boxes.add(new Box(diffuseReflection, specularReflection, power, new Point3D(MINx, MINy, MINz), new Point3D(MAXx, MAXy, MAXz)));
        }
    }

    private void parseRectangles(JSONArray rectangles) {
        for (int i = 0; i < rectangles.length(); i++) {
            JSONObject rectangle = rectangles.getJSONObject(i);
            Point3D point1 = parsePoint(rectangle, "POINT" + 1);
            Point3D point2 = parsePoint(rectangle, "POINT" + 2);
            Point3D point3 = parsePoint(rectangle, "POINT" + 3);
            Point3D point4 = parsePoint(rectangle, "POINT" + 4);
            RayColor diffuseReflection = parseDiffuseReflection(rectangle);
            RayColor specularReflection = parseSpecularReflection(rectangle);
            double power = rectangle.getDouble("Power");
            this.rectangles.add(new Rectangle(diffuseReflection, specularReflection, power, point1, point2, point3, point4));
        }
    }

    private void parseSpheres(JSONArray spheres) {
        for (int i = 0; i < spheres.length(); i++) {
            JSONObject sphere = spheres.getJSONObject(i);
            Point3D center = parsePoint(sphere, "CENTER");
            int radius = sphere.getInt("RADIUS");
            RayColor diffuseReflection = parseDiffuseReflection(sphere);
            RayColor specularReflection = parseSpecularReflection(sphere);
            double power = sphere.getDouble("Power");
            this.spheres.add(new Sphere(diffuseReflection, specularReflection, power, center, radius));
        }
    }

    private void parseTriangles(JSONArray triangles) {
        for (int i = 0; i < triangles.length(); i++) {
            JSONObject triangle = triangles.getJSONObject(i);
            Point3D point1 = parsePoint(triangle, "POINT" + 1);
            Point3D point2 = parsePoint(triangle, "POINT" + 2);
            Point3D point3 = parsePoint(triangle, "POINT" + 3);
            RayColor diffuseReflection = parseDiffuseReflection(triangle);
            RayColor specularReflection = parseSpecularReflection(triangle);
            double power = triangle.getDouble("Power");
            this.triangles.add(new Triangle(diffuseReflection, specularReflection, power, point1, point2, point3));
        }
    }

    private void parsePrimitives(JSONObject primitives) {
        parseBoxes(primitives.getJSONArray("Boxes"));
        parseRectangles(primitives.getJSONArray("Rectangles"));
        parseSpheres(primitives.getJSONArray("Spheres"));
        parseTriangles(primitives.getJSONArray("Triangles"));
    }

    public Scene(File file) {
        lightSources = new LinkedList<>();
        boxes = new LinkedList<>();
        rectangles = new LinkedList<>();
        spheres = new LinkedList<>();
        triangles = new LinkedList<>();
        primitives = new LinkedList<>();

        byte[] encodedBytes;
        try {
            encodedBytes = Files.readAllBytes(file.toPath());
            JSONObject sampleObject = new JSONObject(new String(encodedBytes, StandardCharsets.UTF_8));
            parseAmbientLight(sampleObject.getJSONObject("Ambient light"));
            parseLightSources(sampleObject.getJSONObject("Light sources"));
            parsePrimitives(sampleObject.getJSONObject("Primitives"));
            primitives.addAll(boxes);
            primitives.addAll(triangles);
            primitives.addAll(rectangles);
            primitives.addAll(spheres);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AbstractPrimitive> getPrimitives() {
        return primitives;
    }

    public void drawPrimitives(Painter painter) {
        for (AbstractPrimitive primitive : primitives) {
            primitive.drawPrimitive(painter);
        }
    }

    public RayColor getAmbientLight() {
        return new RayColor(ambientLight);
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }

    public Point3D getMinPoint() {
        Point3D min = null;
        for (AbstractPrimitive primitive : primitives) {
            min = Point3D.minOnAllCoordinates(min, primitive.getMinPoint());
        }
        return min;
    }

    public Point3D getMaxPoint() {
        Point3D max = null;
        for (AbstractPrimitive primitive : primitives) {
            max = Point3D.maxOnAllCoordinates(max, primitive.getMaxPoint());
        }
        return max;
    }
}
