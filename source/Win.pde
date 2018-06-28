class Win {
  int recordFinalTime;
  boolean stopCountBlock = false;
  int copyCountBlock = 0;
  void display() {
    fill (0, 255, 0);
    rect (0, 0, width, height);
    stopGame = true; // Use to make the game stop running

    fill(255);
    rect (10, 10, width-20, height/2 + 200, 200);//Congra button + Score Result
    rect (100, 600, 200, 50);//playagain button
    rect (650, 600, 200, 50);//next level button
     //
     effect.display();
    fill (255,0,0);
    textFont(createFont("Times", 55));
    text("CONGRATULATIONS", width/2, 50);
    textAlign(LEFT,CENTER);
    text("Barriers Changed: ", 100, 120);
    
    //The score.countBlock does not stop even after Win screen apear, so we will pass the value of score.countBlock to 
    //copyCountBlock once and print that value on the screen.
    if (stopCountBlock == false){
      if (score.countBlock >= 3){
        copyCountBlock = score.countBlock - 3;
      }
      if (score.countBlock < 3){
        copyCountBlock = 0;
      }
      stopCountBlock = true;
    }
    text(copyCountBlock, 800, 120);
   
    text("Enemies Avoided: ", 100, 190);
    text(score.countNoHit, 800, 190);
    text("Lives Remaining: ", 100,  260);
    text(life - score.countHit, 800, 260);
    text("Time Spent: ", 100, 330);
   // text((inputSecond/60 - t.t_m) + " : " + (inputSecond%60 - t.t_s), 800, 330);
    recordFinalTime = t.t_m*60 + t.t_s; //Cover the mins & secs into Secs
    text((inputSecond-recordFinalTime)/60 + " : " + (inputSecond-recordFinalTime)%60,800,330);
    text("Score: ", 100, 400);
    text(score.score, 800, 400);
    //text("Average Level Score: ", 100, 470);
    //text(score.totalScore/level, 800, 470);  // This value is inaccurate if the player does not play levels in order.
    textFont(createFont("Times", 25));
    textAlign(CENTER,CENTER);
    text("PLAY AGAIN", 190, 620);
    text("NEXT LEVEL", 740, 620);
    //Show the sum of the Score of all level the player played (only once then stop, using boolean addScoreAlready
    if (addScoreAlready == false) {
      score.setTotalScore();
      //System.out.println(score.totalScore);
      //System.out.println(score.countTimeRemain);
     // println(score.listScore[level-1]);
      addScoreAlready = true;
    }
    //Test: Use to show if listScore[i] can store the score for each level or not.
    for (int i = 0;i<level;i++){
     // text(score.listScore[i],400,i*50 + 200);
    }
  }
  void press() {
    stopGame = true;
    //Play again button, when press, play different game at the same level
    if (mouseX >= 100 && mouseX <= 300  && mouseY >= 600 && mouseY <= 650 && mousePressed) {
      fill (255, 0, 0, 200);    
      win = false;
      lose = false;
      playAgain = true;
      stopGame = false;// The game become playable
    }

    //The color of the PlayAgain button when no press
    else if (mouseX >= width/2 - 250 && mouseX <= (width/2-250)+width/2  + 150 && mouseY >= height/2 + 200 && mouseY <= (height/2+200)+(height/2 + 50) || press == false) {
      fill(255);
    }
    //Next Lever button, when press, advance to more difficult level
    if (mouseX >= 650 && mouseX <= 850&& mouseY >= 600 && mouseY <= 650 && mousePressed) {
      fill(255, 0, 0, 255);
     // levelTime += 15;
      levelEnemy+= 1;
      win = false;
      lose = false;
      nextLevel = true;
      stopGame = false;//the game become playable
      addScoreAlready = false; //Only go to next level, it will add new Score to the TotalScore, playagain and play more time won't add new Score
      score.updateListScore = false; //False means NOT YET, it will update then
    }
    //the color of the Next Lever button when no press
    else if (mouseX >= width/2 +100 && mouseX <= (width/2+100)+width/2  + 150 && mouseY >= height/2 + 200 && mouseY <= (height/2+200)+(height/2 + 50)
      || press == false) {
      fill(255);
    }
  }
}

