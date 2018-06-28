//Instruction instruct = new Instruction;
class Instruction {
  //PImage instructionImage;
  Instruction () {
  }
  void load() {
    //instructionImage = loadImage ("InstructionImage.png");
  }
  void display(){
    background(0);
    //image(instructionImage,50,0,800,500);
    textSize(40);
    fill(255);
    textAlign(CENTER);
    text("INSTRUCTIONS",width/2,35);
    textAlign (LEFT);
    textSize(25);
    text("   CONTROLS:",0,70);
    textSize(15);
    text("    -   Left-click on yellow triangles to add blue barriers. Left-click on blue barriers to remove them.", 0,100);
    text("    -   Right-click (anywhere on screen) to toggle between going through the current tunnel and bouncing back out without going through.",0,120); 
    text("            (Only when the ball is shrinking or turning around inside a tunnel. Going through is the default behaviour every time).",0,140); 
    text("    -   Press the Space Bar to jump over enemies.",0,160);
    text("    -   Press 'p' to pause the game.",0,180);
    textSize(25);
    text("   GAME ELEMENTS:",0,220);
    textSize(15);
    text("    -   Yellow Triangles: Your ball and all enemies can pass through these.",0,250);
    text("    -   Blue Triangles: Block your ball and some enemies. You can add or remove them.",0,270);
    text("    -   Grey Triangles: Block your ball and all enemies. Cannot be added or removed.",0,290);
    text("    -   Red Triangles: Tunnels which \"teleport\" your ball and enemies. The two ends of a tunnel turn green when your ball is inside it.",0,310);
    text("            When your ball is shrinking or turning around inside a green tunnel, you can right-click to toggle where the ball emerges.",0,330);
    text("            A yellow arrow indicates the place where your ball will emerge, as well as the direction in which your ball will travel.",0,350);    
    text("    -   Tunnel with Central White Dot: The Hole - The level exit.",0,370);
    text("    -   Blue Ball: Your character",0,390);
    text("    -   Other Balls: Enemies",0,410);
    textSize(25);
    text("   GAME OBJECTIVE AND TIPS:",0,450);
    textSize(15);
    text("    -   Direct the ball to the Hole before the time limit, without losing all of your lives.",0,480);
    text("    -   Each failure to jump over an enemy will cost you a life.",0,500);
    text("    -   Collisions with enemies do not occur inside tunnels. Consequently, jumping over enemies in tunnels does not raise your score.",0,520);
    text("    -   You cannot add or remove a blue barrier while your ball or an enemy is inside the triangle in question.",0,540);
    text("    -   Improve your score by jumping over enemies, placing or removing few blue barriers, and finishing the level quickly.",0,560);

  }
}

