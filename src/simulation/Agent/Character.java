package simulation.Agent;

import BESA.ExceptionBESA;
import BESA.Kernell.System.AdmBESA;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.Controls.BulletControl;
import simulation.SmaaatApp;
import simulation.Controls.WalkerNavControl;
import simulation.utils.Const;
import simulation.utils.SpatialSeenObject;
import simulation.utils.Utils;

public abstract class Character implements Savable, PhysicsCollisionListener {

    protected SmaaatApp app;
    protected Node node;
    protected float radius = 0.2f;
    protected float height = 0.4f;
    protected float sightRange = 2f;
    protected float mass = 15.0f;
    protected float live = 10;
    protected float speed = 3;
    protected static Sphere bullet;
    protected float bulletRadius = 0.05f;
    protected float bulletSpeed = 10;
    protected Date lastShootTime;
    protected int shootingRestTime = 200;
    protected boolean left_handed = true;
    protected boolean allowMovement = true;
    protected Vector3f atractionPoint;
    protected WalkerNavControl wNavControl;
    protected String alias;
    protected AdmBESA admLocalBESA;
    private double passwdAg;
    
    public Character(SmaaatApp app, String name, Vector3f position, Vector3f direction,float radius,AdmBESA admLocal) {
        this.admLocalBESA=admLocal;
        this.radius=radius;
        this.height=2*radius;
        this.alias=name;
        this.app = app;
        this.node = new Node("Node_" + name);
        this.passwdAg=0.91;
        node.setLocalTranslation(position);
        //node.attachChild(createSpatialGeometry(name));

        //((Spatial) (node)).setUserData(Const.Character, this);

        shootingRestTime = Utils.randomInteger(100, 300);
        setupPhysics();
        setupControllers(direction);

        bullet = new Sphere(4, 4, bulletRadius);

        app.getCharacterNode().attachChild(node);
    }
    

    public Character(SmaaatApp app, String name, Vector3f position, Vector3f direction,float radius) {
        this.radius=radius;
        this.height=2*radius;
        this.alias=name;
        this.app = app;
        this.node = new Node("Node_" + name);

        node.setLocalTranslation(position);
        //node.attachChild(createSpatialGeometry(name));

        //((Spatial) (node)).setUserData(Const.Character, this);

        shootingRestTime = Utils.randomInteger(100, 300);
        setupPhysics();
        setupControllers(direction);

        bullet = new Sphere(4, 4, bulletRadius);

        app.getCharacterNode().attachChild(node);
    }

    private void setupPhysics() {
        BetterCharacterControl physicsCharacter = new BetterCharacterControl(radius, height, mass);
        node.addControl(physicsCharacter);
        app.getPhysicsSpace().add(physicsCharacter);
        app.getPhysicsSpace().addCollisionListener(this);
    }

    private void setupControllers(Vector3f direction) {
        if(admLocalBESA!=null){
            wNavControl = new WalkerNavControl(this, direction,admLocalBESA,alias);
        }else{
            wNavControl = new WalkerNavControl(this, direction);
        }
        node.addControl(wNavControl);
    }

    protected Geometry createSpatialGeometry(String name) {
        Sphere s = new Sphere(10, 10, this.radius);
        Geometry g = new Geometry(name, s);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        g.setMaterial(mat);
        g.setLocalTranslation(0, this.height / 2, 0);
        return g;
    }

