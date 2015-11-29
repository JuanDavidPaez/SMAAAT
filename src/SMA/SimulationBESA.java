package SMA;

import BESA.Kernel.System.AdmBESA;
import SMA.Agents.Agent;
import SMA.Agents.Agent.AgentType;
import SMA.World.WorldAgent;
import Utils.Utils;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.GridPoint;
import com.jme3.math.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationBESA {

    AdmBESA admLocal;
    WorldAgent worldAgent;

    public static void main(String[] args) {
        SimulationBESA simulationBESA = new SimulationBESA();
    }

    public SimulationBESA() {

        admLocal = AdmBESA.getInstance();
        worldAgent = WorldAgent.createWorldAgent();
        worldAgent.setWorldIsReadyListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                worldIsReady();
            }
        });
        worldAgent.start();
    }

    protected void worldIsReady() {

        //worldAgent.addExitObject();
        //initSpecificAgentsSmallFloor();
        //initSpecificAgents();
        initAgentsRandom(20, 20);
    }

    protected void initSpecificAgentsSmallFloor() {
        Vector3f pos1 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(6, 6));
        Agent a1 = Agent.CreateAgent(AgentType.Explorer, "Agent_1", pos1, new Vector3f(0, 0, 1));
        a1.start();

        Vector3f pos2 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(8, 6));
        Agent a2 = Agent.CreateAgent(AgentType.Enemy, "Agent_2", pos2, new Vector3f(0, 0, 1));
        a2.start();
    }

    protected void initSpecificAgents() {
        Vector3f pos1 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(18, 30));
        Agent a1 = Agent.CreateAgent(AgentType.Explorer, "Agent_1", pos1, new Vector3f(0, 0, 1));
        a1.start();

        Vector3f pos2 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(2, 30));
        Agent a2 = Agent.CreateAgent(AgentType.Enemy, "Agent_2", pos2, new Vector3f(0, 0, 1));
        a2.start();
    }

    protected void initAgentsRandom(int explorers, int enemies) {
        int totalAgents = explorers + enemies;
        Floor3D floor = worldAgent.getWorldApp().getFloor3D();
        FloorData fData = floor.getFloorData();
        int[] points = fData.walkablePointsToArray();
        Integer[] ids = Utils.randomNumbersArray(totalAgents, 0, points.length);

        int n = 0;
        for (Integer id : ids) {
            n++;
            AgentType type = null;
            if (explorers > 0) {
                type = AgentType.Explorer;
                explorers--;
            } else if (enemies > 0) {
                type = AgentType.Enemy;
                enemies--;
            }


            Vector3f pos = floor.GridPointToVector3f(fData.getPointFromId(points[id]));
            Agent a = Agent.CreateAgent(type, "Agent_" + n, pos, new Vector3f(0, 0, 1));
            a.start();
        }
    }
}
