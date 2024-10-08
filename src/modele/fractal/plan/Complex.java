package modele.fractal.plan;

/**
 * L'objet Complexe est compose de deux entiers decimaux: une partie reelle et imaginaire.
 * On applique plusieurs calculs sur les complexes.
 * La classe est immuable.
 */
public record Complex(double real, double imaginary) {

    public static Complex createComplex(double real, double imaginary) {
        return new Complex(real, imaginary);
    }

    public Complex sum(Complex c) {
        return new Complex(this.real + c.getReal(), this.imaginary + c.getImaginary());
    }

    public Complex multiplication(Complex c) {
        return new Complex(this.real * c.getReal() - this.imaginary * c.getImaginary(), this.real * c.getImaginary() + this.real * c.getImaginary());
    }

    public String toString() {
        return this.real + "_i_" + this.imaginary;
    }

    public double getReal() {return this.real;}
    public double getImaginary() {return this.imaginary;}

}
