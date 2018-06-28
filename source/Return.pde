//Return _return = new Return ();
class Return {
  PImage previous;
  PImage returnMenu;
  boolean isReturn;
  Return () {
  }
  void load() {
    previous = loadImage("ReturnButton.png");
    returnMenu = loadImage("MenuButton.png");
  }
  void press() {    
    //Okay, here is a trick. When You open the returnButton.png, you should notice that there is a black zone
    //around the Return arrow. Currently, I don't have any program to cut around the Return arrow, so when I put
    //it on the screen, it will have white zone around that button instead. So I put it on the black rectangle, 
    //print and cut it, and paste it to this one.
    //So the rectangle below will hide that trouble. It has same color with the black zone around Return button
    fill(0);
    noStroke();
    rect(0,565,width,height);
    //Print the Return Button out onto the black rect
    image(previous, 5, 595);
    //fill(255,0,0,150);
    //rect(10,615,355,50);//The region we can click to return. Because this image is different to another
    //so I used this rect to calculate apporoximately where the mouse should press to return back to last page
    //it will not show on the screen.
    if (mouseX >= 10 && mouseX <= 365 && mouseY >= 615 && mouseY <= 665 && mousePressed) {
      menu.chooseLevel = false;
      selectLevel.getNewLevel = false;
      menu.chooseInstruction = false;
      menu.chooseCredit = false;      
    }
  }
  void pressMenu(){
    image(returnMenu,0,640);
    //rect (0,640,150,100);/the region where we can click to return
    if (mouseX >= 0 && mouseX <= 150 && mouseY >=640 && mouseY <=740 && mousePressed){
      isReturn = true;
      menu.changeButton = true;
      menu.chooseStart = false;
    }
  }
}

