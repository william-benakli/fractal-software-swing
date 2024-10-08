package modele.thread;

import java.awt.*;
import modele.fractal.Fractal;
import modele.fractal.plan.Complex;

/**
 * Cette class est un thread qui execute tous les calculs tout seul
 */
public class ThreadLinearComplex extends Thread{

    private final Fractal fractal;

    public ThreadLinearComplex(Fractal fractal){
        this.fractal =fractal;
    }

    @Override
    public void run() {
        final double y1 = (fractal.getPlanComplex().getPosB().getY()*2)/fractal.getDeplacement();
        final double deplacement = fractal.getDeplacement();

        for (double i = 0; i < fractal.getImage().getWidth(); i += 1) {
            for (double j = 0; j < fractal.getImage().getHeight(); j += +1) {
                final Complex complex = Complex.createComplex(((i-(y1/2))*deplacement), ((j-(y1/2))*deplacement));
                final int indiceDivergent =  fractal.divergenceIndex(complex);
                final float hue = Math.abs(((float) indiceDivergent / fractal.getIteMax()) - ((float) fractal.getHue() / 360));
                final float saturation = 1f;
                final float brightness = 0.95f;
                fractal.getImage().setRGB((int) (i), (int) (j),  Color.getHSBColor(hue, saturation, brightness).getRGB());
            }
        }
    }
}
