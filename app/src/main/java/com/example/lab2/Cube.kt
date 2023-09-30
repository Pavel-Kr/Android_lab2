package com.example.lab2

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube(x: Float, y: Float, z: Float,
           width: Float, height: Float, depth: Float) {
    private val vertexShaderSrc =
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderSrc =
                "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val vertices: FloatArray = floatArrayOf(
        x, y, z,
        x + width, y, z,
        x + width, y + height, z,
        x, y + height, z,
        x, y, z + depth,
        x + width, y, z + depth,
        x + width, y + height, z + depth,
        x, y + height, z + depth
    )
    private val vertexStride = 3 * Float.SIZE_BYTES
    private val indices: ShortArray = shortArrayOf(
        0, 2, 3,
        0, 1, 2,
        1, 6, 2,
        1, 5, 6,
        0, 5, 1,
        0, 4, 5,
        0, 3, 7,
        0, 7, 4,
        3, 2, 6,
        3, 6, 7,
        4, 7, 6,
        4, 6, 5,
    )

    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private val shaderHandler = ShaderHandler()
    private val program: Int = shaderHandler.loadShaders(vertexShaderSrc, fragmentShaderSrc)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(indices.size * Short.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices)
                position(0)
            }
        }

    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun draw(vpMatrix: FloatArray){
        GLES20.glUseProgram(program)
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES20.glEnableVertexAttribArray(0)

        GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
            GLES20.glUniform4fv(colorHandle, 1, color, 0)
        }

        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

        GLES20.glGetUniformLocation(program, "uMVPMatrix").also {
            GLES20.glUniformMatrix4fv(it, 1, false, mvpMatrix, 0)
        }

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    fun translate(x: Float, y: Float, z: Float){
        Matrix.translateM(modelMatrix, 0, x, y, z)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float){
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z)
    }

    fun scale(x: Float, y: Float, z: Float){
        Matrix.scaleM(modelMatrix, 0, x, y, z)
    }

    fun setRotate(angle: Float, x: Float, y: Float, z: Float){
        Matrix.setRotateM(modelMatrix, 0, angle, x, y, z)
    }
}