package Renderer;

import AnhNe.Firstep.Window;
import AnhNe.Utility.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 1000;

    private static List<Lines2D> lines = new ArrayList<>();
    // 6 floats per vertex (3 for position, 3 for color), 2 vertices per line
    private static float[] lineVertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/DebugLines2D.glsl");

    private static int VAO_ID, VBO_ID;

    private static boolean initializeState = false;

    public static void  start(){
        // Generate VAO
        VAO_ID = glGenVertexArrays();
        glBindVertexArray(VAO_ID);

        // Generate VBO
        VBO_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,VBO_ID);
        glBufferData(GL_ARRAY_BUFFER, lineVertexArray.length * Float.BYTES , GL_DYNAMIC_DRAW);

        // enable the vertex attribute
            // 0: x,y,z position
        glVertexAttribPointer(0,3,GL_FLOAT,false,6*Float.BYTES,0);
        glEnableVertexAttribArray(0);
            // 1: r,g,b color
        glVertexAttribPointer(1,3,GL_FLOAT,false,6*Float.BYTES,3*Float.BYTES);
        glEnableVertexAttribArray(1);
            // TODO: set up the line width = thickness
        glLineWidth(2.0f);
    }

    public static void beginFrame(){
        if(!initializeState){
            start();
            initializeState = true;
        }

        // Clear the dead lines
        for (int i = 0; i < lines.size(); i++){
            if (lines.get(i).beginFrame() <= 0){
                lines.remove(i);
                i--;
            }
        }
    }

    public static void drawLine2D(){
        if (lines.size() <= 0){
            return;
        }
        int index = 0;
        for (Lines2D line : lines){
            for (int i = 0; i < 2; i++){
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // load the position
                lineVertexArray[index + 0] = position.x;
                lineVertexArray[index + 1] = position.y;
                lineVertexArray[index + 2] = -10.0f;

                // load the color
                lineVertexArray[index + 3] = color.x;
                lineVertexArray[index + 4] = color.y;
                lineVertexArray[index + 5] = color.z;

                index += 6;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, VBO_ID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(lineVertexArray, 0, lines.size() * 6 * 2));

        // Use the shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // Bind the VAO
        glBindVertexArray(VAO_ID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch ( Brezenham's line algorithm )
        glDrawArrays(GL_LINES, 0, lines.size() * 2);

        // Disable the location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Unbind the shader
        shader.detach();
    }

    // ========================================================
    // Add Lines2D methods
    // ========================================================
    public static void addLine2D(Vector2f from, Vector2f to){
        // TODO: constant color for common color like RED, GREEN, BLUE
        addLine2D(from, to, new Vector3f(0.0f, 1.0f, 0.0f), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to,Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime){
        if (lines.size() >= MAX_LINES){
            return;
        }
        DebugDraw.lines.add(new Lines2D(from, to, color, lifetime));
    }
}
