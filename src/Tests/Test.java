package Tests;

import BESA.Kernel.System.AdmBESA;
import Utils.Circle3d;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test extends SimpleApplication {

    public static void main(String[] args) {
        //createGame();
    }

    public static void createGame() {

        Test app = new Test();
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
        Floor3D f = new Floor3D(fd, this, rootNode);
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
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

        long t2 = System.currentTimeMillis();
        long r = TimeUnit.MILLISECONDS.toMillis(t2 - t1);
        System.out.println(r);
    }
}
