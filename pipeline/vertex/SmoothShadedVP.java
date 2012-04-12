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
    protected Matrix4f projection = new Matrix4f();
    
    /** This is the viewportMatrix matrix. */
    protected Matrix4f viewport = new Matrix4f();
    
    protected Vector<PointLight> lights = new Vector<PointLight>();
    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
        // TODO 1 (DONE)
        m.set(pipe.modelviewMatrix);
        projection.set(pipe.projectionMatrix);
        viewport.set(pipe.viewportMatrix);
    }
   
    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // TODO 1 (DONE)
    	Vector4f normal = new Vector4f(n.x,n.y,n.z,0);
        output.v.set(v.x, v.y, v.z, 1);
        // multiply v by modelview matrix, this gives vertex pos in "eye space"
        m.rightMultiply(output.v);
    	// do the same with the normal
        m.rightMultiply(normal);
        
        // compute color at vertex:
        Color3f ct = new Color3f(c);
        // start with c scaled by Pipeline.ambientIntensity
        ct.scale(Pipeline.ambientIntensity);

        Point3f p = new Point3f(output.v.x,output.v.y,output.v.z);
    	Vector3f norm = new Vector3f(normal.x,normal.y,normal.z);
    	// Vector from Vertex Position to Eye
    	Vector3f toEye = new Vector3f(p);
    	toEye.scale(-1);
    	
    	Vector3f lV = new Vector3f();
    	Vector3f h = new Vector3f();

        // for each light, add diffuse and specular term, using normal, light direction, and eye direction
        for(int i=0;i<Pipeline.lights.size();i++) {
        	PointLight l = Pipeline.lights.get(i);
	
	        // specular term uses Pipeline.specularColor and assumes light color is full white
        	
			// Calculate l and h
	        // note that light positions are given in eye space, and the camera is at the origin
			lV.sub(l.getPosition(),p);
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
			ct.x += (c.x * intensity.x * a);
			ct.y += (c.y * intensity.y * a);
			ct.z += (c.z * intensity.z * a);
			// Phong Component
			ct.x += (Pipeline.specularColor.x * intensity.x * b);
			ct.y += (Pipeline.specularColor.y * intensity.y * b);
			ct.z += (Pipeline.specularColor.z * intensity.z * b);
			
        }
        // in the end, clamp the resulting color to [0, 1] and store in attributes
    	ct.clamp(0,1);
        output.setAttrs(nAttr());
        output.attrs[0] = ct.x;
        output.attrs[1] = ct.y;
        output.attrs[2] = ct.z;
        
        // also project vertex position to screen space
        projection.rightMultiply(output.v);
        viewport.rightMultiply(output.v);
        
    }

}