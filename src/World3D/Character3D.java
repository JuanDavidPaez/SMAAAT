package World3D;

import SMA.Agents.Agent.AgentType;
import SMA.GuardsData.MoveData;
import Utils.Const;
import Utils.Utils;
import World3D.Controls.WalkerNavControl;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorDataChunk;
import World3D.Floor.GridPoint;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Character3D implements Savable {

    protected WorldApp app;
    protected Node node;
    protected float radius = Const.CharacterRadius;
    protected float height = radius * 2.0f;
    protected float sightRange = (2.0f * Const.FloorGridCellSize);
    protected float mass = 15.0f;
    protected float speed = 2;
    protected String agentAlias;
    protected AgentType agentType;

    public Character3D(WorldApp app, String name, Vector3f position, Vector3f direction, AgentType type) {
        this.app = app;
        this.node = new Node(Utils.GetNodeName(name));
        this.agentAlias = name;
        this.agentType = type;
        
        node.setLocalTranslation(position);
        node.attachChild(createSpatialGeometry(name));
        Spatial arrow = Utils.createDebugArrow(app.getAssetManager(), Vector3f.ZERO, new Vector3f(0, 0, 0.5f), node);
        arrow.getLocalTranslation().setY(height / 2);

        ((Spatial) (node)).setUserData(Const.Character, this);

        setupPhysics();
        setupControllers(direction);

        app.getCharacterNode().attachChild(node);
    }

    private void setupPhysics() {
        BetterCharacterControl physicsCharacter = new BetterCharacterControl(radius, height, mass);
        node.addControl(physicsCharacter);
        app.getPhysicsSpace().add(physicsCharacter);
    }

    private void setupControllers(Vector3f direction) {
        WalkerNavControl wNavControl = new WalkerNavControl(this, direction);
        node.addControl(wNavControl);
    }

    protected Geometry createSpatialGeometry(String name) {
        Sphere s = new Sphere(10, 10, this.radius);
        Geometry g = new Geometry(name, s);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        ColorRGBA color = Utils.getColorForAgentGeometry(this.agentType);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setLocalTranslation(0, this.height / 2, 0);
        return g;
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(float tpf) {
    }

    public WorldApp getApp() {
        return app;
    }

    public Floor3D getFloor3D() {
        return getApp().getFloor3D();
    }

    public float getRadius() {
        return radius;
    }

    public float getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public Vector3f getPosition() {
        Vector3f position = node.getWorldTranslation().clone();
        position.setY(this.getHeight() / 2);
        return position;
    }

    public Vector3f getDirection() {
        BetterCharacterControl control = node.getControl(BetterCharacterControl.class);
        Vector3f direction = control.getViewDirection().normalize().clone();
        return direction;
    }

    public void moveCharacter(MoveData md) {
        WalkerNavControl control = node.getControl(WalkerNavControl.class);
        control.enqueueMovementRequest(md);
    }

    public FloorDataChunk getCurrentFloorView() {
        Vector3f position = node.getWorldTranslation();
        GridPoint p = getFloor3D().Vector3fToGridPoint(position);
        return getFloor3D().getFloorDataPartialView(p, sightRange);
    }

    public List<Object3D> getSeenCharacters() {
        List<Spatial> characters = app.getCharacterNode().getChildren();
        List<Object3D> seenObjects = new ArrayList<Object3D>();
        if (characters != null && characters.size() > 0) {
            Vector3f myPosition = this.node.getWorldTranslation().clone();
            myPosition.setY(height / 2.0f);
            for (Spatial s : characters) {
                if (!s.equals(this.node) && !s.getName().toLowerCase().contains(Const.DebugWord)) {
                    Vector3f charPosition = s.getWorldTranslation().clone();
                    charPosition.setY(myPosition.y);
                    float distance = myPosition.distance(charPosition);
                    if (distance <= (this.sightRange + this.radius)) {
                        /*Corrimiento del punto fuera del volumen de la geometria del character*/
                        Vector3f direction = charPosition.subtract(myPosition).normalize();
                        Vector3f rayOrigin = myPosition.add(direction.mult(radius + (radius * 0.1f)));

                        Ray r = new Ray(rayOrigin, direction);
                        r.setLimit(sightRange);
                        Vector3f dir = direction.mult(sightRange);

                        CollisionResults results = new CollisionResults();
                        app.getRootNode().collideWith(r, results);
                        if (results.size() > 0) {
                            CollisionResult cr = results.getClosestCollision();
                            String name = cr.getGeometry().getParent().getName();
                            
                            if (name.equals(s.getName())) {
                                Character3D c =  s.getUserData(Const.Character);
                                seenObjects.add(new Object3D(c.agentAlias, charPosition, c.agentType));
                            }
                        }
                    }
                }
            }
        }
        return seenObjects;
    }
}
