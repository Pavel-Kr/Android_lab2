package com.example.lab2

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

fun getErrors(){
    var error = GLES20.glGetError()
    while (error != GLES20.GL_NO_ERROR){
        when(error){
            GLES20.GL_INVALID_ENUM -> println("ERROR: Invalid enum")
            GLES20.GL_INVALID_VALUE -> println("ERROR: Invalid value")
            GLES20.GL_INVALID_OPERATION -> println("ERROR: Invalid operation")
            GLES20.GL_INVALID_FRAMEBUFFER_OPERATION -> println("ERROR: Invalid framebuffer operation")
            GLES20.GL_OUT_OF_MEMORY -> println("ERROR: Out of memory")
        }
    }
}

class Square(x: Float, y: Float, width: Float, height: Float) {
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

    val coords = floatArrayOf(
        x, y,
        x + width, y,
        x + width, y + height,
        x, y + height
    )

    private val coordsStride = 2 * Float.SIZE_BYTES

    private val color = floatArrayOf(
        0.7f, 0.2f, 0.3f, 1.0f
    )

    private val shaderHandler = ShaderHandler()
    private val program: Int = shaderHandler.loadShaders(vertexShaderSrc, fragmentShaderSrc)
    private var positionHandle: Int = 0

    private var drawOrder = shortArrayOf(0, 1, 2, 2, 3, 0)

    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(coords.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(coords)
                position(0)
            }
        }

    private val indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * Short.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun draw(vpMatrix: FloatArray){
        GLES20.glUseProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glVertexAttribPointer(
            positionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            coordsStride,
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

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

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
