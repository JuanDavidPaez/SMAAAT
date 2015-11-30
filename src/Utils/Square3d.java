package Utils;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

public class Square3d extends Mesh {

    private Vector3f center;
    private float radius;

    public Square3d(Vector3f center, float radius) {
        super();
        this.center = center;
        this.radius = radius;

        setMode(Mode.Lines);
        updateGeometry();
    }

    protected void updateGeometry() {
        int samples = 4;
        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);
        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);
        short[] indices = new short[8];

        float rate = FastMath.TWO_PI / (float) samples;
        float angle = FastMath.QUARTER_PI;
        int idc = 0;
        for (int i = 0; i < samples; i++) {
            float x = FastMath.cos(angle) + center.x;
            float z = FastMath.sin(angle) + center.z;

            positions.put(x * radius).put(center.y).put(z * radius);
            normals.put(new float[]{0, 1, 0});

            indices[idc++] = (short) i;
            if (i < samples - 1) {
                indices[idc++] = (short) (i + 1);
            } else {
                indices[idc++] = 0;
            }

            angle += rate;
        }

        setBuffer(Type.Position, 3, positions);
        setBuffer(Type.Normal, 3, normals);
        setBuffer(Type.Index, 2, indices);

        setBuffer(Type.TexCoord, 2, new float[]{0, 0, 1, 1});

        updateBound();
    }
}
