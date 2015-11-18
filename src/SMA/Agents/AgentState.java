package SMA.Agents;

import AI.Pathfinding.PatrolPath;
import BESA.Kernel.Agent.StateBESA;
import World3D.Floor.FloorData;
import com.jme3.math.Vector3f;

public class AgentState extends StateBESA {

    public static float minDistanceFromTargetPoint  = 0.05f;
    
    public boolean agentRegisteredInWorld = false;
    public Vector3f position;
    public Vector3f direction;
    public FloorData floor;
    public PatrolPath currentPatrolPath;
    public Vector3f globalTargetPosition = new Vector3f(-9.5f,0,-9.5f);
    public Vector3f inmediateTargetPosition;
    
    public AgentState(Vector3f position, Vector3f direction) {
        this.position = position;
        this.direction = direction;
    }
    
}
