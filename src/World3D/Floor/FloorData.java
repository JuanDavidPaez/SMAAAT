package World3D.Floor;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class FloorData {

    public int pxResolution;
    public int XSize;
    public int YSize;
    public List<GridPoint> walls = new ArrayList();
    public Map<String,Path> paths = new HashMap();

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

    public FloorData() {
    }

    public FloorData(int xSize, int ySize, int resolution) {
        this.XSize = xSize;
        this.YSize = ySize;
        this.pxResolution = resolution;
    }

    public void addWall(GridPoint p) {
        if (p != null) {
            for (GridPoint w : walls) {
                if (p.x == w.x && p.y == w.y) {
                    return;
                }
            }
            if (p.x >= 0 && p.y >= 0 && p.x < XSize && p.y < YSize) {
                walls.add(p);
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
            GridPoint rw = null;
            for (GridPoint w : walls) {
                if (p.x == w.x && p.y == w.y) {
                    rw = w;
                    break;
                }
            }
            if (rw != null) {
                walls.remove(rw);
            }
        }
    }

    public int[] floorWallsToArray() {

        int[] aWalls = null;
        if (walls != null && walls.size() > 0) {
            aWalls = new int[walls.size()];
            int i = 0;
            for (GridPoint w : walls) {
                aWalls[i] = gridPoint2ArrayIndex(w);
                i++;
            }
        }
        return aWalls;
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

    public FloorData getFloorPartialView(GridPoint center, int viewRadius) {
        FloorData f = new FloorData(this.XSize, this.YSize, this.pxResolution);
        List<GridPoint> view = new ArrayList<GridPoint>();
        GridPoint p1 = new GridPoint(center.x - viewRadius, center.y - viewRadius);
        GridPoint p2 = new GridPoint(center.x + viewRadius, center.y + viewRadius);
        for (GridPoint w : walls) {
            if (w.x >= p1.x && w.x <= p2.x) {
                if (w.y >= p1.y && w.y <= p2.y) {
                    view.add(new GridPoint(w.x, w.y));
                }
            }
        }
        f.addWalls(view);
        return f;
    }

    public void mergeFloorData(FloorData aux) {
        this.addWalls(aux.walls);
    }

    public void openInJFrame() {

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
}
