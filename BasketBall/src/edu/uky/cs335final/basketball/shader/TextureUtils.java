package edu.uky.cs335final.basketball.shader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;

/*
    The utility methods provided by this class are slightly
     modified versions of the classes developed by Kevin Brothalar
    in "OpenGL ES 2 for Android":

    http://pragprog.com/book/kbogla/opengl-es-2-for-android
 */

public class TextureUtils {

    private static final String TAG = TextureUtils.class.getCanonicalName();

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if(textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final Options options = new Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if(bitmap == null) {
            Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

}
