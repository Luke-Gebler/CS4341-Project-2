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
}
