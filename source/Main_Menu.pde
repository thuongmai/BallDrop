//Main_Menu menu = new Main_Menu;
class Main_Menu {
  boolean go = false;
  boolean chooseStart;
  boolean chooseLevel;
  boolean chooseInstruction;
  boolean chooseCredit;
  boolean changeButton=false; //From Start button change to Resume button
  PImage title;
  PImage [] button;
  Main_Menu() {
    chooseStart = false;
    chooseLevel = false;
    chooseInstruction = false;
    chooseCredit = false;
    button = new PImage [5];//4->5
  }
  void load () {    
    title = loadImage ("BallDrop.png");
    button [0] = loadImage ("StartButton.png");
    button [4] = loadImage("ResumeButton.png");// 3->4
    button [1] = loadImage ("SelectLevelButton.png");//
    button [2] = loadImage ("InstructionButton.png");//1 -> 2
    button [3] = loadImage ("CreditButton.png");//2 -> 3
  }
  void chooseMenu () {
    background(0);
    image(title, 300, 10);
    //Setup Size Font and color
    textSize(25);
    fill(255,255,0);
    if (changeButton == false || selectLevel.getNewLevel == true){
      text("You will start at level " + selectLevel.getLevel + "/15", 325,160);
      image(button[0],330,250,325,62);
    }
    else if (changeButton == true){
      text("You are playing level " + level + "/15 now", 325,160);
      image(button[4],330,250,325,62);//3->4
    }
    for (int i = 1;i<button.length-1;i++) {
      image(button[i], 330, i*100 + 250,325,62);
      //rect(330,i*100+250,325,62);
    }
    
    // Instruct new players to try the tutorial
    textAlign(LEFT,TOP);
    textSize(20);
    textLeading(25);
    fill(255);
    text("The first level is a tutorial. \nTo play the tutorial, either press Start, if you have not selected another level, or select Level 1.",50,640);
    
    if (mouseX >= 330 && mouseX <= 655) {
      if (mouseY >= 250 && mouseY <= (250 + 62) && chooseStart == false && mousePressed ) {
        isPaused = false;
        chooseStart = true;
        println ("Start");
      }
      if (mouseY >= 350 && mouseY <= (350 + 62) && chooseLevel == false && mousePressed) {
        //tutor.display();
        //chooseInstruction = true;
        chooseLevel = true;
        println ("Select Level");
      }
      if (mouseY >= 450 && mouseY <= (450 + 62) && chooseInstruction == false && mousePressed ) {
        //chooseCredit = true;
        chooseInstruction = true;
        //credit.resetCredit = true;
        println ("Instruction");
      }
      if (mouseY >= 550 && mouseY <= (550 + 62) && chooseCredit == false && mousePressed){
        chooseCredit = true;
        credit.resetCredit = true;
        println("Credit");
      }
    }
  }
}

