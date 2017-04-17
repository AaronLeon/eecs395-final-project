public class SPlayer implements Player {
    private String color;
    private Pawn[] pawns = new Pawn[4];
    private int[] dice = new int[4];
    private Move[] moves = new Move[4];

    public SPlayer(String c) {
        color = c;
    }

    public String getColor() {
        return color;
    }

    public Pawn[] getPawns() {
        return pawns;
    }

    public void startGame(String newColor) {
        color=newColor;
        for (int i = 0; i < pawns.length; i++) {
            pawns[i] = new Pawn(i, newColor);
        }
        //TODO: Not implemented yet 
    }

    public Move[] doMove(Board brd, int[] rolls) {
        moves=new Move[4];//clears old moves
        dice=rolls;

        //TODO: Not implemented yet
        //check that the move is possible given the die rolls

        return moves;
    }



    public boolean allOut(){
        for(int i = 0; i < pawns.length; i++){
            if(pawns[i].home==true){
                return false;
            }
        }
        return true;
    }

    public EnterPiece tryEnterPiece(int i){
        if (pawns[i].home==false){
            return null;
        }
        if (dice[0]+dice[1]==5){
            dice[0]=0;
            dice[1]=0;
            //update pawns array
            return new EnterPiece(pawns[i]);
        } else if (dice[2]+dice[3]==5){
            dice[2]=0;
            dice[3]=0;
            //update pawns array
            return new EnterPiece(pawns[i]);
        }
        return null;
    }

    public void doublesPenalty() {
        //TODO: Not implemented yet 
    }
}
