package modele.fractal;

import modele.fractal.plan.Complex;
import java.awt.image.BufferedImage;
import modele.fractal.plan.PlanComplex;

/**
 * Fractal est une class abstract qui est scelle et etendu que par deux ensembles: Julia et Mandelbrot.
 */
public abstract sealed class Fractal permits Julia, Mandelbrot {

    protected final int hue;
    protected final int iteMax;
    protected final int threadUse;
    protected final double deplacement;
    protected final Complex typeComplex;
    protected final PlanComplex planComplex;
    protected final BufferedImage imageRepresentation;
    protected final long tempsMs = System.currentTimeMillis();

    public Fractal(BufferedImage image, PlanComplex planComplex, Complex typeComplex, double deplacement, int iteMax, int threadUse, int hue) {
        this.planComplex = planComplex;
        this.typeComplex = typeComplex;
        this.deplacement = deplacement;
        this.iteMax = iteMax;
        this.imageRepresentation = image;
        this.hue = hue;
        this.threadUse = threadUse;
    }

    /**
     * Permets de calculer la divergence d'un complexe.
     * @param zn un complexe de l'ensemble de Julia ou de Mandelbrot
     * @return un entier indiquant l'indice de divergence ou bien le nombre max d'iterations choisies par l'utilisateur
     */
    abstract public int divergenceIndex(Complex zn);

    public int getHue(){return this.hue;}
    public int getIteMax(){return this.iteMax;}
    public long getTempsMs(){return this.tempsMs;}
    public int getThreadUse(){return this.threadUse;}
    public Complex getType(){return this.typeComplex;}
    public double getDeplacement(){return this.deplacement;}
    public BufferedImage getImage(){return this.imageRepresentation;}
    public PlanComplex getPlanComplex(){return PlanComplex.createPlanComplex(planComplex.getPosA(), planComplex.getPosB());}
}
