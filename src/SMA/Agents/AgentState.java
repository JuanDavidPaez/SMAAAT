package SMA.Agents;

import AI.Pathfinding.PatrolPath;
import BESA.Kernel.Agent.StateBESA;
import World3D.Floor.FloorData;
import World3D.Floor.GridPoint;
import World3D.Floor.Path;
import com.jme3.math.Vector3f;
import java.awt.Color;
import java.util.ArrayList;

public class AgentState extends StateBESA {

    public static float minDistanceFromTargetPoint  = 0.05f;
    
    public boolean agentRegisteredInWorld = false;
    public Vector3f position;
    public Vector3f direction;
    public FloorData floor;
    public PatrolPath currentPatrolPath;
    public Vector3f globalTargetPosition = new Vector3f(1f,0,1f);
    public Vector3f inmediateTargetPosition;
    
    public AgentState(Vector3f position, Vector3f direction) {
        this.position = position;
        this.direction = direction;
    }
    
}
