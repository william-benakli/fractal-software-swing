package modele.thread;

import java.awt.*;
import modele.fractal.Fractal;
import modele.fractal.plan.Complex;
import java.util.concurrent.RecursiveAction;

/**
 * ThreadForkComplex est une class immuable qui implemente RecursiveAction.
 * La fonction run est redefini et represente le calcul a effectue pour generer une fractale.
 */
public class ThreadForkComplex extends RecursiveAction {

    private final Fractal fractal;
    private final double from, to, depart;

    public ThreadForkComplex(Fractal fractal, double from, double to, double depart) {
        this.from = from;
        this.to = to;
        this.fractal = fractal;
        this.depart = depart;
    }

    @Override
    protected void compute() {
        int dist=fractal.getImage().getWidth()/fractal.getThreadUse();
        if (Math.abs(from - to) <= dist) {
            computeDirectly();
            return;
        }
        double middle = ((from + to) / 2.0);
        invokeAll(
                new ThreadForkComplex(fractal, from, middle, depart),
                new ThreadForkComplex(fractal, middle, to, depart)
        );
    }

    //On parcours en colonne pas en Ligne
    public void computeDirectly() {
        final double deplacement = fractal.getDeplacement();
        final double y1 = (fractal.getPlanComplex().getPosA().getY()) / deplacement;
        final double y2 = (fractal.getPlanComplex().getPosB().getY()) / deplacement;

        int pixelY = 0, pixelX = (int)Math.abs(depart-this.from);
        for (double i =this.from; i < this.to && pixelX < fractal.getImage().getWidth(); i += 1) {
            for (double j = y1; j < y2 && pixelY < fractal.getImage().getHeight(); j += 1) {
                final Complex complex = Complex.createComplex((i*deplacement), (j*deplacement));
                final int indiceDivergent =  fractal.divergenceIndex(complex);
                final float hue = Math.abs(((float) indiceDivergent / fractal.getIteMax()) - ((float) fractal.getHue() / 360));
                final float saturation = 1f;
                final float brightness = 0.95f;
                fractal.getImage().setRGB(pixelX, pixelY,  Color.getHSBColor(hue, saturation, brightness).getRGB());
                pixelY++;
            }
            pixelY = 0;
            pixelX++;
        }
    }
}