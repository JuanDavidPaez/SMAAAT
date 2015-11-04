package Simulation.Agent;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.List;
import Simulation.utils.Const;
import Simulation.SmaaatApp;
import Simulation.utils.SpatialSeenObject;

public class HostageAgent extends Character {

    boolean rescued = false;

    public HostageAgent(SmaaatApp app, Vector3f position, Vector3f direction) {
        super(app, Const.HostageAgent, position, direction);
        speed = (speed * 0.2f);
    }

    @Override
    protected Geometry createSpatialGeometry(String name) {
        Geometry g = super.createSpatialGeometry(name);
        g.getMaterial().setColor("Color", ColorRGBA.Yellow);
        return g;
    }

    @Override
    protected boolean shallShootCharacter(Character c) {
        return false;
    }

    public void hostageRescued() {
        rescued = true;
    }

    @Override
    protected void handleSeenObjects(List<SpatialSeenObject> list) {
        Vector3f exitPosition = null;
        if (list.size() > 0) {
            this.allowMovement = true;

            Spatial s = list.get(0).spatial;
            if (s.getName().contains(Const.Exit)) {
                exitPosition = s.getLocalTranslation().clone();
            } else if (s.getName().contains(Const.EnemyAgent) || s.getName().contains(Const.GuardianAgent)) {
                Vector3f position = s.getWorldTranslation().clone();
                Vector3f direction = node.getWorldTranslation().subtract(position).normalize();
                position.addLocal(direction.mult(radius * 2.5f));
                atractionPoint = position;
            }

            if (atractionPoint == null && exitPosition != null) {
                atractionPoint = exitPosition;
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (rescued) {
            this.killCharacter();
        }
    }
}