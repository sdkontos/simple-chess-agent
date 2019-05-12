import org.jetbrains.annotations.NotNull;
import java.lang.*;
import java.util.ArrayList;

public class Minimax {

    private final int INFINITY = 100000;
    private int depth = 3;
    private boolean myTurn;
    private World world;
    private String bestMove;
    private int bestMoveValue;

    public Minimax(boolean myTurn, World world) {
        this.depth = 3;
        this.myTurn = myTurn;
        this.world = world;
    }

    public String applyMinimaxRoot(){
        ArrayList<String> availableMoves;
        availableMoves = world.getAvailableMoves();
        this.bestMoveValue = - INFINITY;

        for(int i=0; i<availableMoves.size(); i++){
            String newMove = availableMoves.get(i);
            String[] undoInfo = world.makeMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),9,9);
            int value = minimax(depth-1,myTurn);
            world.undoMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),undoInfo);
            if(value >= bestMoveValue){
                bestMoveValue = value;
                bestMove = newMove;
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean myTurn){
        if(depth == 0){
            return evaluateWorld(world);
        }
        ArrayList<String> availableMoves;
        availableMoves = world.getAvailableMoves();

        if(myTurn){
            int bestMoveVal = -INFINITY;
            for(int i=0; i<availableMoves.size(); i++){
                String newMove = availableMoves.get(i);
                String[] undoInfo = world.makeMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),9,9);
                this.bestMoveValue = Math.max(bestMoveVal,minimax(depth-1, true));
                world.undoMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),undoInfo);
            }
            return bestMoveValue;
        }
        else{
            int bestMoveVal = -INFINITY;
            for(int i=0; i<availableMoves.size(); i++){
                String newMove = availableMoves.get(i);
                String[] undoInfo = world.makeMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),9,9);
                this.bestMoveValue = Math.min(bestMoveVal,minimax(depth-1, false));
                world.undoMove(Character.getNumericValue(newMove.charAt(0)),Character.getNumericValue(newMove.charAt(1)),Character.getNumericValue(newMove.charAt(2)),Character.getNumericValue(newMove.charAt(3)),undoInfo);
            }
            return bestMoveValue;
        }
    }

    private int evaluateWorld(@NotNull World world){
        String[][] board = world.getBoard();
        int totalEvaluation = 0;
        for(int i=0; i<world.getRows(); i++){
            for(int j=0; j<world.getColumns(); j++){
                totalEvaluation += getCellValue(world.getBoard()[i][j]);
            }
        }
        return totalEvaluation;
    }

    private int getCellValue(@NotNull String piece){
        String firstLetter = Character.toString(piece.charAt(0));
        int firstLetterINT;
        String secondLetter = Character.toString(piece.charAt(1));

        if(secondLetter.equals(" "))      // empty cell
            return 0;
        

        int absoluteValue;

        if(secondLetter.equals("P"))      // pawn
            absoluteValue = 1;
        
        else if(secondLetter.equals("R")) // rook
            absoluteValue = 3;
        
        else                           // king
            absoluteValue = 9;
        
        if(firstLetter.equals("P"))          // prize
            absoluteValue = 1;
        

        if(firstLetter.equals("W"))
            firstLetterINT = 0;
        
        else
            firstLetterINT = 1;
        

        if(firstLetterINT == world.getMyColor())
            return absoluteValue;
        
        else
            return -absoluteValue;
        

    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
