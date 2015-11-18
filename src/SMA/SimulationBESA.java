package SMA;

import BESA.Kernel.System.AdmBESA;
import SMA.Agents.Agent;
import SMA.World.WorldAgent;
import com.jme3.math.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationBESA {

    AdmBESA admLocal;
    WorldAgent worldAgent;
    
    public static void main(String[] args) {
        SimulationBESA simulationBESA = new SimulationBESA();
    }

    public SimulationBESA(){

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
        
        worldAgent.addExitObject();
        
        Agent explorer1 = Agent.CreateAgent("Explorer1",new Vector3f(0,0,0), new Vector3f(0,0,1));
        explorer1.start();

    }
}
