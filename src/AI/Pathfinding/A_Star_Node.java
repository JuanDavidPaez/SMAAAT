package AI.Pathfinding;

public class A_Star_Node {

    public enum AsNodeType {

        Empty, Obstacle
    }
    private int id = -1;
    private int x = -1;
    private int y = -1;
    private Float G_cost;
    private Float H_cost;
    private AsNodeType type = AsNodeType.Empty;
    private Integer parentNodeId;

    public A_Star_Node(int id, int x, int y, AsNodeType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Float getFcost() {
        return G_cost + H_cost;
    }

    public int getId() {
        return id;
    }

    public AsNodeType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setType(AsNodeType type) {
        this.type = type;
    }

    public Integer getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(Integer parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public Float getGcost() {
        return G_cost;
    }

    public Float getHcost() {
        return H_cost;
    }

    public void setGcostAndHcost(Float G_cost, Float H_cost) {
        this.G_cost = G_cost;
        this.H_cost = H_cost;
    }
}
