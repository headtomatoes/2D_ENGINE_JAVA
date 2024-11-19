package AnhNe.Utility;

public class Time {
    public static float timeStarted = System.nanoTime();
    public static final long SECOND = 1000000000L;
    public static double delta = 0;
    public static long lastFrame = 0;

    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted)*1E-9);
    }
}
