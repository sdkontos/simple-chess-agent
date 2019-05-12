import java.util.ArrayList;

public class Minimax {

    private final int INFINITY = 100000;
    private int depth;
    private boolean white;
    private World world;
    private String bestMove;
    private int bestMoveValue;

    public Minimax(int depth, boolean white, World world) {
        this.depth = depth;
        this.white = white;
        this.world = world;
    }

    public void applyMinimaxRoot(boolean white, int depth){
        this.white = white;
        ArrayList<String> availableMoves = new ArrayList<>();
        availableMoves = world.getAvailableMoves();
        this.bestMoveValue = - INFINITY;

        for(int i=0; i<availableMoves.size(); i++){
            String newMove = availableMoves.get(i);

        }

    }



    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