    public List<SpatialSeenObject> getSeenCharacters() {
        List<Spatial> characters = app.getCharacterNode().getChildren();
        List<SpatialSeenObject> seenObjects = new ArrayList<SpatialSeenObject>();
        if (characters != null && characters.size() > 0) {
            for (Spatial s : characters) {

                if (!s.equals(this.node) && !s.getName().toLowerCase().contains("debug")) {
                    float distance = this.node.getWorldTranslation().distance(s.getWorldTranslation());
                    if (distance <= this.sightRange) {

                        Vector3f position = this.node.getWorldTranslation().clone();
                        position.setY(height / 2);
                        Vector3f direction = s.getWorldTranslation().subtract(this.node.getWorldTranslation());

                        position.addLocal(direction.normalize().mult(radius + (radius * 0.1f)));

                        Ray r = new Ray(position, direction.normalize());
                        r.setLimit(sightRange);
                        /*
                         if (test != null) {
                         test.removeFromParent();
                         }
                         test = Utils.createDebugArrow(app.getAssetManager(), r.origin, r.direction, app.getRootNode());
                         */
                        CollisionResults results = new CollisionResults();
                        app.getRootNode().collideWith(r, results);
                        if (results.size() > 0) {
                            CollisionResult cr = results.getClosestCollision();
                            String name = cr.getGeometry().getParent().getName();

                            if (name != null && !name.equals("floor") && !name.toLowerCase().contains("debug")) {
                                seenObjects.add(new SpatialSeenObject(s, distance));
                            }
                        }
                    }
                }
            }
        }
        
        Collections.sort(seenObjects, new Comparator<SpatialSeenObject>() {
            public int compare(SpatialSeenObject o1, SpatialSeenObject o2) {
                if (o1.distance == o1.distance) {
                    return 0;
                } else {
                    return o2.distance > o1.distance ? -1 : 1;
                }
            }
        });
        return seenObjects;
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

    public void shoot(Vector3f target) {

        boolean allowShooting = isAbleToShoot();

        if (!allowShooting) {
            return;
        }

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        mat.setColor("Color", ColorRGBA.Red);

        Geometry ball_geo = new Geometry("bullet", bullet);
        ball_geo.setMaterial(mat);
        app.getRootNode().attachChild(ball_geo);

        Vector3f position = node.getWorldTranslation().clone();
        Vector3f direction = target.subtract(node.getWorldTranslation()).normalize();
        
        //this.node.getControl(WalkerNavControl.class).setViewDirection(direction);
        
        position.setY(height / 2);
        position.addLocal(direction.mult(radius + (bulletRadius * 2)));
        ball_geo.setLocalTranslation(position);
        BulletControl ball_phy = new BulletControl(1f);

        ball_geo.addControl(ball_phy);
        app.getPhysicsSpace().add(ball_phy);
        ball_phy.setLinearVelocity(direction.mult(bulletSpeed));
        ball_phy.setGravity(Vector3f.ZERO.setY(-0.01f));
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void collision(PhysicsCollisionEvent event) {

        if (event.getNodeA() == node || event.getNodeB() == node) {
            if (event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")) {
                live--;
            }
        }
    }

    protected void killCharacter() {
        WalkerNavControl c = this.node.getControl(WalkerNavControl.class);
        c.setEnabled(false);
        this.node.removeFromParent();
        this.app.getPhysicsSpace().removeCollisionListener(this);
        this.app.getPhysicsSpace().remove(this.node.getControl(BetterCharacterControl.class));
          System.out.println("KILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
        if(this.admLocalBESA!=null){
          
            try {
                this.admLocalBESA.killAgent(alias,  passwdAg);
            } catch (ExceptionBESA ex) {
                Logger.getLogger(Character.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void update(float tpf) {
        if (live <= 0) {
            killCharacter();
        } else {
            //System.out.println(live);
            checkSeenObjects();
        }
    }

    protected boolean shallShootCharacter(Character c) {
        if (c == null) {
            return false;
        }
        return !(c.getClass().equals(this.getClass()));
    }

    protected void checkSeenObjects() {
        List<SpatialSeenObject> list = getSeenCharacters();
        if (list.size() > 0) {
            this.allowMovement = false;
            for (int i = 0; i < list.size(); i++) {
                Spatial fs = list.get(i).spatial;
                if (fs.getName().toLowerCase().contains("agent")) {
                    Character c = fs.getUserData(Const.Character);
                    if (this.shallShootCharacter(c)) {
                        shoot(c.node.getWorldTranslation());
                    }
                }
            }

            handleSeenObjects(list);

        } else {
            allowMovement = true;
        }
    }

    abstract protected void handleSeenObjects(List<SpatialSeenObject> list);

    public SmaaatApp getApp() {
        return app;
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

    public boolean getLeft_handed() {
        return this.left_handed;
    }

    public boolean getAllowMovement() {
        return this.allowMovement;
    }

    public Vector3f getAtractionPoint() {
        return this.atractionPoint;
    }

    public void atractionPointReached() {
        this.atractionPoint = null;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public float getSightRange() {
        return sightRange;
    }

    public void setSightRange(float sightRange) {
        this.sightRange = sightRange;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getLive() {
        return live;
    }

    public void setLive(float live) {
        this.live = live;
    }

    public static Sphere getBullet() {
        return bullet;
    }

    public static void setBullet(Sphere bullet) {
        Character.bullet = bullet;
    }

    public float getBulletRadius() {
        return bulletRadius;
    }

    public void setBulletRadius(float bulletRadius) {
        this.bulletRadius = bulletRadius;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public Date getLastShootTime() {
        return lastShootTime;
    }

    public void setLastShootTime(Date lastShootTime) {
        this.lastShootTime = lastShootTime;
    }

    public int getShootingRestTime() {
        return shootingRestTime;
    }

    public void setShootingRestTime(int shootingRestTime) {
        this.shootingRestTime = shootingRestTime;
    }

    public boolean isLeft_handed() {
        return left_handed;
    }

    public void setLeft_handed(boolean left_handed) {
        this.left_handed = left_handed;
    }

    public boolean isAllowMovement() {
        return allowMovement;
    }

    public void setAllowMovement(boolean allowMovement) {
        this.allowMovement = allowMovement;
    }

    public WalkerNavControl getwNavControl() {
        return wNavControl;
    }

    public void setwNavControl(WalkerNavControl wNavControl) {
        this.wNavControl = wNavControl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    
    
}
