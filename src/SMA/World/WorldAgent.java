package SMA.World;

import World3D.WorldApp;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernellAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import SMA.Agents.Agent;
import SMA.Agents.Guards.AgentSensorGuard;
import SMA.GuardsData.CollisionData;
import SMA.GuardsData.WorldInfoData;
import SMA.GuardsData.Message;
import SMA.GuardsData.MoveData;
import SMA.GuardsData.RegisterAgentData;
import SMA.GuardsData.ShootData;
import Utils.Const;
import SMA.World.Guards.WorldInfoRequestGuard;
import Utils.Config;
import Utils.Const.Aliases;
import World3D.Character3D;
import World3D.Exit;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
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
            wa = new WorldAgent(Aliases.WorldAgentAlias, e, s);
        } catch (ExceptionBESA ex) {
            Logger.getLogger(WorldAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wa;
    }

    private WorldAgent(String alias, StateBESA state, StructBESA structAgent) throws KernellAgentExceptionBESA {
        super(alias, state, structAgent, Config.BESApassword);
        worldApp = WorldApp.createWorldApp(this);
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

    public WorldApp getWorldApp() {
        return this.worldApp;
    }

    private Character3D findAgentCharacter(String agentAlias) {
        Spatial s = worldApp.getCharacterNode().getChild(Agent.getAgentNodeName(agentAlias));
        if (s != null) {
            return s.getUserData(Const.Character);
        } else {
            return null;
        }
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

    private Character3D getRegisteredAgentCharacter(String agentAlias) {
        if (getWorldState().registeredAgents.contains(agentAlias)) {
            return findAgentCharacter(agentAlias);
        }
        return null;
    }

    private void replyMessage(Message msg) {
        msg.updateToReply();
        Agent.sendMessage(msg);
    }

    public void handleRegisterAgentRequest(RegisterAgentData r) {
        WorldState s = getWorldState();
        final String agentAlias = r.fromAgentAlias();
        if (getRegisteredAgentCharacter(agentAlias) == null) {
            s.registeredAgents.add(agentAlias);
            final RegisterAgentData data = r;
            execAsyncInWorldThread(new Callable<Void>() {
                public Void call() throws Exception {
                    new Character3D(worldApp, agentAlias, data.position, data.direction, data.agentType);
                    return null;
                }
            });
            replyMessage(r);
        }
    }

    public void handleWorldInfoRequest(WorldInfoData i) {
        final Character3D c = getRegisteredAgentCharacter(i.fromAgentAlias());
        if (c == null) {
            return;
        }
        c.enqueueWorldInfoRequest(i);
    }

    @Deprecated
    public void handleWorldInfoRequest_Old(WorldInfoData i) {
        final Character3D c = getRegisteredAgentCharacter(i.fromAgentAlias());
        if (c == null) {
            return;
        }

        final WorldInfoData aux = i;
        execAsyncInWorldThread(new Callable<Void>() {
            public Void call() throws Exception {
                aux.position = c.getPosition();
                aux.direction = c.getDirection();
                aux.partialFloorView = c.getCurrentFloorView();
                aux.seenObjects = c.getSeenCharacters();
                return null;
            }
        });
        replyMessage(i);
    }

    public void handleMoveRequest(final MoveData md) {
        final Character3D c = getRegisteredAgentCharacter(md.fromAgentAlias());
        if (c == null) {
            return;
        }
        c.moveCharacter(md);
        replyMessage(md);
    }

    public void handleShootRequest(ShootData i) {
        final Character3D c = getRegisteredAgentCharacter(i.fromAgentAlias());
        if (c == null) {
            return;
        }
        c.shoot(i);
        replyMessage(i);
    }

    public void notifyAgentHitttedByBullet(Character3D character, CollisionResult collision) {
        CollisionData msg = new CollisionData(this.getAlias(), character.getAgentAlias(), AgentSensorGuard.class);
        msg.contactPoint = collision.getContactPoint();
        msg.live = character.getLive();
        Agent.sendMessage(msg);
        if (character.getLive() == 0) {
            getWorldState().registeredAgents.remove(character.getAgentAlias());
        }
    }

    public void notifyAgentWorldInfoData(Character3D character, WorldInfoData wid) {
        replyMessage(wid);
    }
}
