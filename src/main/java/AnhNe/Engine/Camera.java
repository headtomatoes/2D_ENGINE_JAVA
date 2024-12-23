package AnhNe.Engine;

import org.joml.Matrix4f;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    //x, y, z, w
    private Matrix4f projectionMatrix, viewMatrix , inverseProjection, inverseView;
    public Vector2f position;
    private Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
    private float zoom = 1.0f;

    // Constructor creates a camera with a given position and initializes the projection and view matrix
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    // Adjust the projection matrix in the world space
    public void adjustProjection() {
        // careful with the order of multiplication because after the first multiplication, the viewMatrix is changed and the second multiplication will be based on the changed viewMatrix
        // so the order of multiplication is important
        // SCALE -> ROTATE -> TRANSLATE

        // multiply with the identity matrix to create a copy of the projection matrix
        projectionMatrix.identity();

        // ortho(left, right, bottom, top, near, far)
        // left, right: the coordinates of the left and right vertical clipping planes
        // bottom, top: the coordinates of the bottom and top horizontal clipping planes
        // near, far: the distances to the near and far depth clipping planes
        // the near and far values are the distances from the camera to the near and far clipping planes
        // the limits of the world space are 32.0f * 40.0f and 32.0f * 21.0f in the x and y axis = 1280.0f and 672.0f
        projectionMatrix.ortho(0.0f, projectionSize.x * this.zoom , 0.0f, projectionSize.y * this.zoom, 0.0f, 100.0f);

        // invert the projection matrix to get the inverse projection matrix
        // get the inverse projection matrix to transform the screen space to the world space
        projectionMatrix.invert(inverseProjection);
    }

    // Adjust the view matrix in the world space
    public Matrix4f getViewMatrix() {
        // cameraFront is the direction the camera is looking at in the world space
        // default cameraFront is (x == 0.0f, y == 0.0f, z == -1.0f) which is the forward direction
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f); // == forward direction

        // cameraUp is the up direction of the camera because the camera can rotate around the x and y axis
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);  // == up direction

        // multiply with the identity matrix to create a copy of the view matrix
        this.viewMatrix.identity();

        // lookAt(V3D.f eye, V3D.f center, V3D.f up)
        // eye: the position of the camera
        // center: the position the camera is looking at
        // up: the up direction of the camera
        // set the world space position into the view matrix AKA the camera position
        this.viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                                cameraFront.add(position.x, position.y, 0.0f),
                                cameraUp);

        // invert the view matrix to get the inverse view matrix
        // get the inverse view matrix to transform the world space to the camera space
        this.viewMatrix.invert(inverseView);

        return this.viewMatrix;
    }

    // Get the projection matrix
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    // Get the inverse projection matrix
    public Matrix4f getInverseProjectionMatrix() {
        return this.inverseProjection;
    }

    // Get the inverse view matrix
    public Matrix4f getInverseViewMatrix() {
        return this.inverseView;
    }

    // Get the projection size
    public Vector2f getProjectionSize() {
        return this.projectionSize;
    }

    public float getZoom() {
        return this.zoom;
    }

    public void setZoom(float value) {
        this.zoom = value;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }
}
