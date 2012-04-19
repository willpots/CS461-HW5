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
 * This FP works just like the PhongShadedFP, but also incorporates a texture.
 * 
 * @author ags
 */
public class TexturedPhongFP extends PhongShadedFP {
	// The number of fragment attributes expected from the vertex processor
	public int nAttr() {
		return 8; 	// normal (x,y,z), texture coordinates (u,v), fragment
					// position (x,y,z)
	}

	/**
	 * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
	 */
	public void fragment(Fragment f, FrameBuffer fb) {
		// TODO 2 (DONE)

		Color3f textureColor = new Color3f();
		Vector2f inCoords = new Vector2f(f.attrs[4], f.attrs[5]);
		texture.sample(inCoords, textureColor);

		Color3f outColor = new Color3f(0,0,0);
		Vector3f lV = new Vector3f();
		Vector3f h = new Vector3f();

		Vector3f norm = new Vector3f(f.attrs[1], f.attrs[2], f.attrs[3]);
		norm.normalize();

		Point3f position = new Point3f(f.attrs[6], f.attrs[7], f.attrs[8]);
		Vector3f toEye = new Vector3f(position);
		toEye.scale(-1);
		toEye.normalize();

		for (int i = 0; i < Pipeline.lights.size(); i++) {
			PointLight l = Pipeline.lights.get(i);
	        // specular term uses Pipeline.specularColor and assumes light color is full white
        	
			// Calculate l and h
	        // note that light positions are given in eye space, and the camera is at the origin
			lV.sub(l.getPosition(),position);
			lV.normalize();
			h.add(toEye,lV);
			h.normalize();
			
			// Pull Light Intensity
			Color3f intensity = l.getIntensity();
			// Perform Math Calculations before rather than trying to iterate through the Math library 8129418239 times.
			double a = Math.max(0,lV.dot(norm));
	        // also uses Pipeline.specularExponent
			double b = Math.pow(Math.max(0, norm.dot(h)),Pipeline.specularExponent);
			// Lambertian Component
	        // diffuse term uses vertex color c and light color
			outColor.x += (textureColor.x * intensity.x * a);
			outColor.y += (textureColor.y * intensity.y * a);
			outColor.z += (textureColor.z * intensity.z * a);
			// Phong Component
			outColor.x += (Pipeline.specularColor.x * intensity.x * b);
			outColor.y += (Pipeline.specularColor.y * intensity.y * b);
			outColor.z += (Pipeline.specularColor.z * intensity.z * b);

		}

		outColor.clamp(0, 1);
		
		if (f.attrs[0] < fb.getZ(f.x, f.y))
			fb.set(f.x, f.y, outColor.x, outColor.y, outColor.z, f.attrs[0]);
	}

}
