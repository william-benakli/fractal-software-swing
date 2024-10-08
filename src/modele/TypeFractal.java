package modele;

import modele.fractal.plan.Complex;

public enum TypeFractal {

    ENSEMBLE_JULIA_C1(new Complex(-0.8, 0.156)),
    ENSEMBLE_JULIA_C2(new Complex(0.285, 0.01)),
    ENSEMBLE_JULIA_C3(new Complex(-1.417022285618 , 0.0099534)),
    ENSEMBLE_JULIA_C4(new Complex(-0.038088 , 0.9754633 )),
    ENSEMBLE_JULIA_C5(new Complex(0.285 , 0.013 )),
    ENSEMBLE_JULIA_C6(new Complex(-1.476 , 0)),
    ENSEMBLE_JULIA_C7(new Complex(-0.4, 0.6)),
    ENSEMBLE_JULIA_C8(new Complex(0.3, 0.5));

    private final Complex type;

    TypeFractal(Complex c) {this.type = c;}

    public Complex getType() {return this.type;}

}
