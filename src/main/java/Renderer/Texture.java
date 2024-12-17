package Renderer;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;         // no change after initialization
    private int textureID; // change after each initialization
    private int width, height;

    public Texture() { // default constructor = error
        textureID = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height) { // set up a blank texture for the framebuffer
        this.filePath = "Generated";

        // Generate texture on GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set the texture min mag options (on the currently bound texture object)
        // Set texture min mag to GL_LINEAR (interpolation) when stretching and shrinking the image
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
    }

    public void init(String filePath){
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

        // Flip the image vertically
        stbi_set_flip_vertically_on_load(true);
        // stbi library function to load the image
        ByteBuffer image = stbi_load(filePath, width, height, nrChannels, 0);

        // Check if the image is loaded
        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);
            if(nrChannels.get(0) == 3) {
                // RGB image
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if(nrChannels.get(0) == 4) {
                // RGBA image
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels '" + nrChannels.get(0) + "'";
            }
        }else {
            assert false : "Error: (Texture) Failed to load image '" + filePath + "'";
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

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getID() {
        return this.textureID;
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(!(obj instanceof Texture)) {
            return false;
        }
        Texture objTex = (Texture) obj;
        return objTex.getWidth() == this.getWidth() && objTex.getHeight() == this.getHeight() && objTex.getID() == this.getID() && objTex.getFilePath().equals(this.getFilePath());
    }


    public void cleanup() {
        glDeleteTextures(textureID);
    }
}
