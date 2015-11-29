package World3D;

import BESA.ExceptionBESA;
import BESA.Kernel.System.AdmBESA;
import Utils.Const;
import Utils.Utils;
import World3D.Floor.Floor3D;
import com.jme3.app.SimpleApplication;
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

    private DirectionalLight cameraLight;
    private BulletAppState bulletAppState;
    private Node characterNode;
    private Floor3D floor3D;
    private java.awt.event.ActionListener initAppCompletedListener;

    private WorldApp() {
    }

    public static WorldApp createWorldApp() {
        WorldApp app = new WorldApp();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("SMAAAT");
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        //app.start();
        return app;
    }

    @Override
    public void simpleInitApp() {

        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        flyCam.setMoveSpeed(20);

        String floorFilePath = Utils.getResourceFilePath(Const.SmaaatFloorFileName);
        floor3D = new Floor3D(floorFilePath, assetManager);
        Node floorNode = floor3D.getFloorNode();
        rootNode.attachChild(floorNode);

        //cam.setLocation(new Vector3f(0, 10, 10));
        cam.setLocation(new Vector3f(-6, 11, 7));
        cam.lookAt(floorNode.getWorldTranslation(), Vector3f.UNIT_Y);

        setupLighting();
        setupKeys();

        bulletAppState = new BulletAppState();
//        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);

        CollisionShape floorShape = CollisionShapeFactory.createMeshShape(floorNode);
        RigidBodyControl floorRigidBody = new RigidBodyControl(floorShape, 0);
        floorNode.addControl(floorRigidBody);
        bulletAppState.getPhysicsSpace().add(floorRigidBody);

        characterNode = new Node();
        rootNode.attachChild(characterNode);

        if (initAppCompletedListener != null) {
            initAppCompletedListener.actionPerformed(null);
        }
    }

    public void setupLighting() {
        cameraLight = new DirectionalLight();
        cameraLight.setColor(ColorRGBA.White);
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
        rootNode.addLight(cameraLight);
    }

    @Override
    public void simpleUpdate(float tpf) {
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    private void setupKeys() {
        inputManager.addMapping("KEY_1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "KEY_1");
        inputManager.addMapping("KEY_2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "KEY_2");
        inputManager.addMapping("KEY_3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addListener(this, "KEY_3");
    }

    public void onAction(String name, boolean isPressed, float tpf) {

        Vector3f position = Vector3f.ZERO;
        if (name.equals("KEY_1")) {
            cam.setLocation(position.add(new Vector3f(0, 3, 0)));
            cam.lookAt(position,  Vector3f.UNIT_Z.negate());
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

    public void setInitAppCompletedListener(java.awt.event.ActionListener a) {
        this.initAppCompletedListener = a;
    }

    @Override
    public void destroy() {
        try {
            AdmBESA.getInstance().kill(Const.BESApassword);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(WorldApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.destroy();
        System.exit(0);
    }
}
