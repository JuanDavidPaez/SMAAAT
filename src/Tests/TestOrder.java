package Tests;

import AI.Pathfinding.A_Star_Node;
import Utils.Const;
import Utils.Utils;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.FloorPoint;
import World3D.Floor.GridPoint;
import World3D.Floor.GridRegion;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestOrder extends SimpleApplication {

    public static void main(String[] args) {
        tests t = new tests();
    }

    public static void createGame() {

        TestOrder app = new TestOrder();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("SMAAAT");
        app.setSettings(settings);
        app.start();

    }

    @Override
    public void simpleInitApp() {

        FloorData fd = new FloorData(4, 4, 10);
        Floor3D f = new Floor3D(fd, assetManager);
        Node floorNode = f.getFloorNode();
        rootNode.attachChild(floorNode);

        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        flyCam.setMoveSpeed(20);
    }

    public void measureTime() {
        long t1 = System.currentTimeMillis();

        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestOrder.class.getName()).log(Level.SEVERE, null, ex);
        }

        long t2 = System.currentTimeMillis();
        long r = TimeUnit.MILLISECONDS.toMillis(t2 - t1);
        System.out.println(r);
    }
}
class tests {

    private A_Star_Node nodes[];
    List<Integer> openSet = new ArrayList<Integer>();
    private List<A_Star_Node> list = new ArrayList<A_Star_Node>();
    public tests() {


        /*newNode(0, 13.0f, 30.0f);
        newNode(1, 18.0f, 25.0f);
        newNode(2, 18.0f, 25.0f);
        newNode(3, 13.0f, 32.0f);
        newNode(4, 15.0f, 30.0f);
        newNode(5, 17.0f, 28.0f);
        newNode(6, 19.0f, 26.0f);
        newNode(7, 18.0f, 27.0f);
        newNode(8, 19.0f, 26.0f);
        newNode(9, 19.0f, 26.0f);
        newNode(10, 19.0f, 26.0f);
        newNode(11, 19.0f, 26.0f);
        newNode(12, 16.0f, 29.0f);
        newNode(13, 14.0f, 31.0f);
        newNode(14, 14.0f, 31.0f);
        newNode(15, 42.0f, 14.0f);
        newNode(16, 40.0f, 16.0f);
        newNode(17, 43.0f, 13.0f);
        newNode(18, 39.0f, 17.0f);
        newNode(19, 41.0f, 15.0f);
        newNode(20, 40.0f, 18.0f);
        newNode(21, 44.0f, 14.0f);
        newNode(22, 45.0f, 15.0f);
        newNode(23, 41.0f, 19.0f);
        newNode(24, 42.0f, 20.0f);
        newNode(25, 43.0f, 21.0f);
        newNode(26, 44.0f, 22.0f);
        newNode(27, 45.0f, 23.0f);
        newNode(28, 46.0f, 24.0f);
        newNode(29, 47.0f, 25.0f);
        newNode(30, 48.0f, 26.0f);
        newNode(31, 10.0f, 33.0f);
        newNode(32, 12.0f, 33.0f);
        newNode(33, 12.0f, 33.0f);*/

        newNode(31, 10.0f, 33.0f);
        newNode(0, 13.0f, 30.0f);
        newNode(1, 18.0f, 25.0f);
        newNode(2, 18.0f, 25.0f);
        newNode(3, 13.0f, 32.0f);
        newNode(4, 15.0f, 30.0f);
        newNode(5, 17.0f, 28.0f);
        newNode(6, 19.0f, 26.0f);
        newNode(7, 18.0f, 27.0f);
        newNode(8, 19.0f, 26.0f);
        newNode(9, 19.0f, 26.0f);
        newNode(10, 19.0f, 26.0f);
        newNode(11, 19.0f, 26.0f);
        newNode(12, 16.0f, 29.0f);
        newNode(13, 14.0f, 31.0f);
        newNode(14, 14.0f, 31.0f);
        newNode(15, 42.0f, 14.0f);
        newNode(16, 40.0f, 16.0f);
        newNode(17, 43.0f, 13.0f);
        newNode(18, 39.0f, 17.0f);
        newNode(19, 41.0f, 15.0f);
        newNode(20, 40.0f, 18.0f);
        newNode(21, 44.0f, 14.0f);
        newNode(22, 45.0f, 15.0f);
        newNode(23, 41.0f, 19.0f);
        newNode(24, 42.0f, 20.0f);
        newNode(25, 43.0f, 21.0f);
        newNode(26, 44.0f, 22.0f);
        newNode(27, 45.0f, 23.0f);
        newNode(28, 46.0f, 24.0f);
        newNode(29, 47.0f, 25.0f);
        newNode(30, 48.0f, 26.0f);
        
        newNode(32, 12.0f, 33.0f);
        //newNode(33, 12.0f, 33.0f);
        
        
        
        nodes = list.toArray(new A_Star_Node[0]);
        
        
        Collections.sort(openSet, new NodeCostComparator());
        System.out.println(openSet);
        for (Integer i : openSet) {
            System.out.print(nodes[i].getId() + ",");
        }

    }

    protected void newNode(int id, float h, float g) {
        A_Star_Node n = new A_Star_Node(id, 0, 0, A_Star_Node.AsNodeType.Empty);
        n.setGcostAndHcost(g, h);
        list.add(n);
        openSet.add(id);
    }

    protected class NodeCostComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Integer n1 = (Integer) o1;
            Integer n2 = (Integer) o2;

            //System.out.println(n1 + " " + n2);

            if (nodes[n1].getFcost() == nodes[n2].getFcost()) {
                if (nodes[n1].getHcost() == nodes[n2].getHcost()) {
                    return 0;
                } else {
                    return (nodes[n1].getHcost() > nodes[n2].getHcost()) ? 1 : -1;
                }
            } else {
                return (nodes[n1].getFcost() > nodes[n2].getFcost()) ? 1 : -1;
            }
        }
    }
}
