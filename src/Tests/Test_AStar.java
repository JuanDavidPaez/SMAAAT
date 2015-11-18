package Tests;

import Utils.Const;
import Utils.Utils;
import World3D.Floor.GridPoint;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class Test_AStar {

    A_Star_Panel guiPanel;
    String floorFilePath;
    GridPoint start;
    GridPoint end;

    public Test_AStar() {
        //test1();
        test2();
        guiPanel = new A_Star_Panel(floorFilePath, start, end);
        setupJFrame();
    }

    private void test1() {
        floorFilePath = Utils.getResourceFilePath("Smaaat/15x15_empty.smaaat");
        //floorFilePath = Utils.getResourceFilePath("Smaaat/15x15_obstacles.smaaat");
        start = new GridPoint(8, 11);
        end = new GridPoint(5, 3);
    }

    private void test2() {
        floorFilePath = Utils.getResourceFilePath("Smaaat/50x50_complex.smaaat");
        start = new GridPoint(26, 11);
        end = new GridPoint(23, 35);
    }
    
    public void setupJFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("A Star test");
        frame.setSize(guiPanel.getPixelWidth() + 20, guiPanel.getPixelHeight() + 40);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(guiPanel);
        frame.addKeyListener(buildKeyListener());

    }

    private KeyListener buildKeyListener() {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        };
    }

    public void startTest() {
        guiPanel.start();
    }

    public static void main(String[] args) {
        Test_AStar test = new Test_AStar();
        test.startTest();
    }
}
