package modele;

import java.util.Random;
import controleur.Controleur;
import modele.thread.ThreadType;
import modele.fractal.plan.Complex;
import modele.fractal.plan.PlanComplex;
import modele.fractal.plan.PointDouble;

public class Parser {

    private final Command<String> directory= new Command<>(
            "directory",
            "fractal_"+getId(),
            "Permets de choisir le nom du fichier (sans son extension) dans lequel la fractale va être générée."
    );

    private final Command<Complex> type= new Command<>(
            "type     ",
            new Complex(-0.8, 0.156),
            "Permets de choisir le type de fractale à générer."
    );

    private final Command<ThreadType> generator= new Command<>(
            "generator",
            ThreadType.ForkJoinPool,
            "Permets de choisir une méthode de génération de fractales. Multi thread, single thread ou bien encore en ForkJoinPool."
    );

    private final Command<Integer> color= new Command<>(
            "color    ",
            196,
            "Permets de choisir une couleur de base pour la colorisation de la fractale."
    );

    private final Command<Double> size= new Command<>(
            "size     ",
            0.00250,
            "Permets de choisir le pas utilise pour générer la fractale."
    );

    private final Command<Boolean> gui= new Command<>(
            "gui      ",
            true,
            "Permets de choisir entre une utilisation graphique et en ligne de commande."
    );

    private final Command<PlanComplex> pc= new Command<>(
            "plan     ",
            new PlanComplex(PointDouble.createPoint(-1,-1), PointDouble.createPoint(1,1)),
            "Permets de choisir la dimension du plan complexe."
    );

    private final Command<Integer> ite= new Command<>(
            "iteration",
            999,
            "Permets de changer le nombre d'itérations maximum pour calculer l'indice de divergence de chaque point du plan."
    );

    private final Command<Integer> ensemble=new Command<>(
            "ensemble ",
            0,
            "Permets de choisir l'ensemble de la fractale à générer."
    );

    private final Command<Integer> nbrThread=new Command<>(
            "number   ",
            Runtime.getRuntime().availableProcessors(),
            "Permets de gérer le nombre de Thread qui va calculer la fractale."
    );

    private final Command<String> read= new Command<>(
            "read     ",
            "fractal",
            "Permets d'importer un fichier de configuration."
    );


    public Command<Integer> getColor() {return color;}
    public Command<String> getDirectory() {return directory;}
    public Command<ThreadType> getGenerator() {return generator;}
    public Command<Double> getDeplacement() {return size;}
    public Command<Complex> getType() {return type;}
    public Command<String> getRead() {return read;}
    public Command<Boolean> getGui() {return gui;}
    public Command<PlanComplex> getPc() {return pc;}
    public Command<Integer> getIte() {return ite;}
    public Command<Integer> getEnsemble() {return ensemble;}
    public Command<Integer> getNbrThread() {return nbrThread;}

