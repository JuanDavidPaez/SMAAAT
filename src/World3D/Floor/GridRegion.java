package World3D.Floor;

import java.util.ArrayList;
import java.util.List;

public class GridRegion {

    public GridPoint startPoint;
    public int width;
    public int height;

    public GridRegion(GridPoint startPoint, int width, int height) {
        this.startPoint = startPoint;
        this.width = width;
        this.height = height;
    }

    public static List<GridRegion> floorWallPointsToRegions(FloorData floorData) {
        List<GridRegion> regions = new ArrayList<GridRegion>();
        int[][] wallArray = new int[floorData.YSize][floorData.XSize];

        /*Inicialización en -1*/
        for (int i = 0; i < wallArray.length; i++) {
            for (int j = 0; j < wallArray[0].length; j++) {
                wallArray[i][j] = -1;
            }
        }
        /*Translado de puntos desde la lista a la matriz*/
        for (GridPoint w : floorData.getWalls()) {
            wallArray[w.y][w.x] = 0;
        }

        int ySize = wallArray.length;
        int xSize = wallArray[0].length;
        int xid = 0, yid = 0;
        /*Busqueda de lineas horizontales*/
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (wallArray[y][x] == 0) {
                    boolean ok = false;
                    ok = (x < xSize - 1 && wallArray[y][x + 1] == 0);
                    ok = (ok || (x > 0 && wallArray[y][x - 1] == 1));
                    if (ok) {
                        wallArray[y][x] = 1;
                    }
                }
            }
        }
        /*Busqueda de lineas verticales*/
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                if (wallArray[y][x] == 0) {
                    wallArray[y][x] = 2;
                } else if (wallArray[y][x] == 1) {
                    boolean ok = false;
                    ok = (y > 0 && y < ySize - 1 && wallArray[y - 1][x] == 2 && wallArray[y + 1][x] == 0);
                    if (ok) {
                        wallArray[y][x] = 2;
                    }
                }
            }
        }
        /*Regiones Horizontales*/
        for (int y = 0; y < ySize; y++) {
            GridRegion gr = null;
            for (int x = 0; x < xSize; x++) {
                if (wallArray[y][x] == 1) {
                    if (gr == null) {
                        gr = new GridRegion(new GridPoint(x, y), 1, 1);
                    } else {
                        gr.width++;
                    }
                } else {
                    if (gr != null) {
                        regions.add(gr);
                        gr = null;
                    }
                }
            }
            if (gr != null) {
                regions.add(gr);
            }
        }

        /*Regiones Verticales*/
        for (int x = 0; x < xSize; x++) {
            GridRegion gr = null;
            for (int y = 0; y < ySize; y++) {
                if (wallArray[y][x] == 2) {
                    if (gr == null) {
                        gr = new GridRegion(new GridPoint(x, y), 1, 1);
                    } else {
                        gr.height++;
                    }
                } else {
                    if (gr != null) {
                        regions.add(gr);
                        gr = null;
                    }
                }
            }
            if (gr != null) {
                regions.add(gr);
            }
        }

        /*DEPURACION*/
        /*
         for (int i = 0; i < wallArray.length; i++) {
         System.out.print("\r\n");
         for (int j = 0; j < wallArray[0].length; j++) {
         String c = " ";
         if (wallArray[i][j] == 1) {
         c = "H";
         } else if (wallArray[i][j] == 2) {
         c = "V";
         }

         System.out.print(c);
         }
         }
         System.out.println("");
         for (GridRegion r : regions) {
         System.out.println("start: " + r.startPoint.x + " " + r.startPoint.y + "  w:" + r.width + " h:" + r.height);
         }
         */
        return regions;
    }
}
