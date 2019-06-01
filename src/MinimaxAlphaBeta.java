import java.lang.*;
import java.util.ArrayList;
import java.util.Random;

public class MinimaxAlphaBeta {

    private static final int WHITE = 0;
    private static final int EMPTY = 0;
    private static final int BLACK = 1;
    private int depth;
    private int myColor;
    private int oppColor;
    private Random rand;
    private int[][] evalWhiteSidePawns;
    private int[][] evalWhiteCenterPawns;
    private int[][] evalWhiteRooks;
    private int[][] evalWhiteKing;
    private int[][] evalBlackSidePawns;
    private int[][] evalBlackCenterPawns;
    private int[][] evalBlackRooks;
    private int[][] evalBlackKing;

    public MinimaxAlphaBeta(int depth, int myColor) {

        this.depth = depth;
        this.myColor = myColor;

        initializeEvaluatedPawns();

        rand = new Random();

        if(myColor == WHITE)
            oppColor = BLACK;
        else
            oppColor = WHITE;
    }

    public String decide(World world){

        ArrayList<String> availableMoves = world.getAvailableMoves();
        ArrayList<String> state = new ArrayList<>();

        float[] costs = new float[availableMoves.size()];

        // get best move
        for(int i=0; i<availableMoves.size(); i++) {
            state.add(availableMoves.get(i));

            // Alpha - Beta Minimax
            float tmpCost = minValue(world,state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,1);

            // PV Search Minimax
            //float tmpCost = pvSearch(world,state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,1,oppColor);

            // PV Search with Null-Window Search Minimax
            //float tmpCost = pvSearchNW(world,state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,1,oppColor);

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

        else
            return availableMoves.get(maxIndex);


    }

    private float minValue(World w, ArrayList<String> state, float alpha, float beta, int depth){

        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b);
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
            return evaluateWorld(w, b);
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

    private float pvSearch(World w, ArrayList<String> state, float alpha, float beta, int depth, int color){

        float score;

        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b);
        }

