package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This FP does a texture lookup rather to determine the color of a fragment. It
 * also uses the z-buffering technique to draw the correct fragment.
 * 
 * @author ags
 */
public class TexturedFP extends FragmentProcessor {

	// The number of fragment attributes expected from the vertex processor
	public int nAttr() {
		return 5; // surface color (r,g,b), texture coordinates (u,v)
	}

	/**
	 * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
	 */
	public void fragment(Fragment f, FrameBuffer fb) {
		// TODO 1 (DONE)
		if (f.attrs[0] < fb.getZ(f.x, f.y)) {
			Color3f outColor = new Color3f();
			Vector2f inCoords = new Vector2f(f.attrs[4], f.attrs[5]);

			texture.sample(inCoords, outColor);

			outColor.clamp(0, 1);

			fb.set(f.x, f.y, f.attrs[1] * outColor.x, f.attrs[2] * outColor.y,
					f.attrs[3] * outColor.z, f.attrs[0]);
		}
	}
}