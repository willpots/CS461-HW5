package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

public class ReflectionMapFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 9;   // diffuse color (r,g,b), normal (x,y,z), fragment position (x,y,z)
    }

    public void fragment(Fragment f, FrameBuffer fb) {
		Color3f diffuseColor = new Color3f(f.attrs[1],f.attrs[2],f.attrs[3]);
		Vector3f normal = new Vector3f(f.attrs[4],f.attrs[5],f.attrs[6]);
		Point3f position = new Point3f(f.attrs[7],f.attrs[8],f.attrs[9]);
		Vector3f V = new Vector3f(position);
		float d = normal.dot(V);
		
		Vector3f r = new Vector3f(normal);
		r.scale(-2*d);
		r.add(V);
		
		float m = (float) (2*Math.sqrt(Math.pow(r.x, 2)+Math.pow(r.y, 2)+Math.pow(r.z+1, 2)));
		float u = (float) ((r.x/m)+0.5);
		float v = (float) ((r.y/m)+0.5);
		
		Color3f outColor = new Color3f();
		Vector2f inCoords = new Vector2f(u,v);

		texture.sample(inCoords, outColor);

		outColor.clamp(0, 1);
		
		if (f.attrs[0] < fb.getZ(f.x, f.y)) {
			fb.set(f.x, f.y, diffuseColor.x * outColor.x, diffuseColor.y * outColor.y,
					diffuseColor.z * outColor.z, f.attrs[0]);
		}

    }
}
