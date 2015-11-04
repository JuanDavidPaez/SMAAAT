package SimulationBESA.Data;

import BESA.Kernel.Agent.Event.DataBESA;
import com.jme3.math.Quaternion;

public class MoveData extends DataBESA {
    public String agentAlias;
    public Quaternion rotation;
    public boolean forward;

}
