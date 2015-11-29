package World3D.Floor;

public class FloorPoint extends GridPoint {

    public enum FloorPointType {
        Empty, Wall, Unknown, Character, Myself, Exit
    }
    private final int id;
    private FloorPointType type;

    public FloorPoint(int id, int x, int y) {
        super(x, y);
        this.id = id;
        this.type = FloorPointType.Empty;
    }

    public FloorPoint(int id, int x, int y, FloorPointType type) {
        super(x, y);
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setType(FloorPointType type) {
        this.type = type;
    }

    public FloorPointType getType() {
        return type;
    }

    @Override
    public FloorPoint clone() {
        return new FloorPoint(this.id, this.x, this.y, this.type);
    }
    
    
}
