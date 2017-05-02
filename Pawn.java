import java.util.Comparator;

class Pawn {
  int /* 0-3 */ id;
  String color;
  Board.BoardComponent bc;
  int location;

  Pawn(){

  }
  Pawn (int id, String color) {
    this.id = id;
    this.color = color;
    this.bc = Board.BoardComponent.NEST;
    this.location = id;
  }

  Pawn (int id, String color, Board.BoardComponent bc, int location) {
    this.id = id;
    this.color = color;
    this.bc = bc;
    this.location = location;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Pawn)) {
      return false;
    }
    Pawn p = (Pawn) other;
    return this.id == p.id
            && this.color.equals(p.color)
            && this.bc == p.bc
            && this.location == p.location;
  }

  public class PawnCompare implements Comparator<Pawn> {
    @Override
    public int compare(Pawn p1, Pawn p2){
      return p1.location-p2.location;
    }
  }

}
