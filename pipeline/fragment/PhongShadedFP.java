package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This is the fragment program which actually uses a shading model to compute
 * the color on a per fragment basis.
 * 
 * @author ags
 */
public class PhongShadedFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 9;   // diffuse color (r,g,b), normal (x,y,z), fragment position (x,y,z)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
		Color3f inColor = new Color3f(f.attrs[1],f.attrs[2],f.attrs[3]);
		Color3f outColor = new Color3f(f.attrs[1],f.attrs[2],f.attrs[3]);
		Vector3f lV = new Vector3f();
		Vector3f h = new Vector3f();
		
		outColor.scale(Pipeline.ambientIntensity);
		
		Vector3f norm = new Vector3f(f.attrs[4],f.attrs[5],f.attrs[6]);
		norm.normalize();

		Point3f position = new Point3f(f.attrs[7],f.attrs[8],f.attrs[9]);
		Vector3f toEye = new Vector3f(position);
		toEye.scale(-1);
		toEye.normalize();

		for (int i = 0; i < Pipeline.lights.size(); i++) {
			PointLight l = Pipeline.lights.get(i);
			lV.sub(l.getPosition(), position);
			lV.normalize();
			h.add(toEye, lV);
			h.normalize();

			Color3f intensity = l.getIntensity();
			// Perform Math Calculations before rather than trying to iterate
			// through the Math library 8129418239 times.
			double a = Math.max(0, lV.dot(norm));
			// also uses Pipeline.specularExponent
			double b = Math.pow(Math.max(0, norm.dot(h)), Pipeline.specularExponent);


			// Lambertian Component
			
			// diffuse term uses vertex color c and light color
			outColor.x += (inColor.x * intensity.x * a);
			outColor.y += (inColor.y * intensity.y * a);
			outColor.z += (inColor.z * intensity.z * a);
			
			// Phong Component
			outColor.x += (Pipeline.specularColor.x * b);
			outColor.y += (Pipeline.specularColor.y * b);
			outColor.z += (Pipeline.specularColor.z * b);

		}
		outColor.clamp(0, 1);
		
		if (f.attrs[0] < fb.getZ(f.x, f.y))
			fb.set(f.x, f.y, outColor.x, outColor.y, outColor.z, f.attrs[0]);
	}
}
