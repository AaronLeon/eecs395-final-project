/**
 * Created by yulunwu on 4/30/17.
 */
import java.util.*;

public class MPlayer extends SPlayer {
    String color;

    public MPlayer(String c) {
        startGame(c);
    }

    public void startGame(String color) {
        super.startGame(color);
    }


    public Pawn[] ForwardSortPawns(Pawn [] pawns,int home){
        for(Pawn pawn: pawns){
            if (pawn.location<home){
                pawn.location+=100;
            }
        }
        Arrays.sort(pawns);
        for(Pawn pawn: pawns){
            if (pawn.location<100){
                pawn.location-=100;
            }
        }
        return pawns;
    }

    public Move[] ForwardMove(Parcheesi game, int[] rolls) {
        Move[] moves = new Move[1];
        Pawn[] sortedPawns = new Pawn[4];
        sortedPawns=ForwardSortPawns(game.board.pawns.get(color),game.board.NEST_LOCATIONS.get(color));


        for(Pawn pawn : sortedPawns){
            if ((pawn.bc == Board.BoardComponent.NEST)) {
                if(game.canEnter(rolls)){
                    //enter
                } else if(pawn.bc == Board.BoardComponent.HOMEROW){
                    //try to enter home
                }
                else{
                    //try move main
                }
            }
        }
        return moves;
    }

    /*
        if ((pawn.bc == Board.BoardComponent.NEST)) {
        return canEnter(dice);
        //check if integers in dice can sum to 5
    } else if (pawn.bc == Board.BoardComponent.HOMEROW) {
        //TODO: Make isBlocked accepted MoveHome as well..this doesn't check all squares for blockades
        for (int d : dice) {
            if (board.homeRows.get(player.color)[pawn.location + d] instanceof Blockade) {
                return false;
            }
        }
    } else {
        for (int d : dice) {
            MoveMain testMove = new MoveMain(pawn, d);
            if (!isBlocked(testMove)) {
                return true;
            }
        }
  */



    public Pawn[] reverse(Pawn[] pawns){
        Pawn tempPawn=new Pawn();
        tempPawn=pawns[3];
        pawns[3]=pawns[0];
        pawns[0]=tempPawn;
        tempPawn=pawns[2];
        pawns[2]=pawns[1];
        pawns[1]=tempPawn;
        return pawns;
    }

    public Move[] BackwardMove(Parcheesi game, int[] rolls){
        Move[] moves = new Move[4];
        Pawn[] sortedPawns = new Pawn[4];
        sortedPawns=ForwardSortPawns(game.board.pawns.get(color),game.board.NEST_LOCATIONS.get(color));
        sortedPawns=reverse(sortedPawns);


        for(Pawn pawn : sortedPawns){
            if (game.canMove((SPlayer)this,pawn,rolls,game.board)){
                //make move
            }
        }

        return moves;
    }


    public void doublesPenalty() {
        //TODO: Not implemented yet
    }
}