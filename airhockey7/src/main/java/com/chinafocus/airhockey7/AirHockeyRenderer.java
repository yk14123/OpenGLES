package com.chinafocus.airhockey7;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.chinafocus.airhockey7.objects.Mallet;
import com.chinafocus.airhockey7.objects.Table;
import com.chinafocus.airhockey7.programs.ColorShaderProgram;
import com.chinafocus.airhockey7.programs.TextureShaderProgram;
import com.chinafocus.airhockey7.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.f, 0.f, 0.f, 0.f);
        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey);
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

        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
