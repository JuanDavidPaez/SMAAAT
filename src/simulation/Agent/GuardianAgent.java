package simulation.Agent;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.List;
import simulation.utils.Const;
import simulation.SmaaatApp;
import simulation.utils.SpatialSeenObject;

public class GuardianAgent extends Character {
    
    public GuardianAgent(SmaaatApp app, Vector3f position, Vector3f direction) {
        super(app, Const.GuardianAgent, position, direction);
        speed = (speed*0.6f);
                
    }

    @Override
    protected Geometry createSpatialGeometry(String name) {
        Geometry g = super.createSpatialGeometry(name);
        g.getMaterial().setColor("Color", ColorRGBA.Cyan);
        return g;
    }

    @Override
    protected boolean shallShootCharacter(Character c) {
        boolean shoot = super.shallShootCharacter(c);
        if (c instanceof ExplorerAgent || c instanceof HostageAgent) {
            shoot = false;
        }
        return shoot;
    }

    @Override
    protected void handleSeenObjects(List<SpatialSeenObject> list) {
    }
}
