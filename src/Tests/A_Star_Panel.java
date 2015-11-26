package Tests;

import AI.Pathfinding.A_Star;
import World3D.Floor.FloorEditor;
import World3D.Floor.GridPoint;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class A_Star_Panel extends FloorEditor {

    public GridPoint start;
    public GridPoint end;
    public List<GridPoint> path;

    public A_Star_Panel(String floorFilePath, GridPoint start, GridPoint end) {
        this.start = start;
        this.end = end;
        this.loadFile(floorFilePath);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintAStarAlgorithm(g);
    }

    private void paintAStarAlgorithm(Graphics g) {
        paintPath(g);
        drawRectangle(g, start, Color.BLACK, 1, Color.BLUE);
        drawRectangle(g, end, Color.BLACK, 1, Color.RED);
    }

    private void paintPath(Graphics g) {
        if (path != null) {
            for (GridPoint p : path) {
                drawRectangle(g, p, Color.GREEN, 1, Color.GREEN);
            }
        }
    }

    public void start() {
        A_Star astar = new A_Star(this.getXSize(), this.getYSize());
        astar.addObstacles(floorData.floorObstaclesToArray());
        int[] pathArray = astar.findPath(floorData.gridPoint2ArrayIndex(start), floorData.gridPoint2ArrayIndex(end));
        path = floorData.arrayIndex2GridPointList(pathArray);
        repaint();        
    }
}
