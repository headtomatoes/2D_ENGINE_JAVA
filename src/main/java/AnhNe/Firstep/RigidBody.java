package AnhNe.Firstep;

import AnhNe.Components.Component;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RigidBody extends Component {
    private int colliderType;
    private float friction = 0.8f;
    public Vector3f velocity = new Vector3f(0.5f, 0.5f, 0.0f);
    public transient Vector4f tmp = new Vector4f(1, 1, 1, 1);
}
