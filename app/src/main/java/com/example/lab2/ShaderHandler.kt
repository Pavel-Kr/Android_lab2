package com.example.lab2

import android.opengl.GLES20

class ShaderHandler {
    private var vertexShader: Int = 0
    private var fragmentShader: Int = 0
    private var shaderProgram: Int = 0

    fun loadShaders(vertexSrc: String, fragmentSrc: String): Int {
        println("Vertex shader")
        vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSrc)
        println("Fragment shader")
        fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSrc)
        shaderProgram = GLES20.glCreateProgram()
        //println(GLES20.glGetProgramInfoLog(shaderProgram))
        getErrors()
        GLES20.glAttachShader(shaderProgram, vertexShader)
        //println(GLES20.glGetProgramInfoLog(shaderProgram))
        getErrors()
        GLES20.glAttachShader(shaderProgram, fragmentShader)
        //println(GLES20.glGetProgramInfoLog(shaderProgram))
        getErrors()
        GLES20.glLinkProgram(shaderProgram)
        println(GLES20.glGetProgramInfoLog(shaderProgram))
        getErrors()
        //println(GLES20.glGetProgramInfoLog(shaderProgram))
        //GLES20.glDeleteShader(shaderProgram)
        return shaderProgram
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        getErrors()
        GLES20.glShaderSource(shader, source)
        getErrors()
        GLES20.glCompileShader(shader)
        println(GLES20.glGetShaderInfoLog(shader))
        getErrors()
        return shader
    }
}