package views;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WorldAthletics extends JFrame {
    private JPanel mainPanel;
    private JButton btn_atletas;
    private JButton btn_exit;
    private JButton btn_gerirPr;
    private JButton btn_gerirEv;
    private JTextPane textPane1;
    private JTextPane textPane2;
    private JButton btn_tendencias;
    private JButton btn_medalhas;
    private JButton btn_new_atleta;
    private JButton btn_modalidades;
    private JButton btn_definicoes;

    public void openView()
    {
        this.setVisible(true);
    }

    public WorldAthletics() {
        super("World Athletics");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        pack();
        setVisible(true);

        btn_exit.addActionListener(this::btn_exitActionPerformed);
        btn_gerirEv.addActionListener(this::btn_gerirEvActionPerformed);
        btn_gerirPr.addActionListener(this::btn_gerirPrActionPerformed);
        btn_atletas.addActionListener(this::btn_atletasActionPerformed);
        btn_modalidades.addActionListener(this::btn_modalidadesActionPerformed);
    }

    private void btn_modalidadesActionPerformed(ActionEvent actionEvent) {
        new Modalidades();
        setVisible(false);
    }

    private void btn_atletasActionPerformed(ActionEvent actionEvent) {
        new Atletas();
        setVisible(false);
    }

    private void btn_gerirPrActionPerformed(ActionEvent actionEvent) {
        new Provas();
        setVisible(false);
    }

    private void btn_gerirEvActionPerformed(ActionEvent actionEvent) {
        new Eventos();
        setVisible(false);
    }

    private void btn_exitActionPerformed(ActionEvent e){
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}

