package SimulationBESA;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
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

public class Test extends SimpleApplication implements ActionListener {

    public static void main(String[] args) {
        Test app = new Test();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("TEST");
        app.setSettings(settings);
        app.start();
    }
    
    private DirectionalLight cameraLight;
    private BulletAppState bulletAppState;
    private Spatial floor;
    private RayTrace tracer;

    @Override
    public void simpleInitApp() {
        floor = assetManager.loadModel("Models/floor.j3o");
        floor.setName("floor");
        rootNode.attachChild(floor);

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
        
        tracer = new RayTrace(rootNode, cam, 160, 128);
        tracer.show();
        tracer.update();
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
        tracer.update();
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
            cam.lookAt(position, Vector3f.UNIT_Y);
        }
        if (name.equals("KEY_2")) {
            cam.setLocation(position.add(new Vector3f(3, 0, 0)));
            cam.lookAt(position, Vector3f.UNIT_Y);
        }
        if (name.equals("KEY_3")) {
        }

    }
    
    public void estimateWalkableFloorSurface(){
    
    
    }

    public class FloorGridPoint {
        int row;
        int column;
        float x;
        float y;
        float height;
        boolean isFloor;
    }
}
