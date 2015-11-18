package AI.Pathfinding;

import World3D.Floor.GridPoint;
import World3D.Floor.Path;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PatrolPath {

    public List<PathPoint> points = new ArrayList();
    public Color color;

    public PatrolPath(Path path) {
        this.color = path.color;
        for (GridPoint p : path.points) {
            this.points.add(new PathPoint(p));
        }
    }

    public PatrolPath(List<GridPoint> points, Color color) {
        this.color = color;
        for (GridPoint p : points) {
            this.points.add(new PathPoint(p));
        }
    }

    public PathPoint getNextPoint(GridPoint currentPosition) {
        updateCurrentPosition(currentPosition);
        PathPoint p = getNextPointToVisit();
        return p;
    }

    private PathPoint getNextPointToVisit() {
        PathPoint p = null;
        for (PathPoint pp : this.points) {
            if (!pp.visited) {
                p = pp;
                break;
            }
        }
        return p;
    }

    private void updateCurrentPosition(GridPoint currentPosition) {
        PathPoint targetPoint = getNextPointToVisit();
        if (targetPoint != null) {
            GridPoint d = GridPoint.estimateAbsoluteDistance(currentPosition, targetPoint);
            if (d.x == 0 && d.y == 0) {
                targetPoint.setVisited(true);
            }
        }
    }

    public class PathPoint extends GridPoint {

        boolean visited = false;

        public PathPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public PathPoint(GridPoint p) {
            this.x = p.x;
            this.y = p.y;
        }

        public void setVisited(boolean v) {
            this.visited = v;
        }
    }
}
