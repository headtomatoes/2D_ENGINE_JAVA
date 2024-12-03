package AnhNe.Components;

import AnhNe.Firstep.Component;

public class FontRenderer extends Component {


    @Override
    public void start() {
        // load the font
        if(gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("FontRenderer started");
        }
    }

    @Override
    public void update(float deltaTime) {
        // render the font
    }
}
