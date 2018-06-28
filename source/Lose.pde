class Lose {
  void display() {
    //fill(0);
    fill (255, 0, 0);
    rect (0, 0, width, height);

    fill(255);
    //One message for the reason why lose, one button to press
    rect (width/2 - 280, height/2 - 200, width/2 + 50, height/2 + 50, 200);//The message for Time out
    rect (width/2 - 100, 600, 200, 50);//the button request for More Time or Play Again

    fill (0);
    if (loseTimeOut == true && loseLife == false) {
      textFont(createFont("Times", 45));
      text("YOU RAN OUT OF TIME!", width/2, height/2-40);
      textSize(25);
      text("HINT: Find a shorter path.", width/2, height/2+20);
      textFont(createFont("Times", 27));
      text("MORE TIME", width/2 - 10, 620);
    }
    if (loseLife == true && loseTimeOut == false) {
      textFont(createFont("Times", 50));
      text("YOU ARE DEAD!", width/2, height/2-60);
      textSize(25);
      text("HINT: Press Space Bar to jump over enemies.", width/2, height/2);
      textFont(createFont("Times", 27));
      text("PLAY AGAIN", width/2 -10, 620);
    }
  }
  void press () {
    //More Time button, when press, add one more min to timeCount
    if (mouseX >= width/2 - 100 && mouseX <= (width/2+100) && mouseY >= 600 && mouseY <= 650 && mousePressed) {
      fill (0, 0, 0, 200);
      win = false;   
      if (loseTimeOut == true) {
        moreTime = true;
      }
      if (loseLife == true) {
        playAgain = true;
      }
      lose = false;  
      loseTimeOut = false;
      loseLife = false;
    }
    //The default color of More Time button
    else if (mouseX >= width/2 - 180 && mouseX <= (width/2-180)+width/2  + 50 && mouseY >= height/2 - 180 && mouseY <= (height/2-180)+(height/2 + 50) || press == false) {
      fill (255);
    }
  }
}

