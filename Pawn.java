import java.util.Comparator;

class Pawn {
  int /* 0-3 */ id;
  String color;
  Board.BoardComponent bc;
  int location;

  Pawn (int id, String color) {
    this.id=id;
    this.color=color;
    this.bc = Board.BoardComponent.NEST;
    this.location = id;
  }

  Pawn (int id, String color, Board.BoardComponent bc, int location) {
    this.id=id;
    this.color=color;
    this.bc = bc;
    this.location = location;
  }

  public boolean inNest() {
    return this.bc == Board.BoardComponent.HOME;
  }

  public boolean inRing() {
    return this.bc == Board.BoardComponent.RING;
  }

  public boolean inHomeRow() {
    return this.bc == Board.BoardComponent.HOMEROW;
  }

  public boolean inHome() {
    return this.bc == Board.BoardComponent.HOME;
  }

  public boolean equals(Pawn other) {
    return other.id  == this.id
            && this.color.equals(other.color)
            && this.bc == other.bc
            && this.location == other.location;
  }

  public class PawnCompare implements Comparator<Pawn> {
    @Override
    public int compare(Pawn p1, Pawn p2){
      return p1.location-p2.location;
    }
  }

}
