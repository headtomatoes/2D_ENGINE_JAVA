package AnhNe.physics2d.forces;

import AnhNe.physics2d.rigidbody.Rigidbody2D;

public interface ForceGenerator {
    void updateForce(Rigidbody2D body, float dt);
}
