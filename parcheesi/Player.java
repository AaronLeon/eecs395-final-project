package parcheesi;

interface Player {
  // inform the player that a game has started
  // and what color the player is.
  void startGame(String color);

  // ask the player what move they want to make
  Move[] doMove(Board board, int[] dice);

  // inform the player that they have suffered
  // a doubles penalty
  void doublesPenalty();
}
