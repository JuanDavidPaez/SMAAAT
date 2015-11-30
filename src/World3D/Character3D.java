package World3D;

import SMA.Agents.Agent;
import SMA.Agents.Agent.AgentType;
import SMA.Agents.Attributes;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.ShootData;
import SMA.GuardsData.WorldInfoData;
import Utils.Config;
import Utils.Const;
import Utils.Utils;
import World3D.Controls.BulletControl;
import World3D.Controls.WalkerNavControl;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorDataChunk;
import World3D.Floor.GridPoint;
import World3D.Object3D;
import World3D.WorldApp;
import com.jme3.bounding.BoundingSphere;
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
import java.util.Date;
import java.util.List;

public class Character3D implements Savable {

    protected WorldApp app;
    protected Node node;
    protected float radius = Config.CharacterRadius;
    protected float height = radius * 2.0f;
    protected float mass = 15.0f;
    protected String agentAlias;
    protected AgentType agentType;
    protected Date lastShootTime;
    public int shootingRestTime = 200;
    protected static Sphere bullet;
    protected static Material bulletMaterial;
    protected float bulletRadius = radius / 5;
    protected float bulletSpeed = 3;
    protected int live = 10;
    protected Attributes attributes;

    /*variables modificadas entre hilos*/
    protected Vector3f shootingTarget;
    protected volatile WorldInfoData worldInfoDataRequest;

