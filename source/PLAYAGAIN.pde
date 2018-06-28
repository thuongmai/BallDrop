class PLAYAGAIN {
  void again () {
    if (playAgain == true) {//Choose play again the level which player already won
      score.updateHighScore = true; //NEED TO UPDATE IF THE NEW SCORE IS HIGHER
      level--;
      more.helpTime = 0; //reset helpTime, as well as in NEXTLEVEL class
      setup();
      playAgain = false;
    }
  }
}

