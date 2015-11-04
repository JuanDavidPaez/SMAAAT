package SimulationBESA.World.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SimulationBESA.Data.InfoRequestData;
import SimulationBESA.Data.MoveData;
import SimulationBESA.World.WorldAgent;
import java.lang.reflect.Type;

public class WorldInfoRequestGuard extends GuardBESA {

    @Override
    public void funcExecGuard(EventBESA ebesa) {
        //System.out.println("Guarda: " + this.getClass().getName());

        WorldAgent wAgent = ((WorldAgent) agent);
        Type dataType = ebesa.getData().getClass();

        if (dataType == InfoRequestData.class) {
            InfoRequestData i = (InfoRequestData) ebesa.getData();
            wAgent.handleWorldInfoRequest(i);
        }

        if (dataType == MoveData.class) {
            MoveData i = (MoveData) ebesa.getData();
            wAgent.handleMoveRequest(i);
        }
    }
}
