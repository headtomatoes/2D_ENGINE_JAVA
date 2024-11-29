package Renderer;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {
    private String filePath;
    private int textureID;


    public Texture(String filePath) {
        this.filePath = filePath;

        // Generate texture on GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set the texture wrapping options (on the currently bound texture object)
        // Set texture wrapping to GL_REPEAT (repeat the image) in both directions on the model surface
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Set texture filtering parameters
        // Set texture filtering to GL_NEAREST (PIXELATE interpolation) when magnifying and minifying the image

        // when stretching the image, it will be pixelated
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // when shrinking the image, it will be pixelated
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Load the image
        IntBuffer width = BufferUtils.createIntBuffer(1);       // width of the image
        IntBuffer height = BufferUtils.createIntBuffer(1);      // height of the image
        IntBuffer nrChannels = BufferUtils.createIntBuffer(1);  // number of color channels in the image (RGB) or (RGBA) 3 or 4

        // stbi library function to load the image
        ByteBuffer image = stbi_load(filePath, width, height, nrChannels, 0);

        // Check if the image is loaded
        if (image != null) {
            // Generate the texture
            // level 0 is the base image level and level n is the nth mipmap reduction image
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            assert false : "Error: Texture not loaded image: " + filePath + "!";
        }

        // Free the image memory because we have already loaded it to the GPU let's free the memory slot for the later use
        stbi_image_free(image);
    }

    public void bind() {
        // Bind the texture to the current active texture unit
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        // Unbind the texture from the current active texture unit to avoid any modification
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
