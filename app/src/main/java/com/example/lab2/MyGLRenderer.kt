package com.example.lab2

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private lateinit var mSquare: Square
    private lateinit var mCube: Cube
    private lateinit var mSphere: Sphere

    private val vpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        mSquare = Square(-0.5f, -0.5f, 1f, 1f)
        mCube = Cube(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f)
        mSphere = Sphere(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()
        mSphere.setRotate(angle, 0f, 1f, 0f)
        mCube.setRotate(angle, 0f, 1f, 0f)
        mCube.translate(0f, 2f, 0f)
        mSquare.draw(vpMatrix)
        mCube.draw(vpMatrix)
        mSphere.translate(0f, -2f, 0f)
        mSphere.draw(vpMatrix)
    }
}