package Simulation;

import Simulation.Agent.ExplorerAgent;
import Simulation.Agent.EnemyAgent;
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
import Simulation.Agent.GuardianAgent;
import Simulation.Agent.HostageAgent;
import Simulation.world.Exit;


public class SmaaatApp extends SimpleApplication implements ActionListener {

    private DirectionalLight cameraLight;
    private BulletAppState bulletAppState;
    private Node characterNode;
    private Spatial floor;

    public static void main(String[] args) {
        SmaaatApp app = new SmaaatApp();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("SMAAAT");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        viewPort.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        flyCam.setMoveSpeed(20);

        floor = assetManager.loadModel("Models/floor.j3o");
        floor.setName("floor");
        rootNode.attachChild(floor);

        /*cam.setParallelProjection(true);
         if (cam.isParallelProjection()) {
         float frustumSize = 5;
         rootNode.setCullHint(CullHint.Never);
         float aspect = (float) cam.getWidth() / cam.getHeight();
         cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
         }*/

        //cam.setLocation(new Vector3f(0, 10, 10));
        cam.setLocation(new Vector3f(-6, 11, 7));
        cam.lookAt(floor.getWorldTranslation(), Vector3f.UNIT_Y);

        setupLighting();
        setupKeys();

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);

        CollisionShape floorShape = CollisionShapeFactory.createMeshShape((Node) floor);
        RigidBodyControl floorRigidBody = new RigidBodyControl(floorShape, 0);
        floor.addControl(floorRigidBody);
        bulletAppState.getPhysicsSpace().add(floorRigidBody);

        characterNode = new Node();

        new GuardianAgent(this, new Vector3f(4, 0, 2), new Vector3f(0, 0, -1));
        new GuardianAgent(this, new Vector3f(-4.5f, 0, 2), new Vector3f(0, 0, -1));
        new EnemyAgent(this, new Vector3f(3, 0, 1), new Vector3f(0, 0, 1));
        new EnemyAgent(this, new Vector3f(2, 0, -2), new Vector3f(0, 0, 1));
        new EnemyAgent(this, new Vector3f(-4, 0, -2), new Vector3f(0, 0, 1));
        new HostageAgent(this, new Vector3f(-1, 0, 2), new Vector3f(1, 0, 0));
        new HostageAgent(this, new Vector3f(-1, 0, -0.5f), new Vector3f(1, 0, 0));
        new ExplorerAgent(this, new Vector3f(-3, 0, -1), new Vector3f(-1, 0, 0));

        Exit e = new Exit(this, new Vector3f(-4.5f, 0, 4.8f), new Vector3f(0.5f, 1, 0.1f));

        rootNode.attachChild(characterNode);

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
        inputManager.addMapping("KEY_F1", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addListener(this, "KEY_F1");
    }

    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("KEY_F1")) {
        }

        Vector3f position = Vector3f.ZERO;
        if (name.equals("KEY_1")) {
            cam.setLocation(position.add(new Vector3f(0, 3, 0)));
            cam.lookAt(position, Vector3f.UNIT_Y);
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
        return this.floor;
    }

    public Node getCharacterNode() {
        return this.characterNode;
    }

    @Override
    public void destroy() {
        super.destroy();
        /*
         new Runnable() {
         public void run() {
         SmaaatApp.main(null);
         }
         }.run();
         */
    }
}
