package World3D.Floor;

import Utils.Config;
import Utils.Const;
import Utils.Utils;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;

public class Floor3D {

    public static float FloorGridCellSize = Config.FloorGridCellSize;
    private FloorData floorData;
    private Node floorNode;

    public Floor3D(String floorFilePath, AssetManager assetManager, Node floorNode) {
        this.floorNode = floorNode;
        this.floorData = FloorData.loadDataFromFile(floorFilePath);
        createFloorGeometryNode(assetManager);
    }

    public Floor3D(FloorData floorData, AssetManager assetManager, Node floorNode) {
        this.floorNode = floorNode;
        this.floorData = floorData;
        createFloorGeometryNode(assetManager);
    }
    private void createFloorGeometryNode( AssetManager assetManager) {

        Vector3f floorPosition = new Vector3f(0, 0, 0).setY(-FloorGridCellSize);
        Vector3f floorSize = new Vector3f(floorData.XSize * FloorGridCellSize, FloorGridCellSize, floorData.YSize * FloorGridCellSize);
        Utils.createCube("BaseFloorPlane",assetManager, floorPosition, floorSize, floorNode, ColorRGBA.Gray);

        List<GridRegion> regions = GridRegion.floorWallPointsToRegions(floorData);
        for (GridRegion r : regions) {
            Vector3f pos = new Vector3f((r.startPoint.x * FloorGridCellSize) - (floorSize.x / 2.0f), 0, (r.startPoint.y * FloorGridCellSize) - (floorSize.z / 2.0f));
            pos.setX(pos.x + (FloorGridCellSize * r.width / 2.0f));
            pos.setZ(pos.z + (FloorGridCellSize * r.height / 2.0f));
            Vector3f size = new Vector3f(FloorGridCellSize * r.width, 1, FloorGridCellSize * r.height);
            Utils.createCube("Wall",assetManager, pos, size, floorNode, ColorRGBA.LightGray);
        }

        Utils.createGrid(assetManager, floorData.XSize, floorData.YSize, FloorGridCellSize, new Vector3f(0, 0, 0).setY(0.005f), floorNode, ColorRGBA.Blue);
    }

    public Node getFloorNode() {
        return floorNode;
    }

    public FloorData getFloorData() {
        return floorData;
    }
    
    public GridPoint Vector3fToGridPoint(Vector3f v) {
        return Vector3fToGridPoint(v, floorData.XSize, floorData.YSize);
    }

    public static GridPoint Vector3fToGridPoint(Vector3f v, int xSize, int ySize) {
        float x = v.x;
        float y = v.z;
        x += (FloorGridCellSize * xSize / 2.0f);
        y += (FloorGridCellSize * ySize / 2.0f);
        return new GridPoint((int) (x / FloorGridCellSize), (int) (y / FloorGridCellSize));
    }

    public Vector3f GridPointToVector3f(GridPoint p) {
        return GridPointToVector3f(p, floorData.XSize, floorData.YSize);
    }

    public static Vector3f GridPointToVector3f(GridPoint p, int xSize, int ySize) {
        float x = (p.x * FloorGridCellSize) + (FloorGridCellSize/2.0f);
        float z = (p.y * FloorGridCellSize) + (FloorGridCellSize/2.0f);
        x -= (FloorGridCellSize * xSize / 2.0f);
        z -= (FloorGridCellSize * ySize / 2.0f);
        return new Vector3f(x, 0, z);
    }

    public FloorDataChunk getFloorDataPartialView(GridPoint center, float viewRadius) {
        int radius = (int)(viewRadius/Config.FloorGridCellSize);
        return floorData.getFloorDataChunk(center, radius);
    }
}
