package vue;

import java.awt.*;
import javax.swing.*;
import modele.TypeFractal;
import modele.thread.ThreadType;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * CreateGraphicsUtils est une fabrique statique d'outils graphique qui permet d'alleger le code
 */
public class CreateGraphicsUtils {

    public static JRadioButton createJRadioButton(boolean selected, String text){
        final JRadioButton  jRadioButton = new JRadioButton (text);
        jRadioButton.setSelected(selected);
        return jRadioButton;
    }

    public static JButton createJButton(String text){
        final JButton button = new JButton(text);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
        return button;
    }

    public static JTextField createJtextField(){
        final JTextField jTextField = new JTextField();
        jTextField.setBounds(1,1,1,1);
        return jTextField;
    }

    public static JComboBox createJComboBoxListFractal(){
        final JComboBox<TypeFractal> jcombox = new JComboBox();
        for(TypeFractal type: TypeFractal.values()){
            jcombox.addItem(type);
        }
        return jcombox;
    }

    public static JComboBox createJComboBoxListThread(){
        final JComboBox<ThreadType> jcombox = new JComboBox();
        for(ThreadType type: ThreadType.values()){
            jcombox.addItem(type);
        }
        return jcombox;
    }

    public static JTabbedPane createJTabbedPane(JPanel ... listComposant) {
        final JTabbedPane jTabbedPane = new JTabbedPane();
        for(JPanel panel : listComposant) jTabbedPane.addTab(panel.getName(), panel);
        return jTabbedPane;
    }

    public static JPanel createJpanel(String name, JComponent ... listComposant) {
        final JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(name));
        for(JComponent panel : listComposant) jpanel.add(panel);
        return jpanel;
    }

    public static JPanel createJpanel(JComponent ... listComposant) {
        final JPanel jpanel = new JPanel();
        for(JComponent panel : listComposant) jpanel.add(panel);
        return jpanel;
    }

    public static JLabel createJLabel(String text) {
        final JLabel label = new JLabel();
        label.setText(text);
        label.setSize(120, 120);
        return label;
    }

    public static JPanel createJpanel(GridLayout grid, JComponent ... listComposant) {
        final JPanel jpanel = new JPanel();
        jpanel.setLayout(grid);
        for(JComponent panel : listComposant) {
            jpanel.add(panel);
        }
        return jpanel;
    }

    public static JSpinner createSpinner(int value, int min, int max, int step) {
        final SpinnerModel model = new SpinnerNumberModel(value, min , max,step);
        final JSpinner spinner = new JSpinner(model);
        return spinner;
    }

    public static JSlider createSlider(int min, int max, int value){
        final JSlider slider =  new JSlider(min, max, value);
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(120);
        slider.setMinorTickSpacing(50);
        return slider;
    }

    public static JFileChooser createJFileChooser(String name, String extensionString, String ... extensions) {
        JFileChooser choose = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        choose.setDialogTitle(name);
        choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        choose.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(extensionString, extensions);
        choose.addChoosableFileFilter(filter);
        return choose;
    }
}
