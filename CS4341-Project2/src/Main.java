public class Main {

    //Game board
    static Board board = new Board();

    //Main function
    public static void main(String[] args) throws Exception {
        printPiece(5, 4);
        board.addPeice(5, 4, 1);
        printPiece(5, 4);
    }

    //Prints the piece of the board at the given coordinates
    public static void printPiece(int x, int y) {
        System.out.println("Piece at " + x + ", " + y + ": " + board.getPiece(x, y));
    }
}
