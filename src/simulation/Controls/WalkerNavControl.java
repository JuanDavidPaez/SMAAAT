package simulation.Controls;

import simulation.Agent.Character;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import simulation.utils.Utils;

public class WalkerNavControl extends AbstractControl implements ActionListener {

    private boolean forward, backward, left, right, turnLeft, turnRight;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    private Spatial arrow;
    private Character character;
    SensorRay srFrontL = new SensorRay();
    SensorRay srFrontR = new SensorRay();
    SensorRay srRightF = new SensorRay();
    SensorRay srRightR = new SensorRay();
    SensorRay srRearR = new SensorRay();
    SensorRay srRearL = new SensorRay();
    SensorRay srLeftR = new SensorRay();
    SensorRay srLeftF = new SensorRay();
    boolean keyBoardPressed = false;
    protected float minDistanceFromAtractionPoint = 0.2f;

    public WalkerNavControl(Character character, Vector3f direction) {
        this.viewDirection = direction;
        this.character = character;
        arrow = Utils.createDebugArrow(character.getApp().getAssetManager(), Vector3f.ZERO, new Vector3f(0, 0, 0.5f), null);
        //setupKeys(character.getApp().getInputManager());
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        ((Node) spatial).attachChild(arrow);
        arrow.getLocalTranslation().setY(character.getHeight() / 2);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getParent() != null) {
            character.update(tpf);
            move(tpf);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void setupKeys(InputManager inputManager) {
        inputManager.addMapping("TurnLeft", new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("TurnRight", new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_NUMPAD9));
        inputManager.addListener(this, "TurnLeft");
        inputManager.addListener(this, "TurnRight");
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        this.resetMove();
        if (name.equals("TurnLeft")) {
            turnLeft = isPressed;
        }
        if (name.equals("TurnRight")) {
            turnRight = isPressed;
        }
        if (name.equals("Up")) {
            forward = isPressed;
        }
        if (name.equals("Down")) {
            backward = isPressed;
        }
        if (name.equals("Left")) {
            left = isPressed;
        }
        if (name.equals("Right")) {
            right = isPressed;
        }
        keyBoardPressed = (left || right || forward || backward || turnLeft || turnRight);
    }

    private void wallFollowerMovement(float tpf) {

        float radius = character.getRadius();
        float height = character.getHeight();
        float sensorLimit = DistanceSensorData.max;
        radius -= 0.03f;

        BetterCharacterControl control = spatial.getControl(BetterCharacterControl.class);
        Vector3f dir = control.getViewDirection().normalize();
        Vector3f pos = spatial.getWorldTranslation().clone();
        pos.setY(height / 2);
        Quaternion q = new Quaternion();
        q.lookAt(dir, Vector3f.UNIT_Y);

        Vector3f dirPerpendicular = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y).mult(dir);

        Ray rFrontL = new Ray(pos.add(q.mult(new Vector3f(radius, 0, radius))), dir);
        Ray rFrontR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, radius))), dir);

        Ray rRightF = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, /**/ -radius))), dirPerpendicular.negate());
        Ray rRightR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, -radius - (radius * 0.5f)))), dirPerpendicular.negate());

        Ray rRearR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, -radius))), dir.negate());
        Ray rRearL = new Ray(pos.add(q.mult(new Vector3f(radius, 0, -radius))), dir.negate());

        Ray rLeftR = new Ray(pos.add(q.mult(new Vector3f(radius, 0, -radius - (radius * 0.5f)))), dirPerpendicular);
        Ray rLeftF = new Ray(pos.add(q.mult(new Vector3f(radius, 0, /**/ -radius))), dirPerpendicular);

        rFrontL.setLimit(sensorLimit);
        rFrontR.setLimit(sensorLimit);
        rRightF.setLimit(sensorLimit);
        rRightR.setLimit(sensorLimit);
        rRearR.setLimit(sensorLimit);
        rRearL.setLimit(sensorLimit);
        rLeftR.setLimit(sensorLimit);
        rLeftF.setLimit(sensorLimit);

        srFrontL.ray = rFrontL;
        srFrontR.ray = rFrontR;
        srRightF.ray = rRightF;
        srRightR.ray = rRightR;
        srRearR.ray = rRearR;
        srRearL.ray = rRearL;
        srLeftR.ray = rLeftR;
        srLeftF.ray = rLeftF;

        DistanceSensorData sdFrontL = checkWallCollision(srFrontL);
        DistanceSensorData sdFrontR = checkWallCollision(srFrontR);
        DistanceSensorData sdRightF = checkWallCollision(srRightF);
        DistanceSensorData sdRightR = checkWallCollision(srRightR);
        DistanceSensorData sdRearR = checkWallCollision(srRearR);
        DistanceSensorData sdRearL = checkWallCollision(srRearL);
        DistanceSensorData sdLeftR = checkWallCollision(srLeftR);
        DistanceSensorData sdLeftF = checkWallCollision(srLeftF);

        debugRayGeometry(srFrontL);
        debugRayGeometry(srFrontR);
        debugRayGeometry(srRightF);
        debugRayGeometry(srRightR);
        debugRayGeometry(srRearR);
        debugRayGeometry(srRearL);
        debugRayGeometry(srLeftR);
        debugRayGeometry(srLeftF);

        this.resetMove();
        int sign = (!character.getLeft_handed()) ? 1 : -1;
        if (character.getAtractionPoint() == null) {
            if (character.getAllowMovement()) {
                this.resetMove();

                /*float angle =  this.viewDirection.normalize().angleBetween(Vector3f.UNIT_X);
                if ((angle % FastMath.HALF_PI)>0.15) {
                    System.out.println("Name: " + spatial.getName());
                    viewDirection = Vector3f.UNIT_Z;
                    control.setViewDirection(viewDirection);
                }*/

                /*No hay obstaculo en ningun sensor: avanza adelante*/
                if (!sdFrontL.collideWithObject && !sdFrontR.collideWithObject
                        && !sdRightF.collideWithObject && !sdRightR.collideWithObject
                        && !sdRearL.collideWithObject && !sdRearR.collideWithObject
                        && !sdLeftF.collideWithObject && !sdLeftR.collideWithObject) {
                    this.forward = true;
                }
                /*Los dos sensores delanteros detectan objeto: Girar 90º */
                if (sdFrontL.collideWithObject && sdFrontR.collideWithObject) {
                    Quaternion rotateL = new Quaternion().fromAngleAxis(sign * FastMath.HALF_PI, Vector3f.UNIT_Y);
                    rotateL.multLocal(viewDirection);
                    control.setViewDirection(viewDirection);
                }

                /*Los dos sensores de la derecha detectan objeto: avanza adelante*/
                if (sdRightF.collideWithObject && sdRightR.collideWithObject
                        || sdRightF.collideWithObject && !sdRightR.collideWithObject) {
                    this.forward = true;
                }
                /*Sensor derecho frontal no detecta pero sensor derecho trasero si detecta: Gira 90º*/
                if (!sdRightF.collideWithObject && sdRightR.collideWithObject) {
                    Quaternion rotateL = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
                    rotateL.multLocal(viewDirection);
                    control.setViewDirection(viewDirection);
                }

                /*Los dos sensores de la izquierda detectan objeto: avanza adelante*/
                if (sdLeftF.collideWithObject && sdLeftR.collideWithObject
                        || sdLeftF.collideWithObject && !sdLeftR.collideWithObject) {
                    this.forward = true;
                }
                /*Sensor izquierda frontal no detecta pero sensor iaquierdo trasero si detecta: Gira  90º*/
                if (!sdLeftF.collideWithObject && sdLeftR.collideWithObject) {
                    Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
                    rotateL.multLocal(viewDirection);
                    control.setViewDirection(viewDirection);
                }

                /*
                 System.out.println("FL: " + sdFrontL.collideWithObject + " FR: " + sdFrontR.collideWithObject
                 + " RF: " + sdRightF.collideWithObject + " RR: " + sdRightR.collideWithObject
                 + " RL: " + sdRearL.collideWithObject + " RR: " + sdRearR.collideWithObject
                 + " LF: " + sdLeftF.collideWithObject + " LR: " + sdLeftR.collideWithObject);
                 */
            }
        } else {

            Vector3f position = spatial.getWorldTranslation().clone();
            if (position.distance(character.getAtractionPoint()) > minDistanceFromAtractionPoint) {
                Vector3f direction = character.getAtractionPoint().subtract(spatial.getWorldTranslation()).normalize();
                Quaternion rot = new Quaternion();
                rot.lookAt(direction, Vector3f.UNIT_Y);
                viewDirection = direction;
                control.setViewDirection(viewDirection);
                forward = true;
            } else {
                character.atractionPointReached();
            }
        }
    }

    private void move(float tpf) {
        float speed = character.getSpeed();
        wallFollowerMovement(tpf);
        BetterCharacterControl control = spatial.getControl(BetterCharacterControl.class);
        Vector3f modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
        walkDirection.set(0, 0, 0);
        if (left || right) {
            modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_X);
            if (right) {
                modelForwardDir.negateLocal();
            }
            walkDirection.addLocal(modelForwardDir.mult(speed));
        }
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(speed));
        }
        if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(speed));
        }
        control.setWalkDirection(walkDirection);

        if (turnLeft) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        }
        if (turnRight) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        control.setViewDirection(viewDirection);
    }

    private void resetMove() {
        this.forward = this.backward = this.left = this.right = this.turnLeft = this.turnRight = false;
    }

    private void debugRayGeometry(SensorRay sr) {
        Vector3f pos = sr.ray.origin;
        Vector3f dir = sr.ray.direction.normalize();
        if (sr.vectorGeometry == null) {
            sr.vectorGeometry = Utils.createDebugArrow(character.getApp().getAssetManager(), pos, dir.mult(sr.ray.limit), spatial.getParent());
        } else {
            sr.vectorGeometry.setLocalTranslation(pos);
            Quaternion q = new Quaternion();
            q.lookAt(dir, Vector3f.UNIT_Y);
            sr.vectorGeometry.setLocalRotation(q);
        }
        if (spatial.getParent() != null) {
            if (sr.contactPointGeometry == null) {
                if (sr.contactPoint != null) {
                    sr.contactPointGeometry = Utils.createDebugBox(character.getApp().getAssetManager(), sr.contactPoint, 0.02f, spatial.getParent());
                }
            } else {
                if (sr.contactPoint != null) {
                    spatial.getParent().attachChild(sr.contactPointGeometry);
                    sr.contactPointGeometry.setLocalTranslation(sr.contactPoint);
                } else {
                    sr.contactPointGeometry.removeFromParent();
                }
            }
        }
    }

    private DistanceSensorData checkWallCollision(SensorRay sr) {
        sr.contactPoint = null;
        Ray ray = sr.ray;
        DistanceSensorData sd = new DistanceSensorData();
        CollisionResults cr = new CollisionResults();
        this.character.getApp().getFloor().collideWith(ray, cr);
        if (cr.size() > 0) {
            CollisionResult c = cr.getClosestCollision();
            float distance = ray.origin.distance(c.getContactPoint());
            if (distance < DistanceSensorData.max) {
                sr.contactPoint = c.getContactPoint();
                sd.collideWithObject = true;
                sd.collisionDistance = distance;
            }
        }
        return sd;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (srFrontL != null && srFrontL.vectorGeometry != null) {
            srFrontL.removeDebugGeometries();
            srFrontR.removeDebugGeometries();
            srRightF.removeDebugGeometries();
            srRightR.removeDebugGeometries();
            srRearR.removeDebugGeometries();
            srRearL.removeDebugGeometries();
            srLeftR.removeDebugGeometries();
            srLeftF.removeDebugGeometries();
        }
    }

    public Character getCharacter() {
        return character;
    }

    public void setViewDirection(Vector3f dir) {
        this.viewDirection = dir;
        BetterCharacterControl control = spatial.getControl(BetterCharacterControl.class);
        control.setViewDirection(viewDirection);
    }

    protected class SensorRay {

        public Ray ray;
        public Spatial contactPointGeometry;
        public Vector3f contactPoint;
        public Spatial vectorGeometry;

        public void removeDebugGeometries() {
            if (this.vectorGeometry != null) {
                this.vectorGeometry.removeFromParent();
            }
            if (this.contactPointGeometry != null) {
                this.contactPointGeometry.removeFromParent();
            }
        }
    }

    protected class DistanceSensorData {

        public final static float min = 0.05f;
        public final static float max = 0.15f;
        public float collisionDistance = 0;
        public boolean collideWithObject = false;
    }
}
