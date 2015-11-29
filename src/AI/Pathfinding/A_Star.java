package AI.Pathfinding;

import AI.Pathfinding.A_Star_Node.AsNodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class A_Star {

    private final float normalCost = 1;
    private final float diagonalCost = (int) (Math.sqrt(2) * normalCost);
    private int XSize;
    private int YSize;
    private A_Star_Node nodes[];
    private List<Integer> openSet;
    private List<Integer> closedSet;
    private int[] path;

    public A_Star(int XSize, int YSize) {
        this.XSize = XSize;
        this.YSize = YSize;
        nodes = new A_Star_Node[XSize * YSize];
        initNodes();
    }

    private void initNodes() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < nodes.length; i++) {
            A_Star_Node n = new A_Star_Node(i, x, y, AsNodeType.Empty);
            nodes[i] = n;
            x++;
            if (x >= XSize) {
                x = 0;
                y++;
            }
        }
    }

    public void addObstacles(int[] obstaclesIds) {
        if (obstaclesIds != null) {
            for (int i : obstaclesIds) {
                addObstacle(i);
            }
        }
    }

    public void addObstacle(int obstacleId) {
        nodes[obstacleId].setType(A_Star_Node.AsNodeType.Obstacle);
    }

    public int[] findPath(int startNodeId, int endNodeId) {

        path = null;
        openSet = new ArrayList<Integer>();
        closedSet = new ArrayList<Integer>();
        nodes[startNodeId].setGcostAndHcost(0f, manhattanDistance(startNodeId, endNodeId));
        openSet.add(startNodeId);

        while (openSet.size() > 0) {
            String exdebug = "";
            try {
                exdebug = printCostException(openSet);
                Collections.sort(openSet, new NodeCostComparator());
            } catch (Exception ex) {
                System.out.println(exdebug);
                throw new RuntimeException(ex.toString());
            }

            int currentNodeId = openSet.get(0);
            openSet.remove(0);
            closedSet.add(currentNodeId);

            if (currentNodeId == endNodeId) {
                reconstructPath(currentNodeId, startNodeId);
                break;
            }
            List<Integer> neighbours = findNodeNeighbours(currentNodeId);
            if (neighbours.size() > 0) {
                for (Integer neighbour : neighbours) {
                    A_Star_Node nNode = nodes[neighbour];
                    boolean diagonal = areDiagonalNodes(currentNodeId, neighbour);
                    float newGCost = nodes[currentNodeId].getGcost() + (diagonal ? diagonalCost : normalCost);
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else if (newGCost >= nNode.getGcost()) {
                        continue;
                    }
                    nNode.setParentNodeId(currentNodeId);
                    nNode.setGcostAndHcost(newGCost, manhattanDistance(nNode.getId(), endNodeId));
                }
            }
        }
        return path;
    }

    private void reconstructPath(int currentNodeId, int startNodeId) {
        path = null;
        List<Integer> auxpath = new ArrayList<Integer>();
        auxpath.add(currentNodeId);
        int id = currentNodeId;
        while (id != startNodeId) {
            id = nodes[id].getParentNodeId();
            auxpath.add(id);
        }
        if (auxpath.size() > 0) {
            path = new int[auxpath.size()];
            int c = 0;
            for (int i = auxpath.size() - 1; i >= 0; i--) {
                path[c] = auxpath.get(i);
                c++;
            }
        }
    }

    private float manhattanDistance(int currentNodeId, int goalNodeId) {
        float Dx = Math.abs(nodes[goalNodeId].getX() - nodes[currentNodeId].getX());
        float Dy = Math.abs(nodes[goalNodeId].getY() - nodes[currentNodeId].getY());
        return (Dx + Dy);
    }

    private List<Integer> findNodeNeighbours(int nodeId) {
        List<Integer> neighbours = new ArrayList<Integer>();
        int[] ids = new int[4];
        ids[0] = nodeId - XSize;//up
        ids[1] = nodeId + XSize;//down
        ids[2] = nodeId - 1;//left
        ids[3] = nodeId + 1;//right
        /*Solo 4 vecinos*/
        /*ids[4] = nodeId - XSize - 1;//upleft
         ids[5] = nodeId - XSize + 1;//upright
         ids[6] = nodeId + XSize - 1;//downleft
         ids[7] = nodeId + XSize + 1;//downright*/
        for (int i : ids) {
            if (i >= 0 && i < nodes.length) {
                if (!nodes[i].getType().equals(AsNodeType.Obstacle)) {
                    if (!closedSet.contains(i)) {
                        neighbours.add(i);
                    }
                }
            }
        }
        return neighbours;
    }

    private boolean areDiagonalNodes(int nodeId1, int nodeId2) {
        return (nodes[nodeId1].getX() == nodes[nodeId2].getX() || nodes[nodeId1].getY() == nodes[nodeId2].getY());
    }

    protected class NodeCostComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Integer n1 = (Integer) o1;
            Integer n2 = (Integer) o2;
            if (nodes[n1].getFcost() == nodes[n2].getFcost()) {
                if (nodes[n1].getHcost() == nodes[n2].getHcost()) {
                    return 0;
                } else {
                    return (nodes[n1].getHcost() > nodes[n2].getHcost()) ? 1 : -1;
                }
            } else {
                return (nodes[n1].getFcost() > nodes[n2].getFcost()) ? 1 : -1;
            }
        }
    }

    public int[] getPath() {
        return path;
    }

    protected String printCostException(List<Integer> set) {
        String str = "";
        for (Integer i : set) {
            str += (i + ". F: " + nodes[i].getFcost() + " H: " + nodes[i].getHcost() + " G: " + nodes[i].getGcost());
            str += "\r\n";
        }
        return str;
    }
}
