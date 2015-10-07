package simulation.Controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;

public class BulletControl extends RigidBodyControl implements PhysicsCollisionListener {

    private boolean hasCollide = false;

    public BulletControl(float mass) {
        super(mass);
    }

    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        super.setPhysicsSpace(space);
        if (space != null) {
            space.addCollisionListener(this);
        }
    }

    public void collision(PhysicsCollisionEvent event) {

        if (space == null) {
            return;
        }
        if (event.getObjectA() == this || event.getObjectB() == this) {
            hasCollide = true;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (hasCollide) {
            space.removeCollisionListener(this);
            space.remove(this);
            spatial.removeFromParent();
        }
    }
}
