package SMA.GuardsData;

import World3D.Floor.FloorDataChunk;
import World3D.Object3D;
import com.jme3.math.Vector3f;
import java.util.List;

public class WorldInfoData extends Message {

    public Vector3f position;
    public Vector3f direction;
    public FloorDataChunk partialFloorView;
    public List<Object3D> seenObjects;

    public WorldInfoData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }

    @Override
    public WorldInfoData clone() {
        WorldInfoData w = new WorldInfoData(this.fromAgentAlias, this.toAgentAlias, this.toGuard);
        //w.id = this.id;
        if(this.replyGuard != null)
            w.setReplyGuard(this.replyGuard);
        return w;
    }
}
