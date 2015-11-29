package SMA.GuardsData;

import SMA.Agents.Agent;
import SMA.Agents.Agent.AgentType;
import SMA.Agents.Attributes;
import com.jme3.math.Vector3f;

public class RegisterAgentData extends Message {

    public Vector3f position;
    public Vector3f direction;
    public AgentType agentType;
    public Attributes attributes;

    public RegisterAgentData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
