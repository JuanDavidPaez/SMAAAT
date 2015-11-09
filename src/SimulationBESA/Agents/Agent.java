package SimulationBESA.Agents;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.Event.DataBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.KernellAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import SimulationBESA.Agents.Guards.AgentSensorGuard;
import SimulationBESA.Data.InfoRequestData;
import SimulationBESA.Data.MoveData;
import SimulationBESA.Data.RegisterAgentData;
import SimulationBESA.Utils.Const;
import SimulationBESA.World.Guards.RegisterAgentGuard;
import SimulationBESA.World.Guards.WorldInfoRequestGuard;
import SimulationBESA.World3D.RobotSensors;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Agent extends AgentBESA {

    public static Agent CreateAgent(String alias) {
        AgentState e = new AgentState();
        StructBESA s = new StructBESA();

        s.addBehavior("SensorUpdate");

        Agent a = null;

        try {
            s.bindGuard("SensorUpdate", AgentSensorGuard.class);
            a = new Agent(alias, e, s);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return a;

    }

    public Agent(String alias, StateBESA state, StructBESA structAgent) throws KernellAgentExceptionBESA {
        super(alias, state, structAgent, Const.BESApassword);
    }

    @Override
    public void setupAgent() {
    }

    @Override
    public void start() {
        super.start();
        sendWorldRegisterRequest();
    }

    @Override
    public void shutdownAgent() {
    }

    public static void sendMessage(Class guard, String alias, DataBESA data) {
        EventBESA ev = new EventBESA(guard.getName(), data);
        try {
            AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(alias);
            ah.sendEvent(ev);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //******************BEHAVIORS******************
    public void sendWorldRegisterRequest() {
        RegisterAgentData data = new RegisterAgentData();
        data.agentAlias = this.getAlias();
        data.position = new Vector3f(3, 0, 2);
        data.direction = new Vector3f(0, 0, 1);
        Agent.sendMessage(RegisterAgentGuard.class, Const.WorldAgentAlias, data);
    }

    public void sendWorldInfoRequest() {
        InfoRequestData i = new InfoRequestData();
        i.agentAlias = this.getAlias();
        Agent.sendMessage(WorldInfoRequestGuard.class, Const.WorldAgentAlias, i);
    }

    public void sendMovementRequest(MoveData moveData) {
        moveData.agentAlias = this.getAlias();
        Agent.sendMessage(WorldInfoRequestGuard.class, Const.WorldAgentAlias, moveData);
    }

    public void updateMove(RobotSensors rs) {
        MoveData move = new MoveData();

        if (rs != null) {
            /*No hay obstaculo en ningun sensor: avanza adelante*/
            if (!rs.srFrontL.collideWithObject && !rs.srFrontR.collideWithObject
                    && !rs.srRightF.collideWithObject && !rs.srRightR.collideWithObject
                    && !rs.srRearL.collideWithObject && !rs.srRearR.collideWithObject
                    && !rs.srLeftF.collideWithObject && !rs.srLeftR.collideWithObject) {
                move.forward = true;
            }
            /*Los dos sensores delanteros detectan objeto: Girar 90º */
            if (rs.srFrontL.collideWithObject && rs.srFrontR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }

            /*Los dos sensores de la derecha detectan objeto: avanza adelante*/
            if (rs.srRightF.collideWithObject && rs.srRightR.collideWithObject
                    || rs.srRightF.collideWithObject && !rs.srRightR.collideWithObject) {
                move.forward = true;
            }
            /*Sensor derecho frontal no detecta pero sensor derecho trasero si detecta: Gira 90º*/
            if (!rs.srRightF.collideWithObject && rs.srRightR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }

            /*Los dos sensores de la izquierda detectan objeto: avanza adelante*/
            if (rs.srLeftF.collideWithObject && rs.srLeftR.collideWithObject
                    || rs.srLeftF.collideWithObject && !rs.srLeftR.collideWithObject) {
                move.forward = true;
            }
            /*Sensor izquierda frontal no detecta pero sensor izquierdo trasero si detecta: Gira  90º*/
            if (!rs.srLeftF.collideWithObject && rs.srLeftR.collideWithObject) {
                Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
                move.rotation = rotate;
            }
            if (move.rotation == null && move.forward == false) {
                move.forward = true;
            }
        }
        sendMovementRequest(move);
    }
}
