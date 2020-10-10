import java.io.*; 
public class Board {
    
    //Main board
    private int[][] board;

    //Creates 15x15 board (initialized with 0 in every spot)
    public Board() {
        board = new int[15][15];
    }

    //Adds a piece to the board (given the input from move_file EX: teamname D 4)
        //String move: move_file text
        //Int color: 1 for our piece, 2 for opponents
    public void addPiece(String move, int color) {
        if(move != null) {
            String[] moves = move.split(" ");
            int x = translateLetter(moves[1]);
            int y = Integer.parseInt(moves[2]) - 1;
            addPiece(x, y, color);
        }
    }

    //prints our local board, used for bug fixing
    public void printBoard() {
        for (int y = 0; y < 15; y++) {
            System.out.print("[");
            for (int x = 0; x < 15; x++) {
                if(x < 14) {
                    System.out.print(board[x][y] + ", ");
                } else {
                    System.out.print(board[x][y] + "]\n");
                }
            }
        }
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
    	      			FileWriter myWriter = new FileWriter(teamname + ".txt");
    	    	      	myWriter.write(teamname + " " + row + " " + col);
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
    
    // Translates letter to number in array
    public static int translateLetter(String letter) {
        switch(letter) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            case "F":
                return 5;
            case "G":
                return 6;
            case "H":
                return 7;
            case "I":
                return 8;
            case "J":
                return 9;
            case "K":
                return 10;
            case "L":
                return 11;
            case "M":
                return 12;
            case "N":
                return 13;
            case "O":
                return 14;
            default:
                return -1;
        }
    }

    //Translates number in board array to coorresponding letter for writing to move_file
    public static String translateInt(int num) {
        switch(num) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            case 8:
                return "I";
            case 9:
                return "J";
            case 10:
                return "K";
            case 11:
                return "L";
            case 12:
                return "M";
            case 13:
                return "N";
            case 14:
                return "O";
            default:
                return "Z";
        }
    }
}
