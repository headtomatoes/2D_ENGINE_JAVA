package AnhNe.Firstep;


import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
    }

    // Return the type of the component that we want to get from the list of components of the GameObject
    public <Type extends Component> Type getComponent(Class<Type> componentClass) {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                try {
                    return componentClass.cast(component);
                }catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component '" + component.getClass() + "' to '" + componentClass + "'";
                }
            }
        }
        return null;
    }

    // return true if the component is added successfully or false if the component is already existed or the component is null
    public <Type extends Component> void removeComponent(Class<Type> componentClass) {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                components.remove(component);
                return;
            }
        }
    }

    // Add a component to the list of components of the GameObject
    public void addComponent(Component component) {
        components.add(component);
        // just like in Unity, upon adding an object or component
        // we assign the reference to which the component belongs to (in this case is the GameObject)
        // so that the component can access the GameObject whenever it needs
        component.gameObject = this;
    }

    public void update(float deltaTime) {
        for (Component component : components) {
            component.update(deltaTime);
        }
    }

    public void start() {
        for (Component component : components) {
            component.start();
        }
    }
}
