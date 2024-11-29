package AnhNe.Firstep;

public abstract class Scene {
    protected Camera camera;

    public Scene() {

    }

    public abstract void update(float deltaTime);

    public abstract void render();

    public abstract void imgui();

    public abstract void imguiUpdate();

    public abstract void cleanUp();

    public void init() {
    }
}
