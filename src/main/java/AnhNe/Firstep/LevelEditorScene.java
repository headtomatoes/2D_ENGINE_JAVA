package AnhNe.Firstep;


import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private String vertexShaderSource = "#version 460 core\n" +
            "\n" +
            "   layout(location = 0) in vec3 aPos;\n" +
            "   layout(location = 1) in vec4 aCol;\n" +
            "\n" +
            "   out vec4 fCol;\n" +
            "\n" +
            "   void main()\n" +
            "   {\n" +
            "      fCol = aCol;\n" +
            "      gl_Position = vec4(aPos, 1.0);\n" +
            "   }";
    private String fragmentShaderSource = "#version 460 core\n" +
            "\n" +
            "   in vec4 fCol;\n" +
            "\n" +
            "   out vec4 col;\n" +
            "\n" +
            "    void main()\n" +
            "    {\n" +
            "        col = fCol;\n" +
            "    }";

    // Shader variables
    private int vertexID, fragmentID, shaderProgram; // IDs for the shaders and the linking program
    private int vaoID, vboID, eboID; // IDs for the VAO, VBO and EBO

    // Vertex Array Object (VAO) and Vertex Buffer Object (VBO)
    private float[] vertexArray = {
        // normalized device coordinates (NDC) for the vertexArray (x, y, z, r, g, b, a)
        // -1.0f to 1.0f is the range of NDC
        // 1.Position                               //2.Color
         0.7f,  -0.7f,  0.0f,                       1.0f, 0.0f, 0.0f, 1.0f,      // bottom right    0
        -0.7f,   0.7f,  0.0f,                       0.0f, 1.0f, 0.0f, 1.0f,      // top left        1
         0.7f,   0.7f,  0.0f,                       0.0f, 0.0f, 1.0f, 1.0f,      // top right       2
        -0.7f,  -0.7f,  0.0f,                       1.0f, 1.0f, 0.0f, 1.0f,      // bottom left     3
    };

    // IMPORTANT: Must be in counter-clockwise order to let OpenGL know what the front face is.
    private int[] elementArray = {
        /*
               *(1)  <==    *(2)
               V    -       ^
               V       -    ^
               *(3)  ==>    *(0)
         */
        2, 1, 0, // top right triangle
        0, 1, 3  // bottom left triangle
    };


    public LevelEditorScene() {

    }

    @Override
    public void init() {
        // =================== OPENGL ===================
        // Compile the link shaders
        // ===============================================

        // First: load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Second: pass the shader source to the GPU
        glShaderSource(vertexID, vertexShaderSource);

        // Third: compile the shader
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";  // Stop the program if there is an error
        }

        // First: load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Second: pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentShaderSource);

        // Third: compile the shader
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";  // Stop the program if there is an error
        }

        // Link the vertex and fragment shader into a shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // Check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";  // Stop the program if there is an error
        }

        // ================================================================================
        // Generate VAO, VBO and EBO buffer objects, and send the vertex data to the GPU
        // ================================================================================

        vaoID = glGenVertexArrays();   // Create a VAO and get the ID
        glBindVertexArray(vaoID);     // Make the VAO the current Vertex Array Object by binding it

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);  // Create a FloatBuffer with the length of the vertexArray
        vertexBuffer.put(vertexArray).flip();  // Put the vertexArray into a FloatBuffer, and flip it to prepare it to be read from later on in the program

        // Create the VBO upload the vertex buffer
        vboID = glGenBuffers();  // Create a VBO and get the ID
        glBindBuffer(GL_ARRAY_BUFFER, vboID);  // Make the VBO the current Array Buffer object by binding it
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);  // Upload the VBO to the GPU with our vertices in it

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Create and add the vertex attribute pointers for the VAO (how the VAO should read the VBO)
        int positionsSize = 3;  // The size of the position vector, which is 3 (x, y, z)
        int colorSize = 4;      // The size of the color vector, which is 4 (r, g, b, a)
        int floatSizeBytes = 4; // A float is 4 bytes
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;  // The size of a vertex in bytes (how many bytes each vertex is)

        // Tell OpenGL how to handle the VBO
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);  // Position attribute
        glEnableVertexAttribArray(0);  // Enable the attribute at position 0 in the .glsl file

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);  // Color attribute
        glEnableVertexAttribArray(1);  // Enable the attribute at position 1 in the .glsl file
    }
    @Override
    public void update(float deltaTime) {
        // Bind the shader program
        glUseProgram(shaderProgram);
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the vertices
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Disable the vertex attribute pointers
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Unbind the VAO
        glBindVertexArray(0); // Unbind the VAO when we're done so we don't accidentally draw extra stuff or tamper with its bound buffers
        // Unbind the shader program
        glUseProgram(0); // Unbind the shader program when we're done so we don't accidentally modify it
    }
    @Override
    public void render() {
    }
    @Override
    public void imgui() {
    }
    @Override
    public void imguiUpdate() {
    }
    @Override
    public void cleanUp() {
    }
}