        boolean bSearchPV = colorToBool(color);

        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,color);

        for(int i=0; i<moves.size(); i++){

            state.add(moves.get(i));

            if(bSearchPV)
                score = -pvSearch(w, state, -beta, -alpha, depth + 1, oppositeColor(color));
            else{
                score = -pvSearch(w, state, -alpha-1, -alpha, depth+1, oppositeColor(color));
                if(score > alpha) // fail-soft -> re-search
                    score = -pvSearch(w, state, -beta, -alpha, depth+1 , oppositeColor(color));
            }

            state.remove(state.lastIndexOf(moves.get(i)));

            if(score >= beta)
                return beta; // fail hard -> beta cut-off

            if(score > alpha)
                alpha = score; // like Minimax

            bSearchPV = colorToBool(oppositeColor(color));

        }

        return alpha; // fail hard -> alpha cut-off

    }

    private float pvSearchNW(World w, ArrayList<String> state, float alpha, float beta, int depth, int color){

        float score;

        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b);
        }

        boolean bSearchPV = colorToBool(color);

        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,color);

        for(int i=0; i<moves.size(); i++){

            state.add(moves.get(i));

            if(bSearchPV)
                score = -pvSearch(w, state, -beta, -alpha, depth + 1, oppositeColor(color));
            else{
                score = -zwSearch(w, state, -alpha, depth+1, oppositeColor(color));
                if(score > alpha) // fail-soft -> re-search
                    score = -pvSearch(w, state, -beta, -alpha, depth+1 , oppositeColor(color));
            }

            state.remove(state.lastIndexOf(moves.get(i)));

            if(score >= beta)
                return beta; // fail hard -> beta cut-off

            if(score > alpha)
                alpha = score; // like Minimax

            bSearchPV = colorToBool(oppositeColor(color));

        }

        return alpha; // fail hard -> alpha cut-off

    }

    private float zwSearch(World w, ArrayList<String> state, float beta, int depth, int color){

        float score;
        float alpha = beta - 1;

        if(depth > this.depth){
            String [][] b = w.getBoardAfterMove(state);
            return evaluateWorld(w, b);
        }

        ArrayList<String> moves = w.getAvailableMovesAfterMove(state,color);

        for(int i=0; i<moves.size(); i++){
            state.add(moves.get(i));
            score = -zwSearch(w, state, 1-beta, depth+1, oppositeColor(color));
            state.remove(state.lastIndexOf(moves.get(i)));
            if(score >= beta)
                return beta; // fail-hard beta-cutoff
        }
        return alpha; // fail-hard return alpha
    }

    private int evaluateWorld(World world, String[][] board){
        int totalEvaluation = 0;
        for(int i=0; i<world.getRows(); i++){
            for(int j=0; j<world.getColumns(); j++){
                totalEvaluation += getCellValue(board[i][j], i, j);
            }
        }
        return totalEvaluation;
    }

    private int getCellValue(String piece, int i, int j){
         int color = WHITE;

        try {
            char firstLetter = piece.charAt(0);
            char secondLetter = piece.charAt(1);

            if(firstLetter == 'W')
                color = WHITE;
            else if(firstLetter == 'B')
                color = BLACK;
            if(firstLetter == ' ')      // empty cell
                return 0;


            int absoluteValue;

            if(secondLetter == 'P'){      // pawn
                if(color == WHITE)
                    absoluteValue = 2 + evalWhiteCenterPawns[i][j];
                else
                    absoluteValue = 2 + evalBlackCenterPawns[i][j];
            }

            else if(secondLetter == 'R') {   // rook
                if(color == WHITE)
                    absoluteValue = 10 + evalWhiteRooks[i][j];
                else
                    absoluteValue = 10 + evalBlackRooks[i][j];
            }

            else{ // king
                if(color == WHITE)
                    absoluteValue = 50 + evalWhiteKing[i][j];
                else
                    absoluteValue = 50 + evalBlackKing[i][j];
            }


            if(firstLetter == 'P')         // prize
                absoluteValue = 5;

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

    private void initializeEvaluatedPawns(){
        evalWhiteSidePawns = new int[][]{
                {1, 2, 3, 2, 1},
                {-1, 1, 2, 1, -1},
                {-1, 1, 2, 1, -1},
                {0, 1, 2, 1, 0},
                {0, 1, 2, 1, 0},
                {-1, 2, 0, 2, -1},
                {-1, 0, 0, 0, -1}
        };

        evalWhiteCenterPawns = new int[][]{
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0},
                {0, 0, -1, 0, 0},
                {0, 0, -1, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        evalWhiteRooks = new int[][]{
                {0, 0, 0, 0, 0},
                {0, 0, -1, 0, 0},
                {0, 0, -1, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        evalWhiteKing = new int[][]{
                {-10, -10, -10, -10, -10},
                {-8, -8, -10, -8, -8},
                {-6, -7, -7, -7, -6},
                {-4, -4, -5, -4, -4},
                {-1, -1, -1, -1, -1},
                {1, 1, 0, 1, 1},
                {1, 1, 0, 1, 1}
        };

        // reverse arrays for black pawns
        evalBlackSidePawns = new int[7][5];
        evalBlackCenterPawns = new int[7][5];
        evalBlackRooks = new int[7][5];
        evalBlackKing = new int[7][5];

        for(int i=0; i<7; i++){
            for(int j=0; j<5; j++){
                evalBlackSidePawns[i][j] = evalWhiteSidePawns[6-i][j];
                evalBlackCenterPawns[i][j] = evalWhiteCenterPawns[6-i][j];
                evalBlackRooks[i][j] = evalWhiteRooks[6-i][j];
                evalBlackKing[i][j] = evalWhiteKing[6-i][j];
            }
        }
    }

    private int oppositeColor(int color){

        if(color == WHITE){
            return BLACK;
        }
        else
            return WHITE;
    }

    private boolean colorToBool(int color){
        if(color == WHITE)
            return true;
        else
            return false;
    }

}
