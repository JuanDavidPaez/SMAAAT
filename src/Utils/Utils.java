package Utils;

import SMA.Agents.Agent;
import SMA.Agents.Agent.AgentType;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static Geometry createDebugArrow(AssetManager assetManager, Vector3f pos, Vector3f dir, Node node) {
        Arrow arrow = new Arrow(Vector3f.UNIT_Z.mult(dir.length()));
        arrow.setLineWidth(3);
        Geometry mark = new Geometry(Const.DebugWord + "Arrow", arrow);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mat);
        mark.setLocalTranslation(pos);

        Quaternion q = new Quaternion();
        q.lookAt(dir, Vector3f.UNIT_Y);
        mark.setLocalRotation(q);

        if (node != null) {
            node.attachChild(mark);
        }
        return mark;
    }

    public static Geometry createDebugBox(AssetManager assetManager, Vector3f pos, float side, Node node) {
        Box s = new Box(side, side, side);
        Geometry mark = new Geometry(Const.DebugWord + "Box", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        mat.setColor("Color", ColorRGBA.Blue);
        mark.setMaterial(mat);
        mark.setLocalTranslation(pos);
        if (node != null) {
            node.attachChild(mark);
        }
        return mark;
    }

    public static Geometry createDebugSphere(AssetManager assetManager, Vector3f pos, float radius, Node node) {
        Sphere s = new Sphere(10, 10, radius);
        Geometry mark = new Geometry(Const.DebugWord + "Sphere", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        mat.setColor("Color", ColorRGBA.Green);
        mark.setMaterial(mat);
        mark.setLocalTranslation(pos);
        if (node != null) {
            node.attachChild(mark);
        }
        return mark;
    }

    public static Geometry createCube(String name, AssetManager assetManager, Vector3f pos, Vector3f size, Node node, ColorRGBA color) {

        Vector3f _size = size.clone().divideLocal(2);
        Vector3f _pos = pos.clone();
        _pos.setY(_pos.y + _size.y);
        Box s = new Box(_size.x, _size.y, _size.z);
        Geometry geo = new Geometry(name, s);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        geo.setLocalTranslation(_pos);
        if (node != null) {
            node.attachChild(geo);
        }
        return geo;
    }

    public static Geometry createGrid(AssetManager assetManager, int xSize, int ySize, float resolution, Vector3f pos, Node node, ColorRGBA color) {
        Vector3f _pos = pos.clone();
        _pos.setX(_pos.x - (xSize / 2.0f * resolution));
        _pos.setZ(_pos.z - (ySize / 2.0f * resolution));
        Grid grid = new Grid(ySize + 1, xSize + 1, resolution);
        Geometry g = new Geometry(Const.DebugWord + "Grid", grid);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setLocalTranslation(_pos);
        if (node != null) {
            node.attachChild(g);
        }
        return g;
    }

    public static int randomInteger(int min, int max) {
        XSRandom rand = new XSRandom();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static String getResourceFilePath(String fileName) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        File f = new File(classLoader.getResource(fileName).getFile());
        return f.getAbsolutePath();
    }

    public static Integer[] randomNumbersArray(int amount, int min, int max) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = min; i < max; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        Integer[] vector = new Integer[amount];
        if (amount > max) {
            amount = max;
        }
        for (int i = 0; i < amount; i++) {
            vector[i] = list.get(i);
        }
        return vector;
    }

    public static int[] integerListToIntArray(List<Integer> l) {
        if (l != null && l.size() > 0) {
            int[] a = new int[l.size()];
            int c = 0;
            for (Integer i : l) {
                a[c] = i;
                c++;
            }
            return a;
        }
        return null;
    }

    public static ColorRGBA getColorForAgentGeometry(AgentType type) {
        ColorRGBA color = null;
        if (type == Agent.AgentType.Enemy) {
            color = ColorRGBA.Red;
        }
        if (type == Agent.AgentType.Explorer) {
            color = ColorRGBA.Green;
        }
        if (type == Agent.AgentType.Hostage) {
            color = ColorRGBA.Yellow;
        }
        if (type == Agent.AgentType.Protector) {
            color = ColorRGBA.Blue;
        }
        return color;
    }
}
