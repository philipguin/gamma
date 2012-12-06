package com.gamma;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.util.SparseIntArray;

public class TextureManager
{
	private final Resources resources;
	private final Set<Integer> resourceIDs = new HashSet<Integer>();
	private final SparseIntArray loadedTextureIDs = new SparseIntArray();
	private final ByteBuffer tempBuffer = ByteBuffer.allocateDirect(0x1000000).order(ByteOrder.nativeOrder());
	private int[] textureName = new int[1];
	
	private boolean useMipmaps = false, blurTexture = false, clampTexture = true;
	
	public TextureManager(Resources resources)
	{
		this.resources = resources;
	}
	
	public void bindTexture(GL10 gl, int resourceID)
	{
		int id = loadedTextureIDs.get(resourceID, -1);
		
		if (id != -1)
		{
			gl.glBindTexture(GLES10.GL_TEXTURE_2D, id);
			return;
		}
		
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceID);

    	gl.glGenTextures(1, textureName, 0);
    	id = textureName[0];
		gl.glBindTexture(GLES10.GL_TEXTURE_2D, id);
		
        if (useMipmaps)
        {
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST_MIPMAP_LINEAR);
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
        }
        else
        {
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
        }

        if (blurTexture)
        {
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
        }

        if (clampTexture)
        {
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S, GLES10.GL_CLAMP_TO_EDGE);
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T, GLES10.GL_CLAMP_TO_EDGE);
        }
        else
        {
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S, GLES10.GL_REPEAT);
            GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T, GLES10.GL_REPEAT);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixels[] = new int[width * height];
        byte bytes[] = new byte[width * height * 4];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int p = 0; p < pixels.length; ++p)
        {
            int a = pixels[p] >>> 24 & 0xff;
            int r = pixels[p] >>> 16 & 0xff;
            int g = pixels[p] >>> 8 & 0xff;
            int b = pixels[p] & 0xff;

            /*if (options != null && options.anaglyph)
            {
                int i3 = (r * 30 + g * 59 + b * 11) / 100;
                int k3 = (r * 30 + g * 70) / 100;
                int i4 = (r * 30 + b * 70) / 100;
                r = i3;
                g = k3;
                b = i4;
            }*/

            bytes[p * 4 + 0] = (byte)r;
            bytes[p * 4 + 1] = (byte)g;
            bytes[p * 4 + 2] = (byte)b;
            bytes[p * 4 + 3] = (byte)a;
        }

        tempBuffer.clear();
        tempBuffer.put(bytes);
        tempBuffer.position(0).limit(bytes.length);
        GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width, height, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, tempBuffer);

        if (useMipmaps)
        {
            for (int l = 1; l <= 4; l++)
            {
                int j1 = width >> l - 1;
                int l1 = width >> l;
                int j2 = height >> l;

                for (int l2 = 0; l2 < l1; l2++)
                {
                    for (int j3 = 0; j3 < j2; j3++)
                    {
                        int l3 = tempBuffer.getInt((l2 * 2 + 0 + (j3 * 2 + 0) * j1) * 4);
                        int j4 = tempBuffer.getInt((l2 * 2 + 1 + (j3 * 2 + 0) * j1) * 4);
                        int k4 = tempBuffer.getInt((l2 * 2 + 1 + (j3 * 2 + 1) * j1) * 4);
                        int l4 = tempBuffer.getInt((l2 * 2 + 0 + (j3 * 2 + 1) * j1) * 4);
                        int i5 = alphaBlend(alphaBlend(l3, j4), alphaBlend(k4, l4));
                        tempBuffer.putInt((l2 + j3 * l1) * 4, i5);
                    }
                }

                GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, l, GLES10.GL_RGBA, l1, j2, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, tempBuffer);
            }
        }
    	
    	resourceIDs.add(resourceID);
    	loadedTextureIDs.put(resourceID, id);
	}
	
    private int alphaBlend(int x, int y)
    {
        int ax = (x & 0xff000000) >>> 24 & 0xff;
        int ay = (y & 0xff000000) >>> 24 & 0xff;
        char a = '\377';

        if (ax + ay < 255)
        {
            a = '\0';
            ax = 1;
            ay = 1;
        }
        else if (ax > ay)
        {
            ax = 255;
            ay = 1;
        }
        else
        {
            ax = 1;
            ay = 255;
        }

        // Weighted averages
        int rx = ax * (x >> 16 & 0xff);
        int gx = ax * (x >> 8 & 0xff);
        int bx = ax * (x & 0xff);
        int ry = ay * (y >> 16 & 0xff);
        int gy = ay * (y >> 8 & 0xff);
        int by = ay * (y & 0xff);
        int r = (rx + ry) / (ax + ay);
        int g = (gx + gy) / (ax + ay);
        int b = (bx + by) / (ax + ay);
        return a << 24 | r << 16 | g << 8 | b;
    }
}
