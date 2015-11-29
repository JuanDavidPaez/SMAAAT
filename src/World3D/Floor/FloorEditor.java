package World3D.Floor;

import World3D.Floor.FloorPoint.FloorPointType;
import com.google.gson.Gson;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FloorEditor extends JPanel {

    // <editor-fold desc="Propiedades y atributos">
    public static String fileExtension = "smaaat";
    protected FloorData floorData;
    private GridPoint mouseHoverGridPoint = null;
    private boolean erase;
    private boolean allowEdition = true;
    // </editor-fold>

    //<editor-fold desc="Metodos">
    public FloorEditor() {
        floorData = new FloorData(10, 17, 10);
        init();
    }

    public FloorEditor(FloorData fData) {
        floorData = fData;
        init();
    }

    private void init() {
        this.setXandYSize(floorData.XSize, floorData.YSize);
        this.addKeyListener(buildKeyListener());
        this.addMouseListener(buildMouseListener());
        this.addMouseMotionListener(buildMouseMotionListener());
    }

    private int gridToPixel(int i) {
        return i * getPixelResolution();
    }

    private GridPoint pixelPointToGridPoint(Point p) {
        int x = p.x / getPixelResolution();
        int y = p.y / getPixelResolution();
        if (x >= this.getXSize() || y >= this.getYSize()) {
            return null;
        } else {
            return new GridPoint(x, y);
        }
    }

    private void addWall(GridPoint p) {
        floorData.addWall(p);
    }

    private void removeWall(GridPoint p) {
        floorData.removeWall(p);
    }

    private void floorSizeChanged() {
        this.setPreferredSize(new Dimension(getPixelWidth() + 10, getPixelHeight() + 10));
        this.repaint();
    }

    public void addFloorSurroundingWalls() {
        for (int i = 0; i < getXSize(); i++) {
            addWall(new GridPoint(i, 0));
            addWall(new GridPoint(i, getYSize() - 1));
        }
        for (int i = 0; i < getYSize(); i++) {
            addWall(new GridPoint(0, i));
            addWall(new GridPoint(getXSize() - 1, i));
        }
    }

    public void saveFile(String path) {

        if (path != null) {
            String filePath = path;
            String ext = ("." + fileExtension);
            if (!filePath.endsWith(ext)) {
                filePath += ext;
            }

            Gson gson = new Gson();
            String jsonData = gson.toJson(this.floorData, FloorData.class);

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

        FloorData fData = FloorData.loadDataFromFile(filePath);

        if (fData != null) {
            floorData = new FloorData(fData.XSize, fData.YSize, fData.pxResolution);
            if (fData.points != null && fData.points.length > 0) {
                for (FloorPoint p : fData.points) {
                    this.floorData.setPointType(p, p.getType());
                }
            }
            repaint();
        }
    }

    public void startPaintLoop() {
        final FloorEditor panel = this;
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FloorEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    panel.repaint();
                }
            }
        };
        r.run();
    }
    //</editor-fold>

    //<editor-fold desc="Mouse and keyboard listeners">
    private void handleMouseAddWall(Point mousePoint) {
        GridPoint p = pixelPointToGridPoint(mousePoint);
        if (allowEdition) {
            if (!erase) {
                addWall(p);
            } else {
                removeWall(p);
            }
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

    private KeyListener buildKeyListener() {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="Metodos pintado">
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintFloorPoints(g);
        paintAditionalPaths(g);
        paintAditionalPoints(g);
        paintBackGroundGrid(g);
        paintHighlightedPoints(g);
    }

    protected void paintBackGroundGrid(Graphics g) {
        g.setColor(Color.BLACK);
        /*Pintar Lineas verticales*/
        for (int i = 0; i <= this.getXSize(); i++) {
            g.drawLine(gridToPixel(i), 0, gridToPixel(i), gridToPixel(getYSize()));
        }
        /*Pintar lineas horizontales*/
        for (int i = 0; i <= this.getYSize(); i++) {
            g.drawLine(0, gridToPixel(i), gridToPixel(getXSize()), gridToPixel(i));
        }
    }

    protected void paintFloorPoints(Graphics g) {
        for (FloorPoint p : this.floorData.points) {
            paintFloorPoint(p, g);
        }
    }

    protected void paintAditionalPaths(Graphics g) {
        if (floorData.getPaths() != null && floorData.getPaths().size() > 0) {
            for (Map.Entry<String, Path> e : floorData.getPaths().entrySet()) {
                Path p = e.getValue();
                if (p != null && p.points.size() > 0) {
                    for (GridPoint gp : p.points) {
                        drawRectangle(g, gp, p.color, 0, p.color);
                    }
                }
            }
        }
    }

    protected void paintAditionalPoints(Graphics g) {
        if (floorData.pointsSupplier != null) {
            List<FloorPoint> pointList = floorData.pointsSupplier.getPointsToPaint();

            if (pointList != null && pointList.size() > 0) {
                for (FloorPoint p : pointList) {
                    paintFloorPoint(p, g);
                }
            }
        }
    }

    protected void paintHighlightedPoints(Graphics g) {
        if (this.mouseHoverGridPoint != null) {
            drawRectangle(g, mouseHoverGridPoint, Color.BLUE, 2, null);

            int x = this.gridToPixel(mouseHoverGridPoint.x) + (int) (floorData.pxResolution * 1);
            int y = this.gridToPixel(mouseHoverGridPoint.y) + (int) (floorData.pxResolution * 1);
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial Bold", Font.BOLD, 10));
            g.drawString(mouseHoverGridPoint.toString(), x, y);
        }
    }

    private void paintFloorPoint(FloorPoint p, Graphics g) {
        Color color = null;

        if (p.getType() == FloorPointType.Wall) {
            color = Color.BLACK;
        } else if (p.getType() == FloorPointType.Empty) {
            color = Color.WHITE;
        } else if (p.getType() == FloorPointType.Unknown) {
            color = Color.GRAY;
        }else if (p.getType() == FloorPointType.Character){
            color = Color.MAGENTA;
        }else if (p.getType() == FloorPointType.Myself){
            color = Color.blue;
        }  else {
            throw new RuntimeException("No se ha especificado color para el tipo de objeto " + p.getType());
        }
        drawRectangle(g, p, null, 0, color);
    }

    protected void drawRectangle(Graphics g, GridPoint gridPoint, Color borderColor, int border, Color fillColor) {
        if (gridPoint.x > this.getXSize() - 1 || gridPoint.y > this.getYSize() - 1) {
            throw new RuntimeException("El punto que se quiere pintar se encuentra por fuera del área de la grilla.");
        }

        Graphics2D g2 = (Graphics2D) g;
        int x = gridToPixel((int) gridPoint.x);
        int y = gridToPixel((int) gridPoint.y);

        if (fillColor != null) {
            g.setColor(fillColor);
            g.fillRect(x, y, getPixelResolution() + 1, getPixelResolution() + 1);
        }
        if (border > 0) {
            if (border > getPixelResolution() / 2) {
                border = getPixelResolution() / 2;
            }
            g2.setStroke(new BasicStroke(border));
            g.setColor(borderColor);
            g.drawRect(x + border / 2, y + border / 2, getPixelResolution() - border + 1, getPixelResolution() - border + 1);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters y Setters ">
    public int getPixelResolution() {
        return floorData.pxResolution;
    }

    public int getXSize() {
        return floorData.XSize;
    }

    public int getYSize() {
        return floorData.YSize;
    }

    public int getPixelWidth() {
        return getXSize() * getPixelResolution();
    }

    public int getPixelHeight() {
        return getYSize() * getPixelResolution();
    }

    public void setXandYSize(int XSize, int YSize) {
        this.floorData.XSize = XSize;
        this.floorData.YSize = YSize;
        floorSizeChanged();
    }

    public void setErase(boolean erase) {
        this.erase = erase;
    }

    public void setAllowEdition(boolean b) {
        this.allowEdition = b;
    }
    //</editor-fold>
}
