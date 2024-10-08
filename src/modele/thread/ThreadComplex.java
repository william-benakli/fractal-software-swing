package modele.thread;

import java.awt.*;
import modele.fractal.Fractal;
import modele.fractal.plan.Complex;

/**
 * ThreadComplex est une class immuable qui implemente Runnable.
 * La fonction run est redefini et represente le calcul a effectue pour generer une fractale.
 */
public record ThreadComplex(int departX, int finX, int pixelX, Fractal fractal) implements Runnable {

    @Override
    public void run() {
        final double deplacement = fractal.getDeplacement();
        final double y1 = (fractal.getPlanComplex().getPosA().getY()) / deplacement;
        final double y2 = (fractal.getPlanComplex().getPosB().getY()) / deplacement;

        int pixelY = 0, pixelX = this.pixelX;
        for (double i = departX; i < finX && pixelX < fractal.getImage().getWidth(); i += 1) {
            for (double j = y1; j < y2 && pixelY < fractal.getImage().getHeight(); j += 1) {
                final Complex complex = Complex.createComplex((i * deplacement), (j * deplacement));
                final int indiceDivergent = fractal.divergenceIndex(complex);
                final float hue = Math.abs(((float) indiceDivergent / fractal.getIteMax()) - ((float) fractal.getHue() / 360));
                final float saturation = 1f;
                final float brightness = 0.95f;
                fractal.getImage().setRGB(pixelX, pixelY, Color.getHSBColor(hue, saturation, brightness).getRGB());
                pixelY++;
            }
            pixelY = 0;
            pixelX++;
        }
    }
}