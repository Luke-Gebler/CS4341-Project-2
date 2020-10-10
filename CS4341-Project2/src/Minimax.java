import java.util.ArrayList;

public class Minimax {

    int fourScore = 1000;
    int threeScore = 100;
    int twoScore = 5; //Somewhat of a bug... twos will get counted twice so their value was halfed
    private int winScore = Integer.MAX_VALUE;
    
    
    long nineSecondsNano = 9000000000L;
    long eightSecondsNano = 8000000000L;

    /* Main minimax function
        Board: current board
        depth: how many layers deep you want to search
        alpha: initialized at negative infinity
        beta: initialized at positive infinity
        maximizingPlayer: true if our turn, false otherwise

    */ 

    //WORK IN PROGRESS:
        //currently, the scorePosition(), getValidMoves(), is_terminal(), boardFull(), copyBoard() are all working as intended
            //The only thing not working is the minimax function... unknown reason
    public Node minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int x = 0;
        int y = 0;

        //Depth reached or terminal state reached (5 in a row)
        if (depth == 0 || is_terminal(board)) {
            return new Node(scorePosition(board, maximizingPlayer), x, y);
        }
        //Game is over. Board is full
        else if(boardFull(board)) {
            return new Node(0, x, y);
        }
        ArrayList<Coordinate> validMoves = getValidMoves(board);
        if(validMoves.size() == 0) {
            return new Node(scorePosition(board, maximizingPlayer), x, y);
        }

        //1 second left, purge every child and make depth = 1 (pretty much just take whatever move is best so far)
        if(System.nanoTime() - Main.startTime >= nineSecondsNano) {
            for(int i = 1; i < validMoves.size(); i++) {
                validMoves.remove(1);
            }
            depth = 1;
        }
        
