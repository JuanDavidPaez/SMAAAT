package World3D.Floor;

public class GridPoint {

    public int x;
    public int y;

    public GridPoint() {
    }

    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y;
    }

    /**
     * Estimate the absolute distance between two points, (result do not
     * includes direction)
     *
     * @param p1
     * @param p2
     * @return
     */
    public static GridPoint estimateAbsoluteDistance(GridPoint p1, GridPoint p2) {
        GridPoint p = estimateDistance(p1, p2);
        p.x = Math.abs(p.x);
        p.y = Math.abs(p.y);
        return p;
    }

    /**
     * Estimate the distance vector between two points, (result includes
     * direction)
     *
     * @param p1
     * @param p2
     * @return
     */
    public static GridPoint estimateDistance(GridPoint p1, GridPoint p2) {
        GridPoint p = new GridPoint();
        p.x = p2.x - p1.x;
        p.y = p2.y - p1.y;
        return p;
    }

    @Override
    public boolean equals(Object o) {
        GridPoint p = (GridPoint) o;
        return (this.x == p.x && this.y == p.y);
    }
}