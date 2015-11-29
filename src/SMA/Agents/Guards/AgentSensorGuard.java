package SMA.Agents.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import SMA.Agents.Agent;
import SMA.Agents.States.AgentState;
import SMA.GuardsData.CollisionData;
import SMA.GuardsData.HostageRescuedData;
import SMA.GuardsData.Message;
import SMA.GuardsData.WorldInfoData;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import SMA.GuardsData.ShootData;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class AgentSensorGuard extends GuardBESA {

    int count = 0;
    long lastTime = 0;

    @Override
    public void funcExecGuard(EventBESA ebesa) {

        AgentState state = (AgentState) this.agent.getState();
        Type dataType = ebesa.getData().getClass();

        if (dataType == RegisterAgentData.class) {
            state.agentRegisteredInWorld = true;
            ((Agent) agent).sendWorldInfoRequest();
        }

        if (dataType == WorldInfoData.class) {
            //addTick();
            WorldInfoData i = (WorldInfoData) ebesa.getData();
            if (i != null) {
                ((Agent) agent).processWorldInfoResponse(i);
            } else {
                ((Agent) agent).sendWorldInfoRequest();
            }
        }

        if (dataType == MoveData.class) {
            ((Agent) agent).sendWorldInfoRequest();
        }

        if (dataType == ShootData.class) {
            ((Agent) agent).sendWorldInfoRequest();
        }

        if (dataType == CollisionData.class) {
            ((Agent) agent).processBulletCollision((CollisionData) ebesa.getData());
        }
        
        if (dataType == HostageRescuedData.class) {
            ((Agent) agent).hostageRescued((HostageRescuedData) ebesa.getData());
        }
    }

    private void addTick() {
        long time = System.currentTimeMillis();
        count++;
        if (count == 0 || TimeUnit.MILLISECONDS.toSeconds(time - lastTime) >= 1) {
            lastTime = time;
            System.out.println(count + " executions in 1 seconds");
            count = 0;
        }
    }
}
