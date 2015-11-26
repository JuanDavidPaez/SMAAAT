package SMA.World;

import World3D.WorldApp;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernellAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import SMA.Agents.Agent;
import SMA.GuardsData.InfoRequestData;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import Utils.Const;
import Utils.Utils;
import SMA.World.Guards.WorldInfoRequestGuard;
import World3D.Character3D;
import World3D.Exit;
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
        s.addBehavior("WorldInfoRequestGuard");
        WorldAgent wa = null;
        try {
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

    public void handleRegisterAgentRequest(RegisterAgentData r) {
        WorldState s = getWorldState();
        final String agentAlias = r.fromAgentAlias();
        if (!agentIsRegistered(agentAlias)) {
            s.registeredAgents.add(agentAlias);
            final RegisterAgentData data = r;
            execAsyncInWorldThread(new Callable<Void>() {
                public Void call() throws Exception {
                    new Character3D(worldApp, agentAlias, data.position, data.direction);
                    return null;
                }
            });
            r.updateToReply();
            Agent.sendMessage(r);
        }
    }

    public void handleWorldInfoRequest(InfoRequestData i) {
        final String agentAlias = i.fromAgentAlias();
        if (agentIsRegistered(agentAlias)) {
            final String nodeName = agentAlias;
            final InfoRequestData ird = i;
            /*execAsyncInWorldThread(new Callable() {
                public Object call() throws Exception {*/
                    Character3D c = worldApp.getRootNode().getChild(Utils.GetNodeName(nodeName)).getUserData(Const.Character);
                    //c.updateDistanceSensorsData();
                    //ird.robotSensors =  c.robotSensors.clone();
                    ird.partialFloorView  = c.getCurrentFloorView();
                    ird.position = c.getPosition();
                    ird.direction = c.getDirection();
                    /*return null;
                }
            });*/
            
            i.partialFloorView = ird.partialFloorView;
            i.updateToReply();
            Agent.sendMessage(i);
        }
    }

    public void handleMoveRequest(final MoveData md) {
        final String agentAlias = md.fromAgentAlias();
        if (agentIsRegistered(agentAlias)) {
            final String nodeName = agentAlias;
            /*execAsyncInWorldThread(new Callable<Void>() {
                public Void call() throws Exception {*/
                    Character3D c = worldApp.getRootNode().getChild(Utils.GetNodeName(nodeName)).getUserData(Const.Character);
                    c.moveCharacter(md);
                    /*return null;
                }
            });*/
            md.updateToReply();
            Agent.sendMessage(md);
        }
    }
}
