//Score score = new Score (); in main class
class Score {
  int countBlock = 0;//if player add or remove Block, they lose 1 score, they can add/remove block at least three times, the fourth time will 
  //begin to lose score.
  int countHit ;//Count how many time the enemy hit the ball. The Maximum life will be 10. Now it will reset for every new level. For countHit increase, the life = 10 - countHit;
  int score ; // the temporary formula: 10000 - 200*countBlock - 500*countHit + 300*countNoHit;
  int countNoHit;//When player jump over Enemy, get 300 scores. and countNoHit adds 1;
  int countTimeRemain; //Count the time remaining in class Time Count. The formula is in TimeCount class, in method count()
  int totalScore = 0;
  int [] listScore; //Use to store all the score through all the level, in case player press Play Again and they get higher score, it will be override to get new one
  boolean updateListScore = false; //If updateLifeScore = false = NOT YET update, then update. 
  boolean updateHighScore = false; //If player wins the game, then press PLAY AGAIN button with the higher score than the last one, update the new higher score instead.
  // False = NOT NEED UPDATE. Check PLAYAGAIN class for this one.
  Score () {
    listScore = new int [15];
  }
  void display () {
    //Count Block steup
    countTimeRemain = 0;
    countBlock = 0;
    countHit = 0;
    countNoHit = 0;
    score = 0;
  }
  void calculate() {
    if (countBlock > 3) {
      score = -5*(countBlock-3) + 30*countNoHit + countTimeRemain;// Calculation of final score for win game, which countTimeRemain = (t_m*60+t_s)*20;
    }
    else if (countBlock <= 3) {
      score = 30*countNoHit + countTimeRemain;//Remember we let player has 3 free block to use;
    }
    //text ("Score:" + score, 900, 90);
  }
  void setTotalScore () {
    if (updateListScore == false) { //False = NOT UPDATE YET
      if (updateHighScore == false) { // False = NO NEED UPDATE, NO PLAY AGAIN
        listScore[level-1] = score;
        totalScore = totalScore + listScore[level-1];
      }
      else if (updateHighScore == true) { //Player plays that level again, update new higher score if needed
        if (score > listScore[level-1]) {
          totalScore = totalScore - listScore[level-1]; //We don't need the older lower score anymore, so totalScore - listScore[previous score]
          listScore[level-1] = score; //Update the new higher score
          totalScore = totalScore + listScore[level-1];
          updateHighScore = false;
        }
      }
      updateListScore = true;
    }
  }
}

