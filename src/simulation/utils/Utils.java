package simulation.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class Utils {

    public static Geometry createDebugArrow(AssetManager assetManager, Vector3f pos, Vector3f dir, Node node) {
        Arrow arrow = new Arrow(Vector3f.UNIT_Z.mult(dir.length()));
        arrow.setLineWidth(3);
        Geometry mark = new Geometry("DebugArrow", arrow);
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
        Geometry mark = new Geometry("DebugBox", s);
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
        Geometry mark = new Geometry("DebugSphere", s);
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

    public static int randomInteger(int min, int max) {
        XSRandom rand = new XSRandom();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
