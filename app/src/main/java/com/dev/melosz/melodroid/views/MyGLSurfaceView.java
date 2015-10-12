package com.dev.melosz.melodroid.views;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.dev.melosz.melodroid.utils.MyGLRenderer;

/**
 * This is from Android's OpenGL example from developer.android.comS
 * TODO: Delete or expand.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

    }
}
