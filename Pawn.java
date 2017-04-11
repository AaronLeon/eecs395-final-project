class Pawn {
  int /* 0-3 */ id;
  String color;
  int location;
  Pawn (int id, String color) {
    this.id=id;
    this.color=color;
    this.location=-1;
    this.home=true;
    this.runway=false;
  }
}
