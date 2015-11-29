package World3D;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Object3DMap {

    private Map<Integer, Object3D> map;

    public Object3DMap() {
        this.map = Collections.synchronizedMap(new HashMap<Integer, Object3D>());
    }

    /**
     * *
     *
     * @param value
     * @return 1 si la lista se modificó, 0 si no se modificó
     */
    public int put(Object3D value) {
        int change = 0;
        Object3D old = this.map.put(value.hashCode(), value);
        if (old == null) {
            change = 1;
        } else if (old.position3D.equals(value.position3D)) {
            change = 1;
        }
        return (change);
    }

    public Collection<Object3D> getObjects() {
        return map.values();
    }

    public int size() {
        return this.map.size();
    }

    public void clear() {
        map.clear();
    }

    public void remove(Object3D o) {
        map.remove(o.hashCode());
    }
    
    public Iterator<Entry<Integer,Object3D>> getIterator(){
        return map.entrySet().iterator();
    }
}
