package pipeline.vertex;

import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

/**
 * Passes the normals and the colors of each vertex on to be rasterized, and
 * later shaded during the fragment stage of the pipeline. This results in the
 * highest quality images, but results in costly computation.
 * 
 * @author ags
 */
public class FragmentShadedVP extends VertexProcessor {

	/** This is the modelView matrix. */
	protected Matrix4f m = new Matrix4f();

	/** This is the projectionMatrix matrix. */
	protected Matrix4f projection = new Matrix4f();

	/** This is the viewportMatrix matrix. */
	protected Matrix4f viewport = new Matrix4f();

	protected Vector<PointLight> lights = new Vector<PointLight>();

	// The number of fragment attributes provided to the fragment processor

	// The number of fragment attributes provided to the fragment processor
	public int nAttr() {
		return 9; // diffuse color (r,g,b), normal (x,y,z), fragment position
					// (x,y,z)
	}

	/**
	 * @see VertexProcessor#updateTransforms(Pipeline)
	 */
	public void updateTransforms(Pipeline pipe) {
		// TODO 2
		m.set(pipe.modelviewMatrix);
		projection.set(pipe.projectionMatrix);
		viewport.set(pipe.viewportMatrix);
	}

	/**
	 * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f,
	 *      Vertex)
	 */
	public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
		// TODO 2
		Vector4f normal = new Vector4f(n.x, n.y, n.z, 0);
		output.v.set(v.x, v.y, v.z, 1);
		// multiply v by modelview matrix, this gives vertex pos in "eye space"
		m.rightMultiply(output.v);
		// do the same with the normal
		m.rightMultiply(normal);

		// compute color at vertex:
		Color3f ct = new Color3f(c);
		// start with c scaled by Pipeline.ambientIntensity

		output.setAttrs(nAttr());
		output.attrs[0] = ct.x;
		output.attrs[1] = ct.y;
		output.attrs[2] = ct.z;
		
		output.attrs[3] = normal.x;
		output.attrs[4] = normal.y;
		output.attrs[5] = normal.z;

		// also project vertex position to screen space

		
		output.attrs[6] = output.v.x;
		output.attrs[7] = output.v.y;
		output.attrs[8] = output.v.z;
		
		projection.rightMultiply(output.v);
		viewport.rightMultiply(output.v);

	}

}