package SMA.GuardsData;

import World3D.Floor.FloorDataChunk;
import World3D.Object3D;
import com.jme3.math.Vector3f;
import java.util.List;

public class InfoRequestData extends Message {

    public Vector3f position;
    public Vector3f direction;
    public FloorDataChunk partialFloorView;
    public List<Object3D> seenObjects;

    public InfoRequestData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
