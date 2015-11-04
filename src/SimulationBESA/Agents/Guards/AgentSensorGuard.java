package SimulationBESA.Agents.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SimulationBESA.Agents.Agent;
import SimulationBESA.Agents.AgentState;
import SimulationBESA.Data.InfoRequestData;
import SimulationBESA.Data.MoveData;
import SimulationBESA.Data.RegisterAgentData;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentSensorGuard extends GuardBESA {

    @Override
    public void funcExecGuard(EventBESA ebesa) {
        //System.out.println("Guarda: " + this.getClass().getName());

        AgentState state = (AgentState) this.agent.getState();
        Type dataType = ebesa.getData().getClass();

        if (dataType == RegisterAgentData.class) {
            state.agentRegisteredInWorld = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AgentSensorGuard.class.getName()).log(Level.SEVERE, null, ex);
            }
            ((Agent) agent).sendWorldInfoRequest();
        }

        if (dataType == InfoRequestData.class) {
            InfoRequestData i = (InfoRequestData) ebesa.getData();
            if (i != null) {
                ((Agent) agent).updateMove(i.robotSensors);
            } else {
                ((Agent) agent).sendWorldInfoRequest();
            }
        }
        
        if (dataType == MoveData.class) {
            ((Agent) agent).sendWorldInfoRequest();
        }

    }
}
