import views.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Eventos extends JFrame{
    private JPanel eventos;
    private JTextPane textPane1;
    private JButton btn_novo;
    private JButton btn_elimi;
    private JButton btn_prog;
    private JButton btn_importar;
    private JButton btn_voltar;

    public Eventos(){
        setContentPane(eventos);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        pack();

        btn_voltar.addActionListener(this::btn_voltarActionPerformed);
    }

    private void btn_voltarActionPerformed(ActionEvent e) {
        new Main();
        setVisible(false);
    }

    public static void eventos(String[] args) {
        new Eventos().setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
