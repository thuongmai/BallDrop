//TimeCount t = new timeCount; in main program
class TimeCount {
  int t_m;
  int t_s;
  int recordTime;//To record time from millis(). For example: we setup recordTime = millis(). Then we let millis() runs from 0.
//if millis () - recordTime >= 1000 (1000 millisecond = 1 second) , then we begin the action. after that, recordTime is updated
//by recordTime = millis() again.
  boolean timeOut ;//if the time is over, then timeOut is True. The player loses the game

  TimeCount ( ) {    
  }
  void display(){
    //inputSecond = addSecond;
    inputSecond = addSecond;
    t_m = inputSecond / 60; // covert second to min by devide it by 60.
    t_s = inputSecond % 60; // The remain will be second
    recordTime = millis(); //store the current time
  }
  void count () {
    timeOut = false;
    textAlign(CENTER);
    if (millis() - recordTime >= 1000) { // 1000 millisecond = 1 second
      recordTime = millis(); //update the stored time
      t_s--;
      if (t_s < 0) {
        t_s = 59;
        t_m --;
      }
      if (t_s == 0 && t_m == 0) {
        timeOut = true;
      }
    }
    fill(255,255,0);
    textFont(createFont("Times", 50));
    text(t_m + " : " + t_s, 70, 80);
    //Calculate CountTimeRemain at the same time
    score.countTimeRemain =( (t_m * 60 + t_s) ) * 20;
    //println (score.countTimeRemain);
  }
}

