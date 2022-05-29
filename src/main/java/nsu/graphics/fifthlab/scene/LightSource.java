package nsu.graphics.fifthlab.scene;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.RayColor;


public class LightSource {
    private final Point3D position;
    private final RayColor color;

    public LightSource(int Lx, int Ly, int Lz, int Lr, int Lg, int Lb) {
        this.position = new Point3D(Lx, Ly, Lz);
        this.color = new RayColor(Lr, Lg, Lb);
    }

    public Point3D getPosition() {
        return position;
    }

    public RayColor getColor() {
        return new RayColor(color);
    }
}
