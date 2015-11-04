package SimulationBESA.World;

import SimulationBESA.World3D.WorldApp;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernellAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import SimulationBESA.Agents.Agent;
import SimulationBESA.Agents.Guards.AgentSensorGuard;
import SimulationBESA.Data.InfoRequestData;
import SimulationBESA.Data.MoveData;
import SimulationBESA.Data.RegisterAgentData;
import SimulationBESA.Utils.Const;
import SimulationBESA.Utils.Utils;
import SimulationBESA.World.Guards.RegisterAgentGuard;
import SimulationBESA.World.Guards.WorldInfoRequestGuard;
import SimulationBESA.World3D.Character3D;
import SimulationBESA.World3D.Exit;
import SimulationBESA.World3D.RobotSensors;
import com.jme3.math.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldAgent extends AgentBESA implements ActionListener {

    WorldApp worldApp;
    ActionListener worldIsReadyListener;

    public static WorldAgent createWorldAgent() {
        WorldState e = new WorldState();
        StructBESA s = new StructBESA();
        s.addBehavior("RegisterAgentGuard");
        s.addBehavior("WorldInfoRequestGuard");
        WorldAgent wa = null;
        try {
            s.bindGuard("RegisterAgentGuard", RegisterAgentGuard.class);
            s.bindGuard("WorldInfoRequestGuard", WorldInfoRequestGuard.class);
            wa = new WorldAgent(Const.WorldAgentAlias, e, s);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(WorldAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wa;
    }

    private WorldAgent(String alias, StateBESA state, StructBESA structAgent) throws KernellAgentExceptionBESA {
        super(alias, state, structAgent, Const.BESApassword);
        worldApp = WorldApp.createWorldApp();
        worldApp.setInitAppCompletedListener(this);
        worldApp.start();
    }

    @Override
    public void setupAgent() {
    }

    @Override
    public void shutdownAgent() {
    }

    public void actionPerformed(ActionEvent e) {
        if (this.worldIsReadyListener != null) {
            worldIsReadyListener.actionPerformed(e);
        }
    }

    public void setWorldIsReadyListener(java.awt.event.ActionListener a) {
        this.worldIsReadyListener = a;
    }

    public WorldState getWorldState() {
        return (WorldState) this.getState();
    }

    //******************BEHAVIORS******************
    private <T> T execAsyncInWorldThread(Callable<T> func) {
        Future<T> future = worldApp.enqueue(func);
        T result = null;
        try {
            result = future.get();
        } catch (Exception ex) {
            Logger.getLogger(WorldAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void addExitObject() {
        Exit e = new Exit(this.worldApp, new Vector3f(-4.5f, 0, 4.8f), new Vector3f(0.5f, 1, 0.1f));
    }

    private boolean agentIsRegistered(String agentAlias) {
        return (getWorldState().registeredAgents.contains(agentAlias));
    }

    public boolean registerAgent(RegisterAgentData agentData) {
        WorldState s = getWorldState();
        if (!agentIsRegistered(agentData.agentAlias)) {
            s.registeredAgents.add(agentData.agentAlias);
            final RegisterAgentData data = agentData;
            execAsyncInWorldThread(new Callable<Void>() {
                public Void call() throws Exception {
                    new Character3D(worldApp, data.agentAlias, data.position, data.direction);
                    return null;
                }
            });
            return true;
        }
        return false;
    }

    public void handleWorldInfoRequest(InfoRequestData i) {
        if (agentIsRegistered(i.agentAlias)) {
            final String nodeName = i.agentAlias;
            RobotSensors rs = execAsyncInWorldThread(new Callable<RobotSensors>() {
                public RobotSensors call() throws Exception {
                    Character3D c = worldApp.getRootNode().getChild(Utils.GetNodeName(nodeName)).getUserData(Const.Character);
                    c.updateDistanceSensorsData();
                    return c.robotSensors.clone();
                }
            });
            i.robotSensors = rs;
            Agent.sendMessage(AgentSensorGuard.class, i.agentAlias, i);
        }
    }

    public void handleMoveRequest(final MoveData md) {
        if (agentIsRegistered(md.agentAlias)) {
            final String nodeName = md.agentAlias;
            execAsyncInWorldThread(new Callable<Void>() {
                public Void call() throws Exception {
                    Character3D c = worldApp.getRootNode().getChild(Utils.GetNodeName(nodeName)).getUserData(Const.Character);
                    c.moveCharacter(md);
                    return null;
                }
            });
            Agent.sendMessage(AgentSensorGuard.class, md.agentAlias, md);
        }
    }
}
