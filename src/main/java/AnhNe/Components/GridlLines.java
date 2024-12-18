package AnhNe.Components;

import AnhNe.Firstep.Window;
import AnhNe.Utility.Settings;
import AnhNe.Renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridlLines extends Component{

//    @Override
//    public void update(float dt) {
//        // snap the lines to the grid
//        Vector2f cameraPosition = Window.getScene().camera().position;
//        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();
//
//        int firstX = ((int) (cameraPosition.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
//        int firstY = ((int) (cameraPosition.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;
//
//        int numVerticalLines = (int) (projectionSize.x / Settings.GRID_WIDTH) + Settings.GRID_WIDTH * 2; // 1280 / 32 = 40
//        int numHorizontalLines = (int) (projectionSize.y / Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT * 2; // 672 / 32 = 21
//
//        int height = (int) projectionSize.y;
//        int width = (int) projectionSize.x;
//
//        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
//        Vector3f color = new Vector3f(0.0f, 0.0f, 0.0f);
//        for (int i = 0; i < maxLines; i++){
//            int x = firstX + (Settings.GRID_WIDTH * i);    // 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512, 544, 576, 608, 640, 672, 704, 736, 768, 800, 832, 864, 896, 928, 960, 992, 1024, 1056, 1088, 1120, 1152, 1184, 1216, 1248
//            int y = firstY + (Settings.GRID_HEIGHT * i);   // 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 480, 512, 544, 576, 608, 640
//            if (i < numVerticalLines){
//                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color, 10);
//            }
//            if (i < numHorizontalLines){
//                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color, 10);
//            }
//        }
//    }
    @Override
    public void update(float deltaTime) {
        // Get camera position and projection size
        Vector2f cameraPosition = Window.getScene().camera().position;
        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();

        // Calculate the first position for grid lines
        int firstX = ((int) (cameraPosition.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
        int firstY = ((int) (cameraPosition.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        // Calculate the number of vertical and horizontal lines based on projection size
        int numVerticalLines = (int) (projectionSize.x / Settings.GRID_WIDTH) + 2; // Add buffer
        int numHorizontalLines = (int) (projectionSize.y / Settings.GRID_HEIGHT) + 2; // Add buffer

        // Color of grid lines (constant)
        Vector3f color = new Vector3f(0.0f, 0.0f, 0.0f);

        // Draw vertical lines
        for (int i = 0; i < numVerticalLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i); // x positions for vertical lines
            DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + projectionSize.y), color, 10);
        }

        // Draw horizontal lines
        for (int i = 0; i < numHorizontalLines; i++) {
            int y = firstY + (Settings.GRID_HEIGHT * i); // y positions for horizontal lines
            DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + projectionSize.x, y), color, 10);
        }
    }
}
