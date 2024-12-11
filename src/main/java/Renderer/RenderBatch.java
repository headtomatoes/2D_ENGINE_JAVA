package Renderer;

import AnhNe.Components.SpriteRenderer;
import AnhNe.Firstep.Window;
import AnhNe.Utility.AssetPool;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex of 2D game => 2D vertex
    //=========
    // position                             color                                texture coordinates(UV mapping)            texture ID
    // x, y                                 r, g, b, a
    // float, float                         float, float, float, float           float, float                               float
    // TODO: cache for projection matrix and view matrix
    // TODO: reduce the number use of hasRoom and hasTextureRoom
    // TODO: more efficient way to look for the texture ID in the list of textures(HashMap <Texture, Integer> rather than List<Texture>)

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    // for add more data, we can add more offset like COLOR_OFFSET + COLOR_SIZE * Float.BYTES

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE; // 2 + 4 + 2 + 1 = 9
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] spritesArray;
    private int numSprites;
    private boolean hasRoom;
    private float[] verticesArray;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures; // list of textures currently is being used in the batch
    private int VAO_ID, VBO_ID;
    private int maxBatchSize;
    private Shader shader;

    //blending variables
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.spritesArray = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices per sprite == 1 quad
        this.verticesArray = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start() {
        //Generate and bind the Vertex Array Object (VAO)

        VAO_ID = glGenVertexArrays(); // Create a VAO and get its ID
        glBindVertexArray(VAO_ID); // Make the VAO the current Vertex Array Object by binding it

        //Allocate a space in GPU memory for the vertices, and create a Vertex Buffer Object (VBO) to store the vertices

        VBO_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_ID); // Make the VBO the current Array Buffer Object by binding it
        glBufferData(GL_ARRAY_BUFFER, verticesArray.length * Float.BYTES, GL_DYNAMIC_DRAW); // Upload the VBO to the GPU with our vertices in it

        //Create and upload the indices buffer
        //Reduce the vertex duplicate by using auto indexing

        int EBO_ID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Enable the buffer attributes pointers
            //Enable the position attribute
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

            //Enable the color attribute
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

            //Enable the texture coordinates attribute
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

            //Enable the texture ID attribute
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer sprite) {
        //Get the index and add renderObject to the spritesArray

        //we want to add the sprite to the end of the current array
        // if the sprite.length is 5 => 0, 1, 2, 3, 4
        //, we want to add the sprite to the 5th index

        int index = this.numSprites;
        this.spritesArray[index] = sprite;
        this.numSprites++;

        //Add the texture to the list of textures
        if(sprite.getTexture() != null) {                   // Check if this sprite has a texture
            if(!textures.contains(sprite.getTexture())) {   // Check if this texture is already in the list
                textures.add(sprite.getTexture());          // Add the texture to the list if it is not
            }
        }
        //Add properties to local vertices array
        loadVertexProperties(index);

        //Check if there is still room
        if (this.numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;

        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer sprite = spritesArray[i];
            if(sprite.isDirty()) {
                loadVertexProperties(i);
                sprite.setClean();
                rebufferData = true;
            }
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, VBO_ID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, verticesArray);
        }

        glBindBuffer(GL_ARRAY_BUFFER, VBO_ID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verticesArray);

        //Use the shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

            //Bind the textures before we draw to be able to use them in the shader
        for(int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);       // Activate the texture slot i
            textures.get(i).bind();                 // Bind the texture to the slot i respectively to the texture slot i
        }

        shader.uploadIntArray("uTextures", texSlots); // Upload all the texture slots to the shader

        //Bind the VAO
        glBindVertexArray(VAO_ID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Draw the batch
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        //Unbind and disable and detach everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (Texture texture : textures) {
            texture.unbind();
        }

        shader.detach();
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.spritesArray[index];

        // Find the offset within the array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTextureCoordinates();

        int texID = 0;       // Default texture ID is 0 for non-textured objects EX: color
        // [0 , texture1, texture2, texture3, ...]
        // loop through the array of textures to find the index of the texture
        if(sprite.getTexture() != null) {                       // Check if this sprite has a texture
            for (int i = 0; i < textures.size(); i++) {         // Find the index of the texture in the list
                if(textures.get(i).equals(sprite.getTexture())) {
                    texID = i + 1;
                    break;
                }
            }
        }
        // Add the vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if(i == 1) {
                yAdd = 0.0f;
            } else if(i == 2) {
                xAdd = 0.0f;
            } else if(i == 3) {
                yAdd = 1.0f;
            }
            //load position
            verticesArray[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            verticesArray[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //load color
            verticesArray[offset + 2] = color.x;
            verticesArray[offset + 3] = color.y;
            verticesArray[offset + 4] = color.z;
            verticesArray[offset + 5] = color.w;

            //load texture coordinates
            verticesArray[offset + 6] = texCoords[i].x;
            verticesArray[offset + 7] = texCoords[i].y;
            //load texture ID
            verticesArray[offset + 8] = texID;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per 1 triangle)
        int [] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Quad 1                                 Quad 2
        // 3, 2, 0, 0, 2, 1                       7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex + 0] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textures.contains(texture);
    }

    public int getZIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch object) {
        // Compare the zIndex of the current object with the object passed in
        return Integer.compare(this.zIndex, object.zIndex);
        // if the current object is greater than the object passed in, return 1
        // if the current object is less than the object passed in, return -1
        // if the current object is equal to the object passed in, return 0
    }
}
