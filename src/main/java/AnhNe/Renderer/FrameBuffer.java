package AnhNe.Renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int fboID = 0;
    private Texture texture = null;


    public FrameBuffer(int width, int height) {
        // Generate the framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // Create the texture to render the data to and attach it to the framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getID(), 0);

        // Create the render buffer object to store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        // Check if the framebuffer is complete
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer is not complete!";
        }

        // Unbind the framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        //glViewport(0, 0, texture.getWidth(), texture.getHeight());
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        //glViewport(0, 0, Window.get().getWidth(), Window.get().getHeight());
    }
    public int getTextureID() {
        return texture.getID();
    }

    public int getFboID() {
        return fboID;
    }


}
