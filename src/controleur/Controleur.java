package controleur;

import vue.Vue;
import modele.*;
import java.io.*;
import java.util.ArrayList;
import modele.fractal.Julia;
import javax.imageio.ImageIO;
import modele.fractal.Fractal;
import modele.thread.ThreadType;
import modele.fractal.Mandelbrot;
import modele.thread.ThreadComplex;
import modele.fractal.plan.Complex;
import java.awt.image.BufferedImage;
import modele.fractal.plan.PlanComplex;
import modele.fractal.plan.PointDouble;
import modele.thread.ThreadForkComplex;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;
import modele.thread.ThreadLinearComplex;

/**
 * Cette classe permet d'appliquer les actions de la vue sont executees sur le modele.
 */
public class Controleur {

    private Fractal fractal;
    public Vue vueGraphique;
    private final Parser parser;
    private BufferedImage image;

    public Controleur() {
        vueGraphique = new Vue(getControler());
        this.parser = new Parser();
        final PlanComplex c = new PlanComplex(PointDouble.createPoint(-1,-1), PointDouble.createPoint(1,1));
        this.image = new BufferedImage((int) ((c.getPosB().getX() - c.getPosA().getX()) / 0.00250), (int) ((c.getPosB().getY() - c.getPosA().getY()) / 0.00250), BufferedImage.TYPE_INT_RGB);
        this.fractal = new Julia.Builder().image(image).deplacement(0.00250).typeComplex(TypeFractal.ENSEMBLE_JULIA_C1.getType()).planComplex(c)
                .nbThread(getCoeurFromFractal())
                .hue(196)
                .iteMax(999)
                .build();
    }

    /**
     * Cette fonction execute la commande associee au type de thread demande.
     * @param type est un des types d'utilisations de thread
     */
    public void threadRun(ThreadType type){
        switch (type) {
            case ThreadBasic -> genererFractalByThreadComplex();
            case ForkJoinPool -> genererFractalByForkJoinPool();
            default -> genererFractalByLineareThread();
        }

    }

    /**
     * Cette fonction renvoie le temps ecoule entre le temps de la fractale et le temps actuel.
     * @return long correspondant au temps de generation
     */
    public long getCurrentTime(){
        return System.currentTimeMillis() - fractal.getTempsMs();
    }

    /**
     * Cette fonction redefinie l'objet fractal avec ses nouveaux parametres.
     * @param type est le complexe qui forme l'ensemble de julia
     * @param planComplex est le plan complexe
     * @param deplacement est le pas de separation entre chaque point du plan
     * @param iteMax nombre d'iterations max pour chaque point du plan
     * @param color couleur dans le modele HSV
     * @param nbThread nombre de threads pour chaque methode
     * */
    public void updateFractal(Complex type, PlanComplex planComplex, double deplacement, int iteMax, int nbThread, int color) {
        final double x1 = planComplex.getPosA().getX();
        final double x2 = planComplex.getPosB().getX();
        final double y1 = planComplex.getPosA().getY();
        final double y2 = planComplex.getPosB().getY();

        try{
            this.image =  new BufferedImage((int) ((x2 - x1) / deplacement), (int) ((y2 - y1) / deplacement), BufferedImage.TYPE_INT_RGB);
        }catch (IllegalArgumentException e){
            System.err.println("Erreur : vos parametres sont trop grands.");
            return;
        }
        this.fractal = new Julia.Builder()
                .image(image)
                .deplacement(deplacement)
                .typeComplex(type)
                .planComplex(new PlanComplex(PointDouble.createPoint(x1, y1), PointDouble.createPoint(x2, y2)))
                .iteMax(iteMax)
                .nbThread(nbThread)
                .hue(color)
                .build();
    }

    /**
     * Cette fonction redefinit l'objet fractal avec ses nouveaux parametres.
     * @param planComplex est le plan complexe
     * @param deplacement est le pas de separation entre chaque point du plan
     * @param iteMax nombre d'iterations max pour chaque point du plan
     * @param color couleur dans le modele HSV
     * @param nbThread nombre de threads pour chaque methode
     * */
    public void updateFractal(PlanComplex planComplex, double deplacement, int iteMax, int nbThread, int color) {
        final double x1 = planComplex.getPosA().getX();
        final double x2 = planComplex.getPosB().getX();
        final double y1 = planComplex.getPosA().getY();
        final double y2 = planComplex.getPosB().getY();

        try{
            this.image =  new BufferedImage((int) ((x2 - x1) / deplacement), (int) ((y2 - y1) / deplacement), BufferedImage.TYPE_INT_RGB);
        }catch (IllegalArgumentException e){
            System.err.println("Erreur : vos parametres sont trop grands.");
            return;
        }
        this.fractal = new Mandelbrot.Builder()
                .image(image)
                .deplacement(deplacement)
                .planComplex(new PlanComplex(PointDouble.createPoint(x1, y1), PointDouble.createPoint(x2, y2)))
                .iteMax(iteMax)
                .typeComplex(new Complex(0,0))
                .nbThread(nbThread)
                .hue(color)
                .build();
    }

