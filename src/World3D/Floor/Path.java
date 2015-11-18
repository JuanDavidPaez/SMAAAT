package World3D.Floor;

import java.awt.Color;
import java.util.List;

public class Path {

    public List<GridPoint> points;
    public Color color;

    public Path(List<GridPoint> points, Color color) {
        this.points = points;
        this.color = color;
    }
}
