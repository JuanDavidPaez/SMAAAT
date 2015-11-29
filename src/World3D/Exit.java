package World3D;

import SMA.Agents.Agent;
import Utils.Config;
import Utils.Const;
import Utils.Utils;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.io.IOException;

public class Exit implements Savable, PhysicsCollisionListener {

    protected WorldApp app;
    protected Node node;
    private int hostagesRescued = 0;

    public Exit(WorldApp app, Vector3f position, Vector3f size, Node floorNode, boolean horizontal) {

        String name = Const.Exit;
        this.app = app;
        this.node = new Node(Const.NodePrefix + name);
        node.setLocalTranslation(new Vector3f(position.x, position.y + (size.y / 2.0f), position.z));
        createSpatialGeometry(position, size);

        ((Spatial) (node)).setUserData(Const.Exit, this);

        setupPhysics(size, horizontal);

        floorNode.attachChild(node);
    }

    protected void createSpatialGeometry(Vector3f position, Vector3f size) {
        Utils.createCube(node.getName(), app.getAssetManager(), new Vector3f(0, -size.y / 2.0f, 0), size, node, ColorRGBA.Magenta);
    }

    private void setupPhysics(Vector3f size, boolean horizontal) {
        Vector3f _size = size.clone();
        _size.divideLocal(2.0f);
        if (horizontal) {
            _size.addLocal(0, 0, Config.FloorGridCellSize);
        } else {
            _size.addLocal(Config.FloorGridCellSize, 0, 0);
        }

        BoxCollisionShape box = new BoxCollisionShape(_size);
        GhostControl ghostControl = new GhostControl(box);
        node.addControl(ghostControl);
        app.getPhysicsSpace().add(ghostControl);
        app.getPhysicsSpace().addCollisionListener(this);
    }

    public void write(JmeExporter ex) throws IOException {
    }

    public void read(JmeImporter im) throws IOException {
    }

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() == this.node || event.getNodeB() == this.node) {
            if (event.getNodeA().getUserData(Const.Character) != null
                    || event.getNodeB().getUserData(Const.Character) != null) {
                Spatial s = (event.getNodeA() != this.node) ? event.getNodeA() : event.getNodeB();
                Character3D c = s.getUserData(Const.Character);
                if (c.getAgentType() == Agent.AgentType.Hostage) {
                    hostagesRescued++;
                    c.hostageReachExit();
                }
            }
        }
    }
}
