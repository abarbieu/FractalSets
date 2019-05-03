import processing.core.PImage;

public interface Fractal {
    int colorPoint(Point p);
    PImage render(Viewport section, Viewport screen);
    String getStats();
}