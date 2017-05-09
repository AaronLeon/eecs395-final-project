package parcheesi;

public class Pawn {
  public int /* 0-3 */ id;
  public String color;
  public Board.BoardComponent bc;
  public int location;

  public Pawn() {}

  public Pawn (int id, String color) {
    this.id = id;
    this.color = color;
    this.bc = Board.BoardComponent.NEST;
    this.location = id;
  }

  public Pawn (int id, String color, Board.BoardComponent bc, int location) {
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
}
