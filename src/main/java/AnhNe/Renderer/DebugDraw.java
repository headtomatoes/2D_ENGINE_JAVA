package AnhNe.Renderer;

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
            if (lines.get(i).beginFrame() < 0){
                lines.remove(i);
                i--;
            }
        }
    }

    public static void drawLine2D(){
        if (lines.isEmpty()){
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
        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

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

    // ========================================================
    // Add BOX_2D methods
    // ========================================================
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation){
        // TODO: constant color for common color like RED, GREEN, BLUE
        addBox2D(center, dimension,rotation, new Vector3f(0.0f, 1.0f, 0.0f), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation,Vector3f color){
        addBox2D(center, dimension, rotation, color, 1);
    }
    public static void addBox2D(Vector2f center, Vector2f dimension, float rotation, Vector3f color, int lifetime) {
        // Calculate the box's bounding corners
        Vector2f halfDimension = new Vector2f(dimension).div(2);  // Half dimensions
        Vector2f min = new Vector2f(center).sub(halfDimension);
        Vector2f max = new Vector2f(center).add(halfDimension);

        // Define the four corners of the box (without rotation yet)
        Vector2f[] vertices = new Vector2f[4];
        vertices[0] = new Vector2f(min.x, min.y); // Bottom-left
        vertices[1] = new Vector2f(min.x, max.y); // Top-left
        vertices[2] = new Vector2f(max.x, max.y); // Top-right
        vertices[3] = new Vector2f(max.x, min.y); // Bottom-right

        // If rotation is needed, rotate each vertex
        if (rotation != 0.0f) {
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = rotate(vertices[i], rotation, center); // Rotate vertices around the center
            }
        }

        // Add the edges of the box (4 lines)
        addLine2D(vertices[0], vertices[1], color, lifetime); // 0 -> 1
        addLine2D(vertices[1], vertices[2], color, lifetime); // 1 -> 2
        addLine2D(vertices[2], vertices[3], color, lifetime); // 2 -> 3
        addLine2D(vertices[3], vertices[0], color, lifetime); // 3 -> 0
    }


    public static Vector2f rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        // Translate the vector back to the origin
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        // Convert the angle from degrees to radians
        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        // Rotate the point
        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        // Translate the point back to its original position
        return new Vector2f(xPrime + origin.x, yPrime + origin.y); // Return a new rotated vector
    }

    // ========================================================
    // Add CIRCLE_2D methods
    // ========================================================
    public static void addCircle2D(Vector2f center, float radius){
        // TODO: constant color for common color like RED, GREEN, BLUE
        addCircle2D(center, radius, new Vector3f(0.0f, 1.0f, 0.0f), 1);
    }

    public static void addCircle2D(Vector2f center, float radius,Vector3f color){
        addCircle2D(center, radius, color, 1);
    }
    public static void addCircle2D(Vector2f center, float radius, Vector3f color, int lifetime){
        int segments = 30; // 360 segments for a full circle
        Vector2f[] points = new Vector2f[segments];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius, 0);
            rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if(i > 0){
                addLine2D(points[i-1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifetime);
    }
}
