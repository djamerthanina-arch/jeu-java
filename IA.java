
import java.util.ArrayList;
import java.util.Random;

public class IA {

    private Random random = new Random();

    public int[] jouer(Plateau plateau, char couleur) {
        ArrayList<int[]> coupsPossibles = new ArrayList<>();

        for (int i = 0; i < plateau.getTaille(); i++) {
            for (int j = 0; j < plateau.getTaille(); j++) {
                if (plateau.estVide(i, j) && !plateau.estSuicide(i, j, couleur)) {
                    coupsPossibles.add(new int[]{i, j});
                }
            }
        }

        if (coupsPossibles.isEmpty()) {
            return new int[]{-1, -1};
        }

        return coupsPossibles.get(random.nextInt(coupsPossibles.size()));
    }
}
