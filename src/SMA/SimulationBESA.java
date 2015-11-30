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

        //initSpecificAgentsSmallFloor();
        //initSpecificAgents();
        initAgentsRandomPositions(5, 5, 5, 5);
    }

    protected void initSpecificAgentsSmallFloor() {
        Vector3f pos1 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(4, 6));
        Agent a1 = Agent.CreateAgent(AgentType.Explorer, pos1, new Vector3f(0, 0, 1));
        a1.start();

        Vector3f pos2 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(8, 6));
        Agent a2 = Agent.CreateAgent(AgentType.Explorer, pos2, new Vector3f(0, 0, 1));
        a2.start();

    }

    protected void initSpecificAgents() {
        Vector3f pos1 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(18, 30));
        Agent a1 = Agent.CreateAgent(AgentType.Explorer, pos1, new Vector3f(0, 0, 1));
        a1.start();

        Vector3f pos2 = worldAgent.getWorldApp().getFloor3D().GridPointToVector3f(new GridPoint(2, 30));
        Agent a2 = Agent.CreateAgent(AgentType.Enemy, pos2, new Vector3f(0, 0, 1));
        a2.start();
    }

    protected void initAgentsRandomPositions(int explorers, int enemies, int hostages, int protectors) {
        int totalAgents = explorers + enemies + hostages + protectors;
        Floor3D floor = worldAgent.getWorldApp().getFloor3D();
        FloorData fData = floor.getFloorData();
        int[] points = fData.walkablePointsToArray();

        if (points.length / 2 < totalAgents) {
            throw new RuntimeException("El mundo contiene muy poco espacio para la cantidad de agentes. Espacio: " + points.length + " Agentes: " + totalAgents);
        }

        Integer[] ids = Utils.randomNumbersArray(totalAgents, 0, points.length);

        for (Integer id : ids) {
            AgentType type = null;
            if (explorers > 0) {
                type = AgentType.Explorer;
                explorers--;
            } else if (enemies > 0) {
                type = AgentType.Enemy;
                enemies--;
            } else if (hostages > 0) {
                type = AgentType.Hostage;
                hostages--;
            } else if (protectors > 0) {
                type = AgentType.Protector;
                protectors--;
            }

            Vector3f pos = floor.GridPointToVector3f(fData.getPointFromId(points[id]));
            Agent a = Agent.CreateAgent(type, pos, new Vector3f(0, 0, 1));
            a.start();
        }
    }
}
