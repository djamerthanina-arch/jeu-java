
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class FenetreJeu extends JFrame {

    private Plateau plateau = new Plateau();
    private char joueurActuel = 'N';
    private int passes = 0;
    private boolean contreIA;

    public FenetreJeu(boolean contreIA) {
        this.contreIA = contreIA;
        setTitle("GroupeMax - Jeu de stratégie");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel grillePanel = creerGrille();
        JPanel boutonsPanel = creerBoutons(grillePanel);

        setLayout(new BorderLayout());
        add(grillePanel, BorderLayout.CENTER);
        add(boutonsPanel, BorderLayout.SOUTH);
    }

    private JPanel creerGrille() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int taille = getWidth() / plateau.getTaille();

                for (int i = 0; i < plateau.getTaille(); i++) {
                    for (int j = 0; j < plateau.getTaille(); j++) {
                        g.drawRect(j * taille, i * taille, taille, taille);
                        char c = plateau.getCase(i, j);
                        if (c == 'N' || c == 'B') {
                            g.setColor(c == 'N' ? Color.BLACK : Color.WHITE);
                            g.fillOval(j * taille + 5, i * taille + 5, taille - 10, taille - 10);
                            g.setColor(Color.BLACK);
                        }
                    }
                }
            }
        };

        panel.setPreferredSize(new Dimension(600, 600));
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int taille = panel.getWidth() / plateau.getTaille();
                int x = e.getY() / taille;
                int y = e.getX() / taille;

                if (!plateau.estVide(x, y) || plateau.estSuicide(x, y, joueurActuel)) {
                    return;
                }

                plateau.placer(x, y, joueurActuel);
                joueurActuel = (joueurActuel == 'N') ? 'B' : 'N';
                passes = 0;
                panel.repaint();

                if (contreIA && joueurActuel == 'B') {
                    IA ia = new IA();
                    int[] coup = ia.jouer(plateau, joueurActuel);
                    if (coup != null) {
                        plateau.placer(coup[0], coup[1], joueurActuel);
                        joueurActuel = 'N';
                        panel.repaint();
                    }
                }
            }
        });

        return panel;
    }

    private JPanel creerBoutons(JPanel grillePanel) {
        JPanel panel = new JPanel();

        JButton passer = new JButton("Passer");
        JButton quitter = new JButton("Quitter");
        JButton sauvegarder = new JButton("Sauvegarder");
        JButton restaurer = new JButton("Restaurer");

        passer.addActionListener(e -> {
            passes++;
            if (passes >= 2) {
                int[] scores = plateau.compterPoints();
                String msg = "Noir: " + scores[0] + " pts\nBlanc: " + scores[1] + " pts\n";
                msg += (scores[0] > scores[1]) ? "Noir gagne !"
                        : (scores[1] > scores[0]) ? "Blanc gagne !" : "Égalité !";
                JOptionPane.showMessageDialog(this, msg, "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                joueurActuel = (joueurActuel == 'N') ? 'B' : 'N';
            }
        });

        quitter.addActionListener(e -> System.exit(0));

        sauvegarder.addActionListener(e -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("sauvegarde.ser"))) {
                out.writeObject(plateau);
                JOptionPane.showMessageDialog(this, "Partie sauvegardée.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de sauvegarde.");
            }
        });

        restaurer.addActionListener(e -> {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("sauvegarde.ser"))) {
                plateau = (Plateau) in.readObject();
                grillePanel.repaint();
                JOptionPane.showMessageDialog(this, "Partie restaurée.");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de restauration.");
            }
        });

        panel.add(passer);
        panel.add(quitter);
        panel.add(sauvegarder);
        panel.add(restaurer);

        return panel;
    }
}
