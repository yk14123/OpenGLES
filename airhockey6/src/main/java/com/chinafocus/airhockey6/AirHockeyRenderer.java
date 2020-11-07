package com.chinafocus.airhockey6;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.chinafocus.airhockey6.util.LoggerConfig;
import com.chinafocus.airhockey6.util.ShaderHelper;
import com.chinafocus.airhockey6.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    // STRIDE 跨距以 字节 为单位
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float[] modelMatrix = new float[16];

    public AirHockeyRenderer(Context context) {
        this.context = context;

        float[] tableVerticesWithTriangles = {
                // Triangle Fan
                // 依次取2个点 和 第一个点 组合成三角形！
                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                -0.5f, -0.8f, 0.0f, 1.0f, 1.0f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.0f, 1.0f, 0.7f, 1.0f, 0.7f,
                0.5f, 0.8f, 0.0f, 1.0f, 0.7f, 0.7f, 1.0f,
                -0.5f, 0.8f, 0.0f, 1.0f, 0.3f, 0.3f, 0.0f,
                -0.5f, -0.8f, 0.0f, 1.0f, 1.0f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                // Mallets
                0.0f, -0.4f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.4f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f

//                // 边框 Line left
//                -0.51f, 0.51f,
//                -0.51f, -0.51f,
//
//                // 边框 Line 下
//                -0.51f, -0.51f,
//                0.51f, -0.51f,
//
//                // 边框 Line 右
//                0.51f, -0.51f,
//                0.51f, 0.51f,
//
//                // 边框 Line 上
//                0.51f, 0.51f,
//                -0.51f, 0.51f,
//
//                // 冰球
//                0.0f, 0.0f

        };

        // 分配本地内存
        vertexData = ByteBuffer
                // 1个float = 4个字节 = 32位
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shager);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

//        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        // STRIDE 因为 颜色属性 和 位置属性 都在一个float数组里面，所以顶点需要跳跃一段距离
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        Matrix.setIdentityM(modelMatrix, 0);
        // 让顶点的z值，从0，平移到透视矩阵里面
        Matrix.translateM(modelMatrix, 0, 0, 0, -2.0f);
        // x方向 旋转 -60° 右手定理
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        // 透视矩阵的区域为 -1 到 -10
        Matrix.perspectiveM(projectionMatrix, 0, 90, (float) width / (float) height, 1.0f, 10.0f);
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 三角形 count = 3个顶点 2个三角形 一共 6个顶点
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // 线 count = 2个顶点
        glDrawArrays(GL_LINES, 6, 2);

        // 点 count = 1个顶点
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);

//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//        // 线 count = 2个顶点 4个线 一共 2*4 = 8个顶点
//        glDrawArrays(GL_LINES, 10, 8);
//
//        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
//        glDrawArrays(GL_POINTS, 18, 1);

    }
}
