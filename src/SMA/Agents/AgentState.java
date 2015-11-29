package SMA.Agents;

import AI.Pathfinding.PatrolPath;
import BESA.Kernel.Agent.StateBESA;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.FloorData.PaintObjectsSupplier;
import World3D.Floor.FloorPoint;
import World3D.Floor.GridPoint;
import World3D.Object3D;
import World3D.Object3DMap;
import World3D.ObjectsLists;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AgentState extends StateBESA implements PaintObjectsSupplier {

    public static float minDistanceFromTargetPoint = 0.05f;
    public boolean agentRegisteredInWorld = false;
    public Vector3f position;
    public Vector3f direction;
    public FloorData floor;
    public PatrolPath currentPatrolPath;
    public GridPoint globalTargetPosition;
    public Vector3f nextMoveToTargetPosition;
    public ObjectsLists agentsList;

    public AgentState(Vector3f position, Vector3f direction) {
        this.position = position;
        this.direction = direction;
        this.agentsList = new ObjectsLists();
    }

    public FloorPoint getPointPosition(Vector3f pos) {
        return this.floor.getPointFromCoordinates(Floor3D.Vector3fToGridPoint(pos, floor.XSize, floor.YSize));
    }

    public List<FloorPoint> getSeenAgentsPositionPoints() {
        List<FloorPoint> list = new ArrayList<FloorPoint>();
        addPointsToGlobalList(agentsList.enemies, list);
        addPointsToGlobalList(agentsList.explorers, list);
        addPointsToGlobalList(agentsList.hostages, list);
        addPointsToGlobalList(agentsList.protectors, list);
        return list;
    }

    private void addPointsToGlobalList(Object3DMap set, List<FloorPoint> list) {
        if (set != null && set.size() > 0) {
            synchronized (set) {
                for (Object3D o : set.getObjects()) {
                    FloorPoint p = getPointPosition(o.position3D).clone();
                    p.setType(FloorPoint.FloorPointType.Character);
                    list.add(p);
                }
            }
        }
    }

    public List<FloorPoint> getPointsToPaint() {
        List<FloorPoint> list = getSeenAgentsPositionPoints();
        FloorPoint p = getPointPosition(this.position).clone();
        p.setType(FloorPoint.FloorPointType.Myself);
        list.add(p);
        return list;
    }
}
