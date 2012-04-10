package pipeline.vertex;

import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.PointLight;
import pipeline.Pipeline;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

/**
 * This triangle processor smoothly interpolates the color across the face of
 * the triangle.
 * 
 * @author ags
 */
public class SmoothShadedVP extends VertexProcessor {
    
    /** This is the modelView matrix. */
    protected Matrix4f m = new Matrix4f();
    
    /** This is the projectionMatrix matrix. */
    protected Matrix4f p = new Matrix4f();
    
    /** This is the viewportMatrix matrix. */
    protected Matrix4f v = new Matrix4f();
    
    public static Vector<PointLight> lights;
 
    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
        // TODO 1
        m.set(pipe.modelviewMatrix);
        lights = Pipeline.lights;
    }
   
    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // TODO 1
    	Vector4f normal = new Vector4f(n.x,n.y,n.z,0);
        output.v.set(v.x, v.y, v.z, 1);
    	
        // multiply v by modelview matrix, this gives vertex pos in "eye space"
        m.rightMultiply(output.v);

    	// do the same with the normal
        m.rightMultiply(normal);
        
        // compute color at vertex:

        // start with c scaled by Pipeline.ambientIntensity
        c.scale(Pipeline.ambientIntensity);
        
        // for each light, add diffuse and specular term, using normal, light direction, and eye direction
        // note that light positions are given in eye space, and the camera is at the origin
        for(int i=0;i<lights.size();i++) {
        	Vector3f lv = new Vector3f();
        	PointLight l = lights.get(i);
        	lv.sub(l.getPosition(), v);
        	l.getIntensity();
        	l.getPosition();
        }
        // diffuse term uses vertex color c and light color
        
        // specular term uses Pipeline.specularColor and assumes light color is full white
        // also uses Pipeline.specularExponent
        
        // in the end, clamp the resulting color to [0, 1] and store in attributes
        output.setAttrs(nAttr());
        output.attrs[0] = 1; // red for now
        output.attrs[1] = 0;
        output.attrs[2] = 0;

        
        // also project vertex position to screen space
    }

}