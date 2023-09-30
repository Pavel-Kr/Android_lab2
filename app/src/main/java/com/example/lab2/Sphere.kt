package com.example.lab2

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class Sphere(cX: Float, cY: Float, cZ: Float, radius: Float) {
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

    private val vertices: MutableList<Float> = mutableListOf()
    private val indices: MutableList<Short> = mutableListOf()

    private val stackCount: Int = 26
    private val sectorCount: Int = 26

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private val shaderHandler = ShaderHandler()
    private val program: Int = shaderHandler.loadShaders(vertexShaderSrc, fragmentShaderSrc)

    private val vertexStride = 3 * Float.SIZE_BYTES
    private val color = floatArrayOf(0.5607843f, 0.843137f, 0.858823f, 1.0f)

    init {
        val stackStep = Math.PI / stackCount
        val sectorStep = 2 * Math.PI / sectorCount
        for(i in 0 .. stackCount){
            val stackAngle = Math.PI / 2 - i * stackStep
            val xz = radius * cos(stackAngle)
            val y = radius * sin(stackAngle)
            for(j in 0 .. sectorCount){
                val sectorAngle = j * sectorStep
                val x = xz * sin(sectorAngle)
                val z = xz * cos(sectorAngle)
                vertices.add((x + cX).toFloat())
                vertices.add((y + cY).toFloat())
                vertices.add((z + cZ).toFloat())
            }
        }

        for(i in 0 until stackCount){
            var k1 = i * (sectorCount + 1)
            var k2 = k1 + sectorCount + 1
            for(j in 0 until sectorCount){
                if(i != 0){
                    indices.add(k1.toShort())
                    indices.add(k2.toShort())
                    indices.add((k1 + 1).toShort())
                }
                if((i + 1) != stackCount){
                    indices.add((k1 + 1).toShort())
                    indices.add(k2.toShort())
                    indices.add((k2 + 1).toShort())
                }
                k1++
                k2++
            }
        }

        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices.toFloatArray())
                    position(0)
                }
            }

        indexBuffer =
        ByteBuffer.allocateDirect(indices.size * Short.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices.toShortArray())
                position(0)
            }
        }

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