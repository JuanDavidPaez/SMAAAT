package World3D.Floor;

public class FloorDataChunk {

    private FloorPoint[] points;
    private int parentFloorXSize;
    private int parentFloorYSize;
    public int pxResolution;

    public FloorDataChunk(FloorPoint[] points, int parentFloorXSize, int parentFloorYSize, int pxResolution) {
        this.points = points;
        this.parentFloorXSize = parentFloorXSize;
        this.parentFloorYSize = parentFloorYSize;
        this.pxResolution = pxResolution;
    }

    public FloorPoint[] getPoints() {
        return points;
    }

    public int getParentFloorXSize() {
        return parentFloorXSize;
    }

    public int getParentFloorYSize() {
        return parentFloorYSize;
    }
    
}
