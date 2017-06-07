package parcheesi;

public class Pawn extends BoardObject {
  public int /* 0-3 */ id;
  public String color;
  public Location location;

  public Pawn() {}

  public Pawn (int id, String color) {
    this.id = id;
    this.color = color;
    this.location = new Location(Board.BoardComponent.NEST, id);
  }

  public Pawn (int id, String color, Location location) {
    this.id = id;
    this.color = color;
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
            && this.location.equals(p.location);
  }
}
