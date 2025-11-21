package net.ccbluex.liquidbounce.utils.shader

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.io.InputStreamReader

class Shader(fragmentShader: String) : MinecraftInstance() {
    private val program = GL20.glCreateProgram()

    init {
        val vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        val fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)

        GL20.glShaderSource(vertexShaderID, readShader("vertex.vsh"))
        GL20.glCompileShader(vertexShaderID)

        GL20.glShaderSource(fragmentShaderID, readShader(fragmentShader))
        GL20.glCompileShader(fragmentShaderID)

        GL20.glAttachShader(program, vertexShaderID)
        GL20.glAttachShader(program, fragmentShaderID)
        GL20.glLinkProgram(program)
    }

    fun use() {
        GL20.glUseProgram(program)
    }

    fun unuse() {
        GL20.glUseProgram(0)
    }

    fun getUniform(name: String): Int {
        return GL20.glGetUniformLocation(program, name)
    }

    private fun readShader(name: String): String {
        return InputStreamReader(mc.resourceManager.getResource(net.minecraft.util.ResourceLocation("mint/shader/$name")).inputStream).readText()
    }
}