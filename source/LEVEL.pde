//LEVEL chooselevel = new LEVEL ();
class LEVEL {
  void up () {
    switch (level) {
    case 1:
      {
        /*Info for each level including 
         1) addSecond = ??? + more.helpTime;     The Given Time Count
         2) nextColumns = ???;                   The amount of column on Hexagon
         3) numEn1 = ???;                        Number of enemy type 1: Normal
         4) numEn2 = ???;                        Number of enemy type 2: through wall
         5) numEn3 = ???;                        Number of enemy type 3: break wall
         6) nextGridBlock = ???;                 Number of permanent block
         7) nextTeleporter= ???;                 Number of teleporter
         8) nextSpeedBall = ???;                 Speed of the Ball
         9) nextSpeedEn = ???;                   Speed of Enemy
         */
        addSecond = 300 + more.helpTime;
        nextColumns = 0;
        numEn1 = 1;
        numEn2 = 0;
        numEn3 = 0;
        nextGridBlock = 0;
        nextTeleporter = 0;
        nextSpeedBall = 0;
        nextSpeedEn = 0;
      };
      break;
    case 2: 
      {
        addSecond = 50 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        // inputSecond = 40;
        nextColumns = 0;//Make more Columns for the next Level
        // nextNumEn += 1;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 1;
        numEn2 = 0;
        numEn3 = 0;
        nextGridBlock = 0; //Increase the amount of permanetly Block
        nextTeleporter =0; //Increase the amount of telepoter Block
        nextSpeedBall = 0;
        nextSpeedEn = 0;
      };
      break;
    case 3:
      {        
        addSecond = 60 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        nextColumns = 0;//Make more Columns for the next Level
        numEn1 = 1;
        numEn2 = 0;
        numEn3 = 1;
        nextGridBlock = 0; //Increase the amount of permanetly Block
        nextTeleporter =0; //Increase the amount of telepoter Block
        nextSpeedBall = 0;
        nextSpeedEn = 0;
      };
      break;
    case 4:
      {
        //addSecond = levelTime;//levelTime is counted in if (win = true && lose = false) condition.
        addSecond = 60 + more.helpTime;
        //nextColumns += 2;//Make more Columns for the next Level
        nextColumns = 0;
        //nextNumEn += 0;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 1;
        numEn2 = 1;
        numEn3 = 1;
        nextGridBlock = 0; //Increase the amount of permanetly Block
        nextTeleporter =0; //Increase the amount of telepoter Block
        nextSpeedBall = 0;
        nextSpeedEn = 0;
      };
      break;
    case 5:
      {
        addSecond = 120 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns += 0;//Make more Columns for the next Level
        nextColumns = 2;
        //nextNumEn += 1;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 2;
        numEn2 = 1;
        numEn3 = 1;
        nextGridBlock = 2; //Increase the amount of permanetly Block
        nextTeleporter = 2; //Increase the amount of telepoter Block        
        nextSpeedBall = 15;
        nextSpeedEn = 15;
      };
      break;
    case 6:
      {
        addSecond = 120 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns += 0;//Make more Columns for the next Level
        nextColumns = 2;
        numEn1 = 2;
        numEn2 = 2;
        numEn3 = 2;
        //nextNumEn += 1;//levelEnemy is counted in if (win=true && lose = false) condition 
        nextGridBlock = 2; //Increase the amount of permanetly Block
        nextTeleporter = 2; //Increase the amount of telepoter Block
        nextSpeedBall = 15;
        nextSpeedEn = 15;
      };
      break;
    case 7:
      {
        addSecond = 150 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns += 2;//Make more Columns for the next Level
        nextColumns = 2;
        //nextNumEn += 0;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 3;
        numEn2 = 3;
        numEn3 = 3;
        nextGridBlock = 4; //Increase the amount of permanetly Block
        nextTeleporter =4 ;//Increase the amount of telepoter Block
        nextSpeedBall = 15;
        nextSpeedEn = 15;
      };
      break;
    case 8:
      {
        addSecond = 150 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns += 0;//Make more Columns for the next Level
        nextColumns = 2;
        //nextNumEn += 1;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 4;
        numEn2 = 4;
        numEn3 = 4;
        nextGridBlock = 4; //Increase the amount of permanetly Block
        nextTeleporter =4 ;//Increase the amount of telepoter Block
        nextSpeedBall = 15;
        nextSpeedEn = 15;
      };
      break;
    case 9:
      {
        addSecond = 200 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns +=0;//Make more Columns for the next Level
        nextColumns = 4;
        // nextNumEn += 0;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 5;
        numEn2 = 5;
        numEn3 = 5;
        nextGridBlock = 6; //Increase the amount of permanetly Block
        nextTeleporter =6 ;//Increase the amount of telepoter Block
        nextSpeedBall = 25;
        nextSpeedEn = 25;
      };
      break;
    case 10:
      {
        addSecond = 200 + more.helpTime;//levelTime is counted in if (win = true && lose = false) condition.
        //nextColumns += 2;//Make more Columns for the next Level
        nextColumns = 4;
        //nextNumEn += 2;//levelEnemy is counted in if (win=true && lose = false) condition 
        numEn1 = 6;
        numEn2 = 6;
        numEn3 = 6;
        nextGridBlock = 8; //Increase the amount of permanetly Block
        nextTeleporter =8 ;//Increase the amount of telepoter Block
        nextSpeedBall = 25;
        nextSpeedEn = 25;
      };
      break;
    case 11:
      {
        addSecond = 200+ more.helpTime;
        nextColumns = 4;
        numEn1 = 6;
        numEn2 = 6;
        numEn3 = 6;
        nextGridBlock = 10;
        nextTeleporter = 10;
        nextSpeedBall = 25;
        nextSpeedEn = 25;
      }  
      break;
    case 12: 
      {
        addSecond = 200+more.helpTime;
        nextColumns = 4;
        numEn1 = 7;
        numEn2 = 7;
        numEn3 = 7;
        nextGridBlock = 10;
        nextTeleporter = 10;
        nextSpeedBall = 25;
        nextSpeedEn = 25;
      }
      break;
    case 13:
      {
        addSecond = 250 + more.helpTime;
        nextColumns = 6;
        numEn1 = 8;
        numEn2 = 8;
        numEn3 = 8;
        nextGridBlock = 12;
        nextTeleporter = 12;
        nextSpeedBall = 35;
        nextSpeedEn = 35;
      }
      break;
    case 14: 
      {
        addSecond = 250 + more.helpTime;
        nextColumns = 6;
        numEn1 = 8;
        numEn2 = 8;
        numEn3 = 8;
        nextGridBlock = 14;
        nextTeleporter = 14;
        nextSpeedBall = 35;
        nextSpeedEn = 35;
      }
      break;  
    case 15:
      {
        addSecond = 250 + more.helpTime;
        nextColumns = 6;
        numEn1 = 9;
        numEn2 = 9;
        numEn3 = 9;
        nextGridBlock = 14;
        nextTeleporter = 14;
        nextSpeedBall = 35;
        nextSpeedEn = 35;
      }
      break;
    default: 
      {
        level = 1;
        addSecond = 300 + more.helpTime;
        nextColumns = 0;
        numEn1 = 1;
        numEn2 = 0;
        numEn3 = 0;
        nextGridBlock = 0;
        nextTeleporter = 0;
        nextSpeedBall = 0;
        nextSpeedEn = 0;
      };
      break;
    }
  }
}

