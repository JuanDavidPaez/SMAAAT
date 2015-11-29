package World3D.Floor;

import Utils.Utils;
import World3D.Floor.FloorPoint.FloorPointType;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class FloorData {

    public int pxResolution;
    public int XSize;
    public int YSize;
    public FloorPoint[] points;
    private transient Map<String, Path> paths = new HashMap();
    public transient PaintObjectsSupplier pointsSupplier;

    public FloorData(int xSize, int ySize, int resolution) {
        this(xSize, ySize, resolution, FloorPointType.Empty);
    }

    public FloorData(int xSize, int ySize, int resolution, FloorPointType initializationType) {
        this.XSize = xSize;
        this.YSize = ySize;
        this.pxResolution = resolution;
        points = new FloorPoint[XSize * YSize];
        initPoints(initializationType);
    }

    private void initPoints(FloorPointType initializationType) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < points.length; i++) {
            FloorPoint p = new FloorPoint(i, x, y, initializationType);
            points[i] = p;
            x++;
            if (x >= XSize) {
                x = 0;
                y++;
            }
        }
    }

    public FloorPoint getPointFromId(int id) {
        return this.points[id];
    }

    public FloorPoint getPointFromCoordinates(int x, int y) {
        int id = (XSize * y) + x;
        return getPointFromId(id);
    }

    public FloorPoint getPointFromCoordinates(GridPoint gp) {
        return getPointFromCoordinates(gp.x, gp.y);
    }

    public void setPointType(GridPoint p, FloorPointType type) {
        getPointFromCoordinates(p.x, p.y).setType(type);
    }

    public void addWall(GridPoint p) {
        if (p != null) {
            if (p.x >= 0 && p.y >= 0 && p.x < XSize && p.y < YSize) {
                setPointType(p, FloorPointType.Wall);
            }
        }
    }

    public void addWalls(List<GridPoint> list) {
        if (list != null && list.size() > 0) {
            for (GridPoint w : list) {
                addWall(w);
            }
        }
    }

    public void removeWall(GridPoint p) {
        if (p != null) {
            setPointType(p, FloorPointType.Empty);
        }
    }

    public List<FloorPoint> getWalls() {
        List<FloorPoint> walls = new ArrayList<FloorPoint>();
        for (FloorPoint p : points) {
            if (p.getType() == FloorPointType.Wall) {
                walls.add(p);
            }
        }
        return walls;
    }

    public int[] floorObstaclesToArray() {
        List<Integer> list = new ArrayList<Integer>();
        for (FloorPoint p : points) {
            if (isObstacle(p)) {
                list.add(p.getId());
            }
        }
        if(list.size() > 0){
            int[] obstacles = Utils.integerListToIntArray(list);
            return obstacles;
        }
        return null;
    }

    public int[] walkablePointsToArray() {
        int[] pointArray = null;
        List<Integer> list = new ArrayList<Integer>();
        for (FloorPoint p : points) {
            if (isWalkable(p)) {
                list.add(p.getId());
            }
        }

        if (list != null && list.size() > 0) {
            pointArray = new int[list.size()];
            int i = 0;
            for (Integer x : list) {
                pointArray[i] = x;
                i++;
            }
        }
        return pointArray;
    }

    public int gridPoint2ArrayIndex(GridPoint p) {
        return p.x + (p.y * XSize);
    }

    public GridPoint arrayIndex2GridPoint(int idx) {
        int y = (int) (idx / XSize);
        int x = idx - (y * XSize);
        return new GridPoint(x, y);
    }

    public List<GridPoint> arrayIndex2GridPointList(int[] pathArray) {
        List<GridPoint> path = null;
        if (pathArray != null && pathArray.length > 0) {
            path = new ArrayList<GridPoint>();
            for (int i : pathArray) {
                path.add(this.arrayIndex2GridPoint(i));
            }
        }
        return path;
    }

    public FloorDataChunk getFloorDataChunk(GridPoint center, int viewRadius) {
        FloorDataChunk fdc = null;
        if (viewRadius > 0 && center.x > 0 && center.x < XSize && center.y > 0 && center.y < YSize) {
            List<FloorPoint> chunkList = new ArrayList<FloorPoint>();
            GridPoint p1 = new GridPoint(center.x - viewRadius, center.y - viewRadius);
            GridPoint p2 = new GridPoint(center.x + viewRadius, center.y + viewRadius);

            if (p1.x < 0) {
                p1.x = 0;
            }
            if (p1.y < 0) {
                p1.y = 0;
            }
            if (p2.x >= XSize) {
                p2.x = XSize - 1;
            }
            if (p2.y >= YSize) {
                p2.y = YSize - 1;
            }
            for (int j = p1.y; j <= p2.y; j++) {
                for (int i = p1.x; i <= p2.x; i++) {
                    chunkList.add(getPointFromCoordinates(i, j));
                }
            }
            fdc = new FloorDataChunk(chunkList.toArray(new FloorPoint[0]), this.XSize, this.YSize, this.pxResolution);
        }
        return fdc;
    }

    public boolean updateDataFromChunk(FloorDataChunk c) {
        boolean dataChanged = false;
        if (this.XSize != c.getParentFloorXSize() || this.YSize != c.getParentFloorYSize()) {
            throw new RuntimeException("Para unir el segmento, el tamaño X y Y de las matrices originales debe ser igual.");
        }
        for (FloorPoint cp : c.getPoints()) {
            FloorPoint p = getPointFromId(cp.getId());
            if (cp.getType() != FloorPointType.Unknown) {
                if (!p.getType().equals(cp.getType())) {
                    p.setType(cp.getType());
                    dataChanged = true;
                }
            }
        }
        return dataChanged;
    }

    public void showInJFrame() {

        final FloorEditor panel = new FloorEditor(this);
        panel.setAllowEdition(false);
        Thread t = new Thread(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setTitle("Floor data view");
                frame.setSize(panel.getPixelWidth() + 20, panel.getPixelHeight() + 40);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                panel.startPaintLoop();
            }
        });
        t.start();
    }

    public static FloorData loadDataFromFile(String filePath) {
        FloorData floorData = null;
        try {
            String jsonFileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            floorData = gson.fromJson(jsonFileContent, FloorData.class);
        } catch (IOException ex) {
            Logger.getLogger(FloorEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return floorData;
    }

    public boolean isObstacle(FloorPoint p) {
        return p.getType().equals(FloorPointType.Wall);
    }

    public boolean isWalkable(FloorPoint p) {
        return (p.getType().equals(FloorPointType.Empty) || p.getType().equals(FloorPointType.Unknown));
    }

    public int[] getPointsArray(Set<FloorPointType> filterTypes) {
        int[] pointArray = null;
        List<Integer> list = new ArrayList<Integer>();
        for (FloorPoint p : points) {
            if (filterTypes.contains(p.getType())) {
                list.add(p.getId());
            }
        }
        if (list != null && list.size() > 0) {
            pointArray = new int[list.size()];
            int i = 0;
            for (Integer x : list) {
                pointArray[i] = x;
                i++;
            }
        }
        return pointArray;
    }

    public Map<String, Path> getPaths() {
        return paths;
    }

    public void addPath(String name, Path path) {
        this.paths.put(name, path);
    }

    public interface PaintObjectsSupplier {
        public List<FloorPoint> getPointsToPaint();
    }
}
