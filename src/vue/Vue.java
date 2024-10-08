package vue;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import modele.TypeFractal;
import controleur.Controleur;
import modele.fractal.Fractal;
import modele.thread.ThreadType;
import modele.fractal.plan.Complex;
import java.awt.image.BufferedImage;
import modele.fractal.plan.PlanComplex;
import javax.swing.text.JTextComponent;

/**
 * Vue est la classe contenant la partie graphique du programme.
 */
public class Vue extends JFrame {

    private final Controleur controler;

    private final FractalGraphic fractalGraphic;

    private JPanel panelFractalOption, panelDeplacementOption, panelPerformOption;


    /*Panel Fractal Option */
    private JPanel choiceFractal;
    private JRadioButton julia, mandelbrot;

    private JComboBox<TypeFractal> listFractal;

    private JPanel panelComplexTextField;
    private JLabel labelI;
    private JTextField complexeReal ,complexeImaginaire;

    private JPanel planComplex;
    private JTextField x,xi,y,yi;

    private JPanel panelColor;
    private JSlider chooserColor;

    private JPanel panelButton;
    private JButton valider,charger,reset,save;
    private JFileChooser choixFichier;

    private JPanel panelInterval;
    private JTextField intervalTextField;
    /*Panel Fractal Option */


    /*Panel Deplacement */
    private JButton zoom, unZoom, left, right, up, down;
    /*Panel Deplacement */


    /*Panel de perfomance */
    private JPanel panelThreadChoice;
    private JComboBox<ThreadType> threadChoice;

    private JPanel panelIterration;
    private JSpinner iteration;

    private JPanel panelThread;
    private JSpinner threadUse;
    /*Panel de perfomance */


    public Vue(Controleur controler){
        this.controler = controler;
        this.fractalGraphic = new FractalGraphic();
    }

    public void start(){
        this.setTitle("Fractal - Projet | 2021 ");
        this.setSize(1000, 720);
        this.setPreferredSize(new Dimension(1000, 720));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        createPanelFractalOption();
        createDeplacementArea();
        createPanelPerfomOption();
        final JTabbedPane tabbedPane = CreateGraphicsUtils.createJTabbedPane(panelFractalOption, panelDeplacementOption, panelPerformOption);
        add(tabbedPane, BorderLayout.WEST);
        add(fractalGraphic, BorderLayout.CENTER);
        fractalGraphic.setBorder(BorderFactory.createTitledBorder("Fractal"));
        updateChamps();
        updateFractal();
        valideListerner();
        zoomAndUnzoomListerner();
        resetListerner();
        deplacementListerner();
        saveImageListerner();
        chargeFractal();
        chargeFileListerner();

    }

    //Fonction d'enrengistrement des evenements

