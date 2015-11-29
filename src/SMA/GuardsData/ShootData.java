package SMA.GuardsData;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class ShootData extends Message {

    public Vector3f target;

    public ShootData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
