/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package edu.uky.cs335final.basketball.shader;

import static android.opengl.GLES20.glUseProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import edu.uky.cs335final.basketball.shader.*;
import android.content.Context;
import android.content.res.Resources;



abstract class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_TIME = "u_Time";       

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
        int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(       
        		
        		
        		
        	
            
            
                readTextFileFromResource(context, vertexShaderResourceId),
                readTextFileFromResource(context, fragmentShaderResourceId));
    }        

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

    public static String readTextFileFromResource(Context context,
            int resourceId) {
            StringBuilder body = new StringBuilder();

            try {
                InputStream inputStream = context.getResources()
                    .openRawResource(resourceId);
                InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
                BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

                String nextLine;

                while ((nextLine = bufferedReader.readLine()) != null) {
                    body.append(nextLine);
                    body.append('\n');
                }
            } catch (IOException e) {
                throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
            } catch (Resources.NotFoundException nfe) {
                throw new RuntimeException("Resource not found: " 
                    + resourceId, nfe);
            }

            return body.toString();
        }

}

