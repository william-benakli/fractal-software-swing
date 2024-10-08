package modele.fractal.plan;

/**
 * PointDouble permet d'avoir une class immuable et ne pas avoir a gerer la defense de fonction.
 */
public record PointDouble(double x, double y) {

    public static PointDouble createPoint(double x, double y) {
        return new PointDouble(x, y);
    }

    public double getX() {return x;}
    public double getY() {return y;}

}