    public Character3D(WorldApp app, String name, Vector3f position, Vector3f direction, AgentType type, Attributes attr) {
        this.app = app;
        this.node = new Node(Agent.getAgentNodeName(name));
        this.agentAlias = name;
        this.agentType = type;
        this.attributes = attr;

        node.setLocalTranslation(position);
        node.attachChild(createSpatialGeometry(name));

        Utils.createDebugArrow(app.getAssetManager(), new Vector3f(0, height / 2, 0), new Vector3f(0, 0, 0.5f), node);
        Utils.createCircle(app.getAssetManager(), new Vector3f(0, Const.FloorGridHeight * 2, 0), getSightRange() + radius, node, Utils.getColorForAgentGeometry(type));

        ((Spatial) (node)).setUserData(Const.Character, this);

        setupPhysics();
        setupControllers(direction);
        setupShootingProperties();

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

    private void setupShootingProperties() {
        shootingRestTime = Utils.randomInteger(100, 300);
        bullet = new Sphere(4, 4, bulletRadius);
        bulletMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMaterial.setColor("Color", ColorRGBA.Red);
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

    public synchronized void update(float tpf) {
        if (live > 0) {
            if (shootingTarget != null) {
                shootBullet(shootingTarget);
                shootingTarget = null;
            }
            searchCollisions();
            if (worldInfoDataRequest != null) {
                sendWorldInfoDataResponse(worldInfoDataRequest);
                worldInfoDataRequest = null;
            }
        }
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
        return attributes.speed;
    }

    public int getLive() {
        return live;
    }

    public String getAgentAlias() {
        return agentAlias;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public float getSightRange() {
        return attributes.sightRange;
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

    public synchronized void shoot(ShootData sd) {
        shootingTarget = sd.target;
    }

    public synchronized void enqueueWorldInfoRequest(WorldInfoData wid) {
        this.worldInfoDataRequest = wid;
    }

    public FloorDataChunk getCurrentFloorView() {
        Vector3f position = getPosition();
        GridPoint p = getFloor3D().Vector3fToGridPoint(position);
        return getFloor3D().getFloorDataPartialView(p, getSightRange());
    }

    public List<Object3D> getSeenCharacters() {
        List<Spatial> characters = app.getCharacterNode().getChildren();
        List<Object3D> seenObjects = new ArrayList<Object3D>();
        if (characters != null && characters.size() > 0) {
            Vector3f myPosition = getPosition().clone();
            myPosition.setY(height / 2.0f);
            for (Spatial s : characters) {
                if (!s.equals(this.node) && !s.getName().toLowerCase().contains(Const.DebugWord)) {
                    Vector3f charPosition = s.getWorldTranslation().clone();
                    charPosition.setY(myPosition.y);
                    if(objectIsInsideSighRange(myPosition, charPosition)){
                        /*Corrimiento del punto fuera del volumen de la geometria del character*/
                        Vector3f direction = charPosition.subtract(myPosition).normalize();
                        Vector3f rayOrigin = myPosition.add(direction.mult(radius + (radius * 0.1f)));

                        Ray r = new Ray(rayOrigin, direction);
                        r.setLimit(getSightRange());
                        Vector3f dir = direction.mult(getSightRange());

                        CollisionResults results = new CollisionResults();
                        app.getNoneBulletsNode().collideWith(r, results);
                        if (results.size() > 0) {
                            CollisionResult cr = results.getClosestCollision();
                            String name = cr.getGeometry().getParent().getName();

                            if (name.equals(s.getName())) {
                                Character3D c = s.getUserData(Const.Character);
                                seenObjects.add(new Object3D(c.agentAlias, charPosition, c.agentType));
                            }
                        }
                    }
                }
            }
        }
        return seenObjects;
    }

    private boolean objectIsInsideSighRange(Vector3f _myPos, Vector3f _objPos) {
        float range = (this.getSightRange() + this.radius);
        boolean ok = false;
        Floor3D f = getApp().getFloor3D();
        Vector3f myPos = f.GridPointToVector3f(f.Vector3fToGridPoint(_myPos));
        Vector3f objPos = f.GridPointToVector3f(f.Vector3fToGridPoint(_objPos));
        float distance = myPos.distance(objPos);
        ok = (distance <= range);
        return ok;
    }

    private boolean isAbleToShoot() {
        boolean ok = true;
        Date d = new Date();
        if (this.lastShootTime == null) {
            this.lastShootTime = d;
        } else {
            long difference = d.getTime() - lastShootTime.getTime();
            if (difference < shootingRestTime) {
                ok = false;
            } else {
                lastShootTime = d;
            }
        }
        return ok;
    }

    private void shootBullet(Vector3f target) {
        if (!isAbleToShoot()) {
            return;
        }
        Geometry ball_geo = new Geometry(Const.Bullet, bullet);
        ball_geo.setMaterial(bulletMaterial);
        app.getBulletsNode().attachChild(ball_geo);

        Vector3f position = getPosition().clone();
        Vector3f direction = target.subtract(position).normalize();

        this.node.getControl(WalkerNavControl.class).setViewDirection(direction);


        /*Ajuste de posicion para que la bala este fuera de la geometria del agente*/
        position.addLocal(direction.mult(radius + (bulletRadius * 2)));
        ball_geo.setLocalTranslation(position);

        BulletControl ball_phy = new BulletControl(1f);
        ball_geo.addControl(ball_phy);
        app.getPhysicsSpace().add(ball_phy);
        ball_phy.setLinearVelocity(direction.mult(bulletSpeed));
        ball_phy.setGravity(Vector3f.ZERO.setY(-0.01f));
    }

    protected void searchCollisions() {
        CollisionResults results = new CollisionResults();
        BoundingSphere bs = new BoundingSphere(this.radius, getPosition());
        app.getBulletsNode().collideWith(bs, results);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Geometry g = closest.getGeometry();
            app.getPhysicsSpace().remove(g.getControl(BulletControl.class));
            g.removeControl(BulletControl.class);
            g.removeFromParent();
            hitttedByBullet(closest);
        }
    }

    protected void hitttedByBullet(CollisionResult collision) {
        live--;
        getApp().getWorldAgent().notifyAgentHitttedByBullet(this, collision);
        if (live <= 0) {
            removeFromWorld();
        }
    }

    protected void removeFromWorld() {
        WalkerNavControl c = this.node.getControl(WalkerNavControl.class);
        c.setEnabled(false);
        this.node.removeFromParent();
        this.app.getPhysicsSpace().remove(this.node.getControl(BetterCharacterControl.class));
    }

    protected void sendWorldInfoDataResponse(WorldInfoData w) {
        WorldInfoData wid = w.clone();
        wid.position = this.getPosition();
        wid.direction = this.getDirection();
        wid.partialFloorView = this.getCurrentFloorView();
        wid.seenObjects = this.getSeenCharacters();
        getApp().getWorldAgent().notifyAgentWorldInfoData(this, wid);
    }

    public void hostageReachExit() {
        getApp().getWorldAgent().notifyHostageReachExit(this);
        removeFromWorld();
    }
}
