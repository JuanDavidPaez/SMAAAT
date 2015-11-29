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
import SMA.GuardsData.CollisionData;
import SMA.GuardsData.WorldInfoData;
import SMA.GuardsData.Message;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import SMA.GuardsData.ShootData;
import Utils.Const;
import SMA.World.Guards.WorldInfoRequestGuard;
import Utils.Config;
import Utils.Const.Aliases;
import Utils.Utils;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.FloorPoint;
import World3D.Floor.FloorPoint.FloorPointType;
import World3D.Floor.GridPoint;
import World3D.Floor.Path;
import World3D.Object3D;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Agent extends AgentBESA {

    public enum AgentType {

        Explorer, Hostage, Enemy, Protector
    };
    private static int counter = 0;
    public final int id;
    private final AgentType agentType;

    public static Agent CreateAgent(AgentType type, String alias, Vector3f position, Vector3f direction) {
        AgentState e = new AgentState(position, direction);
        StructBESA s = new StructBESA();
        s.addBehavior("SensorUpdate");

        Agent a = null;
        try {
            s.bindGuard("SensorUpdate", AgentSensorGuard.class);
            a = new Agent(type, alias, e, s);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return a;
    }

    public Agent(AgentType type, String alias, StateBESA state, StructBESA structAgent) throws KernellAgentExceptionBESA {
        super(alias, state, structAgent, Config.BESApassword);
        counter++;
        this.id = counter;
        this.agentType = type;
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

    public AgentType getAgentType() {
        return agentType;
    }

    public int getId() {
        return id;
    }

    private static void sendMessage(Class guard, String alias, DataBESA data) {
        EventBESA ev = new EventBESA(guard.getName(), data);
        try {
            AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(alias);
            ah.sendEvent(ev);
        } catch (ExceptionBESA ex) {
            //Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendMessage(Message msg) {
        sendMessage(msg.toGuard(), msg.toAgentAlias(), msg);
    }

    public static String getAgentNodeName(String name) {
        return Const.NodePrefix + name;
    }

    public void sendWorldRegisterRequest() {
        RegisterAgentData msg = new RegisterAgentData(this.getAlias(), Aliases.WorldAgentAlias, WorldInfoRequestGuard.class);
        msg.setReplyGuard(AgentSensorGuard.class);
        msg.position = getState().position.clone();
        msg.direction = getState().direction.clone();
        msg.agentType = this.agentType;
        Agent.sendMessage(msg);
    }

    public void sendWorldInfoRequest() {
        WorldInfoData msg = new WorldInfoData(this.getAlias(), Aliases.WorldAgentAlias, WorldInfoRequestGuard.class);
        msg.setReplyGuard(AgentSensorGuard.class);
        Agent.sendMessage(msg);
    }

    public void sendMoveRequest(Vector3f nextMovePosition) {
        AgentState state = getState();

        Vector3f position = state.position;
        Vector3f direction = nextMovePosition.subtract(position).normalize();
        Quaternion rot = new Quaternion();
        rot.lookAt(direction, Vector3f.UNIT_Y);

        MoveData move = new MoveData(this.getAlias(), Aliases.WorldAgentAlias, WorldInfoRequestGuard.class);
        move.setReplyGuard(AgentSensorGuard.class);
        move.direction = direction;
        move.forward = true;
        //waitTime(1);
        Agent.sendMessage(move);
    }

    public void sendShootRequest() {
        ShootData msg = new ShootData(this.getAlias(), Aliases.WorldAgentAlias, WorldInfoRequestGuard.class);
        msg.setReplyGuard(AgentSensorGuard.class);
        msg.target = getState().shootingTarget;
        Agent.sendMessage(msg);
    }

    public void processWorldInfoResponse(WorldInfoData i) {

        boolean dataChanged = updateAgentStateWithWorldInfo(i);
        updateShootingIntentions(i);
        if (getState().shootingTarget == null) {
            updateGlobalDesiredPosition();
            updateMovementIntentions(dataChanged);
        }

        if (getState().nextMoveToTargetPosition != null) {
            sendMoveRequest(getState().nextMoveToTargetPosition);
        } else if (getState().shootingTarget != null) {
            sendShootRequest();
        } else {
            sendWorldInfoRequest();
        }
    }

    private boolean updateAgentStateWithWorldInfo(WorldInfoData i) {
        boolean dataChanged = false;
        AgentState state = getState();
        state.position = i.position;
        state.direction = i.direction;
        /*Actualiza información del piso*/
        if (state.floor == null && i.partialFloorView != null) {
            dataChanged = true;
            state.floor = new FloorData(i.partialFloorView.getParentFloorXSize(),
                    i.partialFloorView.getParentFloorYSize(),
                    i.partialFloorView.pxResolution, FloorPoint.FloorPointType.Unknown);
            if (Config.ShowAgentMapInJFrame && Config.InDebugMode) {
                state.floor.pointsSupplier = state;
                state.floor.showInJFrame();
            }
        } else if (i.partialFloorView != null) {
            dataChanged = state.floor.updateDataFromChunk(i.partialFloorView);
        }
        /*Actualiza información de objetos vistos*/
        dataChanged = (updateCurrentSeenObjects(i) ? true : dataChanged);

        return dataChanged;
    }

    private boolean updateCurrentSeenObjects(WorldInfoData ird) {
        AgentState state = getState();
        boolean change = false;
        change = state.agentsList.updateObjects(ird.seenObjects);

        /*Eliminación de objetos3D que ya no se encuentran en la posición antes memorizada*/
        if (ird.partialFloorView != null && ird.partialFloorView.getPoints().length > 0) {
            for (FloorPoint fp : ird.partialFloorView.getPoints()) {
                if (EnumSet.of(FloorPointType.Unknown, FloorPointType.Empty).contains(fp.getType())) {
                    boolean remove = true;
                    for (Object3D obj : ird.seenObjects) {
                        if (state.getPointPosition(obj.position3D).getId() == fp.getId()) {
                            remove = false;
                            break;
                        }
                    }
                    if (remove) {
                        state.agentsList.removeObjectsInPosition(fp, state.floor);
                    }
                }
            }
        }
        return change;
    }

    private void updateShootingIntentions(WorldInfoData ird) {
        AgentState state = getState();
        for (Object3D o : ird.seenObjects) {
            if (agentType == AgentType.Explorer && o.agentType == AgentType.Enemy) {
                state.shootingTarget = o.position3D;
                clearMovementIntentions();
                return;
            }
        }
        state.shootingTarget = null;
    }

    private void updateGlobalDesiredPosition() {
        boolean reset = false;
        AgentState state = getState();
        /*Nueva posicion aleatoria*/
        if (state.globalTargetPosition == null) {
            state.globalTargetPosition = selectExploringMapDesiredPosition();
        }
        /*Remueve la posicion si el agente ya llegó a ella*/
        if (!reset && state.globalTargetPosition != null && agentIsInTargetPosition(state.globalTargetPosition)) {
            reset = true;
        }
        /*Desiste de la posición si ésta resultó estar ocupada por un obstaculo*/
        if (!reset && state.globalTargetPosition != null && state.floor.isObstacle(state.floor.getPointFromCoordinates(state.globalTargetPosition))) {
            reset = true;
        }
        /*Desiste de la posición deseada si ésta no es alcanzable*/
        if (!reset && state.currentPatrolPath != null && state.nextMoveToTargetPosition == null) {
            reset = true;
        }
        /*Limpia la posicion deseada y el plan*/
        if (reset) {
            clearMovementIntentions();
        }
    }

    private void clearMovementIntentions() {
        getState().nextMoveToTargetPosition = null;
        getState().currentPatrolPath = null;
        getState().globalTargetPosition = null;
    }

    private void updateMovementIntentions(boolean dataChanged) {
        AgentState state = getState();

        if (state.globalTargetPosition != null) {
            GridPoint currentPositionPoint = state.getPointPosition(state.position);
            GridPoint globalTargetPositionPoint = state.globalTargetPosition.Clone();

            /*Solicita cambiar el plan si se encuentra un obstaculo en la ruta del plan actual*/
            if (state.currentPatrolPath != null && dataChanged) {
                if (!isPatrolPathStillValid()) {
                    state.currentPatrolPath = null;
                }
            }

            /*Se calcula un plan de movimiento si aún no se tiene*/
            if (state.currentPatrolPath == null) {
                FloorData floor = state.floor;
                Path movementPath = findPathAStarAlgorithm(floor, currentPositionPoint, globalTargetPositionPoint);
                if (movementPath != null) {
                    state.currentPatrolPath = new PatrolPath(movementPath);
                }
                if (Config.InDebugMode) {
                    floor.addPath("Path", movementPath);
                }
            }
            /*Se busca el siguiente paso en el plan de movimiento*/
            if (state.currentPatrolPath != null && state.currentPatrolPath.points.size() > 0) {
                if (state.nextMoveToTargetPosition == null || agentIsInTargetPositionVector(state.nextMoveToTargetPosition)) {
                    PathPoint nextPoint = state.currentPatrolPath.getNextPoint(currentPositionPoint);
                    if (nextPoint != null) {
                        state.nextMoveToTargetPosition = Floor3D.GridPointToVector3f(nextPoint, state.floor.XSize, state.floor.YSize);
                        state.nextMoveToTargetPosition.setY(state.position.y);
                    }
                }
            }
        }
    }

    private boolean agentIsInTargetPosition(GridPoint targetPosition) {
        Vector3f v = Floor3D.GridPointToVector3f(targetPosition, getState().floor.XSize, getState().floor.YSize);
        return agentIsInTargetPositionVector(v);
    }

    private boolean agentIsInTargetPositionVector(Vector3f targetPosition) {
        Vector3f pos = getState().position.clone().setY(0);
        Vector3f target = targetPosition.clone().setY(0);
        return (pos.distance(target) <= AgentState.minDistanceFromTargetPoint);
    }

    private Path findPathAStarAlgorithm(FloorData floor, GridPoint currentPosition, GridPoint targetPosition) {
        Path path = null;
        A_Star aStar = new A_Star(floor.XSize, floor.YSize);
        aStar.addObstacles(floor.floorObstaclesToArray());

        List<FloorPoint> agentsPositions = getState().getSeenAgentsPositionPoints();
        int[] intarray = new int[agentsPositions.size()];
        int c = 0;
        for (FloorPoint fp : agentsPositions) {
            intarray[c] = fp.getId();
            c++;
        }
        aStar.addObstacles(intarray);

        int[] pathArray = aStar.findPath(floor.gridPoint2ArrayIndex(currentPosition), floor.gridPoint2ArrayIndex(targetPosition));
        if (pathArray != null && pathArray.length > 0) {
            path = new Path(floor.arrayIndex2GridPointList(pathArray), Color.YELLOW);
        }
        return path;
    }

    private boolean isPatrolPathStillValid() {
        AgentState state = getState();
        for (PathPoint pp : state.currentPatrolPath.points) {
            FloorPoint p = state.floor.getPointFromCoordinates(pp.x, pp.y);
            if (state.floor.isObstacle(p)) {
                return false;
            }
            if (getState().agentsList.firstObjectInPosition(p, state.floor) != null) {
                return false;
            }
        }
        return true;
    }

    private GridPoint selectExploringMapDesiredPosition() {
        GridPoint targetPoint = null;
        AgentState state = getState();
        int[] points = state.floor.getPointsArray(EnumSet.of(FloorPointType.Unknown));
        if (points != null && points.length > 0) {
            int posIdx = state.getPointPosition(state.position).getId();
            int idx = Utils.randomInteger(0, points.length - 1);
            targetPoint = (state.floor.getPointFromId(points[idx]).Clone());
        }
        return targetPoint;
    }

    private void waitTime(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void processBulletCollision(CollisionData cd) {
        if (cd.live == 0) {
            getState().alive = false;
            this.shutdownAgent();
        }
    }
}
