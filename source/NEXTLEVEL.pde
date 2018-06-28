class NEXTLEVEL {
  void level () {
    if (nextLevel == true) {//Choose to play next level
      more.helpTime = 0; //Reset the helpTime, as well as in PLAYAGAIN class
      //level++;
      chooseLevel.up();
      setup();
      nextLevel = false;
    }
  }
}

