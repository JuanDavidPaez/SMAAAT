package World3D;

import BESA.ExceptionBESA;
import BESA.Kernel.System.AdmBESA;
import SMA.World.WorldAgent;
import Utils.Config;
import Utils.Const;
import Utils.Utils;
import World3D.Floor.Floor3D;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldApp extends SimpleApplication implements ActionListener {

    private WorldAgent worldAgent;
    private DirectionalLight cameraLight;
    private BulletAppState bulletAppState;
    private Node noneBulletsNode;
    private Node floorNode;
    private Node characterNode;
    private Node bulletsNode;
    private Floor3D floor3D;
    private java.awt.event.ActionListener initAppCompletedListener;

    private WorldApp() {
    }

    public static WorldApp createWorldApp(WorldAgent worldAgent) {
        WorldApp app = new WorldApp();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("SMAAAT");
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        app.worldAgent = worldAgent;
        //app.start();
        return app;
    }

    @Override
    public void simpleInitApp() {

        setupNodesHierarchy();
        setupLighting();
        setupKeys();

        bulletAppState = new BulletAppState();
        //bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(Config.DebugPhysics);

        setupFloor();

        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        flyCam.setMoveSpeed(20);
        Vector3f floorPos = floorNode.getWorldTranslation();
        
        BoundingBox box = (BoundingBox)floorNode.getWorldBound();
        Vector3f dimensions = new Vector3f(0,0,0);
        box.getExtent(dimensions);
        float y = dimensions.x * 2.8f;
        cam.setLocation(new Vector3f(0, y, 0));
        cam.lookAt(floorPos, Vector3f.UNIT_Z.negate());
        
        if (initAppCompletedListener != null) {
            initAppCompletedListener.actionPerformed(null);
        }
    }

    private void setupLighting() {
        cameraLight = new DirectionalLight();
        cameraLight.setColor(ColorRGBA.White);
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
        rootNode.addLight(cameraLight);
    }

    private void setupNodesHierarchy() {
        bulletsNode = new Node(Const.NodeNames.BulletsNode);
        noneBulletsNode = new Node(Const.NodeNames.NoneBulletsNode);
        floorNode = new Node(Const.NodeNames.FloorNode);
        characterNode = new Node(Const.NodeNames.CharacterNode);

        /*
         * 1.RootNode
         * 1.1 bulletsNode
         * 1.2 noneBulletsNode
         * 1.2.1 floorNode
         * 1.2.2 characterNode
         */

        rootNode.attachChild(bulletsNode);
        rootNode.attachChild(noneBulletsNode);
        noneBulletsNode.attachChild(floorNode);
        noneBulletsNode.attachChild(characterNode);
    }

    private void setupFloor() {
        String floorFilePath = Utils.getResourceFilePath(Config.SmaaatFloorFileName);
        floor3D = new Floor3D(floorFilePath, assetManager, floorNode);

        CollisionShape floorShape = CollisionShapeFactory.createMeshShape(floorNode);
        RigidBodyControl floorRigidBody = new RigidBodyControl(floorShape, 0);
        floorNode.addControl(floorRigidBody);
        bulletAppState.getPhysicsSpace().add(floorRigidBody);

    }

    private void setupKeys() {
        inputManager.addMapping("KEY_1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "KEY_1");
        inputManager.addMapping("KEY_2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "KEY_2");
        inputManager.addMapping("KEY_3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addListener(this, "KEY_3");
    }

    @Override
    public void simpleUpdate(float tpf) {
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {

        Vector3f position = Vector3f.ZERO;
        if (name.equals("KEY_1")) {
            cam.setLocation(position.add(new Vector3f(0, 3, 0)));
            cam.lookAt(position, Vector3f.UNIT_Z.negate());
        }
        if (name.equals("KEY_2")) {
            cam.setLocation(position.add(new Vector3f(3, 0, 0)));
            cam.lookAt(position, Vector3f.UNIT_Y);
        }
        if (name.equals("KEY_3")) {
        }

    }

    public BulletAppState getBulletAppState() {
        return this.bulletAppState;
    }

    public PhysicsSpace getPhysicsSpace() {
        return this.bulletAppState.getPhysicsSpace();
    }

    public Spatial getFloor() {
        return this.floor3D.getFloorNode();
    }

    public Floor3D getFloor3D() {
        return this.floor3D;
    }

    public Node getCharacterNode() {
        return this.characterNode;
    }

    public Node getBulletsNode() {
        return this.bulletsNode;
    }

    public Node getNoneBulletsNode() {
        return this.noneBulletsNode;
    }

    public Node getFloorNode() {
        return this.floorNode;
    }

    public WorldAgent getWorldAgent() {
        return worldAgent;
    }

    public void setInitAppCompletedListener(java.awt.event.ActionListener a) {
        this.initAppCompletedListener = a;
    }

    @Override
    public void destroy() {
        try {
            AdmBESA.getInstance().kill(Config.BESApassword);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(WorldApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.destroy();
        System.exit(0);
    }
}
