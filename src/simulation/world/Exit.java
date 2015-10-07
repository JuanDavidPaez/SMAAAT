package simulation.world;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
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
import simulation.Agent.HostageAgent;
import simulation.SmaaatApp;
import simulation.utils.Const;

public class Exit implements Savable, PhysicsCollisionListener {

    protected SmaaatApp app;
    protected Node node;
    Vector3f size;
    private int hostagesRescued = 0;

    public Exit(SmaaatApp app, Vector3f position, Vector3f size) {

        String name = Const.Exit;
        this.size = size;
        this.app = app;
        this.node = new Node("Node_" + name);
        Vector3f pos = position.clone().addLocal(0,size.y,0);
        node.setLocalTranslation(pos);
        node.attachChild(createSpatialGeometry(name));

        ((Spatial) (node)).setUserData(Const.Exit, this);

        setupPhysics();

        app.getCharacterNode().attachChild(node);
    }

    protected Geometry createSpatialGeometry(String name) {
        Box s = new Box(size.x, size.y, size.z);
        Geometry g = new Geometry(name, s);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        g.setMaterial(mat);
        return g;
    }

    private void setupPhysics() {
        
        BoxCollisionShape box = new BoxCollisionShape(size);
        GhostControl ghostControl = new GhostControl(box);
        ghostControl.setPhysicsLocation(new Vector3f(20,5,2));
        node.addControl(ghostControl);
        app.getPhysicsSpace().add(ghostControl);
        app.getPhysicsSpace().addCollisionListener(this);

    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() == this.node || event.getNodeB() == this.node) {
            if (event.getNodeA().getUserData(Const.Character) != null || 
                    event.getNodeB().getUserData(Const.Character) != null) {
                Spatial s = (event.getNodeA() != this.node) ? event.getNodeA() : event.getNodeB();
                Object c = s.getUserData(Const.Character);
                if(c instanceof HostageAgent)
                {
                    hostagesRescued++;
                    ((HostageAgent)c).hostageRescued();
                }
            }
        }
    }
}
