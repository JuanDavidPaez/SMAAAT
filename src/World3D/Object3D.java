package World3D;

import SMA.Agents.Agent;
import SMA.Agents.Agent.AgentType;
import com.jme3.math.Vector3f;

public class Object3D {

    public final String name;
    public final AgentType agentType;
    public Vector3f position3D;

    public Object3D(String name, Vector3f position3D, AgentType type) {
        this.name = name;
        this.agentType = type;
        this.position3D = position3D;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof Object3D)) {
            return false;
        } else {
            Object3D o = (Object3D) obj;
            return (this.agentType == o.agentType && this.name.equals(o.name));
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 3;
        hashCode = 37 * hashCode + (this.name.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Position: " + position3D;
    }
    
    
}
