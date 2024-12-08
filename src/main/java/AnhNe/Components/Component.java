package AnhNe.Components;

import AnhNe.Firstep.GameObject;
import imgui.ImGui;

import org.joml.Vector3f;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private static int GLOBAL_ID_COUNTER = 0; // static variable to keep track of the number of components in the game
    public int uID = -1; // unique id for each component
    public transient GameObject gameObject = null;

    public void update(float deltaTime){

    }

    public void start() {

    }

    public void imgui() {
        try {
            // Java Reflection
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field :fields){
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient){
                    continue;
                }
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate){
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": " , imInt)) {
                        field.set(this, imInt[0]);
                    }
                }else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = {val};
                    if (ImGui.dragFloat(name + ": " , imFloat)) {
                        field.set(this, imFloat[0]);
                    }
                }else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": " , imVec)) {
                        val.x = imVec[0];
                        val.y = imVec[1];
                        val.z = imVec[2];
                        val.set(val.x, val.y, val.z);
                    }
                }
                if (isPrivate){
                    field.setAccessible(false);
                }
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public void generateUID(){
        if (this.uID == -1){
            this.uID = GLOBAL_ID_COUNTER++;
        }
    }

    public int getUID(){
        return this.uID;
    }

    public static void init (int maxID){
        GLOBAL_ID_COUNTER = maxID;
    }
}
