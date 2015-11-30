package SMA.GuardsData;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class MoveData extends Message {

    public Quaternion rotation;
    public Vector3f direction;
    public boolean forward;
    public boolean halfSpeed;

    public MoveData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