    /**
     * Cette fonction execute une methode de thread, ici le thread manuel.
     * */
    public void genererFractalByThreadComplex() {
        final ArrayList<Thread> listThread = new ArrayList<>();

        final int nbrThread=fractal.getThreadUse(); //4
        final int largeurImg=fractal.getImage().getWidth();//800
        int posDepart=(int)(fractal.getPlanComplex().getPosA().getX()/fractal.getDeplacement());
        final int distance= largeurImg/nbrThread;

        for (int i = 0, debut=posDepart; i < nbrThread; i++ , debut+=distance) {
            Thread thread = new Thread(new ThreadComplex( debut, debut+distance, (distance)*(i), fractal));
            thread.start();
            listThread.add(thread);
        }

        try {
            for (Thread threadComplex : listThread) threadComplex.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction execute une methode de thread, sans thread.
     * */
    public void genererFractalByLineareThread() {
       ThreadLinearComplex l = new ThreadLinearComplex(fractal);
        l.start();
        try {
            l.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction execute une methode de thread, ForkJoinPool.
     * */
    public void genererFractalByForkJoinPool() {
        ThreadForkComplex work = new ThreadForkComplex(
                fractal,
                (fractal.getPlanComplex().getPosA().getX())/fractal.getDeplacement(),
                (fractal.getPlanComplex().getPosB().getX())/fractal.getDeplacement(),
                (fractal.getPlanComplex().getPosA().getX())/fractal.getDeplacement()
        );
        (new ForkJoinPool(getCoeurFromFractal())).invoke(work);
    }

    /**
     * Cette fonction verifie si les valeurs sont correctes ni superieur au maximum disponible.
     * @return int valeur du nombre de coeur
     */
    private int getCoeurFromFractal(){
        final int coeurDiponibleMax =  Runtime.getRuntime().availableProcessors();
        if( fractal == null || fractal.getThreadUse() <= 0 || fractal.getThreadUse() >= coeurDiponibleMax){
            return coeurDiponibleMax;
        }
        return fractal.getThreadUse();
    }


    /**
     * Permets d'importer les parametres d'une fractale depuis un fichier.
     * @param fichierFractal Fichier contenant les informations d'une fractale
     */
    public void updateFractalByFile(File fichierFractal){
        readFile(fichierFractal.getPath());
        createFractalObject();
    }

    /**
     * Cree le fichier qui va sauvegarder la derniere ligne de commande valide.
     */
    public void createFileProp(PlanComplex planComplexParam, Complex complexParem, double pas, int hue, String generator, int iterMax, int nbThread){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(parser.getDirectory().getParameter()+".properties"), StandardCharsets.UTF_8))) {
            final PlanComplex planComplex = planComplexParam;
            final Complex typeComplex = complexParem;

            writer.write("-d="+parser.getDirectory().getParameter()+
                    " -e="+parser.getEnsemble().getParameter()+
                    " -p=("+planComplex.getPosA().getX()+","+planComplex.getPosA().getY()+");("+planComplex.getPosB().getX()+","+planComplex.getPosB().getY()+")"+
                    " -t="+typeComplex.getReal()+"+i"+typeComplex.getImaginary()+
                    " -s="+pas+
                    " -c="+hue+
                    " -g="+generator.toUpperCase()+
                    " -f="+iterMax+
                    " -n="+nbThread+"\n");
            writer.write(parser.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Creation de l'objet fractal.
     */
    public void createFractalObject(){
        if (parser.getEnsemble().getParameter() == 0) {
            updateFractal(parser.getPc().getParameter(),
                    parser.getDeplacement().getParameter(),
                    parser.getIte().getParameter(),
                    parser.getNbrThread().getParameter(),
                    parser.getColor().getParameter());
        } else {
            updateFractal(parser.getType().getParameter(),
                    parser.getPc().getParameter(),
                    parser.getDeplacement().getParameter(),
                    parser.getIte().getParameter(),
                    parser.getNbrThread().getParameter(),
                    parser.getColor().getParameter());
        }
        vueGraphique.setThreadUse(parser.getGenerator().getParameter());
    }

    /**
     * Genere une fractale en fonction avec une methode donnee par l'utilisateur.
     */
    public void launchThread(){
        switch (parser.getGenerator().getParameter()){
            case ForkJoinPool -> genererFractalByForkJoinPool();
            case ThreadBasic -> genererFractalByThreadComplex();
            case LinearThread -> genererFractalByLineareThread();
            default -> throw new IllegalArgumentException();
        }
    }

    /**
     * Enregistre une fractale dans un fichier.
     */
    public void createFractalImage(){
        try {
            File outputfile = new File(parser.getDirectory().getParameter()+".png");
            ImageIO.write(getFractal().getImage(), "PNG", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Extrait la premiere ligne d'un fichier pour permettre a l'utilisateur de generer une figure fractale
     * @param fileName Nom du fichier
     */
    public void readFile(String fileName){
        try {
            BufferedReader Buff = new BufferedReader(new FileReader(fileName));
            String text = Buff.readLine();
            String[] args =text.split(" ");
            parser.checkArgs(this, args);
        } catch (IOException e) {
            System.out.println(e.getMessage() + " Generation du fichier a partir des parametres par defaut.");
        }
    }

    public Fractal getFractal(){return this.fractal;}
    public Parser getParser() {return this.parser;}
    public void openGraphique() {vueGraphique.start();}
    private Controleur getControler(){return this;}
}
