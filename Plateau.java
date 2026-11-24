import java.io.Serializable;

public class Plateau implements Serializable {

    private char[][] grille; // La grille de jeu : tableau 2D représentant le plateau
    private final int taille = 10; // Taille fixe du plateau (10x10)

    // Constructeur : initialise la grille en créant le tableau et en remplissant avec '.'
    public Plateau() {
        grille = new char[taille][taille];
        initialiserGrille();
    }

    // Méthode pour remplir la grille avec des '.' indiquant des cases vides
    private void initialiserGrille() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                grille[i][j] = '.';
            }
        }
    }

    // Affiche la grille dans la console avec indices de lignes et colonnes
    public void afficher() {
        System.out.print("    ");
        for (int i = 0; i < taille; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < taille; i++) {
            System.out.print(i + "   ");
            for (int j = 0; j < taille; j++) {
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Vérifie si une case est vide ('.')
    public boolean estVide(int x, int y) {
        return grille[x][y] == '.';
    }

    // Place une pièce de couleur donnée sur la grille si la case est valide et vide
    public boolean placer(int x, int y, char couleur) {
        if (x < 0 || x >= taille || y < 0 || y >= taille || !estVide(x, y)) {
            return false; // Case invalide ou déjà occupée
        }
        grille[x][y] = couleur;
        return true;
    }

    // Retourne la taille du plateau
    public int getTaille() {
        return taille;
    }

    // Retourne le contenu de la case (x,y)
    public char getCase(int x, int y) {
        return grille[x][y];
    }

    // Nouvelle méthode estSuicide selon règle zones privatisées
    public boolean estSuicide(int x, int y, char couleur) {
        if (!estVide(x, y)) {
            return false; // Case occupée : pas un coup valide, donc pas suicide ici
        }

        boolean[][] visite = new boolean[taille][taille];
        Zone zone = new Zone();
        explorerZone(x, y, visite, zone);

        char adversaire = (couleur == 'N') ? 'B' : 'N';

        // Si la zone est entourée uniquement par l’adversaire ET sa taille <= 10 → suicide
        if (zone.entourePar == adversaire && zone.taille <= 10) {
            return true;
        }

        return false; // Sinon, coup autorisé
    }

    // Classe auxiliaire interne pour stocker infos zone privatisée
    private class Zone {
        int taille = 0;
        char entourePar = '.'; // '.' = indéfini / non privatisée, 'N' ou 'B' sinon
    }

    // Parcours récursif de la zone vide connectée à (x,y)
    private void explorerZone(int x, int y, boolean[][] visite, Zone zone) {
        // Si hors limites, la zone touche le bord → non privatisée
        if (x < 0 || x >= taille || y < 0 || y >= taille) {
            zone.entourePar = '.';
            return;
        }

        if (visite[x][y]) {
            return;
        }
        visite[x][y] = true;

        char c = grille[x][y];
        if (c == '.') {
            // Case vide, on compte dans la zone
            zone.taille++;

            // Explore voisins haut, bas, gauche, droite
            explorerZone(x - 1, y, visite, zone);
            explorerZone(x + 1, y, visite, zone);
            explorerZone(x, y - 1, visite, zone);
            explorerZone(x, y + 1, visite, zone);

        } else {
            // Case occupée : vérifier si entourée par une seule couleur
            if (zone.entourePar == '.') {
                zone.entourePar = c;
            } else if (zone.entourePar != c) {
                // Entourée par plusieurs couleurs → zone non privatisée
                zone.entourePar = '.';
            }
        }
    }

    // Compte les points pour chaque joueur seulement pour les groupes >= 10 pièces
    public int[] compterPoints() {
        boolean[][] visite = new boolean[taille][taille]; // Grille pour suivre les cases déjà visitées
        int noir = 0;
        int blanc = 0;

        // Parcours complet du plateau
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                // Si la case n’a pas été visitée et contient une pièce noire ou blanche
                if (!visite[i][j] && (grille[i][j] == 'N' || grille[i][j] == 'B')) {
                    char couleur = grille[i][j];
                    // Calcule la taille du groupe connecté récursivement
                    int tailleGroupe = compterGroupe(i, j, couleur, visite);

                    // Si le groupe a au moins 10 pièces, on l’ajoute au score
                    if (tailleGroupe >= 10) {
                        if (couleur == 'N') {
                            noir += tailleGroupe;
                        } else {
                            blanc += tailleGroupe;
                        }
                    }
                }
            }
        }

        // Retourne un tableau avec les scores : [noir, blanc]
        return new int[]{noir, blanc};
    }

    // Méthode récursive pour compter la taille d’un groupe de pièces connectées
    private int compterGroupe(int x, int y, char couleur, boolean[][] visite) {
        // Condition d’arrêt : hors limites ou case déjà visitée ou case d’une autre couleur
        if (x < 0 || x >= taille || y < 0 || y >= taille) {
            return 0;
        }
        if (visite[x][y] || grille[x][y] != couleur) {
            return 0;
        }

        visite[x][y] = true; // Marque la case comme visitée
        int tailleGroupe = 1; // Compte la case courante

        // Explore récursivement les cases adjacentes (haut, bas, gauche, droite)
        tailleGroupe += compterGroupe(x - 1, y, couleur, visite);
        tailleGroupe += compterGroupe(x + 1, y, couleur, visite);
        tailleGroupe += compterGroupe(x, y - 1, couleur, visite);
        tailleGroupe += compterGroupe(x, y + 1, couleur, visite);

        return tailleGroupe;
    }
}
