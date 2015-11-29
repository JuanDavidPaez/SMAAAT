package Utils;

public final class Const {

    private static class FloorFiles {

        public static final String floor = "Smaaat/floor.smaaat";
        public static final String Empty15x15 = "Smaaat/15x15_empty.smaaat";
        public static final String Obstacles15x15 = "Smaaat/15x15_obstacles.smaaat";
        public static final String Obstacles10x17 = "Smaaat/10x17_obstacles.smaaat";
    }
    
    public static final boolean inDebugMode = true;
    public static final double BESApassword = 0.91;
    
    public static final String DebugWord = "Debug_";
    public static final String NodePrefix = "Node_";
    public static final String AgentAlias = "AgentAlias";
    public static final String Character = "Character";
    public static final String Exit = "Exit";
    
    public static final String SmaaatFloorFileName = FloorFiles.Empty15x15;
    public static final float FloorGridCellSize = 0.4f;
    public static final float CharacterRadius = FloorGridCellSize / 2;
    
    public static class Aliases{
        public static final String WorldAgentAlias = "WorldAgent";    
    }
}
