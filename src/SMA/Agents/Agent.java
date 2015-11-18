package SMA.Agents;

import AI.Pathfinding.A_Star;
import AI.Pathfinding.PatrolPath;
import AI.Pathfinding.PatrolPath.PathPoint;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.Event.DataBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.KernellAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import SMA.Agents.Guards.AgentSensorGuard;
import SMA.GuardsData.InfoRequestData;
import SMA.GuardsData.Message;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import Utils.Const;
import SMA.World.Guards.WorldInfoRequestGuard;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.GridPoint;
import World3D.Floor.Path;
import World3D.RobotSensors;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Agent extends AgentBESA {

    public static Agent CreateAgent(String alias, Vector3f position, Vector3f direction) {
        AgentState e = new AgentState(position, direction);
        StructBESA s = new StructBESA();

        s.addBehavior("SensorUpdate");

        Agent a = null;

        try {
            s.bindGuard("SensorUpdate", AgentSensorGuard.class);
            a = new Agent(alias, e, s);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return a;

    }

    public Agent(String alias, StateBESA state, StructBESA structAgent) throws KernellAgentExceptionBESA {
        super(alias, state, structAgent, Const.BESApassword);
    }

    @Override
    public void setupAgent() {
    }

    @Override
    public void start() {
        super.start();
        sendWorldRegisterRequest();
    }

    @Override
    public void shutdownAgent() {
    }

    @Override
    public AgentState getState() {
        return (AgentState) this.state;
    }

    private static void sendMessage(Class guard, String alias, DataBESA data) {
        EventBESA ev = new EventBESA(guard.getName(), data);
        try {
            AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(alias);
            ah.sendEvent(ev);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendMessage(Message msg) {
        sendMessage(msg.toGuard(), msg.toAgentAlias(), msg);
    }

    //******************BEHAVIORS******************
    public void sendWorldRegisterRequest() {

        RegisterAgentData msg = new RegisterAgentData(this.getAlias(), Const.WorldAgentAlias, WorldInfoRequestGuard.class);
        msg.setReplyGuard(AgentSensorGuard.class);
        msg.position = getState().position.clone();
        msg.direction = getState().direction.clone();
        Agent.sendMessage(msg);
    }

    public void sendWorldInfoRequest() {
        InfoRequestData msg = new InfoRequestData(this.getAlias(), Const.WorldAgentAlias, WorldInfoRequestGuard.class);
        msg.setReplyGuard(AgentSensorGuard.class);
        Agent.sendMessage(msg);
    }

    public void processInfoRequest(InfoRequestData i) {
        AgentState state = getState();
        state.position = i.position;
        state.direction = i.direction;
        if (state.floor == null && i.partialFloorView != null) {
            state.floor = new FloorData(i.partialFloorView.XSize, i.partialFloorView.YSize, i.partialFloorView.pxResolution);
            state.floor.openInJFrame();
        }
        state.floor.mergeFloorData(i.partialFloorView);
        updateMovementIntentions();
    }

    private void updateMovementIntentions() {
        AgentState state = getState();

        if (state.globalTargetPosition != null) {
            GridPoint currentPositionPoint = Floor3D.Vector3fToGridPoint(state.position, state.floor.XSize, state.floor.YSize);
            GridPoint globalTargetPositionPoint = Floor3D.Vector3fToGridPoint(state.globalTargetPosition, state.floor.XSize, state.floor.YSize);

            if (state.currentPatrolPath != null && !isPatrolPathStillValid()) {
                state.currentPatrolPath = null;
            }

            if (state.currentPatrolPath == null) {
                FloorData floor = state.floor;
                Path movementPath = findPathAStarAlgorithm(floor, currentPositionPoint, globalTargetPositionPoint);
                if (movementPath != null) {
                    floor.paths.put("Path", movementPath);
                    state.currentPatrolPath = new PatrolPath(movementPath);
                }
            }

            if (state.currentPatrolPath != null && state.currentPatrolPath.points.size() > 0) {
                if (state.inmediateTargetPosition == null || state.position.distance(state.inmediateTargetPosition) <= AgentState.minDistanceFromTargetPoint) {
                    PathPoint nextPoint = state.currentPatrolPath.getNextPoint(currentPositionPoint);
                    if (nextPoint != null) {
                        state.inmediateTargetPosition = Floor3D.GridPointToVector3f(nextPoint, state.floor.XSize, state.floor.YSize);
                        state.inmediateTargetPosition.setY(state.position.y);
                    }
                }
            }
            sendMoveRequest();
        }
    }

    private Path findPathAStarAlgorithm(FloorData floor, GridPoint currentPosition, GridPoint targetPosition) {
        Path path = null;
        A_Star aStar = new A_Star(floor.XSize, floor.YSize);
        aStar.addObstacles(floor.floorWallsToArray());
        int[] pathArray = aStar.findPath(floor.gridPoint2ArrayIndex(currentPosition), floor.gridPoint2ArrayIndex(targetPosition));
        if (pathArray != null && pathArray.length > 0) {
            path = new Path(floor.arrayIndex2GridPointList(pathArray), Color.YELLOW);
        }
        return path;
    }

    public void sendMoveRequest() {
        AgentState state = getState();
        if (state.inmediateTargetPosition != null) {
            Vector3f position = state.position;
            if (position.distance(state.inmediateTargetPosition) > AgentState.minDistanceFromTargetPoint) {
                Vector3f direction = state.inmediateTargetPosition.subtract(position).normalize();
                Quaternion rot = new Quaternion();
                rot.lookAt(direction, Vector3f.UNIT_Y);

                MoveData move = new MoveData(this.getAlias(), Const.WorldAgentAlias, WorldInfoRequestGuard.class);
                move.setReplyGuard(AgentSensorGuard.class);

                move.direction = direction;
                move.forward = true;

                Agent.sendMessage(move);
            }
        }
    }

    public void sendMoveRequest_WallFollower(RobotSensors rs) {

        MoveData move = new MoveData(this.getAlias(), Const.WorldAgentAlias, WorldInfoRequestGuard.class);
        move.setReplyGuard(AgentSensorGuard.class);

        if (rs != null) {
            /*No hay obstaculo en ningun sensor: avanza adelante*/
            if (!rs.srFrontL.collideWithObject && !rs.srFrontR.collideWithObject
                    && !rs.srRightF.collideWithObject && !rs.srRightR.collideWithObject
                    && !rs.srRearL.collideWithObject && !rs.srRearR.collideWithObject
                    && !rs.srLeftF.collideWithObject && !rs.srLeftR.collideWithObject) {
                move.forward = true;
            }
            /*Los dos sensores delanteros detectan objeto: Girar 90º */
            if (rs.srFrontL.collideWithObject && rs.srFrontR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }

            /*Los dos sensores de la derecha detectan objeto: avanza adelante*/
            if (rs.srRightF.collideWithObject && rs.srRightR.collideWithObject
                    || rs.srRightF.collideWithObject && !rs.srRightR.collideWithObject) {
                move.forward = true;
            }
            /*Sensor derecho frontal no detecta pero sensor derecho trasero si detecta: Gira 90º*/
            if (!rs.srRightF.collideWithObject && rs.srRightR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }

            /*Los dos sensores de la izquierda detectan objeto: avanza adelante*/
            if (rs.srLeftF.collideWithObject && rs.srLeftR.collideWithObject
                    || rs.srLeftF.collideWithObject && !rs.srLeftR.collideWithObject) {
                move.forward = true;
            }
            /*Sensor izquierda frontal no detecta pero sensor izquierdo trasero si detecta: Gira  90º*/
            if (!rs.srLeftF.collideWithObject && rs.srLeftR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }
            if (move.rotation == null && move.forward == false) {
                move.forward = true;
            }
        }

        Agent.sendMessage(move);
    }

    private boolean isPatrolPathStillValid() {
        boolean pathValid = true;
        AgentState state = getState();
        for (PathPoint pp : state.currentPatrolPath.points) {
            for (GridPoint w : state.floor.walls) {
                if (w.equals(pp)) {
                    pathValid = false;
                    break;
                }
            }
        }
        return pathValid;
    }
}
