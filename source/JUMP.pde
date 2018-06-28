class JUMP {
  //ball jump
  float time = 0; //Jmp time length
  //float delay;
  int pastSpacebarFrame = 0;//How long ago was the spacebar pressed?

  JUMP () {
  }
  void action () {
    if (time <= 0 && pastSpacebarFrame < (frameCount - 1)) {
      time = sideTri/2/playerBall.vBase;
    }
  }
  void fall () {
    // If ball is jumping, decrease jumping time counter
    if (time > 0) {
      time --;
    }
  }
  void pastFrame () {
    // Record the frame when the spacebar was last pressed
    if (keyPressed && key == ' ') {
      pastSpacebarFrame = frameCount;
    }
  }
}

