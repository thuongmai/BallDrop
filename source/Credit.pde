//Credit credit = new Credit();
class Credit {
  String name1; //name of each student
  int x; //location width on the screen
  int y; //location height on the screen
  String name2; 
  String studentNumber1; 
  String studentNumber2; 
  int count; //use for movement of the text in void move
  String thankText;
  boolean resetCredit = false;
  //Zero Parameter Constructor
  Credit () {
    name1 = "Bernard Llanos";
    name2 = "Thuong Mai";
    studentNumber1 = "ID: 100793648";
    studentNumber2 = "ID: 100885938";
    x = 300;
    y = 600;
    count = 0;
    thankText = "Thanks for playing!";
  }
  //make the text appear on the screen
  void display() {
    if (resetCredit == true){
      x = 300;
      y = 600;
      resetCredit = false;
    }
    textAlign(LEFT);
    background(0);
    fill (255, 255, 0);
    textFont(createFont("Times", 50));
    text (name1, x, y);
    text (studentNumber1, x, y+100);
    text (name2, x, y+200);
    text (studentNumber2, x, y+300);
    text (thankText, x, y+800);
    move();
  }
  //make the text move from below the screen go up as y1 and y2 are smaller
  void move () {
    for (int i = 0;i<1000;i++) {
      count ++; //because the void draw runs very fast, instead of using frameRate, use count++ to count for every 1000 rates/second of void draw
      if (count % 500 == 0) { //count will plus one for every 1000
        y --;
      }
      if (y <= -550) { //the y will go up until it reach -550
        y = -550;
        noStroke();
      }
    }
  }
}

