
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Jeu {

    private Plateau plateau;
    private Joueur joueur1;
    private Joueur joueur2;
    private Scanner scanner;
    private boolean contreIA;
    private IA ia;
    private int passesConsecutives = 0;

    public Jeu(boolean contreIA) {
        this.contreIA = contreIA;
        plateau = new Plateau();
        joueur1 = new Joueur("Joueur 1", 'N');
        joueur2 = new Joueur(contreIA ? "Ordinateur" : "Joueur 2", 'B');
        scanner = new Scanner(System.in);
        if (contreIA) {
            ia = new IA();
        }
    }

    public void lancer() {
        Joueur courant = joueur1;
        boolean fin = false;

        while (!fin) {
            plateau.afficher();

            if (contreIA && courant.getNom().equals("Ordinateur")) {
                int[] coup = ia.jouer(plateau, courant.getCouleur());
                int x = coup[0];
                int y = coup[1];

                if (x == -1) {
                    System.out.println("L’IA passe son tour !");
                    passesConsecutives++;
                } else {
                    plateau.placer(x, y, courant.getCouleur());
                    System.out.println("L’IA joue en (" + x + "," + y + ")");
                    passesConsecutives = 0;
                }
            } else {
                System.out.println(courant.getNom() + " (" + courant.getCouleur() + "), à vous de jouer !");
                System.out.print("Entrez la ligne (ou -1 pour passer) : ");
                int x = scanner.nextInt();

                if (x == -1) {
                    passesConsecutives++;
                    System.out.println(courant.getNom() + " passe son tour !");
                } else {
                    System.out.print("Entrez la colonne : ");
                    int y = scanner.nextInt();

                    if (!plateau.estVide(x, y)) {
                        System.out.println("Coup invalide : la case est déjà occupée !");
                        continue;
                    }

                    if (plateau.estSuicide(x, y, courant.getCouleur())) {
                        System.out.println("Coup interdit : suicide !");
                        continue;
                    }

                    plateau.placer(x, y, courant.getCouleur());
                    passesConsecutives = 0;
                }
            }

            if (passesConsecutives >= 2) {
                System.out.println("Fin du jeu (deux passes consécutives).");
                fin = true;
            }

            courant = (courant == joueur1) ? joueur2 : joueur1;
        }
    }

    public void sauvegarderPartie() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("partie.sav"))) {
            out.writeObject(plateau);  // Sauvegarde l'état du plateau
            System.out.println("Partie sauvegardée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde !");
            e.printStackTrace();
        }
    }

    public void restaurerPartie() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("partie.sav"))) {
            plateau = (Plateau) in.readObject();  // Restaure l'état du plateau
            System.out.println("Partie restaurée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la restauration !");
            e.printStackTrace();
        }
    }

}
