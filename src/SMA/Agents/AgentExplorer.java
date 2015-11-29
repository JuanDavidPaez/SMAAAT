package SMA.Agents;

import BESA.Kernel.Agent.KernellAgentExceptionBESA;

public class AgentExplorer extends Agent {

    AgentExplorer(AParams p) throws KernellAgentExceptionBESA {
        super(AgentType.Explorer, p);
    }

    @Override
    protected boolean shallShootAgent(AgentType type) {
        return false;
    }
}
