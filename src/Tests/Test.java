package Tests;

import com.jme3.math.Vector3f;

public class Test {

    public static void main(String[] args) {
        
        Vector3f p1 = new Vector3f(0,0,0);
        Vector3f p2 = new Vector3f(1,0,1);
        float r = p1.distance(p2);
        
        System.out.println(r);
    }
   
}