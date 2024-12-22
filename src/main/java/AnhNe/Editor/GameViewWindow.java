package AnhNe.Editor;

import AnhNe.Firstep.Window;
import AnhNe.Input_Manager.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY;

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;


        int textureID = Window.getFrameBuffer().getTextureID();

        ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport() {
        // Add null and bounds checks
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Prevent potential division by zero
        float targetAspect = Math.max(Window.getTargetAspectRatio(), 0.1f);

        // Add safety checks for minimum window size
        windowSize.x = Math.max(windowSize.x, 10);
        windowSize.y = Math.max(windowSize.y, 10);

//        windowSize.x -= ImGui.getScrollX();
//        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / targetAspect;
        if (aspectHeight > windowSize.y) {
            // Switch to pillar box mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * targetAspect;
        }
        return new ImVec2(
                Math.max(aspectWidth, 10),
                Math.max(aspectHeight, 10)
        );
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                          viewportY + ImGui.getCursorPosY());
    }

//    public boolean getWantCaptureMouse() {
//        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
//               MouseListener.getY() <= topY && MouseListener.getY() >= bottomY;
//    }

    public boolean getWantCaptureMouse() {
        float mouseX = MouseListener.getX();
        float mouseY = MouseListener.getY();

        // More robust bounds checking
        return mouseX >= leftX &&
                mouseX <= rightX &&
                mouseY >= topY &&
                mouseY <= bottomY;
    }
}
