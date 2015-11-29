package SMA.Agents;

import BESA.Kernel.Agent.KernellAgentExceptionBESA;

public class AgentHostage extends Agent {

    AgentHostage(AParams p) throws KernellAgentExceptionBESA {
        super(AgentType.Hostage, p);
    }

    @Override
    protected boolean shallShootAgent(AgentType type) {
        return false;
    }
}
