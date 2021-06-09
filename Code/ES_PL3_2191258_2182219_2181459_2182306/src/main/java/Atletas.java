import views.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Atletas extends JFrame{
    private JTextPane textPane1;
    private JButton btn_hist;
    private JButton btn_importar;
    private JButton btn_voltar;
    private JButton btn_novo;
    private JButton btn_detalhes;
    private JPanel atletas;
    private JButton btn_insc;

    public Atletas(){
        setContentPane(atletas);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        pack();

        btn_voltar.addActionListener(this::btn_voltarActionPerformed);
    }

    private void btn_voltarActionPerformed(ActionEvent actionEvent) {
        new Main();
        setVisible(false);
    }
}
