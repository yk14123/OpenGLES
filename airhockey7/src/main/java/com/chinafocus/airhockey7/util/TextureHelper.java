package com.chinafocus.airhockey7.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.*;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {

        final int[] textureObjectIds = new int[1];
        // 创建一个纹理空间
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resoure ID " + resourceId + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        // 把刚刚创建好的2D纹理空间，告诉openGL 目前纹理空间还没有内容
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        // 设置缩小过滤器
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // 设置放大过滤器
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 加载纹理到openGL中
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        // 可以释放java中的bitmap
        bitmap.recycle();
        // 生成mipmap图
        glGenerateMipmap(GL_TEXTURE_2D);
        // 既然已经完成了纹理的加载，我们就解除绑定，防止用其他纹理方法意外改变这个纹理
        // 感觉 GL_TEXTURE_2D 像一个C中的指针一样，指向一个区域，给区域赋值，赋值完后，把指针移到空区域
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

}
