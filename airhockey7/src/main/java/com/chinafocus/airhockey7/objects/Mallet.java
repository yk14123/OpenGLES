package com.chinafocus.airhockey7.objects;

import com.chinafocus.airhockey7.data.VertexArray;
import com.chinafocus.airhockey7.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.chinafocus.airhockey7.Constants.BYTES_PER_FLOAT;

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // Order of coordinates: X, Y, R, G, B
            // Mallets
            0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.4f, 1.0f, 0.0f, 0.0f
    };

    private final VertexArray vertexArray;

    public Mallet() {
        this.vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                colorShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw() {
        // 点 count = 1个顶点
        glDrawArrays(GL_POINTS, 0, 2);
    }

}
