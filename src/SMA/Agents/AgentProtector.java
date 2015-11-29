package SMA.Agents;

import BESA.Kernel.Agent.KernellAgentExceptionBESA;

public class AgentProtector extends Agent {

    AgentProtector(AParams p) throws KernellAgentExceptionBESA {
        super(AgentType.Protector, p);
    }

    @Override
    protected boolean shallShootAgent(AgentType type) {
        return (type == AgentType.Enemy);
    }
}
