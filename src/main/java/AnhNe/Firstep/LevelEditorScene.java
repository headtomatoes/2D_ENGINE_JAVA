package AnhNe.Firstep;


import AnhNe.Utility.Time;
import Renderer.Shader;
import Renderer.Texture;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    // Shader variables
    private int vaoID, vboID, eboID; // IDs for the VAO, VBO and EBO
    private Shader defaultShader;
    private Texture testTexture;

    // Vertex Array Object (VAO) and Vertex Buffer Object (VBO)
    private float[] vertexArray = {
        // normalized device coordinates (NDC) for the vertexArray (x, y, z, r, g, b, a)
        // -1.0f to 1.0f is the range of NDC
        // 1.Position                               //2.Color                       //3.Texture coordinates(UV mapping)
         200.7f,    -0.7f,  0.0f,                   1.0f, 0.0f, 0.0f, 1.0f,         1,0,   // bottom right    0
          -0.7f,   200.7f,  0.0f,                   0.0f, 1.0f, 0.0f, 1.0f,         0,1,   // top left        1
         200.7f,   200.7f,  0.0f,                   0.0f, 0.0f, 1.0f, 1.0f,         1,1,   // top right       2
          -0.7f,    -0.7f,  0.0f,                   1.0f, 1.0f, 0.0f, 1.0f,         0,0    // bottom left     3
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
        this.camera = new Camera(new Vector2f(0.0f, 0.0f));
        // Create the shader
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/image/areUGays.png");
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
        int uvSize = 2;         // The size of the uv vector, which is 2 (u, v)
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;  // The size of a vertex in bytes (how many bytes each vertex is)

        // Tell OpenGL how to handle the VBO
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);  // Position attribute
        glEnableVertexAttribArray(0);  // Enable the attribute at position 0 in the .glsl file

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);  // Color attribute
        glEnableVertexAttribArray(1);  // Enable the attribute at position 1 in the .glsl file

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);  // UV attribute
        glEnableVertexAttribArray(2);  // Enable the attribute at position 2 in the .glsl file
    }
    @Override
    public void update(float deltaTime) {
        // Update the camera
        camera.position.x -= deltaTime * 20.0f;
        camera.position.y -= deltaTime * 10.0f;
        // Update the scene after deltaTime
        defaultShader.use();

        // Upload the Texture to the Shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0); // upload TO OpenGL the texture to slot 0
        glActiveTexture(GL_TEXTURE0);   // Set the active state to texture unit 0
        testTexture.bind();            // Bind the texture to texture unit 0

        // Upload the projection matrix to the GPU for the shader to use
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());

        // Upload the view matrix to the GPU for the shader to use
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        defaultShader.uploadFloat("uTime", Time.getTime());
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
        defaultShader.detach(); // Unbind the shader program when we're done so we don't accidentally modify it
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
