package SMA.World.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SMA.GuardsData.WorldInfoData;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import SMA.GuardsData.ShootData;
import SMA.World.WorldAgent;
import java.lang.reflect.Type;

public class WorldInfoRequestGuard extends GuardBESA {

    @Override
    public void funcExecGuard(EventBESA ebesa) {

        WorldAgent wAgent = ((WorldAgent) agent);
        Type dataType = ebesa.getData().getClass();

        if (dataType == RegisterAgentData.class) {
            RegisterAgentData rad = (RegisterAgentData) ebesa.getData();
            wAgent.handleRegisterAgentRequest(rad);
        }

        if (dataType == WorldInfoData.class) {
            WorldInfoData i = (WorldInfoData) ebesa.getData();
            wAgent.handleWorldInfoRequest(i);
        }

        if (dataType == MoveData.class) {
            MoveData i = (MoveData) ebesa.getData();
            wAgent.handleMoveRequest(i);
        }
        
        if (dataType == ShootData.class) {
            ShootData i = (ShootData) ebesa.getData();
            wAgent.handleShootRequest(i);
        }
    }
}
