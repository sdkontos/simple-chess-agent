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

    private int evaluateWorld(World world){
        String[][] board = world.getBoard();
        int totalEvaluation = 0;
        for(int i=0; i<world.getRows(); i++){
            for(int j=0; j<world.getColumns(); j++){
                totalEvaluation += getCellValue(world.getBoard()[i][j]);
            }
        }
        return totalEvaluation;
    }

    private int getCellValue(String piece){
        String color = Character.toString(piece.charAt(0));
        int colorINT;
        String chessPart = Character.toString(piece.charAt(1));

        if(chessPart.equals(" ")){      // empty cell
            return 0;
        }

        int absoluteValue;

        if(chessPart.equals("P")){      // pawn
            absoluteValue = 1;
        }
        else if(chessPart.equals("R")){ // rook
            absoluteValue = 3;
        }
        else{                           // king
            absoluteValue = 9;
        }

        if(color.equals("W")){
            colorINT = 0;
        }
        else{
            colorINT = 1;
        }

        if(colorINT == world.getMyColor()){
            return absoluteValue;
        }
        else{
            return -absoluteValue;
        }

    }



    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