        if (maximizingPlayer) {
            int max = Integer.MIN_VALUE;
            for(Coordinate c : validMoves) {
                Board b_copy = copyBoard(board);
                b_copy.addPiece(c.x, c.y, 1);
                int eval = minimax(b_copy, depth - 1, alpha, beta, false).score;
                max = Math.max(max, eval);
                if(max == eval) {
                    x = c.x;
                    y = c.y;
                }                

                alpha = Math.max(alpha, eval);
                
                if(beta <= alpha) {
                    break;
                }
            }
            return new Node(max, x, y);
        }
        //Minimizing player (opponent)
        else {
            int min = Integer.MAX_VALUE;
            for(Coordinate c : validMoves) {
                Board b_copy = copyBoard(board);
                b_copy.addPiece(c.x, c.y, 2);
                int eval = minimax(b_copy, depth - 1, alpha, beta, true).score;
                min = Math.min(min, eval);
                if(min == eval) {
                    x = c.x;
                    y = c.y;
                }
                
                beta = Math.max(beta, eval);
                
                if(beta <= alpha) {
                    break;
                }
            }
            return new Node(min, x, y);
        }
    }

  

    //Gives score based on current position of the board
    public int  scorePosition(Board board, boolean maximizingPlayer) {
        double maxScore = getScore(board, true, maximizingPlayer);  //b
        double minScore = getScore(board, false, maximizingPlayer);

        if(maxScore == 0)
            maxScore = 1.0;

        int finalScore = (int)(minScore / maxScore);

        return finalScore;
    }


    public  int getScore(Board board, boolean forStartingPlayer, boolean oppositionTurn)
    {

        int[][] boardArray = board.getBoard();

        int hSCore = getHorizontalScore(boardArray, forStartingPlayer, oppositionTurn);
        int vScore = getVerticalScore(boardArray, forStartingPlayer, oppositionTurn);
        int dScore =  getDiagonalScore(boardArray, forStartingPlayer, oppositionTurn);
        return hSCore + vScore + dScore;
    }




    public   int getDiagonalScore(int[][] board, boolean forStartingPlayer, boolean oppositionTurn ) {

        int diagonalScore = 0;
        int consecutivePieces = 0;
        int potentialBlocks = 2;


        for (int n = 0; n <= 2 * (board.length - 1); n++) { //bottom left
            int diagStart = Math.max(0, n - board.length + 1);
            int diagEnd = Math.min(board.length - 1, n);

            for (int i = diagStart; i <= diagEnd; ++i)
            {
                int j = n - i;

                if(board[i][j] == (forStartingPlayer ? 2 : 1)) {
                    consecutivePieces += 1;
                }
                else if(board[i][j] == 0)
                {
                    if(consecutivePieces > 0)
                    {

                        potentialBlocks -= 1;
                        diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                        potentialBlocks = 1;
                        consecutivePieces = 0;

                    }
                    else {
                        potentialBlocks = 1;
                    }
                }
                else if(consecutivePieces > 0) {
                    diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                    potentialBlocks = 2;
                    consecutivePieces = 0;

                }
                else {
                    potentialBlocks = 2;
                }

            }
            if(consecutivePieces > 0) {
                diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);

            }
            potentialBlocks = 2;
            consecutivePieces = 0;

        }

        for (int n = 1-board.length; n < board.length; n++)
        { //starts top left
            int diagStart = Math.max(0, n);
            int diagEnd = Math.min(board.length + n - 1, board.length-1);
            for (int i = diagStart; i <= diagEnd; ++i) {
                int j = i - n;

                if(board[i][j] == (forStartingPlayer ? 2 : 1)) {
                    consecutivePieces++;
                }
                else if(board[i][j] == 0)
                {
                    if(consecutivePieces > 0) {
                        potentialBlocks--;
                        diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                        potentialBlocks = 1;
                        consecutivePieces = 0;

                    }
                    else {
                        potentialBlocks = 1;
                    }
                }
                else if(consecutivePieces > 0) {
                    diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                    potentialBlocks = 2;
                    consecutivePieces = 0;

                }
                else {
                    potentialBlocks = 2;
                }

            }
            if(consecutivePieces > 0) {
                diagonalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);

            }
            potentialBlocks = 2;
            consecutivePieces = 0;


        }
        return diagonalScore;
    }

    public   int getVerticalScore(int[][] board, boolean forStartingPlayer, boolean oppositionTurn )
    {
        int potentialBlocks = 2;
        int consecutivePieces = 0;

        int verticalScore = 0;

        for(int i=0; i<board[0].length; i++) {
            for(int j=0; j<board.length; j++) {
                if(board[j][i] == (forStartingPlayer ? 2 : 1)) {
                    consecutivePieces++;
                }
                else if(board[j][i] == 0) {
                    if(consecutivePieces > 0) {
                        potentialBlocks--;
                        verticalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                        consecutivePieces = 0;
                        potentialBlocks = 1;
                    }
                    else {
                        potentialBlocks = 1;
                    }
                }
                else if(consecutivePieces > 0) {
                    verticalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                    potentialBlocks = 2;
                    consecutivePieces = 0;

                }
                else {
                    potentialBlocks = 2;
                }
            }
            if(consecutivePieces > 0) {
                verticalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);

            }
            potentialBlocks = 2;
            consecutivePieces = 0;


        }
        return verticalScore;
    }

    public  int getHorizontalScore(int[][] board, boolean forStartingPlayer, boolean oppositionTurn )
    {
        int potentialBlocks = 2;
        int consecutivePieces = 0;

        int horizontalScore = 0;

        for(int i= 0; i < board.length; i++)
        {
            for(int j=0; j<board[0].length; j++)
            {
                if(board[i][j] == (forStartingPlayer ? 2 : 1))
                {
                    consecutivePieces++;
                }

                else if(board[i][j] == 0)
                {
                    if(consecutivePieces > 0) {
                        potentialBlocks--;
                        horizontalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);

                        potentialBlocks = 1;
                        consecutivePieces = 0;

                    }
                    else {
                        potentialBlocks = 1;
                    }
                }
                else if(consecutivePieces > 0) {
                    horizontalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);
                    potentialBlocks = 2;
                    consecutivePieces = 0;

                }
                else {
                    potentialBlocks = 2;
                }
            }
            if(consecutivePieces > 0) {
                horizontalScore += getScoreForPieces(consecutivePieces, forStartingPlayer == oppositionTurn, potentialBlocks);

            }
            potentialBlocks = 2;

            consecutivePieces = 0;

        }
        return horizontalScore;
    }

    public   int getScoreForPieces(int pieceCount, boolean currentTurn, int potentialBlocks) {

        final int guaranteedWinScore = 900000; //4 in a row cannot be blocked! arbitrary large number

        if(potentialBlocks == 2 && pieceCount < 5) return 0;
        switch(pieceCount)
        {
            case 5:
                {
                return winScore;
            }
            case 4: {
                if(currentTurn) return guaranteedWinScore;
                else {
                    if(potentialBlocks == 0) return guaranteedWinScore/4;
                    else return 200;
                }
            }
            case 3: {
                if(potentialBlocks == 0) {
                    if(currentTurn) return 50000; //could adjust these values
                    else return 200;
                }
                else {
                    if(currentTurn) return 10;
                    else return 5;
                }
            }
            case 2: {
                if(potentialBlocks == 0) {
                    if(currentTurn) return 7;
                    else return 5;
                }
                else {
                    return 3;
                }
            }
            case 1: {
                return 1;
            }
        }
        return winScore * 2;
    }
   
    
      /*
    //Gives score based on current position of the board
    public int scorePosition(Board board, boolean maximizingPlayer) {
        int finalScore = 0;
        maximizingPlayer = !maximizingPlayer; //We want to know who last moved
        //If there's a 5 in a row
        if(is_terminal(board)) {
            if(maximizingPlayer) {
                //We win
                return Integer.MAX_VALUE;
            } else {
                //We lose
                return Integer.MIN_VALUE;
            }
        }

        int fours = 0; //number of 4 in a rows we have (still live AKA. still able to become a 5 in a row)
        int threes = 0; //number of 3 in a rows we have (still live)
        int twos = 0; //number of 2 in a rows we have (still live)

        int oFours = 0; //number of opponent 4 in a rows (still live)
        int oThrees = 0; //number of opponent 3 in a rows (still live)
        int oTwos = 0; //number of opponent 2 in rows (still live)

        int[] total = sequencesOnBoard(board);

        fours = total[0];
        oFours = total[1];
        threes = total[2];
        oThrees = total[3];
        twos = total[4] / 2;
        oTwos = total[5] / 2;

        finalScore = (int)(finalScore + (oFours * fourScore * -1.1) + (oThrees * threeScore * -1.1) + (oTwos * twoScore * -1.1)); //Adding all opponent scores AUGMENTED for defensive play currently
        finalScore = finalScore + (fours * fourScore) + (threes * threeScore) + (twos * twoScore); //Adding our points

        return finalScore;
    }

    
    
    //Scores the current board based on # of 4 in a row, 3 in a row, and 2 in a row by each player.
    private int[] sequencesOnBoard(Board board) {
        int[] total = new int[6];
        int fours = 0;
        int oFours = 0;
        int threes = 0;
        int oThrees = 0;
        int twos = 0;
        int oTwos = 0;
        for(int y = 0; y < 15; y++) {
            for(int x = 0; x < 15; x++) {
                int curPiece = board.getPiece(x, y);
                int counter = 0;
                if(x > 0 && x < 14 && y > 0 && y < 14 && curPiece != 0) {
                    if(y > 3) {
                        //Up
                        counter = 0;
                        for (int i = 1; i < 5; i++) {
                            if(y - i >= 0) {
                                int checkPiece = board.getPiece(x, y - i);
                                if(checkPiece == curPiece) {
                                    counter++;
                                }
                                //Edge-case catcher
                                if (i == 2 && counter == 2 && y == 13){
                                    if(board.getPiece(x, y + 1) == curPiece) {
                                        if(curPiece == 1) {
                                            fours++;
                                        } else {
                                            oFours++;
                                        }
                                    }
                                }
                                if (i == 1 && counter == 1){
                                    if(y + 1 < 15) {
                                        if(board.getPiece(x, y + 1) == 0) {
                                            if(curPiece == 1) {
                                                twos++;
                                            } else {
                                                oTwos++;
                                            }
                                        }
                                    }
                                }
                                if (i == 2 && counter == 2){
                                    if(y + 1 < 15) {
                                        if(board.getPiece(x, y + 1) == 0) {
                                            if(curPiece == 1) {
                                                threes++;
                                                twos--;
                                            } else {
                                                oThrees++;
                                                oTwos--;
                                            }
                                        }
                                    }
                                }
                                //four in a row with a space below counts as a seperate 4 in a row
                                if (i == 3 && counter == 3){
                                    if(y + 1 < 15) {
                                        if(board.getPiece(x, y + 1) == 0) {
                                            if(curPiece == 1) {
                                                fours++;
                                                threes--;
                                            } else {
                                                oFours++;
                                                oThrees--;
                                            }
                                        }
                                    }
                                }
                                if(checkPiece != curPiece && checkPiece != 0) {
                                    counter = -100;
                                }
                            }
                        }
                        if(counter == 3) {
                            if(curPiece == 1) {
                                fours++;
                            } else {
                                oFours++;
                            }
                        }
                        if(counter == 2) {
                            if(curPiece == 1) {
                                threes++;
                            } else {
                                oThrees++;
                            }
                        }
                        if(counter == 1) {
                            if(curPiece == 1) {
                                twos++;
                            } else {
                                oTwos++;
                            }
                        }

                        counter = 0;
                        if(x > 3) {
                            //Up-Left
                            for (int i = 1; i < 5; i++) {
                                if(y - i >= 0 && x - i >= 0) {
                                    int checkPiece = board.getPiece(x - i, y - i);
                                    if(checkPiece == curPiece) {
                                        counter++;
                                    }
                                    if (i == 2 && counter == 2 && (x == 13 || y == 13)){
                                        if(x + 1 < 15 && y + 1 < 15) {
                                            if(board.getPiece(x + 1, y + 1) == curPiece) {
                                                if(curPiece == 1) {
                                                    fours++;
                                                } else {
                                                    oFours++;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 1 && counter == 1){
                                        if(x + 1 < 15 && y + 1 < 15) {
                                            if(board.getPiece(x + 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    twos++;
                                                } else {
                                                    oTwos++;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 2 && counter == 2){
                                        if(x + 1 < 15 && y + 1 < 15) {
                                            if(board.getPiece(x + 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    threes++;
                                                    twos--;
                                                } else {
                                                    oThrees++;
                                                    oTwos--;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 3 && counter == 3){
                                        if(x + 1 < 15 && y + 1 < 15) {
                                            if(board.getPiece(x + 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    fours++;
                                                    threes--;
                                                } else {
                                                    oFours++;
                                                    oThrees--;
                                                }
                                            }
                                        }
                                    }
                                    if(checkPiece != curPiece && checkPiece != 0) {
                                        counter = -100;
                                    }
                                }
                            }
                            if(counter == 3) {
                                if(curPiece == 1) {
                                    fours++;
                                } else {
                                    oFours++;
                                }
                            }
                            if(counter == 2) {
                                if(curPiece == 1) {
                                    threes++;
                                } else {
                                    oThrees++;
                                }
                            }
                            if(counter == 1) {
                                if(curPiece == 1) {
                                    twos++;
                                } else {
                                    oTwos++;
                                }
                            }
                        }
                        
                        counter = 0;
                        if(x < 11) {
                            //Up-Right
                            for (int i = 1; i < 5; i++) {
                                if(y - i >= 0 && x + i < 15) {
                                    int checkPiece = board.getPiece(x + i, y - i);
                                    if(checkPiece == curPiece) {
                                        counter++;
                                    }
                                    if (i == 2 && counter == 2 && (x == 1 || y == 13)){
                                        if(x - 1 > 0 && y + 1 < 15) {
                                            if(board.getPiece(x - 1, y + 1) == curPiece) {
                                                if(curPiece == 1) {
                                                    fours++;
                                                } else {
                                                    oFours++;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 1 && counter == 1){
                                        if(x - 1 > 0 && y + 1 < 15) {
                                            if(board.getPiece(x - 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    twos++;
                                                } else {
                                                    oTwos++;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 2 && counter == 2){
                                        if(x - 1 > 0 && y + 1 < 15) {
                                            if(board.getPiece(x - 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    threes++;
                                                    twos--;
                                                } else {
                                                    oThrees++;
                                                    oTwos--;
                                                }
                                            }
                                        }
                                    }
                                    if (i == 3 && counter == 3){
                                        if(x - 1 > 0 && y + 1 < 15) {
                                            if(board.getPiece(x - 1, y + 1) == 0) {
                                                if(curPiece == 1) {
                                                    fours++;
                                                    threes--;
                                                } else {
                                                    oFours++;
                                                    oThrees--;
                                                }
                                            }
                                        }
                                    }
                                    if(checkPiece != curPiece && checkPiece != 0) {
                                        counter = -100;
                                    }
                                }
                            }
                            if(counter == 3) {
                                if(curPiece == 1) {
                                    fours++;
                                } else {
                                    oFours++;
                                }
                            }
                            if(counter == 2) {
                                if(curPiece == 1) {
                                    threes++;
                                } else {
                                    oThrees++;
                                }
                            }
                            if(counter == 1) {
                                if(curPiece == 1) {
                                    twos++;
                                } else {
                                    oTwos++;
                                }
                            }
                        }
                    }
                    counter = 0;
                    if(x < 11) {
                        //Right
                        for (int i = 1; i < 5; i++) {
                            if(x + i < 15) {
                                int checkPiece = board.getPiece(x + i, y);
                                if(checkPiece == curPiece) {
                                    counter++;
                                }
                                if (i == 2 && counter == 2 && x == 1){
                                    if(x - 1 > 0) {
                                        if(board.getPiece(x - 1, y) == curPiece) {
                                            if(curPiece == 1) {
                                                fours++;
                                            } else {
                                                oFours++;
                                            }
                                        }
                                    }
                                }
                                if (i == 1 && counter == 1){
                                    if(x - 1 > 0) {
                                        if(board.getPiece(x - 1, y) == 0) {
                                            if(curPiece == 1) {
                                                twos++;
                                            } else {
                                                oTwos++;
                                            }
                                        }
                                    }
                                }
                                if (i == 2 && counter == 2){
                                    if(x - 1 > 0) {
                                        if(board.getPiece(x - 1, y) == 0) {
                                            if(curPiece == 1) {
                                                threes++;
                                                twos--;
                                            } else {
                                                oThrees++;
                                                oTwos--;
                                            }
                                        }
                                    }
                                }
                                if (i == 3 && counter == 3){
                                    if(x - 1 > 0) {
                                        if(board.getPiece(x - 1, y) == 0) {
                                            if(curPiece == 1) {
                                                fours++;
                                                threes--;
                                            } else {
                                                oFours++;
                                                oThrees--;
                                            }
                                        }
                                    }
                                }
                                if(checkPiece != curPiece && checkPiece != 0) {
                                    counter = -100; //path blocked
                                }
                            }
                        }
                        if(counter == 3) {
                            if(curPiece == 1) {
                                fours++;
                            } else {
                                oFours++;
                            }
                        }
                        if(counter == 2) {
                            if(curPiece == 1) {
                                threes++;
                            } else {
                                oThrees++;
                            }
                        }
                        if(counter == 1) {
                            if(curPiece == 1) {
                                twos++;
                            } else {
                                oTwos++;
                            }
                        }
                    }   
                }
            }
        }
        total[0] = fours;
        total[1] = oFours;
        total[2] = threes;
        total[3] = oThrees;
        total[4] = twos;
        total[5] = oTwos;
        return total;
    }
*/
    //Used so that we can go down a branch without messing up our main board
    private Board copyBoard(Board board) {
        Board b_copy = new Board();
        for(int y = 0; y < 15; y++) {
            for(int x = 0; x < 15; x++) {
                b_copy.addPiece(x, y, board.getPiece(x, y));
            }
        }
        return b_copy;
    }

    //Creates list of coordinates that are valid moves (adjacent to a current piece on the board)
    public ArrayList<Coordinate> getValidMoves(Board board) {
        ArrayList<Coordinate> validMoves = new ArrayList<Coordinate>();
        for(int y = 0; y < 15; y++) {
            for(int x = 0; x < 15; x++) {
                if(board.getPiece(x, y) != 0) {
                    if(x > 0) {
                        //Left
                        if(board.getPiece(x - 1, y) == 0) {
                            if(!duplicateCoordinate(validMoves, new Coordinate(x - 1, y))) {
                                validMoves.add(new Coordinate(x - 1, y));
                            }
                        }
                        if(y > 0) {
                            //Up-left
                            if(board.getPiece(x - 1, y - 1) == 0) {
                                if(!duplicateCoordinate(validMoves, new Coordinate(x - 1, y - 1))) {
                                    validMoves.add(new Coordinate(x - 1, y - 1));
                                }
                            }
                        }
                        if(y < 14) {
                            //Down-left
                            if(board.getPiece(x - 1, y + 1) == 0) {
                                if(!duplicateCoordinate(validMoves, new Coordinate(x - 1, y + 1))) {
                                    validMoves.add(new Coordinate(x - 1, y + 1));
                                }
                            }
                        }
                    }
                    if(x < 14) {
                        //Right
                        if(board.getPiece(x + 1, y) == 0) {
                            if(!duplicateCoordinate(validMoves, new Coordinate(x + 1, y))) {
                                validMoves.add(new Coordinate(x + 1, y));
                            }
                        }
                        //Up-Right
                        if(y > 0) {
                            if(board.getPiece(x + 1, y - 1) == 0) {
                                if(!duplicateCoordinate(validMoves, new Coordinate(x + 1, y - 1))) {
                                    validMoves.add(new Coordinate(x + 1, y - 1));
                                }
                            }
                        }
                        //Down-Right
                        if(y < 14) {
                            if(board.getPiece(x + 1, y + 1) == 0) {
                                if(!duplicateCoordinate(validMoves, new Coordinate(x + 1, y + 1))) {
                                    validMoves.add(new Coordinate(x + 1, y + 1));
                                }
                            }
                        }   
                    }
                    if(y > 0) {
                        //Up
                        if(board.getPiece(x, y - 1) == 0) {
                            if(!duplicateCoordinate(validMoves, new Coordinate(x, y - 1))) {
                                validMoves.add(new Coordinate(x, y - 1));
                            }
                        }
                    }
                    if(y < 14) {
                        //Down
                        if(board.getPiece(x, y + 1) == 0) {
                            if(!duplicateCoordinate(validMoves, new Coordinate(x, y + 1))) {
                                validMoves.add(new Coordinate(x, y + 1));
                            }
                        }
                    }
                }
            }
        }
        return validMoves;
    }

    //Returns true if coordinate already exists in array
    private boolean duplicateCoordinate(ArrayList<Coordinate> validMoves, Coordinate coord) {
        for (Coordinate c : validMoves) {
            if(c.x == coord.x && c.y == coord.y) {
                return true;
            }
        }
        return false;
    }

    //Returns whether the board has no empty spaces
    public boolean boardFull(Board board) {
        for(int y = 0; y < 15; y++) {
            for(int x = 0; x < 15; x++) {
                if(board.getPiece(x, y) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    //Determines if the given game board has a 5 in a row
    public boolean is_terminal(Board board) {
        for(int y = 0; y < 15; y++) {
            for(int x = 0; x < 15; x++) {
                int curPiece = board.getPiece(x, y);
                if(curPiece != 0) {
                    int newX = x;
                    int newY = y;
                    //Straight right
                    if(x < 11) {
                        newX = x;
                        newY = y;
                        while(newX + 1 < 15 && board.getPiece(newX++, y) == curPiece);
                        if(board.getPiece(newX, y) != curPiece) {
                            newX--;
                        }
                        if(newX - x >= 5) {
                            return true;
                        }
                        newX = x;
                        newY = y;
                        //Down-Right Diagonal
                        if(y < 11) {
                            while(newX + 1 < 15 && newY + 1 < 15 && board.getPiece(newX++, newY++) == curPiece);
                            if(board.getPiece(newX, newY) != curPiece) {
                                newX--;
                            }
                            if(newX - x >= 5) {
                                return true;
                            }
                        }
                    }
                    //Straight left
                    if(x > 3) {
                        newX = x;
                        newY = y;
                        while(newX - 1 > 0 && board.getPiece(newX--, y) == curPiece);
                        if(board.getPiece(newX, y) != curPiece) {
                            newX++;
                        }
                        if(x - newX >= 5) {
                            return true;
                        }
                        newX = x;
                        newY = y;
                        //Down-left Diagonal
                        if(y < 11) {
                            while(newX - 1 > 0 && newY + 1 < 15 && board.getPiece(newX--, newY++) == curPiece);
                            if(board.getPiece(newX, newY) != curPiece) {
                                newX++;
                            }
                            if(x - newX >= 5) {
                                return true;
                            }
                        }
                    }
                    //Straight up
                    if(y > 3) {
                        newX = x;
                        newY = y;
                        while(newY - 1 > 0 && board.getPiece(x, newY--) == curPiece);
                        if(board.getPiece(x, newY) != curPiece) {
                            newY++;
                        }
                        if(y - newY >= 5) {
                            return true;
                        }
                        newX = x;
                        newY = y;
                        //Up-left Diagonal
                        if(x > 3) {
                            while(newX - 1 > 0 && newY - 1 > 0 && board.getPiece(newX--, newY--) == curPiece);
                            if(board.getPiece(newX, newY) != curPiece) {
                                newX++;
                            }
                            if(x - newX >= 5) {
                                return true;
                            }
                        }
                        newX = x;
                        newY = y;
                        //Up-right Diagonal
                        if(x < 11) {
                            while(newX + 1 < 15 && newY - 1 > 0 && board.getPiece(newX++, newY--) == curPiece);
                            if(board.getPiece(newX, newY) != curPiece) {
                                newX--;
                            }
                            if(newX - x >= 5) {
                                return true;
                            }
                        }
                    }
                    //Straight down
                    if(y < 11) {
                        newX = x;
                        newY = y;
                        while(newY + 1 < 15 && board.getPiece(x, newY++) == curPiece);
                        if(board.getPiece(x, newY) != curPiece) {
                            newY--;
                        }
                        if(newY - y >= 5) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
