class MORETIME {
  int helpTime;
  void time() {
    if (moreTime == true) { //Need more time? add 1 min to the timeCount, call setup() again to reset that level
      helpTime = 120;
      level--;
      setup();
      moreTime = false;//if moreTime = true, void draw() will call setup() countless time
    }
  }
}

