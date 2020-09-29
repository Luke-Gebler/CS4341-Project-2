public class Board {
    
    //Main board
    private int[][] board;

    //Creates 15x15 board (initialized with 0 in every spot)
    public Board() {
        board = new int[15][15];
    }

    //Adds a piece to the board if no piece exists there yet(board[x][y] == 0)
        //Currently accepts any # for piece parameter since we haven't decided what numbers we're accpeting (probably 1 and 2 for the two players?)
    public boolean addPiece(int x, int y, int piece) {
        boolean flag = false;
        if(board[x][y] == 0) {
            board[x][y] = piece;
            flag = true;
        }
        return flag;
    }

    //Returns the piece (int) of the given coordinate
    public int getPiece(int x, int y) {
        int piece = 0;
        piece = board[x][y];
        return piece;
    }
    
    //untested. This is a function for creating a move file
    //teamname: the name of our team
    //row: the letter of the row that has been selected (might be able to have this be a number but map it to some numbers?)
    //col: the number of the column that has been selected
    public void printMove(String teamname, String row, String col)//
    {
    	try {
    	      	File myObj = new File(teamname + ".txt");
    	      		if (myObj.createNewFile())
    	      		{
    	      			System.out.println("File created: " + myObj.getName()); 
    	      			FileWriter myWriter = new FileWriter(Teamname + ".txt");
    	    	      	myWriter.write(teamaname + " " + row + " " + col);
    	    	      	myWriter.close();
    	    	      	System.out.println("Successfully wrote to the file.");
    	      		} 
    	      		else 
    	      		{
    	      			System.out.println("An error creating the file has occurred");
    	      		}
    		} 
    	catch (IOException e) 
    		{
    	      System.out.println("An error writing the file has occurred.");
    	      e.printStackTrace();
    	    }
    	
    }
    
}
