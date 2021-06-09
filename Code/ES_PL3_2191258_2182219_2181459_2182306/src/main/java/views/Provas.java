package views;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Provas extends JFrame{
    private JTextPane textPane1;
    private JButton btn_insc;
    private JButton btn_importar;
    private JButton btn_voltar;
    private JButton btn_detalhes;
    private JButton btn_novo;
    private JPanel provas;

    public Provas(){
        setContentPane(provas);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        pack();

        btn_voltar.addActionListener(this::btn_voltarActionPerformed);
    }

    private void btn_voltarActionPerformed(ActionEvent actionEvent) {
        new WorldAthletics();
        setVisible(false);
    }
}
