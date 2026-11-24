
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            String[] options = {"Jouer contre l'IA", "Jouer contre un humain"};
            int choix = JOptionPane.showOptionDialog(
                    null,
                    "Choisissez le mode de jeu :",
                    "Mode de jeu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            boolean contreIA = (choix == 0);
            new FenetreJeu(contreIA).setVisible(true);
        });
    }
}
