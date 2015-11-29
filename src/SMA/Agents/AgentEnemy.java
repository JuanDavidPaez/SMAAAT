package SMA.Agents;

import BESA.Kernel.Agent.KernellAgentExceptionBESA;

public class AgentEnemy extends Agent {

    AgentEnemy(AParams p) throws KernellAgentExceptionBESA {
        super(AgentType.Enemy, p);
    }

    @Override
    protected boolean shallShootAgent(AgentType type) {
        return (type == AgentType.Explorer || type == AgentType.Protector);
    }
}
