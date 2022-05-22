package nsu.graphics.fifthlab.scene;

public class LightSource {
    private final int Lx;
    private final int Ly;
    private final int Lz;
    private final int Lr;
    private final int Lg;
    private final int Lb;

    public LightSource(int Lx, int Ly, int Lz, int Lr, int Lg, int Lb){
        this.Lx = Lx;
        this.Ly = Ly;
        this.Lz = Lz;
        this.Lr = Lr;
        this.Lg = Lg;
        this.Lb = Lb;
    }
}
