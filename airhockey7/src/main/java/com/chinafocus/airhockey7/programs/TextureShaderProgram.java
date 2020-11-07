package com.chinafocus.airhockey7.programs;

import android.content.Context;

import com.chinafocus.airhockey7.R;

import static android.opengl.GLES20.*;

public class TextureShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    /**
     * 当OpenGl使用纹理绘制的时候，不需要直接给着色器传递纹理。相反，我们使用纹理单元 texture unit 保存纹理
     * 之所以这样做，因为一个GPU只能同时绘制数量有限的纹理
     * 使用这些纹理单元表示来当前正在被绘制的活动的纹理
     *
     * @param matrix
     * @param textureId
     */
    public void setUniforms(float[] matrix, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        // 设置texture unit to texture unit 0
        // 一共32个纹理单元
        // 设置 活动 的纹理单元 为 纹理单元0 unit0
        glActiveTexture(GL_TEXTURE0);
        // 把纹理 绑定 到 纹理单元0 unit0
        glBindTexture(GL_TEXTURE_2D, textureId);
        // 把被选定的纹理单元，传递给片段着色器中的u_TextureUnit.
        // 告诉sampler 用 单元0 unit0
        // uniform sampler2D u_TextureUnit;
        // 纹理片段着色器中的 sampler2D 实际上 就是纹理单元！
        // 上面是 GL_TEXTURE0 这里x 就传0
        // 上面是 GL_TEXTURE1 这里x 就传1
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
