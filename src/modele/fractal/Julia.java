package modele.fractal;

import modele.fractal.plan.Complex;
import java.awt.image.BufferedImage;
import modele.fractal.plan.PlanComplex;

/**
 * Julia est une class final (qui ne peut pas etre etendue) et qui contient un Builder pour faciliter sa construction.
 */
public final class Julia extends Fractal {

    public static class Builder {

        private int hue;
        private int threadUse;
        private int iteMax = 999;
        private Complex typeComplex;
        private PlanComplex planComplex;
        private double deplacement = 0.00250;
        private BufferedImage imageRepresentation;

        public Builder image(BufferedImage imageRepresentation){
            this.imageRepresentation = imageRepresentation;
            return this;
        }

        public Builder planComplex(PlanComplex complex){
            this.planComplex = complex;
            return this;
        }

        public Builder typeComplex(Complex typeComplex){
            this.typeComplex = typeComplex;
            return this;
        }

        public Builder deplacement(double deplacement){
            this.deplacement = deplacement;
            return this;
        }

        public Builder iteMax(int iteMax){
            this.iteMax = iteMax;
            return this;
        }

        public Builder nbThread(int threadUse){
            this.threadUse = threadUse;
            return this;
        }

        public Builder hue(int hue){
            this.hue = hue;
            return this;
        }

        public Julia build() {
            return new Julia(this);
        }

    }

    private Julia(Builder builder) {
        super(builder.imageRepresentation, builder.planComplex, builder.typeComplex, builder.deplacement, builder.iteMax, builder.threadUse, builder.hue);
    }

    @Override
    public int divergenceIndex(Complex zn){
        int ite;
        for (ite=0; ite < getIteMax() && (Math.sqrt(Math.pow(zn.getImaginary(), 2) + Math.pow(zn.getReal(), 2)) <= 2); ite++){
            zn = zn.multiplication(zn).sum(typeComplex);
        }
        return ite;
    }

}