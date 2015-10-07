package simulation.utils;

import com.jme3.scene.Spatial;

public class SpatialSeenObject {

    public Spatial spatial;
    public float distance;
    
    public SpatialSeenObject(Spatial spatial, float distance)
    {
        this.spatial = spatial;
        this.distance = distance;
    }
}
