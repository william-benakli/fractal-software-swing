package modele.fractal.plan;

/**
 * PlanComplex est compose de deux objets PointDouble.
 */
public record PlanComplex(PointDouble posA, PointDouble posB) {

    public static PlanComplex createPlanComplex(double x1, double y1, double x2, double y2){
        return new PlanComplex(PointDouble.createPoint(x1, y1), PointDouble.createPoint(x2, y2));
    }

    public static PlanComplex createPlanComplex(PointDouble x1, PointDouble x2){
        return new PlanComplex(x1, x2);
    }

    public PointDouble getPosA() {return PointDouble.createPoint(posA.getX(), posA.getY());}
    public PointDouble getPosB() {return PointDouble.createPoint(posB.getX(), posB.getY());}

    @Override
    public String toString(){
        return "["+posA.getX()+","+posA.getY()+"]" + "["+posB.getX()+","+posB.getY()+"]";
    }

}
