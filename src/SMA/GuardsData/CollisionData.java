package SMA.GuardsData;

import com.jme3.math.Vector3f;

public class CollisionData extends Message {

    public Vector3f contactPoint;
    public int live;
    
    public CollisionData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }

}
