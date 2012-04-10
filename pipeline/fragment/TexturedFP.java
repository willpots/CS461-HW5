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
        return 5;   // surface color (r,g,b), texture coordinates (u,v)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
        // TODO 1

        // similar to ColorZBuffer
        // but also samples texture (use texture.sample(inCoords, outColor))
        // and then multiplies surface color with texture color
        // should also clamp to [0, 1]
    }
}