package SMA.Agents.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SMA.Agents.Agent;
import SMA.Agents.AgentState;
import SMA.GuardsData.InfoRequestData;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentSensorGuard extends GuardBESA {

    @Override
    public void funcExecGuard(EventBESA ebesa) {

        AgentState state = (AgentState) this.agent.getState();
        Type dataType = ebesa.getData().getClass();

        if (dataType == RegisterAgentData.class) {
            state.agentRegisteredInWorld = true;
            ((Agent) agent).sendWorldInfoRequest();
        }

        if (dataType == InfoRequestData.class) {
            InfoRequestData i = (InfoRequestData) ebesa.getData();
            if (i != null) {
                ((Agent) agent).processInfoRequest(i);
            } else {
                ((Agent) agent).sendWorldInfoRequest();
            }
        }

        if (dataType == MoveData.class) {
            ((Agent) agent).sendWorldInfoRequest();
        }

    }
}
