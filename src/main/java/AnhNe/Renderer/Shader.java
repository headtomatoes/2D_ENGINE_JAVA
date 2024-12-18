package AnhNe.Renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID; // ID for the shader program in the GPU, for EX: vertex shader and fragment shader
    private boolean statusUse = false; // Check if the shader is being used or not

    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            // Read the file
            // did not optimize for large files
            String source = new String(Files.readAllBytes(Paths.get(filePath))); // Read all bytes from the file

            // Split the string using Regex to get the vertex and fragment shader
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;

            // Find the end of the line
            int eol = source.indexOf("\r\n", index);

            // Get the type of shader
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            // Get the vertex and fragment shader
            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filePath + "'";
        }
    }

    public void compile() {
        // =================== OPENGL ===================
        // Compile the link shaders
        // ===============================================
        int vertexID, fragmentID;

        // First: load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Second: pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);

        // Third: compile the shader
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "ERROR: Vertex shader compilation failed";  // Stop the program if there is an error
        }

        // First: load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Second: pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);

        // Third: compile the shader
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "ERROR: Fragment shader compilation failed";  // Stop the program if there is an error
        }

        // Link the vertex and fragment shader into a shader program
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\tLinking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "ERROR: Shader linking failed";  // Stop the program if there is an error
        }
    }

    public void use() {
        // use the shader program that we created for rendering
        if(!statusUse) {
            glUseProgram(shaderProgramID);
            statusUse = true;
        }
    }

    public void detach() {
        // Unbind the shader program because we don't want to accidentally modify it for the next frame
        glUseProgram(0);
        statusUse = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        // Upload this matrix to the OpenGL buffer to be used for rendering
        // because the OpenGL only accepts the Buffer to render

        // Get the location of the uniform variable in the shader
        int varLocation = glGetUniformLocation(shaderProgramID, varName);

        // Use the shader program before uploading the matrix to make sure we're using the correct shader not the last one
        use();

        // Create a FloatBuffer to store the matrix
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);  // 4x4 matrix = array 1D 16 floats

        // Store the matrix in the buffer
        mat4.get(matBuffer);

        // Upload the matrix to the shader
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadMat2f(String varName, Matrix2f mat2) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4);
        mat2.get(matBuffer);
        glUniformMatrix2fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3.x, vec3.y, vec3.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec2.x, vec2.y);
    }

    public void uploadFloat(String varName, float value) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, value);
    }

    public void uploadInt(String varName, int value) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, value);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}
