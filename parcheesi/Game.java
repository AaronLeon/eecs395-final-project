package parcheesi;

import java.io.IOException;

interface Game {
  // add a player to the game
  void register(Player p);
  
  // start a game
  void start() throws Exception;
}