    /**
     * Prends la ligne de commande entree par l'utilisateur et la parse.
     * @param args Ligne de commande entree par l'utilisateur
     * @throws IllegalArgumentException leve une exception si les arguments ne sont pas au bon format
     */
    public void checkArgs(Controleur c, String[] args) throws IllegalArgumentException{
        if (args.length==0){
            System.out.println(this);
            return;
        }
        for (String cmd: args){
            String[] arr =cmd.split("=");
            if (arr.length!=2)throw new IllegalArgumentException("Erreur d'argument: l'option doit être sous la forme -nomOption=paramètre");
            switch (arr[0]){
                case "-g" -> {
                    switch (arr[1].toUpperCase()){
                        case "FORKJOINPOOL" -> generator.setParameter(ThreadType.ForkJoinPool);
                        case "THREADBASIC" -> generator.setParameter(ThreadType.ThreadBasic);
                        case "LINEARTHREAD" -> generator.setParameter(ThreadType.LinearThread);
                        default -> throw new IllegalArgumentException("Erreur d'argument dans l'option -g: les seuls arguments valident sont; ThreadBasic, ForkJoinPool et LinearThread");
                    }
                }
                case "-c" ->  {
                    try {
                        int Hcolor = Integer.parseInt(arr[1]);
                        if (Hcolor <0 || Hcolor > 360)throw new IllegalArgumentException();
                        color.setParameter(Hcolor);
                    } catch (NumberFormatException e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -c: il faut entrer un entier");
                    }catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -c: l'entier doit être compris entre 0 et 360");
                    }
                }
                case "-d" -> directory.setParameter(arr[1]);
                case "-s" ->  {
                    try {
                        double foo = Double.parseDouble(arr[1]);
                        size.setParameter(foo);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -s: la valeur doit être un nombre décimal");
                    }
                }
                case "-p" ->{
                    String[] resp=(arr[1]).split(";");
                    String[] respSA=resp[0].split(",");
                    String[] respSB=resp[1].split(",");
                    if (resp.length!=2 || respSB.length!=2 || respSA.length!=2 )throw new IllegalArgumentException("Erreur d'argument dans l'option -p: un exemple d'entrée valide (-1,-1);(1,1)");

                    double[] resA=new double[2];
                    double[] resB=new double[2];
                    for (int i=0; i< 2; i++){
                        try{
                            resA[i]=Double.parseDouble(respSA[i].replaceAll("[(),]",""));
                            resB[i]=Double.parseDouble(respSB[i].replaceAll("[(),]",""));
                        }catch (Exception e){
                            throw new IllegalArgumentException("Erreur d'argument dans l'option -p: un exemple d'entrée valide (-1,-1);(1,1)");
                        }
                    }

                    pc.setParameter(
                            new PlanComplex(
                                    PointDouble.createPoint(resA[0], resA[1]),
                                    PointDouble.createPoint(resB[0], resB[1])
                            )
                    );
                }
                case "-n" ->{
                    try{
                        int nbThread=Integer.parseInt(arr[1]);
                        if (nbThread<=0) throw new IllegalArgumentException();
                        nbrThread.setParameter(nbThread);
                    }catch (NumberFormatException e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -c: il faut entrer un entier");
                    }catch (IllegalArgumentException e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -c: l'entier doit être supérieur a 0");
                    }
                }
                case "-f" ->{
                    try{
                        int nbIteration = Integer.parseInt(arr[1]);
                        if (nbIteration<=0) throw new IllegalArgumentException();
                        ite.setParameter(nbIteration);
                    }catch (NumberFormatException e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -f: il faut entrer un entier positif");
                    }catch (IllegalArgumentException e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -f: l'entier doit être supérieur a 0");
                    }
                }
                case "-e" ->{
                    switch (arr[1]){
                        case "1" -> ensemble.setParameter(1);
                        case "0" -> ensemble.setParameter(0);
                        default -> throw new IllegalArgumentException("Erreur d'argument dans l'option -e: les seuls options valides sont 1 pour l'ensemble de Mandelbrot et 0 pour l'ensemble de Julia");
                    }
                }
                case "-t" -> {
                    if (isPresent("-e", args)){
                        if (getValue("-e", args).equals("1"))throw new IllegalArgumentException("Impossible d'utiliser l'option -t si l'option -e vaut 1");
                    }
                    try {
                        String[] subArr =arr[1].split("\\+");
                        if (subArr.length!=2)throw new IllegalArgumentException();
                        double reel=Double.parseDouble(subArr[0]);
                        double imag=Double.parseDouble(subArr[1].replaceAll("i",""));
                        type.setParameter(new Complex(reel,imag));
                    }catch (Exception e){
                        throw new IllegalArgumentException("Erreur d'argument dans l'option -t: il faut entrer un nombre complexe composé de deux nombres décimaux");
                    }
                }
                case "-r" ->{
                    //Lit le fichier entre en parametre
                    if (args.length==1){
                        c.readFile(arr[1]);
                        c.launchThread();
                        c.createFractalImage();
                        informationGeneration(c);
                    }
                    else throw new IllegalArgumentException("Impossible d'utiliser d'autres arguments lors du chargement d'un fichier");
                }
                case "-i" ->{
                    //Lance l'interface graphique
                    boolean bool = Boolean.parseBoolean(arr[1]);
                    gui.setParameter(bool);
                    if (bool){
                        if (args.length==1){
                            try {
                                c.openGraphique();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else throw new IllegalArgumentException();
                    }
                }
                default -> throw new IllegalArgumentException("Option inexistante");
            }
        }
        if (!gui.getParameter()){
            c.launchThread();
            c.createFractalImage();
            c.createFileProp(getPc().getParameter(), getType().getParameter(), getDeplacement().getParameter(), getColor().getParameter(),
                    getGenerator().getParameter().toString(), getIte().getParameter(), getNbrThread().getParameter());
            informationGeneration(c);

        }
    }

    private void informationGeneration(Controleur c){
        System.out.println();
        System.out.println("Generation de votre fractal : " + this.getDirectory().getParameter() + "\n"
                + "Taille du pas: " + c.getFractal().getDeplacement() + "\n"
                + "Taille de l'image: " + c.getFractal().getImage().getWidth() + "*" + c.getFractal().getImage().getHeight() + "\n"
                + "Complexe " + c.getFractal().getType().toString() + "\n"
                + "Plan " + c.getFractal().getPlanComplex().toString()+ "\n"
                + "Temps de realisation: " + c.getCurrentTime() +"ms "+"\n");
    }

    /**
     * Verifie si parametre est present dans la liste d'arguments.
     * @param opt le parametre recherche
     * @param args liste d'arguments entree par l'utilisateur
     * @return indique si l'option est presente
     * @throws IllegalArgumentException en cas d'erreur
     */
    boolean isPresent(String opt, String[] args) throws IllegalArgumentException{
        boolean flag=false;
        for (String cmdCheck: args){
            String[] arrCheck =cmdCheck.split("=");
            if (arrCheck[0].equals(opt)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Recupere la valeur d'une option entree par l'utilisateur.
     * @param opt le parametre recherche
     * @param args liste d'arguments entree par l'utilisateur
     * @return l'argument sous forme de chaine de caractere
     */
    String getValue(String opt, String[] args){
        for (String cmdCheck: args){
            String[] arrCheck =cmdCheck.split("=");
            if (arrCheck[0].equals(opt)) {
                return arrCheck[1];
            }
        }
        return "";
    }

    @Override
    public String toString() {
        //Help
        return "\n# Utilisation des commandes \n"+
                "usage: fractal [-d=directory] [-t=type] [-g=generator] [-c=color] [-s=size] [-p=plan] [-f=iteration] [-e=ensemble] [-n=number]\n"
                + "       fractal [-i=gui]\n\n"
                + getDirectory() + "\n"
                + getType() + "\n"
                + getGenerator() + "\n"
                + getColor() + "\n"
                + getDeplacement() + "\n"
                + getRead() + "\n"
                + getGui() + "\n"
                + getPc() + "\n"
                + getIte() + "\n"
                + getEnsemble() + "\n"
                + getNbrThread();
    }

    private int getId(){
        Random random = new Random();
        final int id = random.nextInt(100000);
        return id;
    }

    /**
     * Le type Command permettant de parser les commandes entree par l'utilisateur.
     * @param <Param> Objet permettant de stocker les commandes de l'utilisateur
     */
    public class Command<Param> {
        private final String name, description;
        private Param parameter;

        public Command(String name, Param parameter, String description){
            this.name=name;
            this.parameter=parameter;
            this.description=description;
        }

        public String getDescription() {return description;}
        public String getName() {return name;}
        public Param getParameter() {return parameter;}

        public void setParameter(Param parameter) {this.parameter = parameter;}

        @Override
        public String toString() {
            return "\t" + getName() + "\t" + getDescription();
        }
    }

}