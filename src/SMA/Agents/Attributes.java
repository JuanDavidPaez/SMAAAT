package SMA.Agents;

import SMA.Agents.Agent.AgentType;
import Utils.Config;

public class Attributes {

    /*Valores estandard*/
    public float sightRange = (2.0f * Config.FloorGridCellSize);
    public float speed = 2;

    public Attributes(AgentType agentType) {
        switch (agentType) {
            case Enemy:
                EnemyAtttributes();
                break;
            case Explorer:
                ExplorerAtttributes();
                break;
            case Hostage:
                HostageAttributes();
                break;
            case Protector:
                ProtectorAtttributes();
                break;
        }
    }

    private void EnemyAtttributes() {
    }

    private void ExplorerAtttributes() {
        this.speed = speed * 1.5f;
        this.sightRange = (3.0f * Config.FloorGridCellSize);
    }

    private void HostageAttributes() {
        this.speed = speed * 0.5f;
        this.sightRange = Config.FloorGridCellSize;
        
    }

    private void ProtectorAtttributes() {
    }
}
