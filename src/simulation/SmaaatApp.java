package simulation;

import simulation.utils.WorldFloor;
import BESA.ExceptionBESA;
import BESA.Kernell.Agent.Event.DataBESA;
import BESA.Kernell.Agent.Event.EventBESA;
import BESA.Kernell.Agent.StructBESA;
import BESA.Kernell.System.AdmBESA;
import BESA.Kernell.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import BESAFile.Agent.Agent;
import BESAFile.Agent.AgentEnemy;
import BESAFile.Agent.AgentExplorer;
import BESAFile.Agent.AgentHostage;
import BESAFile.Agent.AgentProtector;
import BESAFile.Agent.Behavior.AgentEnemyMoveGuard;
import BESAFile.Agent.Behavior.AgentMoveGuard;
import BESAFile.Agent.Behavior.AgentProtectorMoveGuard;
import BESAFile.Agent.Behavior.SubscribeResponseGuard;
import BESAFile.Agent.State.AgentEnemyState;
import BESAFile.Agent.State.AgentExplorerState;
import BESAFile.Agent.State.AgentHostageState;
import BESAFile.Agent.State.AgentProtectorState;
import BESAFile.Agent.State.AgentStateTest;
import BESAFile.Data.ActionData;
import BESAFile.Data.ActionDataAgent;
import BESAFile.Data.SubscribeDataJME;
import BESAFile.Data.Vector3D;
import BESAFile.Agent.Behavior.AgentExplorerMoveGuard;
import BESAFile.Agent.Behavior.AgentHostageMoveGuard;
import BESAFile.Agent.Behavior.AgentNegotiationGuard;
import BESAFile.Agent.Behavior.DecreaseLifeGuard;
import BESAFile.Agent.Behavior.FollowHostageGuard;
import BESAFile.Agent.Behavior.HELPAgentProtectorGuard;
import BESAFile.World.Behavior.AddAgenteFloorGuardJME;
import BESAFile.World.Behavior.ChangeFloorGuardJME;
import BESAFile.World.Behavior.SensorsAgentGuardJME;
import BESAFile.World.Behavior.ShootAgentJME;
import BESAFile.World.Behavior.ShutdownAgentGuardJME;
import BESAFile.World.Behavior.SubscribeGuardJME;
import BESAFile.World.Behavior.UpdateGuardJME;
import BESAFile.World.Behavior.SimulationStartJME;
import BESAFile.World.Model.ModelEdifice;
import BESAFile.World.Model.ModelFloor;
import BESAFile.World.State.WorldStateJME;
import BESAFile.World.WorldAgentJME;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.Agent.EnemyAgent;
import simulation.Agent.ExplorerAgent;
import simulation.Agent.GuardianAgent;
import simulation.Agent.HostageAgent;
import simulation.utils.Const;
import simulation.utils.UtilitiesEdificeFactory;
import simulation.world.Exit;

public class SmaaatApp extends SimpleApplication implements ActionListener {

    private DirectionalLight cameraLight;
    private BulletAppState bulletAppState;
    private Node ediffice;
    private List<Node> wallsFloors;
    private ModelEdifice mEdifice;
    private Node characterNode;
    private float distBetweenFloors;
    private float x;
    private float y;
    private float z;
    private int width;
    private int length;
    private int nFloors;
    private int consecutiveAgenProtector;// 
    private int consecutiveAgenEnemy;
    private int consecutiveAgenHostage;
    private int consecutiveAgenExplorer;
    private static AdmBESA admLocal;
    private double passwdAg;
    private boolean startAPP;
    private Node bulletsNode;
    
