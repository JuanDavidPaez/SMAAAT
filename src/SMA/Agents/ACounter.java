package SMA.Agents;

import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import SMA.Agents.Agent.AgentType;

public class ACounter {

    private int explorers = 0;
    private int enemies = 0;
    private int hostages = 0;
    private int protectors = 0;

    public int newAgent(Agent.AgentType type) {
        int id = 0;
        switch (type) {
            case Enemy:
                enemies++;
                id = enemies;
                break;
            case Explorer:
                explorers++;
                id = explorers;
                break;
            case Hostage:
                hostages++;
                id = hostages;
                break;
            case Protector:
                protectors++;
                id = protectors;
                break;

        }
        return id;
    }


}
