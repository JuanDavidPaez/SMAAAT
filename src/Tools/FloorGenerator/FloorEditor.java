package Tools.FloorGenerator;

import SimulationBESA.Utils.Utils;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FloorEditor extends JPanel {

    // <editor-fold desc="Propiedades y atributos">
    public static String fileExtension = "smaaat";
    private int pxResolution = 10;
    private int XSize;
    private int YSize;
    private GridPoint mouseHoverGridPoint = null;
    private List<GridPoint> walls = new ArrayList<GridPoint>();
    ;
    private boolean erase;
    // </editor-fold>

    //<editor-fold desc="Metodos">
    public FloorEditor() {
        this.setXandYSize(50, 50);
        this.addMouseListener(buildMouseListener());
        this.addMouseMotionListener(buildMouseMotionListener());
    }

    private int gridToPixel(int i) {
        return i * pxResolution;
    }

    private GridPoint pixelPointToGridPoint(Point p) {
        int x = p.x / pxResolution;
        int y = p.y / pxResolution;
        if (x >= this.XSize || y >= this.YSize) {
            return null;
        } else {
            return new GridPoint(x, y);
        }
    }

    private void addWall(GridPoint p) {
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

    private void removeWall(GridPoint p) {
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

    private void floorSizeChanged() {
        this.setPreferredSize(new Dimension(getPixelWidth() + 10, getPixelHeight() + 10));
        /*Remover las paredes que estan por fuera de el tamaño del piso*/
        if (walls != null) {
            for (Iterator<GridPoint> iterator = walls.iterator(); iterator.hasNext();) {
                GridPoint p = iterator.next();
                if (p.x >= XSize || p.y >= YSize) {
                    iterator.remove();
                }
            }
            addFloorSurroundingWalls();
        }
        this.repaint();
    }

    private void addFloorSurroundingWalls() {
        for (int i = 0; i < XSize; i++) {
            addWall(new GridPoint(i, 0));
            addWall(new GridPoint(i, YSize - 1));
        }
        for (int i = 0; i < YSize; i++) {
            addWall(new GridPoint(0, i));
            addWall(new GridPoint(XSize - 1, i));
        }
    }

    public void saveFile(String path) {

        if (path != null) {
            String filePath = path;
            String ext = ("." + fileExtension);
            if (!filePath.endsWith(ext)) {
                filePath += ext;
            }

            FloorData data = new FloorData();
            data.pxResolution = this.pxResolution;
            data.XSize = this.XSize;
            data.YSize = this.YSize;
            data.walls = this.walls;
            Gson gson = new Gson();
            String jsonData = gson.toJson(data, FloorData.class);

            Writer writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
                writer.write(jsonData);
            } catch (IOException ex) {
                Logger.getLogger(FloorEditor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FloorEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void loadFile(String filePath) {

        FloorData floorData = FloorData.loadDataFromFile(filePath);

        if (floorData != null) {
            this.walls.clear();
            //this.pxResolution = floorData.pxResolution;
            this.setXandYSize(floorData.XSize, floorData.YSize);
            if (floorData.walls != null && floorData.walls.size() > 0) {
                for (GridPoint w : floorData.walls) {
                    this.addWall(w);
                }
            }
            repaint();
        }
    }

    //</editor-fold>
    
    //<editor-fold desc="Mouse listeners">
    private void handleMouseAddWall(Point mousePoint) {
        GridPoint p = pixelPointToGridPoint(mousePoint);
        if (!erase) {
            addWall(p);
        } else {
            removeWall(p);
        }
        repaint();
    }

    private MouseListener buildMouseListener() {
        return new MouseListener() {
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    FloorEditor editor = (FloorEditor) e.getComponent();
                    editor.handleMouseAddWall(e.getPoint());
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                FloorEditor editor = (FloorEditor) e.getComponent();
                editor.mouseHoverGridPoint = null;
                repaint();
            }
        };
    }

    private MouseMotionListener buildMouseMotionListener() {
        return new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                FloorEditor editor = (FloorEditor) e.getComponent();
                editor.mouseHoverGridPoint = editor.pixelPointToGridPoint(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    editor.handleMouseAddWall(e.getPoint());
                }
            }

            public void mouseMoved(MouseEvent e) {
                FloorEditor editor = (FloorEditor) e.getComponent();
                editor.mouseHoverGridPoint = editor.pixelPointToGridPoint(e.getPoint());
                repaint();
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="Metodos pintado">
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackGroundGrid(g);
        paintFloorWalls(g);
        paintHighlightedPoints(g);
    }

    private void paintBackGroundGrid(Graphics g) {
        /*Pintar Lineas verticales*/
        for (int i = 0; i <= this.XSize; i++) {
            g.drawLine(gridToPixel(i), 0, gridToPixel(i), gridToPixel(YSize));
        }
        /*Pintar lineas horizontales*/
        for (int i = 0; i <= this.YSize; i++) {
            g.drawLine(0, gridToPixel(i), gridToPixel(XSize), gridToPixel(i));
        }
    }

    private void paintFloorWalls(Graphics g) {
        if (walls.size() > 0) {
            for (GridPoint w : walls) {
                drawRectangle(g, w, null, 0, Color.BLACK);
            }
        }
    }

    private void paintHighlightedPoints(Graphics g) {
        if (this.mouseHoverGridPoint != null) {
            drawRectangle(g, mouseHoverGridPoint, Color.BLUE, 2, null);
        }
    }

    private void drawRectangle(Graphics g, GridPoint gridPoint, Color borderColor, int border, Color fillColor) {

        Graphics2D g2 = (Graphics2D) g;
        int x = gridToPixel((int) gridPoint.x);
        int y = gridToPixel((int) gridPoint.y);

        if (fillColor != null) {
            g.setColor(fillColor);
            g.fillRect(x, y, pxResolution + 1, pxResolution + 1);
        }
        if (border > 0) {
            if (border > pxResolution / 2) {
                border = pxResolution / 2;
            }
            g2.setStroke(new BasicStroke(border));
            g.setColor(borderColor);
            g.drawRect(x + border / 2, y + border / 2, pxResolution - border + 1, pxResolution - border + 1);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters y Setters ">
    public int getXSize() {
        return XSize;
    }

    public int getYSize() {
        return YSize;
    }

    public int getPixelWidth() {
        return XSize * pxResolution;
    }

    public int getPixelHeight() {
        return YSize * pxResolution;
    }

    public void setXandYSize(int XSize, int YSize) {
        this.XSize = XSize;
        this.YSize = YSize;
        floorSizeChanged();
    }

    public void setErase(boolean erase) {
        this.erase = erase;
    }
    //</editor-fold>

    //<editor-fold desc="Metodos Estaticos">
    public static Node createFloorGeometryNode(String filePath, AssetManager assetManager) {
        FloorData floorData = FloorData.loadDataFromFile(filePath);
        Node floorNode = new Node();
        
        float floorResolution = 0.2f;
        Vector3f floorSize = new Vector3f(floorData.XSize * floorResolution, floorResolution, floorData.YSize * floorResolution);
        Utils.createCube(assetManager, new Vector3f(0, 0, 0).setY(-floorResolution), floorSize, floorNode, ColorRGBA.Gray);
        
        List<GridRegion> regions = GridRegion.floorWallPointsToRegions(floorData);
        for (GridRegion r : regions) {
            Vector3f pos = new Vector3f((r.startPoint.x * floorResolution) - (floorSize.x / 2), 0, (r.startPoint.y * floorResolution) - (floorSize.z / 2));
            pos.setX(pos.x + (floorResolution * r.width / 2));
            pos.setZ(pos.z + (floorResolution * r.height / 2));
            Vector3f size = new Vector3f(floorResolution * r.width, 1, floorResolution * r.height);
            Utils.createCube(assetManager, pos, size, floorNode, ColorRGBA.LightGray);
        }
        return floorNode;
    }
    //</editor-fold>
}
