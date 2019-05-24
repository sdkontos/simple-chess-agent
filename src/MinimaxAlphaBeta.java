import java.lang.*;
import java.util.ArrayList;
import java.util.Random;

public class MinimaxAlphaBeta {

    private static final int WHITE = 0;
    private static final int EMPTY = 0;
    private static final int BLACK = 1;
    private final int INFINITY = 100000;
    private int depth;
    private int myColor;
    private int oppColor;
    Random rand;


    public MinimaxAlphaBeta(int depth, int myColor) {

        this.depth = depth;
        this.myColor = myColor;

        rand = new Random();

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
            float tmpCost = minValue(world,state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,1);
            costs[i] = tmpCost;
            state.remove(state.lastIndexOf(availableMoves.get(i)));
        }

        int maxIndex = -1;
        float max = Float.NEGATIVE_INFINITY;
        for(int i=0; i<availableMoves.size(); i++){
            if(costs[i] >= max){
                if (Math.abs(costs[i] - max) < 0.1) // add a little random element
                    if (rand.nextBoolean())
                        continue;
                max = costs[i];
                maxIndex = i;
            }
        }

        if(maxIndex == -1)
            return null;
        else{
            String bestMove = availableMoves.get(maxIndex);
            return bestMove;
        }

    }

    private float minValue(World w, ArrayList<String> state, float alpha, float beta, int depth){

        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b, oppColor);
        }

        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,oppColor);

        if(moves.size() == EMPTY)
            return Float.POSITIVE_INFINITY;

        for(int i=0; i<moves.size(); i++){

            state.add(moves.get(i));
            float tmp = maxValue(w, state, alpha, beta,depth+1);
            state.remove(state.lastIndexOf(moves.get(i)));

            if(tmp < beta)
                beta = tmp;

            if(beta <= alpha)
                break;
        }
        return beta;
    }

    private float maxValue(World w, ArrayList<String> state, float alpha, float beta, int depth){
        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b, myColor);
        }

        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,myColor);
        if(moves.size() == EMPTY)
            return Float.NEGATIVE_INFINITY;

        for(int i=0; i<moves.size(); i++){
            state.add(moves.get(i));
            float tmp = minValue(w, state, alpha, beta, depth+1);
            state.remove(state.lastIndexOf(moves.get(i)));

            if(tmp > alpha)
                alpha = tmp;

            if(beta <= alpha)
                break;
        }
        return alpha;
    }

    private int evaluateWorld(World world, String[][] board, int color){
        int totalEvaluation = 0;
        for(int i=0; i<world.getRows(); i++){
            for(int j=0; j<world.getColumns(); j++){
                totalEvaluation += getCellValue(board[i][j], color);
            }
        }
        return totalEvaluation;
    }

    private int getCellValue(String piece, int color){

        try {
            char firstLetter = piece.charAt(0);
            char secondLetter = piece.charAt(1);

            if(secondLetter == ' ')      // empty cell
                return 0;


            int absoluteValue;

            if(secondLetter == 'P')        // pawn
                absoluteValue = 1;

            else if(secondLetter == 'R')   // rook
                absoluteValue = 3;

            else                           // king
                absoluteValue = 9;

            if(firstLetter == 'P')         // prize
                absoluteValue = 10;

            if(color == myColor){
                return absoluteValue;
            }
            else{
                return -absoluteValue;
            }
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