class bubbleLevel {
  int x;
  int y;
  int diameter;
  bubbleLevel (int x, int y, int diameter) {
    this.x = x;
    this.y = y;
    this.diameter = diameter;
  }
  public void display() {
    fill (255, 255, 0);
    ellipse (x, y, diameter, diameter);
  }
}
//selectedLevel selectLevel = new selectedLevel ();
class selectedLevel {
  int getLevel = 1;
  boolean getNewLevel = false;
  selectedLevel() {
  }
  public void display() {
    ballLevel = new ArrayList (num_x_level);
    for (int j = 0;j<num_y_level;j++) { //ArrayList does not store any information, we need to set it data from the bubble class
      for (int i = 0;i<num_x_level;i++) {
        ballLevel.add(new bubbleLevel (i*150+200, j*140+200, 100));
      }
    }
  }
  public void run() {
    background(0);
    for (int i = 0;i<num_x_level;i++) {
      for (int j=0;j<num_y_level;j++) {
        bubbleLevel b = (bubbleLevel)ballLevel.get(i+j*num_x_level); //class bubble, function get () to get the current ball
        b.display();
        fill(255, 0, 0);
        textSize(50);
        textAlign(CENTER);
        text(i + j * num_x_level + 1, b.x, b.y+15);
        fill(255, 0, 255);
        text("CHOOSE A LEVEL", width/2, 80);
        if (getNewLevel == false){
          fill(0,255,0);
          text("You are in Level " + level + "/15", width/2,130);
        }   
      }
    }
  }
  public void interact() {
    for (int i = 0;i<num_x_level;i++) {
      for (int j = 0;j<num_y_level;j++) {
        bubbleLevel b = (bubbleLevel)ballLevel.get(i+j*num_x_level);
        float distance = dist (mouseX, mouseY, b.x, b.y);
        if (distance <= b.diameter/2) {
          fill(0);
          rect(0,90,width,50);
          println ("i: " + i + " j: " + j +" " + " Selected Level: " + (i+j*num_x_level+1));
          getLevel = i + j * num_x_level + 1;
          fill(0,255,0);
          text("You selected Level " + getLevel+ ". Press Enter.", width/2, 130);
          getNewLevel = true;
        }
      }
    }
    isPausedMenu = true;
    menu.chooseLevel = false;
  }
  public void jumpLevel () {
    if (getNewLevel == true) {//Choose to play next level
      more.helpTime = 0; //Reset the helpTime, as well as in PLAYAGAIN class
      level = getLevel;
      chooseLevel.up();
      //menu.chooseLevel = false;
      setup();
      //menu.chooseStart = true;
      getNewLevel = false;
    }
  }
}

