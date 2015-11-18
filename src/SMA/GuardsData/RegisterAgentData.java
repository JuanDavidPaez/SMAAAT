package SMA.GuardsData;

import com.jme3.math.Vector3f;

public class RegisterAgentData extends Message {

    public Vector3f position;
    public Vector3f direction;

    public RegisterAgentData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