    /**
     *  Cette fonction est l'action lorsque nous appuyons sur valide.
     */
    private void valideListerner() {
        valider.addActionListener(e -> {
            if(mandelbrot.isSelected()){
                try{
                    final PlanComplex planComplex = PlanComplex.createPlanComplex(checkArguments(x), checkArguments(y), checkArguments(xi), checkArguments(yi));
                    controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
                }catch (IllegalArgumentException er){
                    openDialogError("Erreur de taille", er.getMessage());
                }
            }else{
                updateFractal();
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
        });
    }

    /**
     * Cette fonction est l'action lorsque nous zoomons ou dezoomons.
     */
    private void zoomAndUnzoomListerner() {
        final double zoomNumber = 2.5;
        final double facteurDivision = 0.95;

        zoom.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x)/ zoomNumber, checkArguments(y)/ zoomNumber
                    , checkArguments(xi)/ zoomNumber, checkArguments(yi)/ zoomNumber);
            final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
            controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField)/(zoomNumber*facteurDivision), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            if(julia.isSelected()){
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField)/(zoomNumber*facteurDivision), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField)/(zoomNumber*facteurDivision), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }
            updateChamps();
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });

        unZoom.addActionListener(e -> {

            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x)* zoomNumber, checkArguments(y)* zoomNumber
                    , checkArguments(xi)* zoomNumber, checkArguments(yi)* zoomNumber);
            if(julia.isSelected()){
                final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField)*(zoomNumber*facteurDivision), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField)*(zoomNumber*facteurDivision), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }

            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });
    }

    /**
     * Cette fonction est l'action lorsque nous chargeons un fichier.
     */
    private void chargeFileListerner() {
        charger.addActionListener(e -> {
            int res = choixFichier.showOpenDialog(null);
            if (res == JFileChooser.APPROVE_OPTION) {
                final File file = choixFichier.getSelectedFile();
                controler.updateFractalByFile(file);
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });
    }

    /**
     * Cette fonction est l'action lorsque nous remettons a 0 tous les parametres.
     */
    private void resetListerner() {
        reset.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(-1,-1,1,1);
            final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
            controler.updateFractal(complexType, planComplex, 0.00250, 999, 4, 150);
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            setItemAcces(true);
            this.julia.setSelected(true);
            this.mandelbrot.setSelected(false);
            this.threadChoice.setSelectedIndex(0);
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });
    }

    /**
     * Cette fonction est l'action lorsque nous deplacons dans le pan
     */
    private void deplacementListerner() {
        final double deplacementNumber = 1.15*controler.getFractal().getDeplacement();

        down.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x), checkArguments(y)+ deplacementNumber
                    , checkArguments(xi), checkArguments(yi)+deplacementNumber);
            if(julia.isSelected()){
                final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.repaint();
            fractalGraphic.revalidate();
        });

        right.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x)+ deplacementNumber, checkArguments(y)
                    , checkArguments(xi)+deplacementNumber, checkArguments(yi));
            if(julia.isSelected()){
                final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });

        up.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x), checkArguments(y)- deplacementNumber
                    , checkArguments(xi), checkArguments(yi)- deplacementNumber);
            if(julia.isSelected()){
                final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });
        left.addActionListener(e -> {
            final PlanComplex planComplex = PlanComplex.createPlanComplex(
                    checkArguments(x)- deplacementNumber, checkArguments(y)
                    , checkArguments(xi)- deplacementNumber, checkArguments(yi));
            if(julia.isSelected()){
                final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
                controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            updateChamps();
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            fractalGraphic.revalidate();
        });
    }

    /**
     * Cette fonction est l'action lorsque nous cliquons sur Julia ou Mandelbrot.
     */
    private void actionListenerRadioButton(){
        this.julia.addActionListener(e -> {
            setItemAcces(true);
        });
        this.mandelbrot.addActionListener(e -> {
            setItemAcces(false);
        });
    }

    /**
     * Cette fonction est l'action pour enregistrer l'image
     */
    private void saveImageListerner(){
        save.addActionListener(e -> {
            if(mandelbrot.isSelected()){
                final PlanComplex planComplex = PlanComplex.createPlanComplex(checkArguments(x), checkArguments(y), checkArguments(xi), checkArguments(yi));
                controler.updateFractal(planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            }else{
                updateFractal();
            }
            controler.threadRun((ThreadType) threadChoice.getSelectedItem());
            fractalGraphic.setBg(controler.getFractal().getImage());
            fractalGraphic.updateUI();
            controler.createFractalImage();
            final Fractal copy = controler.getFractal();
            controler.createFileProp(copy.getPlanComplex(), copy.getType(), copy.getDeplacement(), copy.getHue(), threadChoice.getSelectedItem().toString(), copy.getIteMax(), copy.getThreadUse());
        });
    }

    //Fin fonction d'enrengistrement des evenements


    //Fonction mise a jour des objets

    private void updateFractal(){
        final PlanComplex planComplex = PlanComplex.createPlanComplex(checkArguments(x), checkArguments(y), checkArguments(xi), checkArguments(yi));
        final Complex complexType = Complex.createComplex(checkArguments(complexeReal), checkArguments(complexeImaginaire));
        controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
    }



    private void chargeFractal(){
        listFractal.addActionListener((e) -> {
            TypeFractal select = (TypeFractal) listFractal.getSelectedItem();
            final PlanComplex planComplex = PlanComplex.createPlanComplex(checkArguments(x), checkArguments(y), checkArguments(xi), checkArguments(yi));
            final Complex complexType = Complex.createComplex(select.getType().getReal(),  select.getType().getImaginary());
            controler.updateFractal(complexType, planComplex, checkArguments(intervalTextField), (Integer) iteration.getValue(), (Integer) threadUse.getValue(), chooserColor.getValue());
            updateChamps();
        });
    }

    //fin fonction mise a jour des objets

    private double checkArguments(JTextComponent s){
        try{
            return Double.parseDouble(s.getText());
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Mauvaise saisie entrée.", "Erreur de valeurs", JOptionPane.ERROR_MESSAGE);
            updateChamps();
        }
        return Double.parseDouble(s.getText());
    }

    //Fonction de creation des elements

    private void createPanelFractalOption(){
        createListFractal();
        createPanelPlanComplex();
        createPanelButton();
        createComplexTextField();
        createIntervalArea();
        createColorArea();
        createRadioButton();
        final GridLayout grid = new GridLayout(0, 1);
        this.panelFractalOption = CreateGraphicsUtils.createJpanel(grid, choiceFractal, listFractal, panelComplexTextField, panelInterval, planComplex, panelColor, panelButton);
        this.panelFractalOption.setName("Option fractal");
    }

    private void createPanelPerfomOption(){
        createSpinnerArea();
        createListThreadArea();
        createCoeurPanel();
        final GridLayout grid = new GridLayout(0, 1);
        panelPerformOption = CreateGraphicsUtils.createJpanel(grid, panelThreadChoice, panelIterration, panelThread);
        this.panelPerformOption.setName("Option perfomance ");

    }

    private void createRadioButton(){
        final ButtonGroup bg=new ButtonGroup();
        this.julia = CreateGraphicsUtils.createJRadioButton(true, "Ensemble de Julia");
        this.mandelbrot = CreateGraphicsUtils.createJRadioButton(false, "Ensemble de Mandelbrot");
        this.choiceFractal = CreateGraphicsUtils.createJpanel("Choix de l'ensemble ", julia, mandelbrot);
        this.choiceFractal.setLayout(new GridLayout(1,0));
        bg.add(julia);
        bg.add(mandelbrot);
        actionListenerRadioButton();
    }

    private void setItemAcces(boolean condition){
        panelComplexTextField.setEnabled(condition);
        listFractal.setEnabled(condition);
        complexeReal.setEnabled(condition);
        complexeImaginaire.setEnabled(condition);
        labelI.setEnabled(condition);
    }

    private void createColorArea(){
        this.chooserColor = CreateGraphicsUtils.createSlider(0,360,0);
        this.panelColor = CreateGraphicsUtils.createJpanel("Choix de la couleur ", this.chooserColor);
    }

    private void createListFractal(){
        this.listFractal = CreateGraphicsUtils.createJComboBoxListFractal();
    }

    private void createPanelPlanComplex(){
        final JLabel labelX, labelY;
        labelX = CreateGraphicsUtils.createJLabel("x");
        labelY = CreateGraphicsUtils.createJLabel("y");
        this.x = CreateGraphicsUtils.createJtextField();
        this.xi = CreateGraphicsUtils.createJtextField();
        this.y = CreateGraphicsUtils.createJtextField();
        this.yi = CreateGraphicsUtils.createJtextField();
        planComplex = CreateGraphicsUtils.createJpanel("Plan Complex", labelX,x,xi,labelY,y,yi);
        this.planComplex.setLayout(new GridLayout(2, 3));
    }

    private void createPanelButton(){
        this.choixFichier = CreateGraphicsUtils.createJFileChooser("Selectionnez un fichier config", "Fichier de configuration.properties", "properties");
        this.valider = CreateGraphicsUtils.createJButton("Valider");
        this.reset = CreateGraphicsUtils.createJButton("Restaurer");
        this.charger = CreateGraphicsUtils.createJButton("Charger");
        this.save = CreateGraphicsUtils.createJButton("Sauvegarder");
        panelButton = CreateGraphicsUtils.createJpanel(valider, reset, charger, save);
        this.panelButton.setLayout(new GridLayout(1, 4));
    }

    private void createComplexTextField(){
        this.labelI = CreateGraphicsUtils.createJLabel("i");
        this.complexeReal = CreateGraphicsUtils.createJtextField();
        this.complexeImaginaire = CreateGraphicsUtils.createJtextField();
        panelComplexTextField = CreateGraphicsUtils.createJpanel("Complex", complexeReal, labelI, complexeImaginaire);
        this.panelComplexTextField.setLayout(new GridLayout(1, 3));
    }

    private void createIntervalArea(){
        final JLabel labelInterval;
        labelInterval = CreateGraphicsUtils.createJLabel("Interval");
        this.intervalTextField = CreateGraphicsUtils.createJtextField();
        this.panelInterval = CreateGraphicsUtils.createJpanel(labelInterval, intervalTextField);
        this.panelInterval.setLayout(new GridLayout(1, 0));
    }

    private void createDeplacementArea(){
        JPanel zoomArea, moveArea;

        this.zoom = CreateGraphicsUtils.createJButton("+");
        this.unZoom = CreateGraphicsUtils.createJButton("-");
        this.left = CreateGraphicsUtils.createJButton("◀");
        this.right = CreateGraphicsUtils.createJButton("▶");
        this.up = CreateGraphicsUtils.createJButton("▲");
        this.down = CreateGraphicsUtils.createJButton("▼");

        zoomArea = CreateGraphicsUtils.createJpanel(zoom, unZoom);
        zoomArea.setLayout(new GridLayout(0,1));
        moveArea = new JPanel();
        moveArea.setLayout(new BorderLayout());
        moveArea.add(left, BorderLayout.WEST);
        moveArea.add(right, BorderLayout.EAST);
        moveArea.add(up, BorderLayout.NORTH);
        moveArea.add(down, BorderLayout.SOUTH);
        moveArea.add(zoomArea, BorderLayout.CENTER);
        this.panelDeplacementOption = CreateGraphicsUtils.createJpanel(moveArea);
        this.panelDeplacementOption.setName("Option deplacement");
        this.panelDeplacementOption.setLayout(new GridLayout(1,0));
    }
    private void createCoeurPanel(){
        this.threadUse = CreateGraphicsUtils.createSpinner(4,1,16,1);
        this.panelThread = CreateGraphicsUtils.createJpanel("Nombre de thread paralleles", threadUse);
    }
    private void updateChamps(){
        this.intervalTextField.setText(String.valueOf(controler.getFractal().getDeplacement()));
        this.x.setText(String.valueOf(controler.getFractal().getPlanComplex().getPosA().getX()));
        this.xi.setText(String.valueOf(controler.getFractal().getPlanComplex().getPosB().getX()));
        this.y.setText(String.valueOf(controler.getFractal().getPlanComplex().getPosA().getY()));
        this.yi.setText(String.valueOf(controler.getFractal().getPlanComplex().getPosB().getY()));
        this.complexeImaginaire.setText(String.valueOf(controler.getFractal().getType().getImaginary()));
        this.complexeReal.setText(String.valueOf(controler.getFractal().getType().getReal()));
        this.iteration.setValue(controler.getFractal().getIteMax());
        this.chooserColor.setValue(controler.getFractal().getHue());
        this.threadUse.setValue(controler.getFractal().getThreadUse());
    }

    private void createListThreadArea(){
        this.threadChoice = CreateGraphicsUtils.createJComboBoxListThread();
        this.panelThreadChoice = CreateGraphicsUtils.createJpanel("Choix du type de thread", threadChoice);
    }

    private void createSpinnerArea(){
        this.iteration = CreateGraphicsUtils.createSpinner(controler.getFractal().getIteMax(), 1, 10000, 1);
        this.panelIterration = CreateGraphicsUtils.createJpanel("Nombre d'iterration ", iteration);

    }

    public void setThreadUse(ThreadType parameter) {
        switch (parameter){
            case ForkJoinPool: this.threadChoice.setSelectedIndex(0);
            case ThreadBasic: this.threadChoice.setSelectedIndex(1);
            case LinearThread:this.threadChoice.setSelectedIndex(2);
        }
    }

    public void openDialogError(String titleError, String error){
        JOptionPane.showMessageDialog(this, error, titleError, JOptionPane.ERROR_MESSAGE);

    }


    /**
     * FractalGraphic est une sous classe private graphique qui permet de faire des actions directement sur la Jpanel
     */
    private class FractalGraphic extends JPanel {

        private BufferedImage image;
        private void setBg(BufferedImage image) {
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            g.setColor(new Color(254,254,254, 200));
            g.fillRect( 5, 20, 350, 70);
            final Fractal fractal = controler.getFractal();
            final String text = "imaginaire: %imaginary% | real : %real% ";

            final String replace = text.replace("%imaginary%", String.valueOf(fractal.getType().getImaginary()))
                    .replace("%real%", String.valueOf(fractal.getType().getReal()));
            final String ligne2 = "Intervalle :" + fractal.getDeplacement() + "| Nombre d'iteration : " + fractal.getIteMax();
            final String ligne3 = "Thread utilisé : " + fractal.getThreadUse() + " | Temps (ms) "+ controler.getCurrentTime() +"ms";
            final String ligne4 = "Methode thread utilisée : " + threadChoice.getSelectedItem();
            g.setColor(Color.BLACK);
            g.drawString(replace,10,35);
            g.drawString(ligne2,10,50);
            g.drawString(ligne3,10,65);
            g.drawString(ligne4,10,80);
        }
    }

}