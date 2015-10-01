package com.dev.melosz.melodroid.views;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.dev.melosz.melodroid.utils.MyGLRenderer;

/**
 * Created by marek.kozina on 9/8/2015.
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
