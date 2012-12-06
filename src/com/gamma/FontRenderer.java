package com.gamma;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;
import android.util.Log;

public class FontRenderer
{
    private int charWidth[];
    public int fontTextureName;

    /** the height in pixels of default text */
    public int FONT_HEIGHT;
    public Random fontRandom;
    private int colorCode[];

    /** The currently bound GL texture ID. Avoids unnecessary glBindTexture() for the same texture if it's already bound. */
    private int boundTextureName;

    private float posX;
    private float posY;
    
	private final ByteBuffer quadTexCoordBufferDirect = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder());
	private final ByteBuffer quadVertexBufferDirect = ByteBuffer.allocateDirect(12 * 4).order(ByteOrder.nativeOrder());
	private final ByteBuffer quadColorBufferDirect = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder());
	private final FloatBuffer quadTexCoordBuffer = quadTexCoordBufferDirect.asFloatBuffer();
	private final FloatBuffer quadVertexBuffer = quadVertexBufferDirect.asFloatBuffer();
	private final FloatBuffer quadColorBuffer = quadColorBufferDirect.asFloatBuffer();
    private final String allowedCharacters;
    
    public FontRenderer(GL10 gl, TextureManager tm, int resourceID, int fontWidth, int fontHeight, String allowedCharacters)
    {
        charWidth = new int[256];
        fontTextureName = resourceID;
        FONT_HEIGHT = 8;
        fontRandom = new Random();
        colorCode = new int[32];
        this.allowedCharacters = allowedCharacters;

    	quadVertexBuffer.put(2, 0f);
    	quadVertexBuffer.put(5, 0f);
    	quadVertexBuffer.put(8, 0f);
    	quadVertexBuffer.put(11, 0f);
    	
        tm.bindTexture(gl, resourceID);
        int[] fontImage = new int[fontWidth * fontHeight];
        gl.glReadPixels(0, 0, fontWidth, fontHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, IntBuffer.wrap(fontImage));
        
        for (int charIndex = 0; charIndex < 256; charIndex++)
        {
            int x = charIndex % 16;
            int y = charIndex / 16;
            int vLine = 7;

            characterLoop:
            while (vLine >= 0)
            {
                int curX = x * 8 + vLine;

                for (int hLine = 0; hLine < 8; ++hLine)
                {
                    int curY = (y * 8 + hLine) * fontWidth;
                    Log.i("BOOH", Long.toString(fontImage[curX + curY] & 0xffffffffL, 16));
                    int alpha = (fontImage[curX + curY] >>> 24) & 0xff;

                    if (alpha > 0)
                    {
                    	++vLine;
                    	break characterLoop;
                    }
                }
                --vLine;
            }

            if (charIndex == 32)
                vLine = 2;

            charWidth[charIndex] = vLine;
        }

        for (int l = 0; l < 32; l++)
        {
            int j1 = (l >> 3 & 1) * 85;
            int l1 = (l >> 2 & 1) * 170 + j1;
            int j2 = (l >> 1 & 1) * 170 + j1;
            int l2 = (l >> 0 & 1) * 170 + j1;

            if (l == 6)
                l1 += 85;

            if (l >= 16)
            {
                l1 /= 4;
                j2 /= 4;
                l2 /= 4;
            }

            colorCode[l] = (l1 & 0xff) << 16 | (j2 & 0xff) << 8 | l2 & 0xff;
        }
    }

    /** Input stream must be in UTF-8! */
    public static String getAllowedCharacters(InputStream inputStream)
    {
        String result = "";

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null)
                if (!line.startsWith("#"))
                	result += line;

            reader.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

        return result;
    }
    
    public void setupStringRendering(GL10 gl)
    {
    	gl.glPushMatrix();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		gl.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, quadTexCoordBufferDirect);
		gl.glVertexPointer(3, GLES10.GL_FLOAT, 0, quadVertexBufferDirect);
		gl.glColorPointer(4, GLES10.GL_FLOAT, 0, quadColorBufferDirect);
    }
    
    public void endStringRendering(GL10 gl)
    {
    	gl.glPopMatrix();
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location.
     */
    private void renderDefaultChar(GL10 gl, TextureManager tm, int par1)
    {
        float f  = (par1 % 16) * 8;
        float f1 = (par1 / 16) * 8;

        if (boundTextureName != fontTextureName)
        {
            tm.bindTexture(gl, fontTextureName);
            boundTextureName = fontTextureName;
        }

        float renderWidth = (float)charWidth[par1] - 0.01F;
        
        float x = f  / 128F;
        float y = f1 / 128F;
        float xEnd = (f + renderWidth) / 128F;
        float yEnd = (f1 + 7.99F) / 128F;

        quadTexCoordBuffer.put(0, x);
		quadTexCoordBuffer.put(1, yEnd);
		quadTexCoordBuffer.put(2, xEnd);
		quadTexCoordBuffer.put(3, yEnd);
		quadTexCoordBuffer.put(4, x);
		quadTexCoordBuffer.put(5, y);
		quadTexCoordBuffer.put(6, xEnd);
		quadTexCoordBuffer.put(7, y);

    	quadVertexBuffer.put(0, posX);
    	quadVertexBuffer.put(1, posY);
    	quadVertexBuffer.put(3, posX + renderWidth);
    	quadVertexBuffer.put(4, posY);
    	quadVertexBuffer.put(6, posX);
    	quadVertexBuffer.put(7, posY + 7.99F);
    	quadVertexBuffer.put(9, posX + renderWidth);
    	quadVertexBuffer.put(10, posY + 7.99F);
    	
    	gl.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4);
        posX += charWidth[par1];
    }

    /**
     * Draws the specified string with a drop shadow.
     */
    public void drawStringWithShadow(GL10 gl, TextureManager tm, String str, int x, int y, int color)
    {
        renderString(gl, tm, str, x + 1, y + 1, color, true);
        renderString(gl, tm, str, x, y, color, false);
    }

    /**
     * Draws the specified string.
     */
    public void drawString(GL10 gl, TextureManager tm, String str, int x, int y, int color)
    {
        renderString(gl, tm, str, x, y, color, false);
    }

    /** Render a single line string at the current (posX,posY) and update posX */
    private void renderStringAtPos(GL10 gl, TextureManager tm, String str, boolean par2)
    {
        boolean flag = false;

        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);

            if (c == '\247' && i + 1 < str.length())
            {
                int j = "0123456789abcdefk".indexOf(str.toLowerCase(Locale.US).charAt(i + 1));

                if (j == 16)
                {
                    flag = true;
                }
                else
                {
                    flag = false;

                    if (j < 0 || j > 15)
                        j = 15;

                    if (par2)
                        j += 16;

                    int l = colorCode[j];
                    
                    float red = (float)(l >> 16 & 0xff) / 255F;
                    float green = (float)(l >> 8 & 0xff) / 255F;
                    float blue = (float)(l & 0xff) / 255F;
                    float alpha = 1f;

        			quadColorBuffer.put(0,  red);
        			quadColorBuffer.put(1,  green);
        			quadColorBuffer.put(2,  blue);
        			quadColorBuffer.put(3,  alpha);
        			quadColorBuffer.put(4,  red);
        			quadColorBuffer.put(5,  green);
        			quadColorBuffer.put(6,  blue);
        			quadColorBuffer.put(7,  alpha);
        			quadColorBuffer.put(8,  red);
        			quadColorBuffer.put(9,  green);
        			quadColorBuffer.put(10, blue);
        			quadColorBuffer.put(11, alpha);
        			quadColorBuffer.put(12, red);
        			quadColorBuffer.put(13, green);
        			quadColorBuffer.put(14, blue);
        			quadColorBuffer.put(15, alpha);
                }

                i++;
                continue;
            }

            int k = allowedCharacters.indexOf(c);

            if (flag && k > 0)
            {
                int i1;

                do i1 = fontRandom.nextInt(allowedCharacters.length());
                while (charWidth[k + 32] != charWidth[i1 + 32]);

                k = i1;
            }

            if (c == ' ')
            {
                posX += 4F;
                continue;
            }

            if (k > 0)
                renderDefaultChar(gl, tm, k + 32);
        }
    }

    /**
     * The actual rendering takes place here.
     */
    private void renderString(GL10 gl, TextureManager tm, String str, int x, int y, int color, boolean par5)
    {
        if (str != null)
        {
            boundTextureName = 0;

            if ((color & 0xfc000000) == 0)
                color |= 0xff000000;

            if (par5)
                color = (color & 0xfcfcfc) >> 2 | color & 0xff000000;

            float red = (float)(color >> 16 & 0xff) / 255F;
            float green = (float)(color >> 8 & 0xff) / 255F;
            float blue = (float)(color & 0xff) / 255F;
            float alpha = (float)(color >> 24 & 0xff) / 255F;

			quadColorBuffer.put(0,  red);
			quadColorBuffer.put(1,  green);
			quadColorBuffer.put(2,  blue);
			quadColorBuffer.put(3,  alpha);
			quadColorBuffer.put(4,  red);
			quadColorBuffer.put(5,  green);
			quadColorBuffer.put(6,  blue);
			quadColorBuffer.put(7,  alpha);
			quadColorBuffer.put(8,  red);
			quadColorBuffer.put(9,  green);
			quadColorBuffer.put(10, blue);
			quadColorBuffer.put(11, alpha);
			quadColorBuffer.put(12, red);
			quadColorBuffer.put(13, green);
			quadColorBuffer.put(14, blue);
			quadColorBuffer.put(15, alpha);
    			
            posX = x;
            posY = y;
            renderStringAtPos(gl, tm, str, par5);
        }
    }

    /** Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s). */
    public int getStringWidth(String par1Str)
    {
        if (par1Str == null)
            return 0;

        int i = 0;

        for (int j = 0; j < par1Str.length(); j++)
        {
            char c = par1Str.charAt(j);

            if (c == '\247')
            {
                j++;
                continue;
            }

            int k = allowedCharacters.indexOf(c);

            if (k >= 0)
            {
                i += charWidth[k + 32];
                continue;
            }
        }

        return i;
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(GL10 gl, TextureManager tm, String par1Str, int par2, int par3, int par4, int par5)
    {
        renderSplitStringNoShadow(gl, tm, par1Str, par2, par3, par4, par5);
    }

    /**
     * renders a multi-line string with wordwrap (maximum length is parameter k) by means of renderSplitString
     */
    private void renderSplitStringNoShadow(GL10 gl, TextureManager tm, String par1Str, int par2, int par3, int par4, int par5)
    {
        renderSplitString(gl, tm, par1Str, par2, par3, par4, par5, false);
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k) and with darker drop shadow color if flag
     * is set
     */
    public void drawSplitString(GL10 gl, TextureManager tm, String par1Str, int par2, int par3, int par4, int par5, boolean par6)
    {
        renderSplitString(gl, tm, par1Str, par2, par3, par4, par5, par6);
    }

    /**
     * Perform actual work of rendering a multi-line string with wordwrap (maximum length is parameter k) and with
     * darkre drop shadow color if flag is set
     */
    private void renderSplitString(GL10 gl, TextureManager tm, String par1Str, int par2, int par3, int par4, int par5, boolean par6)
    {
        String as[] = par1Str.split("\n");

        if (as.length > 1)
        {
            for (int i = 0; i < as.length; i++)
            {
                renderSplitStringNoShadow(gl, tm, as[i], par2, par3, par4, par5);
                par3 += splitStringWidth(as[i], par4);
            }

            return;
        }

        String as1[] = par1Str.split(" ");
        int j = 0;
        String s = "";

        while (j < as1.length)
        {
            String s1;

            for (s1 = s + as1[j++] + " "; j < as1.length && getStringWidth(s1 + as1[j]) < par4; s1 = s1 + as1[j++] + " ") { }

            int k;

            for (; getStringWidth(s1) > par4; s1 = (new StringBuilder()).append(s).append(s1.substring(k)).toString())
            {
                for (k = 0; getStringWidth(s1.substring(0, k + 1)) <= par4; k++) { }

                if (s1.substring(0, k).trim().length() <= 0)
                    continue;

                String s2 = s1.substring(0, k);

                if (s2.lastIndexOf("\247") >= 0)
                    s = "\247" + s2.charAt(s2.lastIndexOf("\247") + 1);

                renderString(gl, tm, s2, par2, par3, par5, par6);
                par3 += FONT_HEIGHT;
            }

            if (getStringWidth(s1.trim()) > 0)
            {
                if (s1.lastIndexOf("\247") >= 0)
                    s = (new StringBuilder()).append("\247").append(s1.charAt(s1.lastIndexOf("\247") + 1)).toString();

                renderString(gl, tm, s1, par2, par3, par5, par6);
                par3 += FONT_HEIGHT;
            }
        }
    }

    /**
     * Returns the width of the wordwrapped String (maximum length is parameter k)
     */
    public int splitStringWidth(String par1Str, int par2)
    {
        String as[] = par1Str.split("\n");

        if (as.length > 1)
        {
            int i = 0;

            for (int j = 0; j < as.length; j++)
                i += splitStringWidth(as[j], par2);

            return i;
        }

        String as1[] = par1Str.split(" ");
        int k = 0;
        int l = 0;

        while (k < as1.length)
        {
            String s;

            for (s = as1[k++] + " "; k < as1.length && getStringWidth(s + as1[k]) < par2; s = s + as1[k++] + " ") { }

            int i1;

            for (; getStringWidth(s) > par2; s = s.substring(i1))
            {
                for (i1 = 0; getStringWidth(s.substring(0, i1 + 1)) <= par2; i1++) { }

                if (s.substring(0, i1).trim().length() > 0)
                    l += FONT_HEIGHT;
            }

            if (s.trim().length() > 0)
                l += FONT_HEIGHT;
        }

        if (l < FONT_HEIGHT)
            l += FONT_HEIGHT;

        return l;
    }
}
