package SimulationBESA.World.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SimulationBESA.Agents.Agent;
import SimulationBESA.Agents.Guards.AgentSensorGuard;
import SimulationBESA.Data.RegisterAgentData;
import SimulationBESA.World.WorldAgent;
import SimulationBESA.World.WorldState;

public class RegisterAgentGuard extends GuardBESA {

    @Override
    public void funcExecGuard(EventBESA ebesa) {
        System.out.println("Guarda: " + this.getClass().getName());

        WorldState state = (WorldState) this.agent.getState();
        
        RegisterAgentData rad = (RegisterAgentData) ebesa.getData();
        if (((WorldAgent)agent).registerAgent(rad)) {
            Agent.sendMessage(AgentSensorGuard.class, rad.agentAlias, rad);
        }
    }
}
