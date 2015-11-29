package World3D;

import SMA.Agents.Agent;
import World3D.Floor.Floor3D;
import World3D.Floor.FloorData;
import World3D.Floor.GridPoint;
import java.util.ArrayList;
import java.util.List;

public class ObjectsLists {

    public Object3DMap explorers;
    public Object3DMap hostages;
    public Object3DMap protectors;
    public Object3DMap enemies;

    public ObjectsLists() {
        this.explorers = new Object3DMap();
        this.hostages = new Object3DMap();
        this.protectors = new Object3DMap();
        this.enemies = new Object3DMap();
    }

    public boolean updateObjects(List<Object3D> objects) {
        int changes = 0;
        if (objects != null && objects.size() > 0) {
            for (Object3D o : objects) {
                if (o.agentType == Agent.AgentType.Enemy) {
                    changes += enemies.put(o);
                }
                if (o.agentType == Agent.AgentType.Explorer) {
                    changes += explorers.put(o);
                }
                if (o.agentType == Agent.AgentType.Hostage) {
                    changes += hostages.put(o);
                }
                if (o.agentType == Agent.AgentType.Protector) {
                    changes += protectors.put(o);
                }
            }
        }
        return changes > 0;
    }

    public Object3D firstObjectInPosition(GridPoint p, FloorData fd) {
        Object3D o = null;
        o = getFirstObjectInSet(enemies, p, fd);
        if (o == null) {
            o = getFirstObjectInSet(explorers, p, fd);
        }
        if (o == null) {
            o = getFirstObjectInSet(hostages, p, fd);
        }
        if (o == null) {
            o = getFirstObjectInSet(protectors, p, fd);
        }
        return o;
    }

    private Object3D getFirstObjectInSet(Object3DMap set, GridPoint p, FloorData fd) {
        Object3D[] o = searchInSet(set, p, fd, true);
        return (o == null) ? null : o[0];
    }

    private Object3D[] searchInSet(Object3DMap set, GridPoint p, FloorData fd, boolean first) {
        List<Object3D> list = new ArrayList<Object3D>();
        for (Object3D o : set.getObjects()) {
            GridPoint gp = Floor3D.Vector3fToGridPoint(o.position3D, fd.XSize, fd.YSize);
            if (p.equals(gp)) {
                list.add(o);
                if (first) {
                    break;
                }
            }
        }
        return (list.isEmpty()) ? null : list.toArray(new Object3D[0]);
    }

    public void removeObjectsInPosition(GridPoint p, FloorData fd) {
        
        Object3D[] list = searchInSet(enemies, p, fd, false);
        remove(list, enemies);
        list = searchInSet(explorers, p, fd, false);
        remove(list, explorers);
        list = searchInSet(hostages, p, fd, false);
        remove(list, hostages);
        list = searchInSet(protectors, p, fd, false);
        remove(list, protectors);

    }

    private void remove(Object3D[] list, Object3DMap set) {
        if (list != null && list.length > 0) {
            for (Object3D o : list) {
                set.remove(o);
            }
        }
    }
}
