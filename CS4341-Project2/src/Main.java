import java.io.*;  // Import the File class
import java.nio.file.*;
import java.util.*; //Wait for a second after making a move in order to give ref time to delete gomokugamers.go

public class Main {

    //Keep track of time
    public static long startTime = System.nanoTime();

    //True when game has not ended
    static boolean playing = true;

    static int depth = 5;

    //Main function
    public static void main(String[] args) throws Exception {
        Board board = new Board();
        Minimax minimax = new Minimax();
        String workingDir = System.getProperty("user.dir"); //Current working directory
        String dirPath = workingDir + "\\gomokugamers.go"; //Path to gomokugamers.go (our team name for)
        String dirEnd = workingDir + "\\end_game"; //Path to end_game file
        String filePath = workingDir + "\\move_file"; //Path to move_file
        Path path = Paths.get(dirPath);
        Path end = Paths.get(dirEnd);

        //Constantly loop waiting for gomokugamers.go to appear
        while(playing) {
            startTime = System.nanoTime();
            if(checkMyTurn(path, end)) {
                FileReader reader = new FileReader(new File(filePath));
                BufferedReader bufferedReader = new BufferedReader(reader);
                String move = bufferedReader.readLine(); //Get opponents move
                reader.close();
                board.addPiece(move, 2); //Add opponents move to our local game board (in order to calculate next best move)
                board.printBoard();

                ArrayList<Coordinate> coords = minimax.getValidMoves(board);
                for(int i = 0; i < Math.floor(coords.size() / 10.0); i++) {
                    if(depth > 1) {
                        depth--;
                    }
                }
                
                Node node = minimax.minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                board.addPiece(node.x, node.y, 1);
                
                FileWriter writer = new FileWriter(new File(filePath));
                String letter = board.translateInt(node.x);
                writer.write("gomokugamers " + letter + " " + (node.y + 1)); //write move to file
                writer.close();
                //board.addPiece(nextMove(), 1); <--- Add our move to our local game board 
                System.out.println("Wrote move to move_file");
                board.printBoard();
                while(Files.exists(path)) { //Waits for ref to delete file before looking for it again
                    continue;
                }
                System.out.println("Go file deleted by ref, waiting for it to be replaced.");
            }
        }
        System.out.println("Game finished");
    }

    //Checks if end_game file is in directory, if it is, end the program
    //Else, return whether gomokugamers.go is in the directory
    public static boolean checkMyTurn(Path path, Path end) {
        if(Files.exists(end)) {
            playing = false;
            return false; 
        }
        return Files.exists(path);
    }
    
}
