import java.lang.*;
import java.util.ArrayList;

public class Minimax {

    private static final int WHITE = 0;
    private static final int EMPTY = 0;
    private static final int BLACK = 1;
    private final int INFINITY = 100000;
    private int depth;
    private int myColor;
    private int oppColor;


    public Minimax(int depth, int myColor) {

        this.depth = depth;
        this.myColor = myColor;

        if(myColor == WHITE)
            oppColor = BLACK;
        else
            oppColor = WHITE;
    }

    public String getMinimaxDecision(World world){

        ArrayList<String> availableMoves = world.getAvailableMoves();
        ArrayList<String> state = new ArrayList<>();

        float[] costs = new float[availableMoves.size()];

        // get best move
        for(int i=0; i<availableMoves.size(); i++) {
            state.add(availableMoves.get(i));
            float tmpCost = minValue(world,state,2);
            costs[i] = tmpCost;
            state.remove(state.lastIndexOf(availableMoves.get(i)));
        }

        int maxIndex = -1;
        float max = Float.NEGATIVE_INFINITY;
        for(int i=0; i<availableMoves.size(); i++){
            if(costs[i] >= max){
                max = costs[i];
                maxIndex = i;
            }
        }

        if(maxIndex == -1)
            return null;
        else
            return availableMoves.get(maxIndex);
    }

    private float minValue(World w, ArrayList<String> state, int depth){

        if(depth >= this.depth)
            return evaluateWorld(w,oppColor);

        float min = Float.POSITIVE_INFINITY;
        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,oppColor);
        if(moves.size() == EMPTY)
            return Float.POSITIVE_INFINITY;

        for(int i=0; i<moves.size(); i++){
            state.add(moves.get(i));
            float tmp = maxValue(w, state, depth+1);
            if(tmp < min)
                min = tmp;
            state.remove(state.lastIndexOf(moves.get(i)));
        }

        return min;
    }

    private float maxValue(World w, ArrayList<String> state, int depth){
        if(depth >= this.depth)
            return evaluateWorld(w,myColor);

        float max = Float.NEGATIVE_INFINITY;
        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,oppColor);
        if(moves.size() == EMPTY)
            return Float.NEGATIVE_INFINITY;

        for(int i=0; i<moves.size(); i++){
            state.add(moves.get(i));
            float tmp = minValue(w, state, depth+1);
            if(tmp > max)
                max = tmp;
            state.remove(state.lastIndexOf(moves.get(i)));
        }
        return max;
    }

    private int evaluateWorld(World world, int color){
        int totalEvaluation = 0;
        for(int i=0; i<world.getRows(); i++){
            for(int j=0; j<world.getColumns(); j++){
                totalEvaluation += getCellValue(world.getBoard()[i][j],color);
            }
        }
        return totalEvaluation;
    }

    private int getCellValue(String piece, int color){
        //System.out.println("\nPiece " + piece + " C: " + color);
        try {
            String firstLetter = Character.toString(piece.charAt(0));
            String secondLetter = Character.toString(piece.charAt(1));

            if(secondLetter.equals(" "))      // empty cell
                return 0;


            int absoluteValue;

            if(secondLetter.equals("P"))        // pawn
                absoluteValue = 1;

            else if(secondLetter.equals("R"))   // rook
                absoluteValue = 3;

            else                                // king
                absoluteValue = 9;

            if(firstLetter.equals("P"))         // prize
                absoluteValue = 1;

            if(color == myColor)
                return absoluteValue;

            else
                return -absoluteValue;
        }
        catch (StringIndexOutOfBoundsException s){
            return 0;
        }
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
