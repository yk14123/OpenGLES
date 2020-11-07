package com.chinafocus.airhockey7.programs;

import android.content.Context;

import com.chinafocus.airhockey7.R;

import static android.opengl.GLES20.*;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform location
    private final int uMatrixLocation;

    // Attribute location
    private final int aPositionLocation;
    private final int aColorLocation;


    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shager);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }

    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
