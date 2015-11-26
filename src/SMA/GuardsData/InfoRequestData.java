package SMA.GuardsData;

import World3D.Floor.FloorData;
import World3D.Floor.FloorDataChunk;
import World3D.RobotSensors;
import com.jme3.math.Vector3f;

public class InfoRequestData extends Message {

    public Vector3f position;
    public Vector3f direction;
    public FloorDataChunk partialFloorView;

    public InfoRequestData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
