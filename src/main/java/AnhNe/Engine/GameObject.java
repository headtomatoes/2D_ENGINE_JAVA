package AnhNe.Engine;


import AnhNe.Components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int GLOBAL_ID_COUNTER = 0; // static variable to keep track of the number of components in the game
    public int uID = -1; // unique id for each component

    private String name;
    private List<Component> components;
    public Transform transform;
    private int zIndex;
    private boolean doSerialization = true;

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;

        this.uID = GLOBAL_ID_COUNTER++; // Potential bug: if we remove a component, the uID will not be unique anymore
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
        component.generateUID();
        this.components.add(component);
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

    public int getZIndex() {
        return this.zIndex;
    }

    public void imgui() {
        for (Component component : components) {
            component.imgui();
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public int getUID() {
        return this.uID;
    }

    public static void init(int maxID){
        GLOBAL_ID_COUNTER = maxID;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return this.doSerialization;
    }
}
