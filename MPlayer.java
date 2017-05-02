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


    public int[] ForwardSortPawns(Pawn [] pawns,int home){
        int[] ids = new int[4];
        int[][] data=new int[4][2];
        int counter=0;
        for(Pawn pawn : pawns){
            int[] temp ={pawn.location,pawn.id};
            data[counter]=temp;
            counter++;
        }
        java.util.Arrays.sort(data, new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[0], b[0]);
            }
        });
        //data should be sorted

        for(int i = 0;i<4;i++){
            ids[i]=data[i][1];
        }

        return ids;
    }


    public Move[] BackwardMove(Parcheesi game, int[] rolls) {
        Move[] moves = new Move[1];
        int[] ids = new int[4];
        ids=ForwardSortPawns(game.board.pawns.get(color),game.board.NEST_LOCATIONS.get(color));
        ids=reverse4(ids);

        for(int x=0;x<4;x++){
            Pawn pawn=game.board.pawns.get(color)[ids[x]];
            if ((pawn.bc == Board.BoardComponent.NEST)) {
                if(game.canEnter(rolls)){
                    moves[0]=new EnterPiece(pawn);
                    //enter
                } else if(pawn.bc == Board.BoardComponent.HOMEROW){
                    for (int d : rolls) {
                        boolean blocked=false;
                        for(int i=0;i<=d;i++){
                            //d+1 because we check d cell as well
                            if (game.board.homeRows.get(color)[pawn.location + i] instanceof Blockade) {
                                blocked=true;
                            }
                        }
                        if(blocked==false){
                            moves[0]=new MoveHome(pawn,pawn.location,d);
                            return moves;
                            //make a move home and return
                        }
                    }
                }
                else{
                    for (int d : rolls) {
                        boolean blocked=false;
                        MoveMain testMove = new MoveMain(pawn, d);
                        if (game.isBlocked(testMove)) {
                            blocked=true;
                        }
                        if(!blocked){
                            moves[0]=new MoveMain(pawn,d);
                            //make move and return move
                        }
                    }
                }
            }
        }
        return moves;
    }


    public Move[] ForwardMove(Parcheesi game, int[] rolls) {
        Move[] moves = new Move[1];
        int[] ids = new int[4];
        ids=ForwardSortPawns(game.board.pawns.get(color),game.board.NEST_LOCATIONS.get(color));

        for(int x=0;x<4;x++){
            Pawn pawn=game.board.pawns.get(color)[ids[x]];
            if ((pawn.bc == Board.BoardComponent.NEST)) {
                if(game.canEnter(rolls)){
                    moves[0]=new EnterPiece(pawn);
                    //enter
                } else if(pawn.bc == Board.BoardComponent.HOMEROW){
                    for (int d : rolls) {
                        boolean blocked=false;
                        for(int i=0;i<=d;i++){
                            //d+1 because we check d cell as well
                            if (game.board.homeRows.get(color)[pawn.location + i] instanceof Blockade) {
                                blocked=true;
                            }
                        }
                        if(blocked==false){
                            moves[0]=new MoveHome(pawn,pawn.location,d);
                            return moves;
                            //make a move home and return
                        }
                    }
                }
                else{
                    for (int d : rolls) {
                        boolean blocked=false;
                        MoveMain testMove = new MoveMain(pawn, d);
                        if (game.isBlocked(testMove)) {
                            blocked=true;
                        }
                        if(!blocked){
                            moves[0]=new MoveMain(pawn,d);
                            //make move and return move
                        }
                    }
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

    public int[] reverse4(int[] index){
        int temp=0;
        temp=index[3];
        index[3]=index[0];
        index[0]=temp;
        temp=index[2];
        index[2]=index[1];
        index[1]=temp;
        return index;
    }

    public void doublesPenalty() {
        //TODO: Not implemented yet
    }
}