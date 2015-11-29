package World3D.Controls;

import SMA.Agents.Agent;
import SMA.GuardsData.MoveData;
import World3D.Character3D;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class WalkerNavControl extends AbstractControl implements ActionListener {

    private boolean forward, backward, left, right, turnLeft, turnRight;
    private Quaternion desiredRotation;
    private Vector3f desiredDirection;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    private Character3D character;
    protected float minDistanceFromAtractionPoint = 0.2f;
    private boolean pendingMoveRequest = false;

    public WalkerNavControl(Character3D character, Vector3f direction) {
        this.viewDirection = direction;
        this.character = character;
        setupKeys(character.getApp().getInputManager());
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
        character.update(tpf);
        move(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void setupKeys(InputManager inputManager) {
        if (1==2) {
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
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        this.resetMoveFlags();
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
    }

    private void move(float tpf) {

        float speed = character.getSpeed();
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
        if (desiredRotation != null) {
            desiredRotation.multLocal(viewDirection);
            desiredRotation = null;
        }
        if (desiredDirection != null) {
            viewDirection = desiredDirection;
        }
        control.setViewDirection(viewDirection);

        if (pendingMoveRequest) {
            pendingMoveRequest = false;
            resetMoveFlags();
        }
    }

    private void resetMoveFlags() {
        this.forward = this.backward = this.left = this.right = this.turnLeft = this.turnRight = false;
        desiredRotation = null;
    }

    public Character3D getCharacter() {
        return character;
    }

    public void setViewDirection(Vector3f dir) {
        this.viewDirection = dir;
        BetterCharacterControl control = spatial.getControl(BetterCharacterControl.class);
        control.setViewDirection(viewDirection);
    }

    public void enqueueMovementRequest(MoveData md) {
        pendingMoveRequest = true;
        this.forward = md.forward;
        this.desiredRotation = md.rotation;
        this.desiredDirection = md.direction;
    }
}
