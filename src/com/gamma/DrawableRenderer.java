package com.gamma;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView.Renderer;

public class DrawableRenderer implements Renderer
{
	private static final float Z_NEAREST = -100f, Z_FURTHEST = 100f;
	
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
    	
    	// Set blending function, to occur with every enabling of "GL_BLEND"
    	gl.glBlendFunc(GLES10.GL_ONE, GLES10.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
    	viewport.setDimensions(width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GLES10.GL_COLOR_BUFFER_BIT | GLES10.GL_DEPTH_BUFFER_BIT);
		gl.glFlush();

		gl.glLoadIdentity();
    	gl.glOrthof(viewport.getLeft(), viewport.getRight(), viewport.getTop(), viewport.getBottom(), Z_NEAREST, Z_FURTHEST);
		drawable.draw(gl, textureManager, viewport);
	}
}
