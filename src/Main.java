import controleur.Controleur;

public class Main {

    public static void main(String[] args) {
        Controleur c = null;
        try {
            c = new Controleur();
            c.getParser().checkArgs(c, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
