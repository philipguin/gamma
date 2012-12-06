package com.gamma;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView.Renderer;

public class DrawableRenderer implements Renderer
{	
	private final IDrawable<? super GL10> drawable;
	private final TextureManager textureManager;
	private final Viewport viewport = new Viewport(0, 0, 640, 480);
	
	public DrawableRenderer(IDrawable<? super GL10> drawable, Resources applicationResources)
	{
		this.drawable = drawable;
		this.textureManager = new TextureManager(applicationResources);
	}
	
	public Viewport getViewport() { return viewport; }
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glMatrixMode(GLES10.GL_MODELVIEW);
		
    	// Enable use of texture coord pointers
    	gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
    	gl.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
    	gl.glEnableClientState(GLES10.GL_COLOR_ARRAY);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
    	viewport.setDimensions(width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		drawable.draw(gl, textureManager, viewport);
	}
}