    public static void main(String[] args) {
        SmaaatApp app = new SmaaatApp();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SMAAAT");
        app.setSettings(settings);
        app.start();
        
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        distBetweenFloors= Const.distBetweenFloors;
        consecutiveAgenProtector=0;
        consecutiveAgenEnemy=0;
        consecutiveAgenExplorer=0;
        consecutiveAgenHostage=0;
        wallsFloors=new ArrayList<Node>();
        try {
            setupBesa();
            createEdifice();
            setupCam();
            setupLighting();
            setupKeys();
            setupBulletPhysics();
            
            characterNode = new Node();
            this.bulletsNode=new Node();
            createAgentExplorer(0, 28,1, new Vector3f(0, 0, 1));
            createAgentProtector(0, 28,0, new Vector3f(0, 0, 1));
            createAgentExplorer(0, 1,28, new Vector3f(0, 0, 1));
            createAgentProtector(0, 0,28, new Vector3f(0, 0, 1));
           
           
            createAgentHostage(1, 4,0, new Vector3f(0, 0, 1));
            createAgentHostage(1, 3,0, new Vector3f(0, 0, 1));
            createAgentEnemy(1, 5,5, new Vector3f(0, 0, 1));
            createAgentEnemy(1, 4,5, new Vector3f(0, 0, 1));
            
            
            createAgentEnemy(1, 25,25, new Vector3f(0, 0, 1));
            createAgentEnemy(1, 5,10, new Vector3f(0, 0, 1));
            createAgentHostage(1, 5,11, new Vector3f(0, 0, 1));
            createAgentHostage(1, 24,25, new Vector3f(0, 0, 1));
            
            /*
            createAgentProtector(0, 9,9, new Vector3f(0, 0, 1));
            createAgentExplorer(0, 6,5, new Vector3f(0, 0, 1));
            createAgentEnemy(0, 4,2, new Vector3f(0, 0, 1));
            createAgentExplorer(0, 4,3, new Vector3f(0, 0, 1));
            createAgentHostage(0, 3,3, new Vector3f(0, 0, 1));
            createAgentProtector(0,  2,3, new Vector3f(0, 0, 1));
            
            createAgentProtector(0, 7,7, new Vector3f(0, 0, 1));
            createAgentExplorer(0, 8,7, new Vector3f(0, 0, 1));
            createAgentHostage(0, 7,8, new Vector3f(0, 0, 1));
            createAgentEnemy(0, 6,6, new Vector3f(0, 0, 1));
            createAgentProtector(0, 6,7, new Vector3f(0, 0, 1));
            createAgentEnemy(0, 7,6, new Vector3f(0, 0, 1));
            //*/
        } catch (ExceptionBESA ex) {
            Logger.getLogger(SmaaatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Exit e = new Exit(this, getPositionVirtiul(0, 7, 0), new Vector3f(0.5f,1,0.1f));
        rootNode.attachChild(characterNode);
        rootNode.attachChild(this.bulletsNode);
        

    }
    
    
    
    private void createAgentProtector(int idFloor,int i,int j,Vector3f direction) throws ExceptionBESA{
    
        AgentProtectorState state = new AgentProtectorState(i,j,idFloor,Const.GuardianAgent+consecutiveAgenProtector,new Vector3D(direction.getX(), direction.getY(), direction.getZ()),0.45f,mEdifice.getWidth(),mEdifice.getLength(),mEdifice.getnFlooors());
        StructBESA struct = new StructBESA();
        struct.addBehavior("agentMove");
        struct.bindGuard("agentMove", AgentProtectorMoveGuard.class);
        struct.addBehavior("HELPAgentProtectorGuard");
        struct.bindGuard("HELPAgentProtectorGuard", HELPAgentProtectorGuard.class);
        struct.addBehavior("SubscribeResponseGuard");
        struct.bindGuard("SubscribeResponseGuard",SubscribeResponseGuard.class);
        struct.addBehavior("AgentNegotiationGuard");
        struct.bindGuard("AgentNegotiationGuard",AgentNegotiationGuard.class);
        struct.addBehavior("DecreaseLifeGuard");
        struct.bindGuard("DecreaseLifeGuard",DecreaseLifeGuard.class);
        
        AgentProtector agent = new AgentProtector(state.getAlias(), state, struct, passwdAg);
        agent.start();
        consecutiveAgenProtector++;
    
    }
    
     private void createAgentEnemy(int idFloor,int i,int j,Vector3f direction) throws ExceptionBESA{
    
        AgentEnemyState state = new AgentEnemyState(i,j,idFloor,Const.EnemyAgent+consecutiveAgenEnemy,new Vector3D(direction.getX(), direction.getY(), direction.getZ()),0.45f,mEdifice.getWidth(),mEdifice.getLength(),mEdifice.getnFlooors());
        StructBESA struct = new StructBESA();
        struct.addBehavior("agentMove");
        struct.bindGuard("agentMove", AgentEnemyMoveGuard.class);
        struct.addBehavior("SubscribeResponseGuard");
        struct.bindGuard("SubscribeResponseGuard",SubscribeResponseGuard.class);
        struct.addBehavior("AgentNegotiationGuard");
        struct.bindGuard("AgentNegotiationGuard",AgentNegotiationGuard.class);
        struct.addBehavior("DecreaseLifeGuard");
        struct.bindGuard("DecreaseLifeGuard",DecreaseLifeGuard.class);
        
        AgentEnemy agent = new AgentEnemy(state.getAlias(), state, struct, passwdAg);
        agent.start();
        consecutiveAgenEnemy++;
     }
     
     private void createAgentHostage(int idFloor,int i,int j,Vector3f direction) throws ExceptionBESA{
        AgentHostageState state = new AgentHostageState(i,j,idFloor,Const.HostageAgent+consecutiveAgenHostage,new Vector3D(direction.getX(), direction.getY(), direction.getZ()),0.45f,mEdifice.getWidth(),mEdifice.getLength(),mEdifice.getnFlooors());
        StructBESA struct = new StructBESA();
        struct.addBehavior("agentMove");
        struct.bindGuard("agentMove", AgentHostageMoveGuard.class);
        struct.addBehavior("SubscribeResponseGuard");
        struct.bindGuard("SubscribeResponseGuard",SubscribeResponseGuard.class);
        struct.addBehavior("AgentNegotiationGuard");
        struct.bindGuard("AgentNegotiationGuard",AgentNegotiationGuard.class);
        struct.addBehavior("DecreaseLifeGuard");
        struct.bindGuard("DecreaseLifeGuard",DecreaseLifeGuard.class);
        struct.addBehavior("FollowHostageGuard");
        struct.bindGuard("FollowHostageGuard",FollowHostageGuard.class);
        
        
        //FollowHostageGuard
        AgentHostage agent = new AgentHostage(state.getAlias(), state, struct, passwdAg);
        agent.start();
        consecutiveAgenHostage++;
     }
     
     private void createAgentExplorer(int idFloor,int i,int j,Vector3f direction) throws ExceptionBESA{
    
        AgentExplorerState state = new AgentExplorerState(i,j,idFloor,Const.ExplorerAgent+consecutiveAgenExplorer,new Vector3D(direction.getX(), direction.getY(), direction.getZ()),0.45f,mEdifice.getWidth(),mEdifice.getLength(),mEdifice.getnFlooors());
        StructBESA struct = new StructBESA();
        struct.addBehavior("agentMove");
        struct.bindGuard("agentMove", AgentExplorerMoveGuard.class);
        struct.addBehavior("SubscribeResponseGuard");
        struct.bindGuard("SubscribeResponseGuard",SubscribeResponseGuard.class);
        struct.addBehavior("AgentNegotiationGuard");
        struct.bindGuard("AgentNegotiationGuard",AgentNegotiationGuard.class);
        struct.addBehavior("DecreaseLifeGuard");
        struct.bindGuard("DecreaseLifeGuard",DecreaseLifeGuard.class);
        
        AgentExplorer agent = new AgentExplorer(state.getAlias(), state, struct, passwdAg);
        agent.start();
        consecutiveAgenExplorer++;
     }
  
      private void createAgentWord(int idFloor) throws ExceptionBESA{
        WorldStateJME ws=new WorldStateJME(this,idFloor);
        StructBESA wrlStruct = new StructBESA();
        wrlStruct.addBehavior("SubscribeGuardJME");
        wrlStruct.addBehavior("SensorsAgentGuardJME");
        wrlStruct.addBehavior("simulationStartJME");
        wrlStruct.addBehavior("ChangeFloorGuardJME");
        wrlStruct.addBehavior("AddAgenteFloorGuardJME");
        wrlStruct.addBehavior("ShootAgentJME");
        wrlStruct.addBehavior("ShutdownAgentGuardJME");
        wrlStruct.bindGuard("SubscribeGuardJME", SubscribeGuardJME.class);
        wrlStruct.bindGuard("SensorsAgentGuardJME", SensorsAgentGuardJME.class);
        wrlStruct.bindGuard("simulationStartJME", SimulationStartJME.class);
        wrlStruct.bindGuard("ChangeFloorGuardJME", ChangeFloorGuardJME.class);
        wrlStruct.bindGuard("AddAgenteFloorGuardJME", AddAgenteFloorGuardJME.class);
        wrlStruct.bindGuard("ShootAgentJME", ShootAgentJME.class);
        wrlStruct.bindGuard("ShutdownAgentGuardJME", ShutdownAgentGuardJME.class);
        //*/
        wrlStruct.addBehavior("UpdateGuardJME");
        wrlStruct.bindGuard("UpdateGuardJME", UpdateGuardJME.class);
        WorldAgentJME wa = new WorldAgentJME(Const.World+idFloor, ws, wrlStruct, passwdAg);
        wa.start();
     }
     
    
    private void createEdifice() throws ExceptionBESA{
        width=Const.width;
        length=Const.length;
        x=Const.x;
        y=Const.y;
        z=Const.z;
        nFloors=Const.nFloors;
        mEdifice= UtilitiesEdificeFactory.createEdifice(width, length, nFloors);
        createVirtualEdifice();
    }
    
    
    private void createVirtualEdifice() throws ExceptionBESA{
        ediffice =new Node("Edifice");//*/
        for (int n=0;n<mEdifice.getnFlooors();n++){
            ModelFloor mf= mEdifice.getFloor(n);
            WorldFloor we=new WorldFloor(assetManager, this.mEdifice.getWidth(), this.mEdifice.getLength(), x, y+distBetweenFloors*n, z);
            Node floor=new Node("Floor"+n);
            Node walls=new Node("Walls"+n);
            ediffice.attachChild(floor);
            floor.attachChild(we.makeFloor());
            floor.attachChild(we.makeGridFloor(ColorRGBA.Blue));
            /*
            floor.attachChild(we.makeWallFloor1());
            floor.attachChild(we.makeWallFloor2());
            floor.attachChild(we.makeWallFloor3());
            floor.attachChild(we.makeWallFloor4());
            //*/
            ediffice.attachChild(we.makeWallFloor1());
            ediffice.attachChild(we.makeWallFloor2());
            ediffice.attachChild(we.makeWallFloor3());
            ediffice.attachChild(we.makeWallFloor4());
            
            for (int i=0;i<this.mEdifice.getWidth();i++){
                for (int j=0;j<this.mEdifice.getLength();j++){
                     switch(mf.get(i, j)){
                         case -1:walls.attachChild(we.makeCubeB("B-"+n+"-"+i+"-"+j, i, j)); break;
                         case -2:walls.attachChild(we.makeCubeb("b-"+n+"-"+i+"-"+j, i, j,0.3f)); break;
                         case -3:walls.attachChild(we.makeDoorUp("b-"+n+"-"+i+"-"+j, i, j,0.3f)); break;
                         case -4:walls.attachChild(we.makeDoorDown("b-"+n+"-"+i+"-"+j, i, j,0.3f)); break;
                              
                     }
                }
            }
            
            //floor.attachChild(walls);
            ediffice.attachChild(walls);
            wallsFloors.add(walls);
            createAgentWord(n);
            
            
        }
        rootNode.attachChild(   ediffice );
      
        
    }
    
    public void setupLighting() {
        cameraLight = new DirectionalLight();
        cameraLight.setColor(ColorRGBA.White);
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
        rootNode.addLight(cameraLight);
    }
    
    private void setupBesa() throws ExceptionBESA{
        this.startAPP=false;
        passwdAg = Const.passwdAg;
        SmaaatApp.admLocal = AdmBESA.getInstance();
   }

    @Override
    public void simpleUpdate(float tpf) {
        cameraLight.setDirection(cam.getDirection().normalizeLocal());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void setupKeys() {
        inputManager.addMapping("KEY_1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "KEY_1");
        inputManager.addMapping("KEY_2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "KEY_2");
        inputManager.addMapping("KEY_SPACE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "KEY_SPACE");
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
        if(name.equals("KEY_SPACE"))
        {   
            if(!this.startAPP){
                for(int i=0;i<nFloors;i++){
                    Agent.sendMessage(SimulationStartJME.class, Const.World+i, new ActionData());
                }
                this.startAPP=true;
            }
        }
    }

    public Node getBulletsNode() {
        return bulletsNode;
    }

    public void setBulletsNode(Node bulletsNode) {
        this.bulletsNode = bulletsNode;
    }
    
    
    
    public BulletAppState getBulletAppState() {
        return this.bulletAppState;
    }

    public PhysicsSpace getPhysicsSpace() {
        return this.bulletAppState.getPhysicsSpace();
    }

    public Spatial getFloor() {
        return this.ediffice;
    }

    public Node getCharacterNode() {
        return this.characterNode;
    }

    public Node getEdiffice() {
        return ediffice;
    }

    public ModelEdifice getmEdifice() {
        return mEdifice;
    }

    public float getDistBetweenFloors() {
        return distBetweenFloors;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getnFloors() {
        return nFloors;
    }

    public double getPasswdAg() {
        return passwdAg;
    }
    
    public Node getRootNode(){
        return rootNode;
    }

    public List<Node> getWallsFloors() {
        return wallsFloors;
    }
    
    @Override
    public void destroy() {
        try {
            AdmBESA.getInstance().kill(passwdAg);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(SmaaatApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.destroy();
        //System.exit(0);
    }

    private void setupBulletPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        
        CollisionShape floorShape = CollisionShapeFactory.createMeshShape(ediffice);
        RigidBodyControl floorRigidBody = new RigidBodyControl(floorShape, 0);
        ediffice.addControl(floorRigidBody);
        bulletAppState.getPhysicsSpace().add(floorRigidBody); 
    }

    private void setupCam() {
        flyCam.setMoveSpeed(20);
        /*
        cam.setLocation(new Vector3f(0, 20, -10));
        cam.lookAt(new Vector3f(0, -10, 3), Vector3f.UNIT_Y);
        //*/
        cam.setLocation(new Vector3f(0, 20, 0));
        cam.lookAt(new Vector3f(0, -10, 0), Vector3f.UNIT_Y);
        //*
    }
   
   
}
