package Simulation.Agent;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.List;
import Simulation.utils.Const;
import Simulation.SmaaatApp;
import Simulation.utils.SpatialSeenObject;

public class ExplorerAgent extends Character {

    public ExplorerAgent(SmaaatApp app, Vector3f position, Vector3f direction) {
        super(app, Const.ExplorerAgent, position, direction);
        speed = (speed*1.3f);
    }

    @Override
    protected Geometry createSpatialGeometry(String name) {
        Geometry g = super.createSpatialGeometry(name);
        g.getMaterial().setColor("Color", ColorRGBA.Blue);
        return g;
    }
    
    @Override
    protected boolean shallShootCharacter(Character c) {
        return false;
    }    
    
    @Override
    protected void checkSeenObjects() {

    }    

    @Override
    protected void handleSeenObjects(List<SpatialSeenObject> list) {

    }
    
}
