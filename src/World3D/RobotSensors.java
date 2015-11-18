package World3D;

import Utils.Utils;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class RobotSensors {

    public DistanceSensor srFrontL = new DistanceSensor();
    public DistanceSensor srFrontR = new DistanceSensor();
    public DistanceSensor srRightF = new DistanceSensor();
    public DistanceSensor srRightR = new DistanceSensor();
    public DistanceSensor srRearR = new DistanceSensor();
    public DistanceSensor srRearL = new DistanceSensor();
    public DistanceSensor srLeftR = new DistanceSensor();
    public DistanceSensor srLeftF = new DistanceSensor();
    private boolean debugMode = false;
    private Node rootNode;
    private AssetManager assetManager;

    public RobotSensors() {
    }

    public void enableDebugMode(Node rootNode, AssetManager assetManager) {
        this.debugMode = true;
        this.rootNode = rootNode;
        this.assetManager = assetManager;
    }

    private void reset() {
        srFrontL.reset();
        srFrontR.reset();
        srRightF.reset();
        srRightR.reset();
        srRearR.reset();
        srRearL.reset();
        srLeftR.reset();
        srLeftF.reset();
    }

    @Override
    public RobotSensors clone() {
        RobotSensors rs = new RobotSensors();
        rs.srFrontL = srFrontL.clone();
        rs.srFrontR = srFrontR.clone();
        rs.srRightF = srRightF.clone();
        rs.srRightR = srRightR.clone();
        rs.srRearR = srRearR.clone();
        rs.srRearL = srRearL.clone();
        rs.srLeftR = srLeftR.clone();
        rs.srLeftF = srLeftF.clone();
        return rs;
    }

    public void update(Vector3f robotDirection, Vector3f robotPosition, float radius, Spatial floor) {
        reset();

        Vector3f pos = robotPosition;
        Vector3f dir = robotDirection;
        float sensorLimit = DistanceSensor.max;
        Quaternion q = new Quaternion();
        q.lookAt(dir, Vector3f.UNIT_Y);

        Vector3f dirPerpendicular = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y).mult(dir);

        Ray rFrontL = new Ray(pos.add(q.mult(new Vector3f(radius, 0, radius))), dir);
        Ray rFrontR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, radius))), dir);

        Ray rRightF = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, -radius))), dirPerpendicular.negate());
        Ray rRightR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, -radius - (radius * 0.5f)))), dirPerpendicular.negate());

        Ray rRearR = new Ray(pos.add(q.mult(new Vector3f(-radius, 0, -radius))), dir.negate());
        Ray rRearL = new Ray(pos.add(q.mult(new Vector3f(radius, 0, -radius))), dir.negate());

        Ray rLeftR = new Ray(pos.add(q.mult(new Vector3f(radius, 0, -radius - (radius * 0.5f)))), dirPerpendicular);
        Ray rLeftF = new Ray(pos.add(q.mult(new Vector3f(radius, 0, -radius))), dirPerpendicular);

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

        checkCollisionToObject(srFrontL, floor);
        checkCollisionToObject(srFrontR, floor);
        checkCollisionToObject(srRightF, floor);
        checkCollisionToObject(srRightR, floor);
        checkCollisionToObject(srRearR, floor);
        checkCollisionToObject(srRearL, floor);
        checkCollisionToObject(srLeftR, floor);
        checkCollisionToObject(srLeftF, floor);

        if (debugMode) {
            debugRayGeometry(srFrontL);
            debugRayGeometry(srFrontR);
            debugRayGeometry(srRightF);
            debugRayGeometry(srRightR);
            debugRayGeometry(srRearR);
            debugRayGeometry(srRearL);
            debugRayGeometry(srLeftR);
            debugRayGeometry(srLeftF);
        }
    }

    private void checkCollisionToObject(DistanceSensor sr, Spatial object) {

        Ray ray = sr.ray;
        CollisionResults cr = new CollisionResults();
        object.collideWith(ray, cr);
        if (cr.size() > 0) {
            CollisionResult c = cr.getClosestCollision();
            float distance = ray.origin.distance(c.getContactPoint());
            if (distance < DistanceSensor.max) {
                sr.contactPoint = c.getContactPoint();
                sr.collideWithObject = true;
                sr.collisionDistance = distance;
            }
        }
    }

    private void debugRayGeometry(DistanceSensor sr) {

        if (sr.ray != null) {
            Vector3f pos = sr.ray.origin;
            Vector3f dir = sr.ray.direction.normalize();
            if (sr.debugVectorGeometry == null) {
                sr.debugVectorGeometry = Utils.createDebugArrow(assetManager, pos, dir.mult(sr.ray.limit), rootNode);
            } else {
                sr.debugVectorGeometry.setLocalTranslation(pos);
                Quaternion q = new Quaternion();
                q.lookAt(dir, Vector3f.UNIT_Y);
                sr.debugVectorGeometry.setLocalRotation(q);
            }
        } else {
            if (sr.debugVectorGeometry != null) {
                sr.debugVectorGeometry.removeFromParent();
                sr.debugVectorGeometry = null;
            }
        }

        if (sr.contactPoint != null) {
            if (sr.debugContactPointGeometry == null) {
                sr.debugContactPointGeometry = Utils.createDebugBox(assetManager, sr.contactPoint, 0.02f, rootNode);
            } else {
                sr.debugContactPointGeometry.setLocalTranslation(sr.contactPoint);
            }
        } else {
            if (sr.debugContactPointGeometry != null) {
                sr.debugContactPointGeometry.removeFromParent();
                sr.debugContactPointGeometry = null;
            }
        }
    }

    @Override
    public String toString() {

        return ("FL: " + srFrontL.collideWithObject + " FR: " + srFrontR.collideWithObject
                + " RF: " + srRightF.collideWithObject + " RR: " + srRightR.collideWithObject
                + " RL: " + srRearL.collideWithObject + " RR: " + srRearR.collideWithObject
                + " LF: " + srLeftF.collideWithObject + " LR: " + srLeftR.collideWithObject);
    }

    public class DistanceSensor {

        public final static float min = 0.05f;
        public final static float max = 0.15f;
        public Ray ray;
        public Vector3f contactPoint;
        public float collisionDistance = 0;
        public boolean collideWithObject = false;
        public Spatial debugContactPointGeometry;
        public Spatial debugVectorGeometry;

        public void removeDebugGeometries() {
            if (this.debugVectorGeometry != null) {
                this.debugVectorGeometry.removeFromParent();
            }
            if (this.debugContactPointGeometry != null) {
                this.debugContactPointGeometry.removeFromParent();
            }
        }

        public void reset() {
            ray = null;
            contactPoint = null;
            collideWithObject = false;
            collisionDistance = 0;
        }

        @Override
        public DistanceSensor clone() {
            DistanceSensor ds = new DistanceSensor();
            if(this.contactPoint != null)
                ds.contactPoint = this.contactPoint.clone();
            ds.collisionDistance = this.collisionDistance;
            ds.collideWithObject = this.collideWithObject;
            return ds;
        }
    }
}
