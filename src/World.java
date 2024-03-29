import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class World {
	private static final int WHITE = 0;
	private static final int BLACK = 1;
	private static final int MINIMAX = 111;
	private static final int MINIMAX_ALPHA_BETA = 222;
	private static final int MINIMAX_PV = 333;
	private static final int MINIMAX_PV_WITH_NULL_WINDOW = 444;
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 3;		// rook can move towards <rookBlocks> blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;
	private int algorithmChoosed;



	public World(int algorithmChoosed){
		this.algorithmChoosed = algorithmChoosed;
		board = new String[rows][columns];
		
		/* represent the board
		
		BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";
		
		board[0][0] = "BP";
		board[0][columns-1] = "BP";
		
		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";
		
		// black king
		board[0][columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";
		
		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";
		
		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";
		
		// white king
		board[rows-1][columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
	}
	
	public void setMyColor(int myColor){
		this.myColor = myColor;
	}
	
	public String selectAction(boolean firstMove){
		String ai_choice;
		availableMoves = new ArrayList<>();
				
		if(myColor == WHITE)	// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();

		if(firstMove) // open move, unnecessary minimax algorithm decision
			return "5141";

		AgentSearch agent = new AgentSearch(Client.MINIMAX_DEPTH,this.myColor,MINIMAX_ALPHA_BETA);
		ai_choice = agent.decide(this);

		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();

		if(ai_choice == null) // in case of minimax failure
			return this.selectRandomAction();
		else
			return ai_choice;
	}
	
	private void whiteMoves(){
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++){
			for(int j=0; j<columns; j++){
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P")){	// it is a pawn

					// check if it can move towards the last row
					if(i-1 == 0 && (Character.toString(board[i-1][j].charAt(0)).equals(" ") 
							         || Character.toString(board[i-1][j].charAt(0)).equals("P"))){
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P")){
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0){
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j-1);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0){
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j+1);
						
						availableMoves.add(move);
					}
				}
				else if(secondLetter.equals("R")){	// it is a rook

					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++){
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++){
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++){
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else{// it is the king

					// check if it can move upwards
					if((i-1) >= 0){
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows){
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W")){
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0){
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns){
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	
	private void blackMoves(){
		String firstLetter = "";
		String secondLetter = "";
		String move = "";

		for(int i=0; i<rows; i++){
			for(int j=0; j<columns; j++){
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P")){	// it is a pawn

					// check if it is at the last row
					if(i+1 == rows-1 && (Character.toString(board[i+1][j].charAt(0)).equals(" ")
										  || Character.toString(board[i+1][j].charAt(0)).equals("P"))){
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P")){
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1){
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j-1);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1){
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j+1);
						
						availableMoves.add(move);
					}
				}
				else if(secondLetter.equals("R")){	// it is a rook

					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++){
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++){
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++){
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++){
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else{ // it is the king

					// check if it can move upwards
					if((i-1) >= 0){
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B")){
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows){
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B")){
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0){
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B")){
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns){
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B")){
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);
						}
					}
				}			
			}	
		}
	}
	
	private String selectRandomAction(){
		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());
		
		return availableMoves.get(x);
	}
	
	public double getAvgBFactor() {
		return nBranches / (double) nTurns;
	}
	
	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY){
		String chessPart = Character.toString(board[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;	
		// check if it is a move that has made a move to the last line
		if(chessPart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) ){
				board[x2][y2] = " ";	// in a case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}
		
		// otherwise
		if(!pawnLastRow){
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}
		
		// check if a prize has been added in the game
		if(prizeX != noPrize){
			board[prizeX][prizeY] = "P";
		}
	}

	public ArrayList<String> getAvailableMoves() {
		return availableMoves;
	}

	public ArrayList<String> getAvailableMovesAfterMove(ArrayList<String> state, int color){


		World tempWorld = new World(algorithmChoosed);
		tempWorld.setMyColor(color);

		for(int y=0; y<tempWorld.getColumns(); y++){
			for (int x=0; x<tempWorld.getRows(); x++){
				tempWorld.getBoard()[x][y] = this.board[x][y];
			}
		}

		for(int i=0; i<state.size(); i++){
			int x1 = Character.getNumericValue(state.get(i).charAt(0));
			int y1 = Character.getNumericValue(state.get(i).charAt(1));
			int x2 = Character.getNumericValue(state.get(i).charAt(2));
			int y2 = Character.getNumericValue(state.get(i).charAt(3));
			tempWorld.makeMove(x1, y1, x2, y2, 9, 9);
		}

		if(tempWorld.getMyColor() == WHITE)
			tempWorld.whiteMoves();
		else
			tempWorld.blackMoves();

		ArrayList<String> futureMoves = tempWorld.getAvailableMoves();

		return futureMoves;
	}

	public String[][] getBoardAfterMove(ArrayList<String> state){
		World tempWorld = new World(algorithmChoosed);

		for(int y=0; y<tempWorld.getColumns(); y++){
			for (int x=0; x<tempWorld.getRows(); x++){
				tempWorld.getBoard()[x][y] = this.board[x][y];
			}
		}

		for(int i=0; i<state.size(); i++){
			int x1 = Character.getNumericValue(state.get(i).charAt(0));
			int y1 = Character.getNumericValue(state.get(i).charAt(1));
			int x2 = Character.getNumericValue(state.get(i).charAt(2));
			int y2 = Character.getNumericValue(state.get(i).charAt(3));
			tempWorld.makeMove(x1, y1, x2, y2, 9, 9);
		}

		return tempWorld.getBoard();
	}

	public String[][] getBoard() {
		return board;
	}

	public int getMyColor() {
		return myColor;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}



}
