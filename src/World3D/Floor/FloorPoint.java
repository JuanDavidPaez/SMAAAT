package World3D.Floor;

public class FloorPoint extends GridPoint {

    public enum FloorPointType {

        Unknown, Empty, Wall
    }
    public int id = -1;
    public FloorPointType type = FloorPointType.Unknown;

    public FloorPoint(int x, int y, FloorPointType type) {
        super(x, y);
        this.type = type;
    }
}
