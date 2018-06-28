import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Thuong_Mai_Bernard_Llanos_Ball_Drop extends PApplet {

// COMP1501A Game Design Project, April 2013
// Game Title: "Ball Drop"
// Project Group:
//     Bernard Llanos, ID: 100793648
//     Thuong Mai, ID: 100885938

//Calling Class 
boolean press = false; //Just keep it. 
Lose lo = new Lose (); // Note: if u know what another name we can put instead of "lo", you can change it. I don't know what other words is suitable here, same with Win and TimeCount
Win  wi = new Win  ();
TimeCount t = new TimeCount ();
Score score = new Score();
NEXTLEVEL next = new NEXTLEVEL ();
PLAYAGAIN play = new PLAYAGAIN ();
MORETIME more = new MORETIME ();
JUMP jump = new JUMP ();
WinEffect effect = new WinEffect();
Main_Menu menu = new Main_Menu();
Instruction instruct = new Instruction ();
Return _return = new Return();
Credit credit = new Credit();
//Boolean add score to the total Score only once
boolean addScoreAlready = false;
LEVEL chooseLevel = new LEVEL();
Triangle currentTri_ball, currentTri_Enemy;
selectedLevel selectLevel = new selectedLevel();

//setup the screen
int sizeWidth = 1000;
int sizeHeight = 700;

//Tutorial Boolean
boolean tutorial = false;
int tutorialIndex = 0;

// Images
PImage imageBackGround;
PImage triImages[][];  // Array for triangle images
/*
First Dimension:
0-2 = Triangles, Type 0 to Type 2
3 = Inactive Teleporter
4 = Active Teleporter
5 = Triangle containing the hole
Second Dimension:
0 = Points Left
1 = Points Right
*/

//Setup Time for Game
int inputSecond;//Use to input the time of the game needed for each level. If inputSecond is over 60, it will automatically change to min
//ex: 125 seconds = 2 min 5 sec
int addSecond; //When player need help, add 1 min to the timeCount
boolean isPaused = false;  // Is the game paused (used in keyPressed())?
//int levelTime = 0; //The higher the level is, the more time player has
int levelEnemy= 0;//The higher the level is, the more enemies on the game
boolean isPausedMenu = false;

//Hole variable
Hole h;

//Win and Lose info of Game
boolean win = false; // The player win , the ball reach the hole in required time and still has lives
boolean lose = false;// There are 2 cases to lose, time out and no life (hit multiple times by enemies)
boolean loseTimeOut = false;//time out
boolean loseLife = false;//death (no more life)
boolean moreTime = false;//player request for more help. Now, it is the only choice for player to choose. In the future, we will add this button inside the game play
boolean playAgain = false;//play at the same level when player already win that level
boolean nextLevel = false;//go to next Level with more column of hexagon and more enemies
boolean stopGame = false;// This one is different from Pause function. When player wins the game, the score will appear, but if player continue to click randomly, the score will reduce 
//due to game is still running. So player wins, stopGame = true, then if(win == false && lose == false) will stop working. 
//If player press Play Again button or Next Level button, stopGame = false.
int level; //Show what level the player current play
int life = 10; //the player only has 5 lives
ArrayList ballLevel;
int num_x_level = 5;
int num_y_level = 3;


// Triangle Grid Variables
Triangle triCalculator;  // Creating a Triangle used for Calculations
int numColumns;  // How many columns in the grid (AN EVEN NUMBER!)?
int numTri;  // How many triangles are in the grid?
Triangle gridTri [];  // Array for storing all triangles in the grid
float hexX1 = sizeWidth/5+30, hexY1 = sizeHeight/3, hexY2 = 2*sizeHeight/3;  // Position variables needed for hexagonal grid
float sideTri;  // Triangle side length
int nextColumns = 0;//Go to next level, there will be more column


// For creating blocks in the grid
int freeTri [];  // Array for storing freeIndices in gridTri[]. Used only during level setup.
int currentFreeTri [];  // Equivalent to freeTri[], but for use during play, not level setup.
int quadSpaces [];  // Array for storing indices of 4-space objects (the hole and player-controlled balls)
int triTypes [];  // Array for storing the desired numbers of triangles of each type
int nextGridBlock = 0; //The number of Permanently Block on the game will increase as the higher level
int nextTeleporter= 0; //The number of Teleporter Block on the game will increase as the higher level

// Enemy Variables
Enemy enemyCalculator;  // Creating an Enemy used to access Enemy class methods
//int numEn;  // Total number of enemies to create before the start of the level
// This variable is no longer accurate once enemies are added or removed during play.
int newEn;  // moveAndBounce() will prompt for generation of new enemies to replace those that fall into the hole
ArrayList arrayEn;  // ArrayList for storing all Enemy objects
int typesEn [];  // Number of enemies to create of each type
float radiiEn [];  // To be passed as radiiCount to createEnemies()
//THE MINIMUM VALUE OF EACH ELEMENT IS 7
//Otherwise, motion will still work, but balls will move backwards before bouncing
float vEn [];  // To be passed as vCount to createEnemies()
Enemy playerBall;
int nextNumEn = 0; //Go to next level, there will be more Enemies
int nextSpeedEn = 0;//Go to next level, the speed of enemy increasing
int nextSpeedBall = 0;//Go to next level, the speed of ball increasing
int numEn1 = 0; //The amount of Enemy type 1
int numEn2 = 0; //The amount of enemy type 2
int numEn3 = 0; //The amount of enemy type 3

// Array for recording which enemies collided with the ball in the last frame
int pastCollisions [];

public void setup() {
  strokeWeight (1);
  size(sizeWidth, sizeHeight);
  smooth();

  //Load Background Image
  imageBackGround = loadImage ("Background_1000by700.png");
  
  //Load Image at Main_Menu
  menu.load();
  _return.load();
  instruct.load();  

  //Setup the score
  score.display();
  //Setup the Win Effect
  effect.install();  

  //Initialize the ballLevel on Select Level button
  selectLevel.display();
  if (selectLevel.getNewLevel == true) {
    level = selectLevel.getLevel;
  }
  else if (selectLevel.getNewLevel == false) {
    level++;
  }
  //Setup level
  chooseLevel.up();
  //Setup time
  t.display();

  // Preparing to initialize Triangle grid
  triCalculator = calculatorTriangle();  // Creating a Triangle used for Calculations
  numColumns = 4 + nextColumns;  // How many columns in the grid?
  numTri = triCalculator.numTriangles(numColumns);  // How many triangles are in the grid?

  // Initialize the grid and related variables
  gridTri = new Triangle[numTri];  // Array for storing all triangles in the grid
  hexX1 = 480-200*sqrt(2);//width/4;
  hexY1 = 175;//height/3;
  hexY2 = 525;//2*height/3;
  sideTri = triCalculator.createGrid(numTri, gridTri, numColumns, hexX1, hexY1, hexY2);  // Creating the grid

  triTypes = new int[4];  // Four types of triangles
  typesEn = new int[4];  // Four types of enemies
  quadSpaces = new int[2];  // Number of four-space reservations needed
  currentFreeTri = new int[numTri];  // For use after setting-up the level

  // Load and resize triangle pictures
  triImages = new PImage[6][2];
  String triImgName;
  for(int i = 0; i < 3; i +=1){
    triImgName = "tri"+i+"left.png";
    triImages[i][0] = loadImage(triImgName);
    triImgName = "tri"+i+"right.png";
    triImages[i][1] = loadImage(triImgName);
  }
  triImages[3][0] = loadImage("tri3left_inactive.png");
  triImages[3][1] = loadImage("tri3right_inactive.png");
  triImages[4][0] = loadImage("tri3left_active.png");
  triImages[4][1] = loadImage("tri3right_active.png");
  triImages[5][0] = loadImage("hole_left.png");
  triImages[5][1] = loadImage("hole_right.png");
  int newSize = (int) sideTri;
  for(int i = 0; i < triImages.length; i +=1){
    triImages[i][0].resize(0,newSize);
    triImages[i][1].resize(0,newSize);
  }

  // Initialize Enemy variables
  enemyCalculator = calculatorEnemy(); // Creating an Enemy used for Calculations
  newEn = 0;  // Currently, no new enemies need to be added, beyond the number created (numEn)
  arrayEn = new ArrayList();  // Initializing ArrayList of enemies
  radiiEn = new float[typesEn.length];  // To be passed as radiiCount to createEnemies()
  radiiEn[0] = 11;
  radiiEn[1] = 10;
  radiiEn[2] = 8;
  radiiEn[3] = 12;
  vEn = new float[typesEn.length];  // To be passed as vCount to createEnemies()
  int vEnBase = 60;  // Base ball speed to be adjusted depending on the Level
  vEn[0] = vEnBase - nextSpeedBall;//50*(4/numColumns);//120//Of the ball
  vEn[1] = vEnBase- nextSpeedEn;//60*(4/numColumns);//Of the enemies
  vEn[2] = vEnBase- nextSpeedEn;
  vEn[3] = vEnBase- nextSpeedEn;

  // The first level is a static (not randomly set-up) tutorial
  // Other levels are randomly-generated.
  if (level != 1) {
    // Initializing arrays of free triangles
    freeTri = new int[numTri];
    for (int i = 0; i < numTri; i +=1) {
      freeTri[i] = i;
    }

    // Setting some blocks in the grid
    triCalculator.reserve4Spaces(numTri, gridTri, numColumns, freeTri, quadSpaces);  // Reserving 4-space areas first
    // Number of each block to create
    triTypes[0] = 0;
    triTypes[1] = 4 + nextGridBlock;  // Number of the grid triangles which are blocks
    triTypes[2] = 0;
    triTypes[3] = 4 + nextTeleporter;  // Teleporters - an EVEN NUMBER!
    triCalculator.createBlocks(numTri, gridTri, freeTri, triTypes);  // Creating Blocks
    triCalculator.verifyBlocks(numTri, gridTri, quadSpaces, freeTri);  // Verifying whether the level is solvable:

    // Initializing Enemy objects
    // numEn = numTri-(4+((quadSpaces.length)-1)*3+triTypes[1]+triTypes[3]);  // Maximum possible number of enemies to create before the start of the level
    // Number of each type of enemy to create
    //numEn = 2 + nextNumEn;
    typesEn[0] = quadSpaces.length-1;  // Number of player-controlled balls
    typesEn[1] = numEn1;//numEn - typesEn[0];
    typesEn[2] = numEn2;
    typesEn[3] = numEn3;
    enemyCalculator.createEnemies(numTri, gridTri, arrayEn, typesEn, sideTri, radiiEn, vEn, freeTri, quadSpaces);  // Populating an ArrayList with Enemy Objects
  }

  // Tutorial level
  if (level == 1) {
    // Reserved spaces for the hole and the player's ball
    quadSpaces[0] = gridTri.length-1;
    quadSpaces[1] = 18;

    // Set some blocks
    int type2blocks[] = {
      4, 8, 11
    };//removable block
    for (int i = 0; i < type2blocks.length; i +=1) {
      gridTri[type2blocks[i]].type = 2;
    }
    //Gray block
    int type1blocks[] = {
      1, 7, 9, 14, 15, 16, 17
    };
    for (int i = 0; i < type1blocks.length; i +=1) {
      gridTri[type1blocks[i]].type = 1;
    }
    //Teleporter
    int type3blocks[] = {
      6, 8, 0, 19
    };  // EVEN NUMBER OF ELEMENTS
    triTypes[3] = type3blocks.length;
    for (int i = 0; i < type3blocks.length; i +=1) {
      gridTri[type3blocks[i]].type = 3;
      if (i % 2 == 0) {
        gridTri[type3blocks[i]].link = type3blocks[i+1];
      } 
      else {
        gridTri[type3blocks[i]].link = type3blocks[i-1];
      }
    }

    // Player's ball
    int triIndex = quadSpaces[1];
    currentTri_ball = gridTri[triIndex];
    float x_ = currentTri_ball.x;
    float y_ = currentTri_ball.y;
    int type_ = 0;
    int path_ = 5;
    float t_ = vEn[type_]*(0.5f-2*sqrt(3)/radiiEn[type_]);
    arrayEn.add(new Enemy(x_, y_, sideTri/radiiEn[type_], sideTri/vEn[type_], path_, triIndex, t_, type_, arrayEn.size()));

    // Enemy
    triIndex = 10;
    currentTri_Enemy = gridTri[triIndex];
    x_ = currentTri_Enemy.x;
    y_ = currentTri_Enemy.y;
    type_ = 1;
    path_ = 3;
    t_ = vEn[type_]*(0.5f-2*sqrt(3)/radiiEn[type_]);
    arrayEn.add(new Enemy(x_, y_, sideTri/radiiEn[type_], sideTri/vEn[type_], path_, triIndex, t_, type_, arrayEn.size()));
  }  // end if(level == 1)

  // Placing the hole
  float xhole = gridTri[quadSpaces[0]].x;
  float yhole = gridTri[quadSpaces[0]].y;
  h = new Hole (xhole, yhole, sideTri);

  // Ball is arrayEn(0), the first element in arrayEn.
  playerBall = (Enemy)arrayEn.get(0);
  // Initializing array of past collisions
  pastCollisions = new int[arrayEn.size()];
}  // end setup()

public void draw() {
  if (menu.chooseStart == false && menu.chooseLevel == false && menu.chooseCredit == false && menu.chooseInstruction == false) {
    if (!isPausedMenu) {
      isPaused = true;
      menu.chooseMenu();
    }
  }
  else if (menu.chooseStart == true && _return.isReturn == true || menu.chooseStart == true) {
    _return.isReturn = false;
    if (!isPaused && !_return.isReturn) {
      {
        image(imageBackGround, 0, 0, 1000, 700);
        more.time();
        play.again();
        next.level();
        selectLevel.jumpLevel();
      }
      //background(255);
      //println(currentTri_ball + ", " + currentTri_Enemy);
      //Press Menu to pause the game and come back to main menu (For Tutorial or Instruction purpose, perhaps)
      _return.pressMenu();
      //Don't remember what it is ???
      press = false;

      //Open the game, this is the default of the game, no Win no Lose
      if (win ==  false && lose == false) { 
        PLAY();
        tutorial();
      }
      else if (win == true && lose == false) {
        wi.display();
        wi.press();
      }
      else if (win == false && lose == true) {
        lo.display();
        lo.press();
      }
    }
  }
  else if (menu.chooseLevel == true) {
    selectLevel.run(); //Interact with the ballLevel in LEVEL class   
    _return.press();
  }
  else if (menu.chooseInstruction == true ) {
    instruct.display();
    _return.press();//Show the return button when needed
  }
  else if (menu.chooseCredit == true ) {
    credit.display();
    _return.press();
  }
  println("LEVEL: " + level);
  println("Get Level: " + selectLevel.getLevel);
}  // end draw()


public Triangle calculatorTriangle() {
  // Creating a Triangle used for Calculations
  float vertices_ [][] = new float [3][2];
  for (int i = 0; i < 3; i+=1) {
    for (int j = 0; j < 2; j+=1) {
      vertices_[i][j] = 0;
    }
  }
  return new Triangle(0.0f, 0.0f, vertices_, true, 0, 0, 0, 0, 0);
}  // end calculatorTriangle()


public Enemy calculatorEnemy() {
  // Creating an Enemy used for Calculations
  return new Enemy(0, 0, 0, 0, 0, 0, 0, 0, 0);
}  // end calculatorEnemy()


public void mousePressed() {
  // Allow player to click to interact with triangles
  if (!isPaused && !_return.isReturn) {
    if (mouseButton == LEFT) {
      // If the mouse is over an empty or player-controlled triangle, change its type
      triCalculator.playerBlock(numTri, gridTri, arrayEn, quadSpaces[0], currentFreeTri, mouseX, mouseY);
    }
    else if (mouseButton == RIGHT && gridTri[playerBall.currentTri].type == 3) {
      if (playerBall.state == 1 || playerBall.state == 3 ||
        (playerBall.state == 4 && playerBall.t > 1) || playerBall.state == 7) {
        //Toggle teleportation of the player's ball, if the ball is inside a teleporter
        playerBall.teleport =!playerBall.teleport;
      }
    }
  }  // end if (!isPaused && !_return.isReturn)
  if (isPaused && tutorial) {
    {
      isPaused = false;
      tutorial = false;
      tutorialIndex++;
    }
  }
  if (menu.chooseLevel == true) {
    selectLevel.getNewLevel = false;
    selectLevel.interact(); // Click the ball to select the Level in Select Level button
    menu.chooseLevel = false;
  }
  if (mouseX >= 10 && mouseX <= 365 && mouseY >= 615 && mouseY <= 665 && mousePressed){
    isPausedMenu = false;
  }
}  // end mousePressed()


public void keyPressed() {
  if (key == ' ') {
    // Jump key
    jump.action();
  }
  if (key == 'p' || key == 'P' || key == 'q') {
    // Pause key
    // 'p' will pause and hide the game
    // 'q' will pause the game, but leave it visible (needed for the project presentation)
    isPaused = !isPaused;
    if (isPaused && key != 'q') {
      fill(255, 0, 0);
      rect(0, 0, width, height);
      fill(255, 255, 0);
      textFont(createFont("Times", 100));
      text("PAUSE", width/2, height/2);
    }
  }
  if (key == 'p' || key == 'P' || key == 'q') {
    loop();
  }
  if (key == ENTER || key == RETURN){
    isPausedMenu = false;
  }
  // For the presentation, pressing the UP and DOWN arrows will increase
  // or decrease Enemy object speeds, respectively.
  if(key == CODED){
    if(keyCode == UP){
      enemyCalculator.speedChange(arrayEn, vEn, true);
    } else if(keyCode == DOWN){
      enemyCalculator.speedChange(arrayEn, vEn, false);
    }
  }  // end if(key == CODED)
}  // end keyPressed()


public void collisions(ArrayList enemies, Enemy ball, float jumpTime) {
  // Check for collisions between the player's ball and enemies

  Enemy en;  // Holder for the current enemy
  float d = 0;  // Distance between enemy and ball

  // Update the pastCollisions array to match the current number of enemy objects
  if (pastCollisions.length < arrayEn.size()) {
    // Update size of pastCollisions
    for (int i = pastCollisions.length; i < arrayEn.size(); i +=1) {
      pastCollisions = append(pastCollisions, 0);
    }
  } 
  else if (pastCollisions.length > arrayEn.size()) {
    for (int i = pastCollisions.length-1; i > arrayEn.size(); i -=1) {
      pastCollisions = shorten(pastCollisions);
    }
  }

  // Check for collisions between the ball and all enemy objects
  for (int i = 1; i < enemies.size(); i +=1) {
    en = (Enemy)enemies.get(i);
    d = dist(en.x, en.y, ball.x, ball.y);
    if (pastCollisions[i] == 0 && d < ball.r + en.r && jumpTime <= 0) {
      // Ball is not jumping, and was not colliding with this enemy in the previous frame
      // A collision is occuring this frame
      // If either the ball, or the enemy are shrinking/growing inside a teleporter, do not hurt the ball
      // Otherwise, the ball is hurt, and the collision is noted so that it will not be repeated.
      pastCollisions[i] = 1;
      if (ball.r == ball.rBase && en.r == en.rBase) {
        // Ouch!
        background (255, 255, 0);
        score.countHit ++;
      }  // end if(ball.r == ball.rBase && en.r == en.rBase)
    } 
    else if (pastCollisions[i] == 0 && d < ball.r + en.r && jumpTime > 0) {
      // Ball is jumping, and was not colliding with this enemy in the previous frame
      // A collision is occuring this frame
      // If either the ball, or the enemy are shrinking/growing inside a teleporter, do not increment the player's score
      // The ball is not hurt, and the collision is noted so that it will not be repeated.
      pastCollisions[i] = 1;
      if (ball.r == ball.rBase && en.r == en.rBase) {
        score.countNoHit ++;
      }  // end if(ball.r == ball.rBase && en.r == en.rBase)
    } 
    else if (d > ball.r + en.r) {
      // There is no collision with the enemy this frame - enable detection of future collisions
      pastCollisions[i] = 0;
    }
  }
}  // end collisions()


public void replaceEnemy() {
  // Attempts to create a new enemy in the current frame
  //   to replace enemies fallen into the hole

  if (newEn > 0) {
    // Choose which type of enemy to create
    int type_ = 1;  // Type of enemy to create (Initially Type 1 - Normal Enemy)
    if (level >= 3) {  // REPLACE WITH LEVEL IN WHICH THE TYPE 3 ENEMY IS INTRODUCED
      float roll = random(0, 1);
      if (roll > 0.6f) {
        type_ = 3;  // Create a block-breaking enemy (Type 3)
      } 
      else if (roll > 0.2f) {
        type_ = 2;  // Create a ghost enemy (Type 2)
      }
    } 
    else if (level >= 2) {  // REPLACE WITH LEVEL IN WHICH THE TYPE 2 ENEMY IS INTRODUCED
      float roll = random(0, 1);
      if (roll > 0.4f) {
        type_ = 2;  // Create a ghost enemy (Type 2)
      }
    }

    // Add the new enemy
    int pastNumber = arrayEn.size();
    enemyCalculator.addEnemy(numTri, gridTri, arrayEn, quadSpaces[0], currentFreeTri, type_, sideTri, radiiEn[type_], vEn[type_]);
    int currentNumber = arrayEn.size();
    newEn -= currentNumber-pastNumber;  // Update number of outstanding enemies to create
  }  // end if(newEn > 0)
}  // end replaceEnemy


public void PLAY() {
  //Begin the Time Count
  t.count();

  // Displaying and animating the level  
  triCalculator.display(numTri, gridTri, sideTri, triTypes[3], quadSpaces[0], playerBall);

  // Testing Enemy Class
  enemyCalculator.display(arrayEn, jump.time);

  newEn += enemyCalculator.moveAndBounce(numTri, gridTri, arrayEn, sideTri, jump.time, quadSpaces[0]);
  replaceEnemy();  // Replace enemies which have fallen into the hole

  // h.display(); // Replaced by the Triangle class display method
  collisions(arrayEn, playerBall, jump.time);

  //check to see if player win the game
  //winGame(sideTri);  
  jump.fall();
  jump.pastFrame();

  if (t.timeOut == true) {//Time is over
    lose = true;
    loseTimeOut = true;
    loseLife = false;//Just make sure that loseLife and loseTimeOut don't overlap each other in the if (win == false && lose == true) condition
  }
  fill(255, 255, 0);
  if (score.countBlock > 3) {//Player has 3 free time to remove or add block. Then it will begin to count
    textFont(createFont("Times", 30));
    text("Number of blocks:" + (score.countBlock - 3), 850, 10);
  }
  else if (score.countBlock <=3) {//The default will appear on the screen when player still has 3 free blocks
    textFont(createFont("Times", 30));
    text("Number of blocks: 0", 855, 10);
  }
  //Appear on the Screen, info of game
  textFont(createFont("Times", 30));

  text("# Jumps Over Enemies: " + score.countNoHit, 817, 50);
  text("LEVEL: " + level, 70, 10);
  fill(255,255,0);
  text("LIVES: " + (life-score.countHit), 900, 670);
  //text(nextSpeedBall, 100, 400); // Use to show the speed of ball and enemies. It with level 7,8 and 9 (Really Challenge)

  //Currently the player will have 5 lives
  if (life - score.countHit == 0) {
    lose = true;
    loseLife = true;
    loseTimeOut = false;//make sure not overlap
  }
  //Calculate the Score
  score.calculate();
}  // end PLAY()

public void tutorial () {
  if (level == 1) {
    println(playerBall.currentTri);
    textSize(30);    
    if (playerBall.currentTri == 18) {
      if (tutorialIndex == 0) {
        fill(0);
        rect(230, 80, 540, 105);
        fill(0, 255, 255);
        text("Welcome to the Ball Drop tutorial", width/2, 100);
        text("(Left-click anywhere to continue)", width/2, 150);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 1) {
        fill(0);
        rect(250, 80, 500, 55);
        fill(0, 255, 255);
        text("Here, you will learn how to play.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 2) {
        fill(0);
        rect(550, 650, 300, 55);
        fill(0, 255, 255);
        text("This is your ball", 700, 680);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 3) {
        fill(0);
        rect(280, 550, 250, 55);
        fill(0, 255, 255);
        text("This is an enemy", 400, 570);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 4) {
        fill(0);
        rect(170, 80, 660, 55);
        fill(0, 255, 255);
        text("Yellow triangles can be changed to blue barriers.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 5) {
        fill(0);
        rect(190, 80, 620, 55);
        fill(0, 255, 255);
        text("Red triangles are tunnels.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 6) {
        fill(0);
        rect(100, 80, 820, 55);
        fill(0, 255, 255);
        text("Grey triangles are permanent barriers.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 7) {
        fill(0);
        rect(700, 450, 300, 200);
        fill(0, 255, 255);
        text("The level exit tunnel or", 850, 500);
        text("\"Hole\". Direct the ball", 850, 550);
        text("here to win the level.", 850, 600);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 8) {
        fill(0);
        rect(120, 0, 580, 150);
        fill(0, 255, 255);
        text("\"Number of blocks\" counts how many times", width/2-85, 10);
        text("you added or removed blue barriers. The", width/2-80, 60);
        text("first three changes are free (not counted).", width/2-80, 110);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 9) {
        fill(0);
        rect(80, 0, 600, 150);
        fill(0, 255, 255);
        text("\"# Jumps Over Enemies\" counts how many", width/2-120, 10);
        text("times you successfully jumped over", width/2-100, 60);
        text("enemies, which increases your score.", width/2-100, 110);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 10) {
        fill(0);
        rect(440, 400, 550, 250);
        fill(0, 255, 255);
        text("This is the number of lives remaining.", 700, 450);
        text("You have 10 lives for each level.", 700, 500);
        text("Each collision with an enemy removes", 700, 550);
        text("one life, so be careful!", 700, 600);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 11) {
        fill(0);
        rect(0, 80, 300, 105);
        fill(0, 255, 255);
        text("Finish the level", 150, 100);
        text("in the time given.", 150, 150);
        tutorial = true;
        isPaused = true;
      }
      if (tutorialIndex == 12) {
        fill(0);
        rect(220, 80, 580, 55);
        fill(0, 255, 255);
        text("Now click on the blue barrier to remove it.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
    }
    if (playerBall.currentTri == 11) {
      if (tutorialIndex == 13) {
        fill(0);
        rect(150, 80, 650, 105);
        fill(0, 255, 255);
        text("Press Space Bar to jump over the enemy.", width/2, 100);
        text("When the ball jumps, it turns white.", width/2, 150);
        tutorial = true;
        isPaused = true;
      }
    }
    if (playerBall.currentTri == 10) {         
      if (tutorialIndex == 14) {  
        fill(0);
        rect(80, 80, 850, 55);
        fill(0, 255, 255);
        text("Place a new blue barrier to prevent the enemy from hitting the ball.", width/2, 100);
        tutorial = true;
        isPaused = true;
      }
    }
    if (playerBall.currentTri == 8) {
      if (tutorialIndex == 15) {
        fill(0);
        rect(250, 0, 500, 55);
        fill(0, 255, 255);
        text("Travelling through a tunnel", width/2, 10);
        tutorial = true;
        isPaused = true;
      }
    }
    if (playerBall.currentTri == 6) {
      fill(0);
      rect(250, 0, 500, 55);
      fill(0, 255, 255);
      text("Exiting a tunnel", width/2, 10);
    }
    if (playerBall.currentTri == 19) {
      if (tutorialIndex == 16) {
        fill(0);
        rect(200, 0, 650, 105);
        fill(0, 255, 255);
        text("Right-click to toggle whether or not", width/2, 10);
        text("the ball will go through the tunnel.", width/2, 60);
        tutorial = true;
        isPaused = true;
      }
    }
    if (playerBall.currentTri >= 20) {
      fill(0);
      rect(200, 80, 650, 55);
      fill(0, 255, 255);
      text("WELL DONE! Now proceed to VICTORY.", width/2, 100);
    }
  }
}

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
  public void display() {
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
  public void move () {
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

// Enemy Class
// ===========
// List of Methods
/*

 void enemyColor(int type) {
 // Setting enemy fill colour based on type
 
 void createEnemies(int numTriangles, Triangle [] grid, ArrayList enemies, int [] numTypes, float side, float [] radiiCount, float [] vCount, int [] freeIndices, int [] spaces) {
 // Creates the initial set of enemies/balls when setting up a level
 // Call this method after finished setting up all Triangle objects
 //    i.e. After calling the method verifyBlocks() in the triangle class
 // Enemies are stored in an ArrayList so that they can be added or removed during play.
 
 void display(ArrayList enemies) {
 // Draw all enemies in the ArrayList enemies
 // Enemies are drawn as circles with little arrows inside indicating their direction
 // This method also has some code which can be uncommented for debugging.
 
 void findFreeTri(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, boolean countAllBalls) {
 // Updates an array to indicate which triangles in the grid are free.
 // Use this function after starting play (i.e. Not during the level setup).
 // There are two versions of this function, depending on the boolean passed as the last argument (see Input Arguments below for details).
 
 void addEnemy(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, int type_, float side, float radiiCount, float vCount) {
 // Adds a single enemy, if there is space in the grid.
 // DO NOT USE this function to create player-controlled balls (Type 0). For now, all player-controlled balls
 //     are created during level setup, not during play. Implement another function if there is a need to
 //     create Type 0 balls during play.
 // The enemy is placed at a position where the sum of its distances to all player-controlled balls is maximized.
 // The enemy is placed at a position that has a path to the hole, such that it could interact with player-controlled balls.
 //     If there is no such position, addEnemy() will not create a new enemy.
 //     Note, however, that the new enemy may be blocked by Type 2 triangles, as these are not considered barriers to a path.
 //     The calculation of the path is performed by the Triangle class method findPath().
 // The enemy starts moving in a direction which intially increases the sum of its distances to all player-controlled balls.
 // Calling this method when there are no player-controlled balls in the level (Type 0 Enemy objects)
 //     will not have any effect.
 
 int maxDistPosition(int numTriangles, Triangle [] grid, ArrayList playerBalls, int [] currentFreeIndices) {
 // Finds the first triangle, starting from a random index, which has the largest sum of its distances to each player-controlled ball.
 // Sub-method of addEnemy()
 // Code has similarities to the reserveSpace() method in the Triangle class
 
int moveAndBounce(int numTriangles, Triangle [] grid, ArrayList enemies, float side, float jumpTime, int hole) {
// Makes Enemy objects move along their paths and bounce around the grid triangles
// This is a macro function which calls other functions appropriate to the movement state of the object.
 
 void removeEnemy(ArrayList enemies, int id){
 // Removes the enemy with the given ID from the ArrayList enemies,
 //     which contains all enemies in existence at the current time.
 // Use this method if an enemy is destroyed because of some event.
 // In the future, should special things happen when enemies are removed (e.g. update player's score),
 //     they would be coded here.

 void speedChange(ArrayList enemies, float [] vCount, boolean up){
   // Iterates over all Enemy objects in the ArrayList enemies
   // Increases the speed of enemy objects if the boolean argument is True
   // Decreases the speed of enemy objects if the boolean argument is False
   // Also affects the speed at which newly created enemies will travel (the vCount array), for consistency.
   // First developed for the project presentation
 
 */

class Enemy {
  float x, y;  // Ball Position
  float r;  // Current ball radius (varies relative to rBase)
  float rBase;  // Ball radius, calculated based on the side length of the grid triangles
  float v;  // Current ball speed (varies relative to vBase), which multiplies the ball's unit direction vector vec[]
  float vBase;  // Ball speed, calculated as a function of the side length of grid triangles
  int path;  // Current path of the ball through the grid of triangles,
  //   which sets the first index used to access elements of pathSet[]
  float pathSet [][] = new float[6][2];  // Six possible main directions of movement:
  /* [0][] = (0,-1)            Up
   [1][] = (-sqrt(3)/2,-1/2) Up Left
   [2][] = (-sqrt(3)/2,1/2)  Down Left
   [3][] = (0,1)             Down
   [4][] = (sqrt(3)/2,1/2)   Down Right
   [5][] = (sqrt(3)/2,-1/2)  Up Right
   */
  float vec [] = new float[2];  // Current unit direction vector of the ball (usually copied from pathSet[][])
  int currentTri;  // ID of the triangle on which the ball is sitting
  int pastTri;  // ID of the ball's previous triangle
  boolean teleport;  // If the ball jumps between the edge and the centre of a teleporter, it will not teleport ("teleport" will be false).
                     // This boolean will remain false if the adjacent triangle that the ball is moving into after not teleporting happens to be the teleporter's linked triangle.
  float t;  // Number of frames until the ball reaches the next transition point between movement states
  int type;  // Type of enemy, which determines its fill colour and other properties
  float alpha;  // Current opacity
  float alphaBase;  // Base opacity
  float alphaMin;  // Minimum opacity
  int state;  // Describes how the enemy is currently moving
  /* 0 - Straight line through the central region of an empty triangle
     1 - Straight line between the edge of an empty triangle and its central region
     2 - Curved bounce off the inside edge of an empty triangle
     3 - Entering a teleporter (edge to rim of drop)
     4 - Descending a teleporter (rim to centre)
     5 - Ascending a teleporter (centre to rim)
     6 - Exiting a teleporter (rim of drop to edge)
     7 - 180 degree turn (used if teleporter is blocked)
     8 - Stopped - "Dead"
  */ 
     
  int id;  // ID of enemy, which equals one plus the number of enemies created before it.

  /* Enemy types
   0 = Player-controlled ball (The code will accomodate creating more than one)
   1 = Normal Enemy
   2 = Enemy which can pass through Type 2 triangles
   3 = Enemy which can destroy Type 2 triangles after a certain number of bounces
 */

  // Constructor
  Enemy(float x_, float y_, float r_, float v_, int path_, int currentTri_, float t_, int type_, int id_) {
    r = rBase = r_;  // To be set later based on calculation in createEnemies()
    
    // Movement variables
    v = vBase = v_;
    path = path_;
    // Initializing set of possible directions, pathSet[6][2]:
    pathSet[0][0] = 0;  // Up
    pathSet[0][1] = -1;
    pathSet[1][0] = -sqrt(3)/2;  // Up Left
    pathSet[1][1] = (float) -1/2;
    pathSet[2][0] = -sqrt(3)/2;  // Down Left
    pathSet[2][1] = (float) 1/2;
    pathSet[3][0] = 0;    // Down
    pathSet[3][1] = 1;
    pathSet[4][0] = sqrt(3)/2;    // Down Right
    pathSet[4][1] = (float) 1/2;
    pathSet[5][0] = sqrt(3)/2;    // Up Right
    pathSet[5][1] = (float) -1/2;
    // Setting current direction
    vec[0] = pathSet[path][0];
    vec[1] = pathSet[path][1];
    // Offset position according to the current direction
    // The resulting position will be at the start of the line segment for movement state "0"
    x = x_+vec[0]*sqrt(3)*r;
    y = y_+vec[1]*sqrt(3)*r;
    
    // Other variables
    currentTri = currentTri_;
    pastTri = currentTri_;
    teleport = true;
    t = t_;
    type = type_;

    if(type == 2){
      alphaBase = 250;
      alphaMin = 50;
    } else {
      alphaBase = 250;
      alphaMin = alphaBase;
    }
    alpha = alphaBase;
        
    state = 0;  // All enemies are created in empty triangles
    id = id_;
  }  // end Constructor


  public void enemyColor(int type, float opacity, float jumpTime) {
    // Setting enemy fill colour based on type
    switch(type) {
    case 0:
      if (jumpTime > 0 ) {
        fill(255);  // White, fully opaque
      } 
      else {
        fill(0xff00FFFD, opacity);  // Bright blue
      }
      break;
    case 1:
      fill(0xffC93737, opacity);  // Red
      break;
    case 2:
      fill(0xffD1439F, opacity);  // Magenta
      break;
    case 3:
      fill(0xffCB7C12, opacity);  // Orange
      break;
    }
  }  // end enemyColor()


  public void createEnemies(int numTriangles, Triangle [] grid, ArrayList enemies, int [] numTypes, float side, float [] radiiCount, float [] vCount, int [] freeIndices, int [] spaces) {
    // Creates the initial set of enemies/balls when setting up a level
    // Call this method after finished setting up all Triangle objects
    //    i.e. After calling the method verifyBlocks() in the triangle class
    // Enemies are stored in an ArrayList so that they can be added or removed during play.
    // Input Arguments
    /*
      numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     enemies = ArrayList to contain all enemies
     numTypes = array with as many elements as there are types of enemies (including Type 0).
     Each element is the number of Type [index] enemies to create.
     side = side length of each triangle in the grid, calculated by createGrid() in the triangle class.
     radiiCount = array with as many elements as there are types of enemies
     Contains the desired number of radii which can fit within the side length of a triangle,
     for each type of enemy.
     In other words, this sets the scale of enemies in proportion to triangles.
     vCount = array with as many elements as there are types of enemies
     Contains the desired number of frames needed for each type of enemy to
     travel a distance equal to the side length of a triangle.
     vCount is kind of like radiiCount, but is used for setting the speed of enemies rather than their radii.
     freeIndices = array with numTriangles elements.
     Unoccupied triangles are indicated by having elements corresponding to their ID equal to their ID.
     Occupied triangles are indicated by having elements corresponding to their ID equal to numTriangles.
     spaces = array containing the indices of the reserved triangles for 4-space objects
     spaces[] was populated by reserve4Spaces() in the Triangle class.
     Elements 1-end are triangles for placing player-controlled balls.
     Element 0 is intended to be the triangle containing the hole.
     */

    // Calculate the radius and speed of each type of enemy
    float radii [] = new float[numTypes.length];
    float speeds [] = new float[numTypes.length];
    for (int i = 0; i < numTypes.length; i +=1) {
      radii[i] = side/radiiCount[i];  // i is the type of enemy
      speeds[i] = side/vCount[i];
    }

    // Initialize other Constructor variables
    float x_ = 0;
    float y_ = 0;
    int path_ = 0;
    int currentTri_ = numTriangles;
    float t_ = 0;

    // Create the desired number of enemies of each type
    // Iterate over each type of enemy
    for (int i = 0; i < numTypes.length; i +=1) {
      // Iterate over the number of enemies of this type to create
      for (int j = 0; j < numTypes[i]; j+=1) {
        // Setting constructor variables
        if (i == 0) {
          // Special code for player-controlled balls (Type 0)
          currentTri_ = spaces[(j+1)];  // Player-controlled balls have pre-reserved triangles
          // spaces[(j+1)] = numTriangles;  // Mark this space as reserved, if desirable.
          // Around each player-controlled ball, place player-controlled blocks (Type 2 Triangles)
          for (int k = 0; k < 3; k +=1) {
            if (grid[currentTri_].adj[k] != numTriangles && grid[grid[currentTri_].adj[k]].type == 0) {
              // This position is inside the grid, and is an empty triangle
              grid[grid[currentTri_].adj[k]].type = 2;
            }
          }  // end for k
        } 
        else {
          currentTri_ = grid[currentTri_].reserveSpace(numTriangles, freeIndices);  // Find a space for the enemy
        }
        path_ = (int) random(0, 6);  // Pick a random direction
        x_ = grid[currentTri_].startingPoints[path_][0];
        y_ = grid[currentTri_].startingPoints[path_][1];
        t_ = vCount[i]*(0.5f-2*sqrt(3)/radiiCount[i]);

        // Use Enemy Constructor and add an element to the enemies ArrayList
        enemies.add(new Enemy(x_, y_, radii[i], speeds[i], path_, currentTri_, t_, i, enemies.size()));
      }  // end for j
    }  // end for i
  }  // end createEnemies()


  public void display(ArrayList enemies, float jumpTime) {
    // Draw all enemies in the ArrayList enemies
    // Enemies are drawn as circles with little arrows inside indicating their direction
    // This method also has some code which can be uncommented for debugging.

    // Initializations
    ellipseMode(CENTER);
    stroke(0);
    Enemy ball;  // Storage for the current enemy
    float r = 0;  // Ball's radius
    float x = 0;  // Ball's x-coordinate
    float y = 0;  // Ball's y-coordinate
    float px = 0;  // Storage for the current ball's path's x-component
    float py = 0;  // Storage for the current ball's path's y-component
    float normx = 0;  // Normal vector x-component
    float normy = 0;  // Normal vector y-component

    for (int i = 0; i < enemies.size(); i +=1) {
      // Collecting data
      ball = (Enemy)enemies.get(i);  // Get the current enemy
      r = ball.r;
      x = ball.x;
      y = ball.y;
      px = (ball.vec[0])*r;
      py = (ball.vec[1])*r;
      normx = -py;
      normy = px;

      // Draw the current ball
      enemyColor(ball.type, ball.alpha, jumpTime);  // Set fill colour
      ellipse(x, y, 2*r, 2*r);  // Circle
      line(x-px, y-py, x+px, y+py);  // Arrow stem
      triangle(x+px, y+py, x+px/3+normx/4, y+py/3+normy/4, x+px/3-normx/4, y+py/3-normy/4);  // Arrow Head
      
      /*
      // Code for Debugging
      fill(0);
      textAlign(CENTER,CENTER);  // Center text
      textSize(3*r/4);
      textLeading(3*r/4);
      // text("Path "+ball.path+"\nTri "+ball.currentTri,x+normx*2,y+normy*2); // Display ball path number and current triangle outside the ellipse
      text("ID "+ball.id,x-normx*2,y-normy*2);  // Display Enemy ID
      // text("State "+ball.state+"\nPath "+ball.path,x-normx*2,y-normy*2);  // Display Enemy state and path
      // text("Teleport\n"+ball.teleport,x-normx*2,y-normy*2);  // Display Enemy teleport boolean
      // text("TriType\n"+gridTri[ball.currentTri].type,x+normx*2,y+normy*2);  // Display the type of the enemy's current triangle
      // text(ball.pastTri,x-normx*2,y-normy*2);  // Display Enemy past triangle
      */
    }  // end for i
  }  // end display()


  public void findFreeTri(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, boolean countAllBalls) {
    // Updates an array to indicate which triangles in the grid are free.
    // Use this function after starting play (i.e. Not during the level setup).
    // There are two versions of this function, depending on the boolean passed as the last argument (see Input Arguments below for details).
    // Input Arguments
    /*
     numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     enemies = ArrayList which contains all enemies currently in existence
     hole = ID of the triangle containing the hole
     currentFreeIndices = empty array of length numTriangles (All exisitng elements will be overwritten)
     countAllBalls = Does the calling function want all balls to count as occupying a triangle, or only player-controlled balls (Type 0)?
     
         If true, triangles which have Enemy objects inside will be noted as occupied,
         but Type 2 triangles will be noted as free (except when occupied by enemy objects).
         This is needed by playerBlock() in the Triangle class.
         
         If false, triangles which have Type != 0 enemy objects inside will be noted as free.
         All triangles not of Type 0 will be noted as occupied.
         Also, triangles adjacent to Type 0 enemy objects or to the hole will be noted as occupied.
         This is what is needed by the method addEnemy() in the Enemy class.
     */
    // Quasi-return value:
    /*
      currentFreeIndices[] is updated such that [ID] = ID indicates the triangle is free,
      while [ID] = numTriangles indicates the triangle is occupied.
    */

    // Which triangles on the grid are, by nature (Type), not free?
    for (int i = 0; i < numTriangles; i +=1) {
      if (grid[i].type == 0 || (grid[i].type == 2 && countAllBalls)) {
        // Free triangle
        currentFreeIndices[i] = i;
      } 
      else {
        // Barrier triangle
        currentFreeIndices[i] = numTriangles;
      }
    }  // end for i over all triangles

    // Next, take the position of the hole
    currentFreeIndices[hole] = numTriangles;
    if (!countAllBalls) {
      // Mark triangles adjcent to the hole as occupied
      for (int i = 0; i < 3; i +=1) {
        if (grid[hole].adj[i] != numTriangles) {
          currentFreeIndices[grid[hole].adj[i]] = numTriangles;
        }
      }  // end for i over triangles adjacent to the hole
    }  // end if(!countAllBalls)

    // Finally, check where all Enemy objects are located.
    Enemy ball;  // Storage for the current enemy    
    for (int i = 0; i < enemies.size(); i +=1) {
      ball = (Enemy)enemies.get(i);  // Get the current enemy
      if (countAllBalls) {
        // Mark all triangles containing balls as occupied
        currentFreeIndices[ball.currentTri] = numTriangles;
        // If a ball is passing between triangles, it will still be inside its past triangle
        if(ball.state == 0 || ball.state == 1 || ball.state == 3){
          // Find the back end of the ball
          float backx = ball.x - ball.r*ball.vec[0];
          float backy = ball.y - ball.r*ball.vec[1];
          if(grid[0].inside(ball.pastTri, grid, backx, backy)){
            // The ball's outer edge is inside its past triangle - mark it as occupied
            currentFreeIndices[ball.pastTri] = numTriangles;
          }
        }  // end if(ball.state == 0 || ball.state == 1 || ball.state == 3)
      } 
      else {
        // We are counting only Type 0 balls and their adjacent triangles as occupied
        if (ball.type == 0) {
          currentFreeIndices[ball.currentTri] = numTriangles;
          for (int j = 0; j < 3; j +=1) {
            if (grid[ball.currentTri].adj[j] != numTriangles) {
              currentFreeIndices[grid[ball.currentTri].adj[j]] = numTriangles;
            }
          }  // end for j
        }  // end if(ball.type == 0)
      }  // end else-block of if(countAllBalls)
    }  // end for i
  }  // end findFreeTri()


  public void addEnemy(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, int type_, float side, float radiiCount, float vCount) {
    // Adds a single enemy, if there is space in the grid.
    // DO NOT USE this function to create player-controlled balls (Type 0). For now, all player-controlled balls
    //     are created during level setup, not during play. Implement another function if there is a need to
    //     create Type 0 balls during play.
    // The enemy is placed at a position where the sum of its distances to all player-controlled balls is maximized.
    // The enemy is placed at a position that has a path to the hole, such that it could interact with player-controlled balls.
    //     If there is no such position, addEnemy() will not create a new enemy.
    //     Note, however, that the new enemy may be blocked by Type 2 triangles, as these are not considered barriers to a path.
    //     The calculation of the path is performed by the Triangle class method findPath().
    // The enemy starts moving in a direction which intially increases the sum of its distances to all player-controlled balls.
    // Calling this method when there are no player-controlled balls in the level (Type 0 Enemy objects)
    //     will not have any effect.
    // Input Arguments
    /*
      numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     enemies = ArrayList which contains all enemies currently in existence
     hole = ID of the triangle containing the hole
     currentFreeIndices = empty array of length numTriangles (All exisitng elements will be overwritten)
     type_ = type of enemy to create
     side = side length of each triangle in the grid, calculated by createGrid() in the triangle class.
     radiiCount = (element of radiiCount[]) Desired number of radii for this type of enemy which can fit within the side length of a triangle
     vCount = (element of vCount[]) Desired number of frames needed for this type of enemy to travel a distance equal to the side length of a triangle
     */
    // Quasi-return value:
    /*
      enemies is updated to contain the new enemy
     */

    // Retrieve all player-controlled balls
    ArrayList playerBalls = new ArrayList();
    Enemy ball;  // Storage for the current enemy
    for (int i = 0; i < enemies.size(); i += 1) {
      ball = (Enemy)enemies.get(i);  // Get the current Enemy object
      if (ball.type == 0) {
        // This is a player-controlled ball. Store it in playerBalls
        playerBalls.add(ball);
      }
    }  // end for i over all enemies

    // Only continue if there is at least one player-controlled ball
    if (playerBalls.size() != 0) {

      // Computing a list of suitable triangles on which the enemy could be placed
      // =========================================================================
      boolean isSpace = false;  // Is there a suitable triangle on which to place the new enemy?
      findFreeTri(numTriangles, grid, enemies, hole, currentFreeIndices, false);  // Get list of all free triangles

      // Using findPath() to determine the path surrounding the hole:
      int holePath [] = new int[numTriangles];
      int holeBarriers [] = new int[numTriangles];
      for (int i = 0; i < numTriangles; i +=1) {
        holePath[i] = numTriangles;
        holeBarriers[i] = numTriangles;
      }  // end for i to initialize findPath() input arrays
      grid[0].findPath(numTriangles, grid, hole, holePath, holeBarriers);  // Compute path

      // Determining the intersection of holePath[] and currentFreeIndices[]
      for (int i = 0; i < numTriangles; i +=1) {
        if (currentFreeIndices[i] != numTriangles) {
          // This triangle is unoccupied. Check if it is on the path.
          if (grid[0].searchArray( holePath, currentFreeIndices[i] ) == numTriangles) {
            // This triangle is not on the path of the hole
            currentFreeIndices[i] = numTriangles;
          }
        }
      }  // end for i over currentFreeIndices[]

      // Is there at least one free triangle on the path of the hole?
      for (int i = 0; i < numTriangles; i +=1) {
        if (currentFreeIndices[i] != numTriangles) {
          isSpace = true;
          break;
        }
      }  // end for i

      // Create the enemy in a free triangle which maximizes the enemy's sum of distances to all player balls
      // ====================================================================================================
      if (isSpace) {

        // Retrieve the triangle where the distance to all player balls is maximized
        int currentTri_ = maxDistPosition(numTriangles, grid, playerBalls, currentFreeIndices);
        Triangle tri = grid[currentTri_];  // Getting this triangle for use in subsequent calculations
        
        // Creating the new Enemy object
        int path_ = (int) random(0, 6);  // Pick a random direction
        float x_ = tri.startingPoints[path_][0];
        float y_ = tri.startingPoints[path_][1];
        float r_ = side/radiiCount;
        float v_ = side/vCount;
        float t_ = vCount*(0.5f-2*sqrt(3)/radiiCount);
        enemies.add(new Enemy(x_, y_, r_, v_, path_, currentTri_, t_, type_, enemies.size()));
      }  // end if(isSpace)
    }  // end if(playerBalls.size != 0)
  }  // end addEnemy()


  public int maxDistPosition(int numTriangles, Triangle [] grid, ArrayList playerBalls, int [] currentFreeIndices) {
    // Finds the first triangle, starting from a random index, which has the largest sum of its distances to each player-controlled ball.
    // Sub-method of addEnemy()
    // Code has similarities to the reserveSpace() method in the Triangle class
    // Input Arguments
    /*
      numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     playerBalls = ArrayList which contains all Type 0 enemies currently in existence
     currentFreeIndices = array of length numTriangles indicating which triangles are currently unoccupied (updated within addEnemy())
     */
    // Return Value: Index of the triangle which meets the maximum distance criteria

    // Initializing
    int index = (int) random(0, numTriangles);  // ID of the current triangle under study, picked randomly initially
    float d = 0;  // Sum of distances for the current triangle under study
    Enemy ball;  // Storage for the current player ball
    int maxid = numTriangles;  // Triangle corresponding to the maximum distance
    float max = 0;  // Current maximum distance
    int inc = (int) random(0, 2)*2-1;  // Will be +1 or -1. Direction to search for the next index.

    // Iterate over all triangles
    for (int i = 0; i < numTriangles; i += 1) {

      if (currentFreeIndices[index] != numTriangles) {
        // Examine this triangle, as it is unoccupied
        for (int j = 0; j < playerBalls.size(); j +=1) {
          // Calculate the sum of the distances
          ball = (Enemy)playerBalls.get(j);  // Get the current player ball
          d += dist(grid[index].x, grid[index].y, ball.x, ball.y);
        }  // end for j over playerBalls

        // Compare with the previously computed maximum sum of distances
        if (d > max) {
          max = d;
          maxid = index;
        }
      }  // end if(currentFreeIndices[index] != numTriangles)

      // Prepare for the next iteration
      d = 0;
      index += inc;  // Go to the next index
      // If we reach a boundary of the array
      if (index == numTriangles) {
        // Start from the beginning
        index = 0;
      } 
      else if (index == -1) {
        // Go back to the end
        index = numTriangles-1;
      }
    }  // end for i over all triangles

    return maxid;
  }  // end maxDistPosition()


  public int moveAndBounce(int numTriangles, Triangle [] grid, ArrayList enemies, float side, float jumpTime, int hole) {
    // Makes Enemy objects move along their paths and bounce around the grid triangles
    // This is a macro function which calls other functions appropriate to the movement state of the object.
    // Each of the sub-functions of moveAndBounce does the following
    /*     -Make the enemy move in the appropriate pattern
           -If the state of movement will change this frame (time remaining is less than 1)
               -Fix the enemy's position, speed, and radius to their values at the transition point
               -Set the enemy's next state of movement
               -Determine how many frames remain until the state of movement will next change
               -Set the enemy's next current triangle, and save the current triangle as its past triangle, if applicable
    */
    // Input arguments
    /*
     numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     enemies = ArrayList which contains all enemies currently in existence
     side = side length of each triangle in the grid, calculated by createGrid() in the triangle class.
     jumpTime = amount of time remaining in the jump of the player's ball
     hole = ID of the triangle containing the hole
            The hole will behave like a blocked teleporter for enemies, or a one-way teleporter for the player's ball
   */
   // Return Value: If enemies fall into the hole, the same number of new enemies must be generated to replace them.

    // Initializing variables
    Enemy ball;  // Storage for the current enemy
    int newEnemies = 0;  // Number of enemies which fall into the hole and need to be replaced
    float depth = 1;  // Depth of a teleporter (affects enemy descent and ascent speed)
                       // Expressed as a multiple of "side".
    float deathTime = 120;  // Number of frames for a "dead" enemy (or the player's ball) to lie still
                            // before being removed from the level

    // Iterate over all enemies in the ArrayList enemies
    for (int i = 0; i < enemies.size(); i +=1) {
      ball = (Enemy)enemies.get(i);  // Get the current enemy
      if(ball.state != 8){
        ball.t -= 1;  // Count down to the next transition point
      }
      
      // An enemy-specific motion control variable
      float rotTime = PI*ball.rBase/ball.vBase;  // Number of frames for a 180 degree rotation

      // Determine which sub-function to execute
      switch(ball.state)
      {
        case 0:
          // Crossing an empty triangle
          move0(ball, grid);
          break;
        case 1:
          // Going to the edge of an empty triangle
          move1(ball, grid, side, hole);
          break;
        case 2:
          // Bounce off of a barrier, while in an empty triangle
          move2(ball, grid, side);
          break;
        case 3:
          // Go from the edge to the rim of a teleporter
          move3(ball, grid, side, depth);
          break;
        case 4:
          // Go to the centre of a teleporter from the rim
          newEnemies += move4(enemies, ball, grid, side, hole);
          break;
        case 5:
          // Go to the rim of a teleporter from the centre
          move5(ball, grid, side, rotTime);
          break;
        case 6:
          // Go from the rim of a teleporter to the edge
          move6(ball, grid, side);
          break;
        case 7:
          // 180 degree turn to bounce off of a barrier while in a teleporter
          move7(ball, grid, side, jumpTime, rotTime);
          break;
        case 8:
          // Dead - Stop, and count down until removal
          if(ball.t >= 0) {
            ball.t = -deathTime; // Initialize death timer
          }
          move8(enemies, ball);
          break;
      }  // end switch(ball.state)
    }  // end for i over all enemies
    
    return newEnemies;
  }  // end moveAndBounce()
  
  
      public void move0(Enemy ball, Triangle [] grid){
        // Sub-Function of moveAndBounce()
        if(ball.t < 1){
          // This frame is a transition point
          Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle

          // Move the enemy to the end of the path
          ball.x = tri.collisionPoints[ball.path][0]-sqrt(3)*ball.r*ball.vec[0];
          ball.y = tri.collisionPoints[ball.path][1]-sqrt(3)*ball.r*ball.vec[1];
          
          // Check what the next movement state should be, and set transition time appropriately
          // Is this triangle at the edge of the grid?
          if (tri.rebound[ball.path][0] == grid.length) {
            // Outside the grid. Make the ball bounce back
            ball.state = 2;
            ball.t = 2*PI*ball.r/(3*ball.v);
          }  // end if-block of if(tri.rebound[ball.path][0] == numTriangles)

          else {
            // There is another triangle over the edge. What kind of triangle is it?
            switch(grid[tri.rebound[ball.path][0]].type) {
            case 0:  // Empty triangle, or
            case 3:  // Teleporter
              ball.pastTri = ball.currentTri;
              ball.currentTri = tri.rebound[ball.path][0];  // This will be the ball's next current triangle
              ball.state = 1;  // Go to this triangle
              ball.t = sqrt(3)*ball.r/ball.v;
              break;
            case 1:  // Barrier
              ball.state = 2;  // Make ball bounce back
              ball.t = 2*PI*ball.r/(3*ball.v);
              break;
            case 2:  // Player-controlled barrier
              if(ball.type == 2){
                // Ghost enemy goes through Type 2 triangles
                ball.pastTri = ball.currentTri;
                ball.currentTri = tri.rebound[ball.path][0];  // This will be the ball's next current triangle
                ball.state = 1;  // Go to this triangle
                ball.t = sqrt(3)*ball.r/ball.v;
              } else {
                ball.state = 2;  // Make ball bounce back
                ball.t = 2*PI*ball.r/(3*ball.v);
              }
              break;
            }  // end switch(grid[tri.rebound[ball.path][0]].type)
          }  // end else-block of if(tri.rebound[ball.path][0] == numTriangles)
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Move according to the state
          ball.x += (ball.v)*(ball.vec[0]);
          ball.y += (ball.v)*(ball.vec[1]);
        }  // end else-block of if(ball.t < 1)
      }  // end move0()
      
      
      public void move1(Enemy ball, Triangle [] grid, float side, int hole){
        // Sub-Function of moveAndBounce()      
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle
  
        if(ball.t < 1){
          // This frame is a transition point
          
          // Move the enemy to the end of the path
          ball.x = tri.startingPoints[ball.path][0];
          ball.y = tri.startingPoints[ball.path][1];
          
          // Set final opacity of a Type 2 enemy
          if(ball.type == 2 && tri.type == 2){
            ball.alpha = ball.alphaMin;
          } else if(ball.type == 2 && tri.type != 2){
            ball.alpha = ball.alphaBase;
          }
          
          // What kind of triangle is it? (Only 0 or 3; The ball would not be in State 1 otherwise.)
          if(tri.id == hole){
              ball.state = 3;  // Go to the rim of the hole
              ball.t = ball.r/ball.v;
          }  // end if-block of if(tri.id == hole)
          else {
            switch(tri.type) {
            case 0:  // Empty triangle, or
            case 2:  // Player-set barrier (for a Type 2 enemy)
              ball.state = 0;  // Go through the empty triangle
              ball.t = (side/2-sqrt(3)*ball.r)/ball.v;
              break;
            case 3:  // Teleporter
              ball.state = 3;  // Go to the rim of the teleporter
              ball.t = ball.r/ball.v;
              break;
            }  // end switch(tri.type)
          }  // end else-block of if(tri.id == hole)
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Move according to the state
          ball.x += (ball.v)*(ball.vec[0]);
          ball.y += (ball.v)*(ball.vec[1]);
          
          // Adjust opacity of Type 2 enemy
          if(ball.type == 2 && tri.type == 2){
            // Going down to minimum opacity when approaching a Type 2 triangle
            if(ball.alpha > ball.alphaMin){
              float tmax = sqrt(3)*ball.r/ball.v;
              ball.alpha = ball.alphaBase-(ball.alphaBase-ball.alphaMin)*(tmax-ball.t)/tmax;
            }
          } else if(ball.type == 2 && tri.type != 2){
            // Going up to full opacity when approaching a Type 0 triangle
            if(ball.alpha < ball.alphaBase){
              float tmax = sqrt(3)*ball.r/ball.v;
              ball.alpha = ball.alphaMin+(ball.alphaBase-ball.alphaMin)*(tmax-ball.t)/tmax;
            }
          }
        }  // end else-block of if(ball.t < 1)
      }  // end move1()
      
      
      public void move2(Enemy ball, Triangle [] grid, float side){
        // Sub-Function of moveAndBounce()
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle
        
        // Geometry variables to shorten calculation code
        float midpoint [] = new float[2];  // Relevant triangle midpoint for the bounce motion
        midpoint[0] = tri.collisionPoints[ball.path][0];
        midpoint[1] = tri.collisionPoints[ball.path][1];
        float v1 [] = new float[2];  // Initial ball direction vector
        v1[0] = ball.pathSet[ball.path][0];
        v1[1] = ball.pathSet[ball.path][1];
        float v2 [] = new float[2];  // Final ball direction vector
        v2[0] = ball.pathSet[tri.rebound[ball.path][1]][0];
        v2[1] = ball.pathSet[tri.rebound[ball.path][1]][1];
        float time = 2*PI*ball.r/(3*ball.v)-ball.t;  // Time since the start of the state
        float angle = PI/3-ball.v*time/ball.r;  // Angle relative to the midline of the triangle
        
        if(ball.t < 1){
          // This frame is a transition point
          // Move the enemy to the end of the path
          ball.x = midpoint[0]+sqrt(3)*ball.r*v2[0];
          ball.y = midpoint[1]+sqrt(3)*ball.r*v2[1];
          // Finalize the path of the ball, and its current direction vector
          ball.path = tri.rebound[ball.path][1];
          ball.vec[0] = v2[0];
          ball.vec[1] = v2[1];
          // Set the next state of the ball
          ball.state = 0;
          ball.t = (side/2-2*sqrt(3)*ball.r)/ball.v;
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Move according to the state
          // Current Position
          ball.x = midpoint[0]+ball.r*((2-cos(angle))/sqrt(3)*(v2[0]-v1[0])-sin(angle)*(v1[0]+v2[0]));
          ball.y = midpoint[1]+ball.r*((2-cos(angle))/sqrt(3)*(v2[1]-v1[1])-sin(angle)*(v1[1]+v2[1]));
          // Current Direction
          ball.vec[0] = cos(angle)*(v1[0]+v2[0])+sin(angle)/sqrt(3)*(v1[0]-v2[0]);
          ball.vec[1] = cos(angle)*(v1[1]+v2[1])+sin(angle)/sqrt(3)*(v1[1]-v2[1]);
          
          // Code for Type 2 triangle-destroying enemy (Type 3 Enemy)
          if(ball.type == 3 && tri.rebound[ball.path][0] != grid.length){
            // The Type 3 enemy is bouncing off a triangle, not the edge of the grid
            float tmid = PI*ball.r/(3*ball.v);
            if(ball.t >= tmid - 0.5f && ball.t < tmid + 0.5f){
              // The ball is at the midpoint of the bounce
              Triangle block = grid[tri.rebound[ball.path][0]];  // Get the adjacent barrier
              if(block.type == 2){
                // Type 2 barriers can be damaged by Type 3 enemies
                block.bounce += 1;
                if(block.bounce == block.bounceMax){
                  // This barrier is now broken
                  block.bounce = 0;
                  block.type = 0;
                }  // end if(block.bounce == block.bounceMax)
              }  // end if(block.type == 2)
            }  // end if(ball.t >= tmid - 0.5 && ball.t < tmid + 0.5)
          }  // end if(ball.type == 3)
        }  // end else-block of if(ball.t < 1)
      }  // end move2()
      
      
      public void move3(Enemy ball, Triangle [] grid, float side, float depth){
        // Sub-Function of moveAndBounce()
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle
        
        if(ball.t < 1){
          // This frame is a transition point

          // Distance and direction to centre of teleporter
          float d = dist(tri.x,tri.y,tri.startingPoints[ball.path][0],tri.startingPoints[ball.path][1]);  // For normalization of ball's direction vector
          ball.vec[0] = (tri.x-tri.startingPoints[ball.path][0])/d;
          ball.vec[1] = (tri.y-tri.startingPoints[ball.path][1])/d;
          
          // Final position of ball
          ball.x = tri.startingPoints[ball.path][0]+ball.vec[0]*ball.r;
          ball.y = tri.startingPoints[ball.path][1]+ball.vec[1]*ball.r;
          
          // Descend towards the centre of this teleporter
          ball.state = 4;
          float distance = side/2/sqrt(3)-ball.r;  // To shorten the following two calculations
          ball.v = ball.vBase*(distance)/sqrt(sq(depth*side)+sq(distance));
          ball.t = distance/ball.v;
        }  // end if-block of if(ball.t < 1)
        
        else {
          
          // Rotating the ball to point at the centre of the teleporter
          float tmax = ball.r/ball.v;  // Total duration of this state
          float d2 = dist(tri.x,tri.y,tri.startingPoints[ball.path][0],tri.startingPoints[ball.path][1]);  // For normalization of v2
          float v2[] = new float[2];
          v2[0] = (tri.x-tri.startingPoints[ball.path][0])/d2;
          v2[1] = (tri.y-tri.startingPoints[ball.path][1])/d2;
          float vCurrent [] = new float[2];  // Ball's current direction vector
          vCurrent[0] = (tmax-ball.t)/tmax*v2[0]+ball.t/tmax*ball.pathSet[ball.path][0];
          vCurrent[1] = (tmax-ball.t)/tmax*v2[1]+ball.t/tmax*ball.pathSet[ball.path][1];
          float vCurrentLength = sqrt(sq(vCurrent[0])+sq(vCurrent[1]));
          
          // Ball's current direction vector
          ball.vec[0] = vCurrent[0]/vCurrentLength;
          ball.vec[1] = vCurrent[1]/vCurrentLength;
          
          // Move ball according to the state
          ball.x += (ball.v)*(v2[0]);
          ball.y += (ball.v)*(v2[1]);
        }  // end else-block of if(ball.t < 1)
      }  // end move3()
      
      
      public int move4(ArrayList enemies, Enemy ball, Triangle [] grid, float side, int hole){
        // Sub-Function of moveAndBounce()
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle

        if(ball.t < 1){
          // This frame is a transition point
          ball.r = 0;  // Ball disappears at the bottom of the teleporter
          
          // Check if the triangle is the hole, or a teleporter
          if(tri.id == hole && ball.type == 0){
            // End the game
            win = true;
          } else if(tri.id == hole && ball.type != 0){
            // Enemy will be removed later in this function
          } else {
            // Triangle is a teleporter
            if(ball.teleport){
              // Teleport the ball, if it will not result in a two-triangle loop
              ball.pastTri = ball.currentTri;
              ball.currentTri = tri.link;
              tri = grid[ball.currentTri];  // Move ball to the linked triangle
            } else {
              // Reset enemy teleportation state
              ball.teleport = true;
            }
          }
          
          // Place ball at centre of current triangle
          ball.x = tri.x;
          ball.y = tri.y;
          // Distance and direction to edge of teleporter
          float d = dist(tri.collisionPoints[ball.path][0],tri.collisionPoints[ball.path][1],ball.x,ball.y);  // For normalization of ball's direction vector
          ball.vec[0] = (tri.collisionPoints[ball.path][0]-ball.x)/d;
          ball.vec[1] = (tri.collisionPoints[ball.path][1]-ball.y)/d;
          // The ball will climb up its current teleporter
          ball.state = 5;
          ball.t = (side/2/sqrt(3)-ball.rBase)/ball.v;
          
          if(tri.id == hole && ball.type != 0){
            // Remove the enemy
            removeEnemy(enemies, ball.id);
            return 1;
          }

        }  // end if-block of if(ball.t < 1)
        
        else {
          // Move according to the state
          ball.x += (ball.v)*(ball.vec[0]);
          ball.y += (ball.v)*(ball.vec[1]);
          float tInterval = (side/2/sqrt(3)-ball.rBase)/ball.v;
          ball.r = ball.rBase*(ball.t/tInterval);
        }  // end else-block of if(ball.t < 1)
        return 0;
      }  // end move4()
      
      public void move5(Enemy ball, Triangle [] grid, float side, float rotTime){
        // Sub-Function of moveAndBounce()
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle

        if(ball.t < 1){
          // This frame is a transition point
          // Final position of ball
          ball.r = ball.rBase;  // Ball is back to full size
          ball.x = tri.collisionPoints[ball.path][0]-ball.vec[0]*ball.r;
          ball.y = tri.collisionPoints[ball.path][1]-ball.vec[1]*ball.r;
          
          // Next state of ball depends on adjacent triangle
          // Is this triangle at the edge of the grid?
          if (tri.rebound[ball.path][0] == grid.length) {
            // Outside the grid. Make the ball bounce back
            ball.state = 7;
            ball.path = tri.rebound[ball.path][1];  // Change path of ball
            ball.t = rotTime;
          }  // end if-block of if(tri.rebound[ball.path][0] == numTriangles)

          else {
            // There is another triangle over the edge. What kind of triangle is it?
            switch(grid[tri.rebound[ball.path][0]].type) {
            case 0:  // Empty triangle, or
            case 3:  // Teleporter
              if(ball.type != 0){
                // Prevent enemies from entering infinite teleportation loops
                if(grid[tri.rebound[ball.path][0]].link == ball.currentTri){
                  // Prevent an infinite teleportation loop
                  ball.teleport = false;
                }
              }
              ball.pastTri = ball.currentTri;
              ball.currentTri = tri.rebound[ball.path][0];  // This will be the ball's next current triangle
              ball.state = 6;  // Go to this triangle
              ball.v = ball.vBase;  // Ball is back to full speed
              ball.t = ball.r/ball.v;
              break;
            case 1:  // Barrier
                ball.state = 7;  // Make ball bounce back
                ball.path = tri.rebound[ball.path][1];  // Change path of ball
                ball.t = rotTime;
                break;
            case 2:  // Player-controlled barrier
              if(ball.type != 2){
                
                // Code for Type 2 triangle-destroying enemy (Type 3 Enemy)
                if(ball.type == 3){
                  Triangle block = grid[tri.rebound[ball.path][0]];  // Get the adjacent barrier
                  block.bounce += 1;
                  if(block.bounce == block.bounceMax){
                    // This barrier is now broken
                    block.bounce = 0;
                    block.type = 0;
                  }  // end if(block.bounce == block.bounceMax)
                }  // end if(ball.type == 3)
                
                ball.state = 7;  // Make ball bounce back
                ball.path = tri.rebound[ball.path][1];  // Change path of ball
                ball.t = rotTime;
                
              } else if(ball.type == 2){
                // Type 2 enemies can pass through type 2 triangles
                ball.pastTri = ball.currentTri;
                ball.currentTri = tri.rebound[ball.path][0];  // This will be the ball's next current triangle
                ball.state = 6;  // Go to this triangle
                ball.v = ball.vBase;  // Ball is back to full speed
                ball.t = ball.r/ball.v;
              }
              break;
            }  // end switch(grid[tri.rebound[ball.path][0]].type)
          }  // end else-block of if(tri.rebound[ball.path][0] == numTriangles)          
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Move according to the state
          ball.x += (ball.v)*(ball.vec[0]);
          ball.y += (ball.v)*(ball.vec[1]);
          float tInterval = (side/2/sqrt(3)-ball.rBase)/ball.v;
          ball.r = ball.rBase*(tInterval-ball.t)/tInterval;
        }  // end else-block of if(ball.t < 1)
      }  // end move5()
      
      public void move6(Enemy ball, Triangle [] grid, float side){
        // Sub-Function of moveAndBounce()
        Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle
        
        if(ball.t < 1){
          // This frame is a transition point
          // Set the ball's final position
          ball.x = tri.startingPoints[ball.path][0];
          ball.y = tri.startingPoints[ball.path][1];
          // Ball's final direction
          ball.vec[0] = ball.pathSet[ball.path][0];
          ball.vec[1] = ball.pathSet[ball.path][1];
          
          // What kind of triangle is it? (Only 0, 2 - Type 2 Enemy only, or 3; The ball would not be in State 6 otherwise.)
          switch(tri.type) {
          case 0:  // Empty triangle
            ball.state = 0;  // Go through the empty triangle
            ball.t = (side/2-sqrt(3)*ball.r)/ball.v;
            break;
          case 2:
            ball.state = 0;  // Go through the triangle
            ball.t = (side/2-sqrt(3)*ball.r)/ball.v;
            ball.alpha = ball.alphaMin;  // Final opacity of Type 2 enemy
            break;
          case 3:  // Teleporter
            ball.state = 3;  // Go to the rim of the teleporter
            ball.t = ball.r/ball.v;
            break;
          }  // end switch(tri.type)
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Rotating the ball to point along its next path
          float tmax = ball.r/ball.v;  // Total duration of this state
          float d1 = dist(tri.x,tri.y,tri.startingPoints[ball.path][0],tri.startingPoints[ball.path][1]);  // For normalization of v2
          float v1[] = new float[2];
          v1[0] = (tri.x-tri.startingPoints[ball.path][0])/d1;
          v1[1] = (tri.y-tri.startingPoints[ball.path][1])/d1;
          float vCurrent [] = new float[2];  // Ball's current direction vector
          vCurrent[0] = ball.t/tmax*v1[0]+(tmax-ball.t)/tmax*ball.pathSet[ball.path][0];
          vCurrent[1] = ball.t/tmax*v1[1]+(tmax-ball.t)/tmax*ball.pathSet[ball.path][1];
          float vCurrentLength = sqrt(sq(vCurrent[0])+sq(vCurrent[1]));
          
          // Ball's current direction vector
          ball.vec[0] = vCurrent[0]/vCurrentLength;
          ball.vec[1] = vCurrent[1]/vCurrentLength;

          // Move according to the state
          ball.x += (ball.v)*(v1[0]);
          ball.y += (ball.v)*(v1[1]);

          // Adjust opacity of Type 2 enemy entering a Type 2 triangle
          if(ball.type == 2 && tri.type == 2){
            // Going down to minimum opacity when approaching a Type 2 triangle
            if(ball.alpha > ball.alphaMin){
              ball.alpha = ball.alphaBase-(ball.alphaBase-ball.alphaMin)*(tmax-ball.t)/tmax;
            }
          }  // end if(ball.type == 2 && tri.type == 2)       
        }  // end else-block of if(ball.t < 1)
      }  // end move6()
      
      public void move7(Enemy ball, Triangle [] grid, float side, float jumpTime, float rotTime){
        // Sub-Function of moveAndBounce()
        if(ball.t < 1){
          // This frame is a transition point
          Triangle tri = grid[ball.currentTri];  // Get the enemy's current triangle
          ball.state = 4;  // Go back down the teleporter
          ball.t = (side/2/sqrt(3)-ball.r)/ball.v;
          // Distance and direction to centre of teleporter
          float d = dist(tri.x,tri.y,ball.x,ball.y);  // For normalization of ball's direction vector
          ball.vec[0] = (tri.x-ball.x)/d;
          ball.vec[1] = (tri.y-ball.y)/d;
        }  // end if-block of if(ball.t < 1)
        
        else {
          // Rotate the ball
          float inc = PI/rotTime;  // Rotation speed and direction
          float vxNew = ball.vec[0]*cos(inc)-ball.vec[1]*sin(inc);
          float vyNew = ball.vec[1]*cos(inc)+ball.vec[0]*sin(inc);
          ball.vec[0] = vxNew;
          ball.vec[1] = vyNew;
        }  // end else-block of if(ball.t < 1)
      }  // end move7()
      
      public void move8(ArrayList enemies, Enemy ball){
        // Sub-Function of moveAndBounce()
        if(ball.t > -2){
          // Remove the corpse from the level
          removeEnemy(enemies, ball.id);          
        } else {
          ball.t += 1;  // Dead enemy objects have an increasing timer
        }
      }  // end move8()


  public void removeEnemy(ArrayList enemies, int id) {
    // Removes the enemy with the given ID from the ArrayList enemies,
    //     which contains all enemies in existence at the current time.
    // Use this method if an enemy is destroyed because of some event.
    // In the future, should special things happen when enemies are removed (e.g. update player's score),
    //     they would be coded here.
    // Input Arguments
    /*
      enemies = contains all enemies currently in the level
      id = ID of the enemy to remove
    */

    enemies.remove(id);  // Removes the enemy in question

    // Update the IDs of all objects in the ArrayList enemies
    Enemy ball;  // Holder for the current enemy
    for (int i = 0; i < enemies.size(); i +=1) {
      ball = (Enemy)enemies.get(i);
      ball.id = i;
    }  // end for i over ArrayList enemies
  }  // end removeEnemy()
  
  
  public void speedChange(ArrayList enemies, float [] vCount, boolean up){
    // Iterates over all Enemy objects in the ArrayList enemies
    // Increases the speed of enemy objects if the boolean argument is True
    // Decreases the speed of enemy objects if the boolean argument is False
    // Also affects the speed at which newly created enemies will travel (the vCount array), for consistency.
    // First developed for the project presentation
    
    float percentChange = 20;  // The percentage by which Enemy object speeds will change
    float factor;  // Factor by which Enemy object speeds will be multiplied
    if(up){
      // Increase speeds
      factor = 1 + percentChange/100;
    } else {
      // Decrease speeds
      factor = 1 - percentChange/100;
    }  // end if(up)
    
    // Apply the change in speed to all Enemy objects
    Enemy ball;  // Holder for the current enemy
    for (int i = 0; i < enemies.size(); i +=1) {
      ball = (Enemy)enemies.get(i);
      ball.vBase *= factor;
      ball.v *= factor;
      ball.t /= factor;  // Time is inversely-proportional to speed
    }  // end for i over ArrayList enemies
    
    // Apply the change in speed to the future generation of Enemy objects in this level
    // This change will be reset by the function which creates new levels, however.
    for(int i = 0; i < vCount.length; i +=1){
      vCount[i] /= factor;
    }  // end for i over vCount[]
  }  // end speedChange()
  
}  // end Enemy class

class Hole {
  float xhole;
  float yhole;
  float diameter;
  int c;
  
  Hole (float x, float y, float side){
    xhole = x;
    yhole = y;
    diameter = side/sqrt(3);
    c = color (255,0,255,200);
  }
  
  public void display(){
    fill (c);
    ellipse(xhole, yhole, diameter,diameter);
  }
}
  
//Instruction instruct = new Instruction;
class Instruction {
  //PImage instructionImage;
  Instruction () {
  }
  public void load() {
    //instructionImage = loadImage ("InstructionImage.png");
  }
  public void display(){
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

class JUMP {
  //ball jump
  float time = 0; //Jmp time length
  //float delay;
  int pastSpacebarFrame = 0;//How long ago was the spacebar pressed?

  JUMP () {
  }
  public void action () {
    if (time <= 0 && pastSpacebarFrame < (frameCount - 1)) {
      time = sideTri/2/playerBall.vBase;
    }
  }
  public void fall () {
    // If ball is jumping, decrease jumping time counter
    if (time > 0) {
      time --;
    }
  }
  public void pastFrame () {
    // Record the frame when the spacebar was last pressed
    if (keyPressed && key == ' ') {
      pastSpacebarFrame = frameCount;
    }
  }
}

//LEVEL chooselevel = new LEVEL ();
class LEVEL {
  public void up () {
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

class Lose {
  public void display() {
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
  public void press () {
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

class MORETIME {
  int helpTime;
  public void time() {
    if (moreTime == true) { //Need more time? add 1 min to the timeCount, call setup() again to reset that level
      helpTime = 120;
      level--;
      setup();
      moreTime = false;//if moreTime = true, void draw() will call setup() countless time
    }
  }
}

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
  public void load () {    
    title = loadImage ("BallDrop.png");
    button [0] = loadImage ("StartButton.png");
    button [4] = loadImage("ResumeButton.png");// 3->4
    button [1] = loadImage ("SelectLevelButton.png");//
    button [2] = loadImage ("InstructionButton.png");//1 -> 2
    button [3] = loadImage ("CreditButton.png");//2 -> 3
  }
  public void chooseMenu () {
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

class NEXTLEVEL {
  public void level () {
    if (nextLevel == true) {//Choose to play next level
      more.helpTime = 0; //Reset the helpTime, as well as in PLAYAGAIN class
      //level++;
      chooseLevel.up();
      setup();
      nextLevel = false;
    }
  }
}

class PLAYAGAIN {
  public void again () {
    if (playAgain == true) {//Choose play again the level which player already won
      score.updateHighScore = true; //NEED TO UPDATE IF THE NEW SCORE IS HIGHER
      level--;
      more.helpTime = 0; //reset helpTime, as well as in NEXTLEVEL class
      setup();
      playAgain = false;
    }
  }
}

//Return _return = new Return ();
class Return {
  PImage previous;
  PImage returnMenu;
  boolean isReturn;
  Return () {
  }
  public void load() {
    previous = loadImage("ReturnButton.png");
    returnMenu = loadImage("MenuButton.png");
  }
  public void press() {    
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
  public void pressMenu(){
    image(returnMenu,0,640);
    //rect (0,640,150,100);/the region where we can click to return
    if (mouseX >= 0 && mouseX <= 150 && mouseY >=640 && mouseY <=740 && mousePressed){
      isReturn = true;
      menu.changeButton = true;
      menu.chooseStart = false;
    }
  }
}

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
  public void display () {
    //Count Block steup
    countTimeRemain = 0;
    countBlock = 0;
    countHit = 0;
    countNoHit = 0;
    score = 0;
  }
  public void calculate() {
    if (countBlock > 3) {
      score = -5*(countBlock-3) + 30*countNoHit + countTimeRemain;// Calculation of final score for win game, which countTimeRemain = (t_m*60+t_s)*20;
    }
    else if (countBlock <= 3) {
      score = 30*countNoHit + countTimeRemain;//Remember we let player has 3 free block to use;
    }
    //text ("Score:" + score, 900, 90);
  }
  public void setTotalScore () {
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


class Star
{
  // class to store info related to a symbolic
  // depiction and animation of a star

    float x, y; // screen location
  int arms; // number of rays
  float sz; // size
  float spin; // current rotation amount (radians)
  float shinetime; // timer for shining animation

  Star(float inx, float iny, int a, float s, float sp)
  {
    shinetime = 0;
    arms = a;
    sz = s;
    spin = sp;
    x = inx;
    y = iny;
  }
}

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
  public void display(){
    //inputSecond = addSecond;
    inputSecond = addSecond;
    t_m = inputSecond / 60; // covert second to min by devide it by 60.
    t_s = inputSecond % 60; // The remain will be second
    recordTime = millis(); //store the current time
  }
  public void count () {
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

// Triangle class
// ==============
// List of Methods
/*
 
 int numTriangles(int columns)
 // Determines the number of triangles in the grid (needed before creating the grid)
 
 float createGrid(int numTriangles, Triangle [] grid, int columns, float x1, float y1, float y2)
 // Initializes a grid of triangles in the shape of a hexagon.
 // Fills in all properties of each triangle in the grid.
 
 void  display (int numTriangles, Triangle [] grid, float side, int numTeleport, int hole Enemy ball) {
 // Display all of the "numTriangles" Triangle objects in the array "grid"
 // The side input argument is the side length of a triangle,
 //    used to set the size of text within a triangle.
 // The numTeleport argument is the number of teleportation triangles (Type 3) in the grid
 // hole is the index of the triangle containing the hole
 // ball is the player's ball, which will affect the display of teleporters
 
  void fractalBreak(float ax, float ay, float bx, float by, float cx, float cy , int num){
    // Sub-function of the display() method
    // Draw a fractal pattern of triangles, given the coordinates of a triangle,
    //   and the number of fractal levels to draw. 
        
 boolean inside(int index, Triangle [] grid, float x, float y)
 // Determines if a point is inside a triangle
 
 int nearest(int numTriangles, Triangle [] grid, float x, float y)
 // Finds the nearest triangle to a point
 
 void playerBlock(int numTriangles, Triangle [] grid, float x, float y)
 // Allows each triangle to act as a button, using the inside() and nearest() functions
 // Requires the findFreeTri() method in the Enemy class
 
 void adjacent(int numTriangles, Triangle [] grid,  int columns)
 // Called by createGrid(), fills in the adj[] array of all grid triangles
 
 int find(int numTriangles, Triangle [] grid, int col_, int row_)
 // Finds the index of a triangle, given its row and column numbers
 // Called by adjacent()
 
 void collisionSet(int numTriangles, Triangle [] grid)
 // Sets the rebound[], collisionPoints[] and startingPoints[] arrays of all triangles in the grid
 // Called by createGrid()
 
 void createBlocks(int numTriangles, Triangle [] grid, int [] freeIndices, int [] numTypes)
 // Randomly changes the types of a number of triangles in the grid (as specified in the arguments passed).
 // Updates the freeIndices array to indicate which triangles are now "occupied"
 
 void reserve4Spaces(int numTriangles, Triangle [] grid, int columns, int [] freeIndices, int [] spaces){
 // Reserves random triangles in the grid and their two or three adjacent triangles.
 Use to place the player-controlled balls and the hole.
 The first reserved space will be on an edge. Use it to place the hole.
 The second reserved space will be on the opposite edge. Use it to place the first ball.
 This special treatment of the first and second spaces is to ensure that the ball initially starts far from the hole.
 // Run this function before placing anything on the grid, such that objects
 //    which require 3-4 empty spaces will not be impossible to find room for later!
 
 int reserveSpace(int numTriangles, int [] freeIndices)
 // Called within other functions to find and reserve an unoccupied triangle in the grid.
 // Updates the freeIndices array to indicate which triangles are now "occupied"
 // For objects that occupy more than one empty triangle, place them at positions in the array
 "spaces[]". This array is setup by the reserve4Spaces() method.
 
 int findEdge(int numTriangles, Triangle [] grid, int columns, int edge) {
 // Returns a random index of a triangle on a given border of the hexagonal grid
 // Called within reserve4Spaces()
 
 void verifyBlocks(int numTriangles, Triangle [] grid, int [] spaces, int [] freeIndices) {
 // Checks if all balls can reach the hole, and if necessary, makes adjustments so that they can reach the hole
 // Run this function after createBlocks() and reserve4Spaces()
 // verifyBlocks() can print out descriptions of any changes that it makes, if these lines are uncommented
 // This function will need to be revised if ever there happens to be more than one hole in a level!
 //    Note that it can handle multiple player-controlled balls, however.
 
 void findPath(int numTriangles, Triangle [] grid, int start, int [] onPath, int [] barriers) {
 // Finds all triangles that a ball could move into if it started from a given triangle
 // Used by verifyBlocks() to determine if a ball could move from one triangle to another.
 // If it encounters unpaired teleporters, this function will not work! Ensure all teleporters are paired.
 // Used by addEnemy() in the Enemy class to determine if a triangle is on a path to the hole.
 
 int searchArray(int [] set, int value) {
 // Search an unordered array for the first occurence of a given integer value
 // Returns the index if found. If not found, returns a value equal to the length of the array.
 
 */

class Triangle {
  float x, y;  // Centre of triangle
  float vertices [][] = new float [3][2];  // Vertices of triangle [A,B,C vertices][x,y-coord], a 3-by-2 array
  // A - [0][x,y] is on the vertical midline of the triangle,
  //    at the same vertical position as its centre
  // B - [1][x,y] is the highest point
  // C - [2][x,y] is the lowest point
  boolean flip;  // True = points left, False = points right
  int id;  // Object identification number
  // Triangles will be numbered top to bottom, left to right in the grid
  int col, row;  // Object column and row in the grid
  int adj [] = new int[3];  // Indices of triangles adjacent to the triangle in question
  // Entries are as follows:
  // [0] - Immediately above
  // [1] - Immediately to the left or right
  // [2] - Immediately below
  // An element equal to numTriangles indicates the side borders on the edge of the hexagonal grid
  int rebound [][] = new int[6][2];  // Chooses a ball's path after a collision with an edge, depending on the ball's incoming direction.
  // Refer to the pathSet[] property of Enemy objects for the meaning of the index of the first dimension
  // Set by the collisionSet() method, which is called by createGrid().
  // Entries are as follows:
  // [][0]: ID of the adjacent triangle which shares the edge, and determines whether the ball bounces
  // [][1]: Path of the ball after a collision with an obstacle beyond this edge
  float collisionPoints [][] = new float[6][2];  // The midpoint of an edge at which a ball will collide, depending on the ball's incoming path
  // Refer to the pathSet[] property of Enemy objects for the meaning of the index of the first dimension
  // Set by the collisionSet() method, which is called by createGrid().
  // The second dimension of the array contains the x,y-coordinates of the midpoints.
  float startingPoints [][] = new float[6][2];  // Same as collisionPoints[][],
  // but the coordinates are where balls start moving from when placed
  // in the triangle.
  int link;  // If the triangle is a teleporter, this is the ID of the teleportation destination triangle
  // Triangles which are not teleporters, or which are only teleportation receivers are given link
  //    values equal to numTriangles.
  // Link values are initialized by createGrid(), but are updated later (e.g. in createBlocks()) if teleporters are created.
  int bounce;  // Number of times a Type 2 triangle has been hit by a block-breaker enemy
  int bounceMax;  // Number of bounces required for a Type 2 triangle to convert back to a Type 0 triangle
  int type;  // Type of triangle, also determines fill colour

  /* Triangle types
   0 = empty
   1 = immutable barrier (not under player's influence)
   2 = player-set barrier (can be added or removed)
   3 = teleporter (transmitter and/or receiver)
   For the time being, all teleporters should be created in linked pairs, and be both transmitters and receivers.
   */

  // Constructor
  Triangle(float x_, float y_, float vertices_ [][], boolean flip_, int id_, int col_, int row_, int link_, int type_) {
    x = x_;
    y = y_;
    // Triangle Vertices
    for (int i = 0; i < 3; i+=1) {
      for (int j = 0; j < 2; j+=1) {
        vertices[i][j] = vertices_ [i][j];
      }
    }
    // Other Primitive Properties
    type = type_;
    link = link_;  // May be changed later by createGrid() or if the triangle becomes a teleporter
    id = id_;
    flip = flip_;
    col = col_;
    row = row_;
    bounce = 0;
    bounceMax = 3;

    // Adjacent triangles indexes will be calculated later by adjacent() within createGrid()
    for (int i = 0; i < 3; i+=1) {
      adj[i] = 0;
    }

    // Collision arrays will be calculated later by collisionSet() within createGrid()
    for (int i = 0; i < 6; i+=1) {
      for (int j = 0; j < 2; j+=1) {
        rebound[i][j] = 0;
        collisionPoints[i][j] = 0;
        startingPoints[i][j] = 0;
      }
    }
  }  // end Constructor


  public int numTriangles(int columns) {
    // Calculate how many triangles in a hexagonal grid with a given number of columns
    // See http://en.wikipedia.org/wiki/Triangular_number for information on triangular numbers
    // T_n = n(n+1)/2 // nth triangular number
    // Note however, that the situation here is different! We are dealing with triangles, not dots.
    // ============================================================================================
    // Columns must be an even number, since a hexagonal grid is symmetrical!
    /* The number of columns is equal to the number of levels in a triangular grid
     containing half of the hexagonal grid as its bottom half set of levels (a trapezoid).
     */
    // The bottom half of the triangular grid
    int numTriangularGrid = 0;
    for (int i = 1, j = 1; j <= columns; i += 2, j += 1) {
      if (j > columns/2) {
        numTriangularGrid += i;
      }
    }
    // Two times the bottom half of the triangular grid
    return 2*numTriangularGrid;
  }  // end numTriangles()


  public float createGrid(int numTriangles, Triangle [] grid, int columns, float x1, float y1, float y2) {
    // Input arguments
    /*
    numTriangles = number of triangles in the grid
     grid = array of size numTriangles to contain all Triangle objects in the grid
     columns = number of columns in the grid
     x1,y1 = coordinates of top left point of hexagonal grid
     y2 = y-coordinate of bottom left of hexagonal grid
     */

    // Calculating the number of triangles in the first column on the left 
    int firstColumn = columns+1;

    // Triangle side length
    float side = abs(y2-y1)/((firstColumn-1)/2);  // Distance between pairs of vertices
    float length = sqrt(3)/2*side;  // Height of a triangle, if the triangle was pointing up 

    // Creating Triangle objects to fill the grid
    // ======================================================================
    // Initializing
    float vertices_ [][] = new float [3][2];  // Array for storing vertex coordinates
    for (int i = 0; i < 3; i+=1) {
      for (int j = 0; j < 2; j+=1) {
        vertices_[i][j] = 0;
      }
    }
    float x_ = 0;
    float y_ = 0;
    int numRows = firstColumn;  // Number of triangles in the first column;
    int id_ = 0;  // First ID number
    int col_ = 0;  // First column index
    int row_ = 0;  // First row index
    boolean flip_ = true;  // First triangle always points left
    int link_ = numTriangles;  // All link values are initially set to the equivalent of a null link
    int type_ = 0;  // All triangles are initially empty

    // Creating Triangle objects to fill columns in the left half of the grid
    for (int i = 0; i < columns/2; i +=1) {
      // Iterate over all columns in the trapezoid
      row_ = 0;  // First row index
      flip_ = true;  // First triangle always points left
      for (int j = 0; j < numRows; j +=1) {
        // Iterate over all rows in each column
        if (flip_) {
          // Triangle points left
          vertices_[0][0] = x1 + col_*length; // Ax
          vertices_[0][1] = y1 + (row_-col_)*side/2; // Ay
          vertices_[1][0] = x1 + (col_+1)*length; // Bx
          vertices_[1][1] = y1 + (row_-1-col_)*side/2; // By
          vertices_[2][0] = x1 + (col_+1)*length; // Cx
          vertices_[2][1] = y1 + (row_+1-col_)*side/2; // Cy
          x_ = vertices_[0][0] + side/sqrt(3);
          y_ = vertices_[0][1];
        } 
        else {
          // Triangle points right
          vertices_[0][0] = x1 + (col_+1)*length; // Ax
          vertices_[0][1] = y1 + (row_-col_)*side/2; // Ay
          vertices_[1][0] = x1 + col_*length; // Bx
          vertices_[1][1] = y1 + (row_-1-col_)*side/2; // By
          vertices_[2][0] = x1 + col_*length; // Cx
          vertices_[2][1] = y1 + (row_+1-col_)*side/2; // Cy
          x_ = vertices_[1][0] + side/sqrt(3)/2;
          y_ = vertices_[0][1];
        }
        // Constructing a triangle
        grid[id_] = new Triangle(x_, y_, vertices_, flip_, id_, col_, row_, link_, type_);
        // Prepare for the next iteration
        id_ += 1;  // Move to next ID number
        row_ += 1;  // Move to next row
        flip_ = !flip_;  // Next triangle has the opposite flip
      }
      numRows += 2;  // Two more triangles in the next row
      col_ += 1;  // Move to next column
    }

    // Creating Triangle objects to fill columns in the right half of the grid
    /* This is a "simple" matter of reflecting the triangles in the left half about the vertical
     line at the centre of the grid. */
    int idRef = id_;  // ID of reference triangle to copy data from
    // Midline of grid
    float xMid = x1 + columns/2*length;
    // Creating Triangle objects to fill columns in the right half of the grid
    for (int i = columns/2; i < columns; i +=1) {
      // Iterate over all columns in the trapezoid
      row_ = 0;
      numRows -= 2;  // Two less triangles from the previous row
      idRef = idRef-numRows; // Start by referencing the triangle a full column back
      for (int j = 0; j < numRows; j +=1) {
        // Iterate over all rows in each column        
        grid[id_] = new Triangle(x_, y_, vertices_, flip_, id_, col_, row_, link_, type_);
        // Need to change the following: x_, y_, vertices_, flip_
        grid[id_].x = (xMid-grid[idRef].x)+xMid;
        grid[id_].y = grid[idRef].y;
        for (int k = 0; k < 3; k+=1) {
          grid[id_].vertices[k][0] = (xMid-grid[idRef].vertices[k][0])+xMid;
          grid[id_].vertices[k][1] = grid[idRef].vertices[k][1];
        }
        grid[id_].flip = !(grid[idRef].flip);
        // Prepare for the next iteration
        id_ += 1;  // Move to next ID number
        idRef += 1;  // Move to the next reference ID
        row_ += 1;  // Move to next row
      }
      col_ += 1;  // Move to next column
      idRef -= numRows;  // Go back to the start of the reference row
    }

    // Call other functions to fill in remaining triangle properties
    // ============================================================================
    // Fill in the adj[] array for each triangle
    adjacent(numTriangles, grid, columns);

    // Fill in the rebound[], collisionPoints[] and startingPoints[] arrays of each triangle
    collisionSet(numTriangles, grid);

    // Return the side length of all triangles
    // ============================================================================
    return side;
  }  // end createGrid()


  public void  display (int numTriangles, Triangle [] grid, float side, int numTeleport, int hole, Enemy ball) {
    // Display all of the "numTriangles" Triangle objects in the array "grid"
    // The side input argument is the side length of a triangle,
    //    used to set the size of text within a triangle.
    // The numTeleport argument is the number of teleportation triangles (Type 3) in the grid
    // hole is the index of the triangle containing the hole
    // ball is the player's ball, which will affect the display of teleporters

    textAlign(CENTER, CENTER);  // Center text
    imageMode(CENTER);  // Center images
    float textDim = side/4;  // Size of text
    textSize(textDim);

    // Initializations
    Triangle tri;  // Holder for the current triangle
    int countTeleport = 0;  // Counter for pairs of teleportation triangles
    int teleportLink[] = new int[numTeleport/2+1];  // Used to identify pairs of teleportation triangles
    boolean match = false;  // Is this teleporter linked to by a previously drawn teleporter?
    for (int i = 0; i < numTeleport/2+1; i += 1) {
      teleportLink[i] = numTriangles;  // Initially assign an invalid link
    }

    // Iterate over all triangles in grid[]
    for (int i = 0; i < numTriangles; i += 1) {

      tri = grid[i];  // Get the current triangle
      
      // Draw the triangle
      noStroke();
      if(tri.type != 3 || (ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id)){
        if(tri.flip){
          // Triangle points left
          if(tri.id != hole){
            // Not the hole
            image(triImages[tri.type][0], tri.vertices[0][0]+sqrt(3)*side/4, tri.vertices[0][1]);
          } else {
            // The hole
            image(triImages[5][0], tri.vertices[0][0]+sqrt(3)*side/4, tri.vertices[0][1]);
          }
        } else {
          // Triangle points right
          if(tri.id != hole){
            // Not the hole
            image(triImages[tri.type][1], tri.vertices[0][0]-sqrt(3)*side/4, tri.vertices[0][1]);
          } else {
            // The hole
            image(triImages[5][1], tri.vertices[0][0]-sqrt(3)*side/4, tri.vertices[0][1]);
          }  
        }          
      } else {
        // This is a teleporter with the ball inside, or with the ball inside its linked partner
        if(tri.flip){
          // Triangle points left
          image(triImages[4][0], tri.vertices[0][0]+sqrt(3)*side/4, tri.vertices[0][1]);
        } else {
          // Triangle points right
          image(triImages[4][1], tri.vertices[0][0]-sqrt(3)*side/4, tri.vertices[0][1]);
        }
      }
      
      // Give triangles an outline, for clarity
      stroke(0xff00FF63);  // Bright green-blue
      noFill();
      triangle(tri.vertices[0][0], tri.vertices[0][1], tri.vertices[1][0], tri.vertices[1][1], tri.vertices[2][0], tri.vertices[2][1]);
      
      // Code to number and arrow (for the player's ball) pairs of teleportation triangles
      if (tri.type == 3) {
        
        // Display pair number
        fill(255);
        if (countTeleport == 0) {
          // First teleporter is in pair "0"
          if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id){
            text(0, tri.x, tri.y);
          }  // end if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id)
          teleportLink[countTeleport] = tri.link;  // Used for searching later
          countTeleport += 1;
        } 
        else {
          // Find the pair of this teleporter
          for (int j = 0; j < countTeleport; j +=1) {
            // Check if a previously-drawn teleporter links here
            if (teleportLink[j] == tri.id) {
              // This teleporter is the second in a pair.
              if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id){
                text(j, tri.x, tri.y);
              }  // end if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id)
              match = true;
            }
          }
          if (!match) {
            // No match so far
            if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id){
              text(countTeleport, tri.x, tri.y);
            }  // end if(ball.currentTri != tri.id && grid[ball.currentTri].link != tri.id)
            teleportLink[countTeleport] = tri.link;
            countTeleport += 1;
          }
        }
        // Prepare for the next iteration
        match = false;
        
        // Display an arrow to show where the ball will leave the teleporter
        if(ball.currentTri == tri.id || grid[ball.currentTri].link == tri.id){
          // Setting arrow fill colours
          int ac = color(255,255,0);  // Active colour
          int ic = color(150);  // Inactive colour
          if(ball.currentTri == tri.id) {
            // Ball is in this triangle
            switch(ball.state){
              case 1:
              case 3:
              case 4:
              case 7:
                if(ball.teleport){
                  fill(ic);
                  stroke(ic);
                } else {
                  fill(ac);
                  stroke(ac);
                }
                break;
              case 5:
              case 6:
                fill(ac);
                stroke(ac);
                break;
            }  // end switch(ball.state)
          }  // end if-block of if(ball.currentTri == tri.id)
          else {
            // Ball is in the teleporter's linked partner
            switch(ball.state){
              case 1:
              case 3:
              case 4:
              case 7:
                if(ball.teleport){
                  fill(ac);
                  stroke(ac);
                } else {
                  fill(ic);
                  stroke(ic);
                }
                break;
              case 5:
              case 6:
                fill(ic);
                stroke(ic);
                break;
            }  // end switch(ball.state)
          }  // end else-block of if(ball.currentTri == tri.id)

          // Draw the arrow
          float px = (ball.pathSet[ball.path][0])*side/8;  // x-component of vector along ball's path
          float py = (ball.pathSet[ball.path][1])*side/8;  // y-component of vector along ball's path
          float normx = -py;  // Normal vector x-component
          float normy = px;  // Normal vector y-component
          float x = tri.collisionPoints[ball.path][0]-px*5/4;  // x-coordinate of arrow centre
          float y = tri.collisionPoints[ball.path][1]-py*5/4;  // y-coordinate of arrow centre
          
          triangle(x+px, y+py, x-normx/3, y-normy/3, x+normx/3, y+normy/3);
          line(x, y, x-px,y-py);
        }  // end if(ball.currentTri == tri.id || grid[ball.currentTri].link == tri.id)
      }  // end if(tri.type == 3)
      
      else if(tri.type == 2 && tri.bounce != 0){
        // Code to display the breaking of Type 2 triangles by Type 3 enemies
        int numLevels = 2*tri.bounce;  // Desired number of fractal levels
        fractalBreak(tri.vertices[0][0], tri.vertices[0][1], tri.vertices[1][0], tri.vertices[1][1], tri.vertices[2][0], tri.vertices[2][1], numLevels);
      }
      
      // Code to Help with Debugging
      /*
      fill(0);  // Text colour should be black
      textSize(side/8);
      textLeading(side/8);
      */
      /*      
       // Code to output actual ID and link numbers of teleportation triangles
       if(tri.type == 3) {
       fill(0);
       text("\n\n"+tri.id+" to "+tri.link,tri.x,tri.y);  // Will output below centre (e.g. below teleporter pair number)
       }
       */
      /*
      // text(tri.id+"\n("+tri.col+","+tri.row+")",tri.x,tri.y);  // Display Triangle ID, row and column numbers
      // text("\n\n"+tri.id,tri.x,tri.y);  // Display Triangle ID numbers
      // text(tri.id+"\n("+tri.adj[0]+","+tri.adj[1]+","+tri.adj[2]+")",tri.x,tri.y);  // Display Triangle ID numbers and indices of adjacent triangles
      text(tri.bounce,tri.x,tri.y);  // Display Triangle bounce counts
      */
    }  // end for i
    imageMode(CORNER);  // Re-setting image alignment
    stroke(0);  // Re-setting stroke colour, just in case
  }  // end display()
  

      public void fractalBreak(float ax, float ay, float bx, float by, float cx, float cy , int num){
        // Sub-function of the display() method
        // Draw a fractal pattern of triangles, given the coordinates of a triangle,
        //   and the number of fractal levels to draw.
        if(num == 0){
          // No code to execute
        } else {
          stroke(0xffD8E4FF);  // Bluish-White
          noFill();
          float midABx = (ax+bx)/2;
          float midABy = (ay+by)/2;
          float midACx = (ax+cx)/2;
          float midACy = (ay+cy)/2;
          float midBCx = (bx+cx)/2;
          float midBCy = (by+cy)/2;
          triangle(midABx,midABy,midACx,midACy,midBCx,midBCy);
          // Call next level of the pattern
          fractalBreak(ax, ay, midABx, midABy, midACx, midACy, num-1);
          fractalBreak(bx, by, midABx, midABy, midBCx, midBCy, num-1);
          fractalBreak(cx, cy, midBCx, midBCy, midACx, midACy, num-1);
          fractalBreak(midABx, midABy, midACx, midACy, midBCx, midBCy, num-1);
        }
      }  // end fractalBreak

  public boolean inside(int index, Triangle [] grid, float x, float y) {
    // Is the point inside a given triangle, or on its border?
    // This function could also be used to check if an impact point is on the part of the line forming a triangle.
    // Input Arguments
    /* index = index of the triangle under study
     grid = array containing all triangles, including the one under study
     x,y = coordinates of the point in question */
    Triangle tri = grid[index];  // Get the specific triangle

    if (tri.flip) {
      // Triangle Points Left
      // Is the point within the horizontal range of the triangle?
      if (x >= tri.vertices[0][0] && x <= tri.vertices[1][0]) {
        // Is the point within the two angled lines of the triangle (upper && lower)?
        if (y >= tri.vertices[1][1] + (x-tri.vertices[1][0])*(tri.vertices[0][1]-tri.vertices[1][1])/(tri.vertices[0][0]-tri.vertices[1][0])
          && y <= tri.vertices[2][1] + (x-tri.vertices[2][0])*(tri.vertices[0][1]-tri.vertices[2][1])/(tri.vertices[0][0]-tri.vertices[2][0])) {
          return true;
        }
      }
    } 
    else {
      // Triangle Points Right
      // Is the point within the horizontal range of the triangle?
      if (x <= tri.vertices[0][0] && x >= tri.vertices[1][0]) {
        // Is the point within the two angled lines of the triangle (upper && lower)?
        if (y >= tri.vertices[1][1] + (x-tri.vertices[1][0])*(tri.vertices[0][1]-tri.vertices[1][1])/(tri.vertices[0][0]-tri.vertices[1][0])
          && y <= tri.vertices[2][1] + (x-tri.vertices[2][0])*(tri.vertices[0][1]-tri.vertices[2][1])/(tri.vertices[0][0]-tri.vertices[2][0])) {
          return true;
        }
      }
    }
    // Point is outside the triangle
    return false;
  }  // end inside()

  public int nearest(int numTriangles, Triangle [] grid, float x, float y) {
    // Find the nearest triangle to a point and return its index
    // If there are multiple triangles the same distance to a point, the one with the smallest index
    //   will be found.
    //
    // Input Arguments
    /* grid = array containing all triangles (length = numTriangles)
     x,y = coordinates of the point in question
     */
    int index = 0;  // Index of the nearest triangle
    float distance = max(width, height);  // Distance to the nearest triangle
    float distance_;  // Temporary distance
    for (int i = 0; i < numTriangles; i += 1) {
      // Iterate over all triangles to minimize distance
      distance_ = dist(x, y, grid[i].x, grid[i].y);
      if (distance_ <= distance) {
        distance = distance_;  // Update minimum value
        index = i;
      }
    }
    return index;
  }  // end nearest()

  public void playerBlock(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, float x, float y) { //change here
    // Macro function which allows the player to change the type of a triangle by clicking on it
    // Input Arguments
    /*
     grid = array containing all triangles (length = numTriangles)
     enemies = ArrayList which contains all enemies currently in existence
     hole = ID of the triangle containing the hole
     currentFreeIndices = empty array of length numTriangles
     x,y = coordinates of the point in question
     */
    // Which triangle was closest to the click?
    int index = nearest(numTriangles, grid, x, y);
    // Was the click inside this triangle?
    boolean clickInside = inside(index, grid, x, y);
    // Change the state of the triangle accordingly
    if (clickInside) {
      // Determine if the triangle is empty
      Enemy ball = (Enemy)enemies.get(0);  // Need to access an Enemy method
      ball.findFreeTri(numTriangles, grid, enemies, hole, currentFreeIndices, true);
      if (currentFreeIndices[index] != numTriangles ) {
        // Unoccupied, Type 0 or 2 triangle
        if (grid[index].type == 0){
          // Create player-controlled block
          grid[index].type = 2;
          if (mousePressed) {
            score.countBlock++;
          }
        } else if (grid[index].type == 2) {
          // Remove player-controlled block
          grid[index].type = 0;
          grid[index].bounce = 0;
          if (mousePressed) {
            score.countBlock++;
          }
        }
      }  // end if (currentFreeIndices[index] != numTriangles )
    }  // end if(clickInside)
  }  // end playerBlock()


  public void adjacent(int numTriangles, Triangle [] grid, int columns) {
    // Find the ID numbers of the three adjacent triangles to each triangle in the grid.
    // The locations of the adjacent triangles depend on the triangle's orientation (flip value)
    // Input Arguments
    /* grid = array containing all triangles (length = numTriangles)
     columns = number of columns in the hexagonal grid
     */
    // Note the use of the find() function, which will automatically locate a triangle bordering on the edge
    //   by returning an index equal to numTriangles.
    for (int i = 0; i < numTriangles; i += 1) {
      // Find the triangle above
      grid[i].adj[0] = find(numTriangles, grid, grid[i].col, grid[i].row-1);
      // Find the triangle below
      grid[i].adj[2] = find(numTriangles, grid, grid[i].col, grid[i].row+1);
      // Find the triangle to the left or right
      if (grid[i].flip) {
        // Triangle points left
        // Find the triangle to the right
        if (grid[i].col < columns/2-1) {
          // Triangle is on the left side of the grid
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col+1, grid[i].row+1);
        } 
        else if (grid[i].col == columns/2-1) {
          // Triangle is on the left of centre column
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col+1, grid[i].row);
        } 
        else {
          // Triangle is on the right side of the grid
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col+1, grid[i].row-1);
        }
      } 
      else {
        // Triangle points right
        // Find the triangle to the left
        if (grid[i].col < columns/2) {
          // Triangle is on the left side of the grid
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col-1, grid[i].row-1);
        } 
        else if (grid[i].col == columns/2) {
          // Triangle is on the right of centre column
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col-1, grid[i].row);
        } 
        else {
          // Triangle is on the right side of the grid
          grid[i].adj[1] = find(numTriangles, grid, grid[i].col-1, grid[i].row+1);
        }
      }
    }
  }  // end adjacent()


  public int find(int numTriangles, Triangle [] grid, int col_, int row_) {
    // Find the index of a triangle in the grid, given its column and row numbers
    // If a triangle at the specified column and row intersection is not found, return an index equal to numTriangles.
    //   Other functions will recognize the meaning of this value.
    // Input Arguments
    /* grid = array containing all triangles (length = numTriangles)
     col_ = column number of the triangle
     row_ = row number of the triangle
     */
    int index = numTriangles;  // Temporarily assign an index outside the grid.
    for (int i = 0; i < numTriangles; i += 1) {
      if (grid[i].col == col_ && grid[i].row == row_) {
        index = i;
        break;
      }
    }
    return index;
  }  // end find()


  public void collisionSet(int numTriangles, Triangle [] grid) {
    // Set the rebound[], collisionPoints[] and startingPoints[] arrays of all Triangles in the array grid[]
    // The contents of the three arrays depend on the triangle's orientation (flip value)
    // Note that the adjacent() method must be called before this function!
    // Input Arguments: grid = array containing all triangles (length = numTriangles)

    Triangle tri;  // Create a holder for the current triangle
    // Iterate over all triangles in grid[]    
    for (int i = 0; i < numTriangles; i +=1) {
      tri = grid[i];  // Get the current triangle   
      if (tri.flip) {

        // Triangle points left
        // Fill in data over the six possible ball movement directions (paths)
        // Refer to the pathSet[] and path[] properties of Enemy objects

        // rebound[] array: Adjacent triangles
        tri.rebound[0][0] = tri.adj[0];
        tri.rebound[1][0] = tri.adj[0];
        tri.rebound[2][0] = tri.adj[2];
        tri.rebound[3][0] = tri.adj[2];
        tri.rebound[4][0] = tri.adj[1];
        tri.rebound[5][0] = tri.adj[1];

        // rebound[] array: Outgoing ball paths
        tri.rebound[0][1] = 4;   
        tri.rebound[1][1] = 3;
        tri.rebound[2][1] = 0;
        tri.rebound[3][1] = 5;
        tri.rebound[4][1] = 2;
        tri.rebound[5][1] = 1;

        // collisionPoints[] and startingPoints[] arrays
        tri.collisionPoints[0][0] = tri.startingPoints[3][0] = (tri.vertices[0][0]+tri.vertices[1][0])/2;  // AB
        tri.collisionPoints[0][1] = tri.startingPoints[3][1] = (tri.vertices[0][1]+tri.vertices[1][1])/2;
        tri.collisionPoints[1][0] = tri.startingPoints[4][0] = (tri.vertices[0][0]+tri.vertices[1][0])/2;  // AB
        tri.collisionPoints[1][1] = tri.startingPoints[4][1] = (tri.vertices[0][1]+tri.vertices[1][1])/2;
        tri.collisionPoints[2][0] = tri.startingPoints[0][0] = (tri.vertices[0][0]+tri.vertices[2][0])/2;  // AC
        tri.collisionPoints[2][1] = tri.startingPoints[0][1] = (tri.vertices[0][1]+tri.vertices[2][1])/2;
        tri.collisionPoints[3][0] = tri.startingPoints[5][0] = (tri.vertices[0][0]+tri.vertices[2][0])/2;  // AC
        tri.collisionPoints[3][1] = tri.startingPoints[5][1] = (tri.vertices[0][1]+tri.vertices[2][1])/2;
        tri.collisionPoints[4][0] = tri.startingPoints[1][0] = tri.vertices[1][0];  // BC
        tri.collisionPoints[4][1] = tri.startingPoints[1][1] = tri.vertices[0][1];
        tri.collisionPoints[5][0] = tri.startingPoints[2][0] = tri.vertices[1][0];  // BC
        tri.collisionPoints[5][1] = tri.startingPoints[2][1] = tri.vertices[0][1];
      } 
      else {
        // Triangle points right

        // rebound[] array: Adjacent triangles
        tri.rebound[0][0] = tri.adj[0];
        tri.rebound[1][0] = tri.adj[1];
        tri.rebound[2][0] = tri.adj[1];
        tri.rebound[3][0] = tri.adj[2];
        tri.rebound[4][0] = tri.adj[2];
        tri.rebound[5][0] = tri.adj[0];

        // rebound[] array: Outgoing ball paths
        tri.rebound[0][1] = 2;
        tri.rebound[1][1] = 5;
        tri.rebound[2][1] = 4;
        tri.rebound[3][1] = 1;
        tri.rebound[4][1] = 0;
        tri.rebound[5][1] = 3;

        // collisionPoints[] and startingPoints[] arrays
        tri.collisionPoints[0][0] = tri.startingPoints[2][0] = (tri.vertices[0][0]+tri.vertices[1][0])/2;  // AB
        tri.collisionPoints[0][1] = tri.startingPoints[2][1] = (tri.vertices[0][1]+tri.vertices[1][1])/2;
        tri.collisionPoints[1][0] = tri.startingPoints[4][0] = tri.vertices[1][0];  // BC
        tri.collisionPoints[1][1] = tri.startingPoints[4][1] = tri.vertices[0][1];
        tri.collisionPoints[2][0] = tri.startingPoints[5][0] = tri.vertices[1][0];  // BC
        tri.collisionPoints[2][1] = tri.startingPoints[5][1] = tri.vertices[0][1];
        tri.collisionPoints[3][0] = tri.startingPoints[0][0] = (tri.vertices[0][0]+tri.vertices[2][0])/2;  // AC
        tri.collisionPoints[3][1] = tri.startingPoints[0][1] = (tri.vertices[0][1]+tri.vertices[2][1])/2;
        tri.collisionPoints[4][0] = tri.startingPoints[1][0] = (tri.vertices[0][0]+tri.vertices[2][0])/2;  // AC
        tri.collisionPoints[4][1] = tri.startingPoints[1][1] = (tri.vertices[0][1]+tri.vertices[2][1])/2;
        tri.collisionPoints[5][0] = tri.startingPoints[3][0] = (tri.vertices[0][0]+tri.vertices[1][0])/2;  // AB
        tri.collisionPoints[5][1] = tri.startingPoints[3][1] = (tri.vertices[0][1]+tri.vertices[1][1])/2;
      }  // end if(tri.flip)
    }  // end for i
  }  // end collisionSet()


  public void createBlocks(int numTriangles, Triangle [] grid, int [] freeIndices, int [] numTypes) {
    // Converts some triangles in the grid into non-empty types.
    // Links between teleportation triangles (Type 3) will be bidirectional between random pairs of indices.
    // Input arguments
    /*
      numTriangles = number of triangles in the grid
     grid = array of size numTriangles which contains all Triangle objects in the grid
     numTypes = array with as many elements as there are types of triangles, containing the desired number of triangles of each type
     The first and third elements (for Type 0 and Type 2 triangles) are not used by the function,
     but are there to avoid confusion over how long the array should be.
     The number of teleportation triangles (Type 3) must be an even number, or the last teleporter created will not link anywhere!
     freeIndices = array with numTriangles elements
     freeIndices should be populated so that each element is equal to its index.
     This array will be updated whenever any triangles become occupied during the setup phase of a level.
     */
    // Quasi Return Values
    /*
      freeIndices[] will be updated such that the element for every triangle in grid[]
     which has a type other than 0 or 2 will be set equal to numTriangles.
     */

    // Iterate over each type of block to create
    int id = 0;  // ID of triangle picked
    int pastid = 0;  // ID of previous triangle picked, used for teleportation triangles

    for (int i = 0; i < numTypes.length; i +=1) {
      if (i != 0 && i != 2) {
        // Iterate over the number of blocks of this type to create
        for (int j = 0; j < numTypes[i]; j +=1) {
          id = reserveSpace(numTriangles, freeIndices);  // Select a triangle to change
          grid[id].type = i;  // Change the triangle's type

          // Special code for teleportation triangles (Type 3)
          if (i==3 && j % 2 == 1) {
            // Link every pair of teleportation triangles created
            grid[id].link = pastid;
            grid[pastid].link = id;
          }

          pastid = id;  // Update pastid for the next iteration
        }  // end for j
      }  // end if(i != 0 && i != 2)
    }  // end for i
  }  // end createBlocks()


  public void reserve4Spaces(int numTriangles, Triangle [] grid, int columns, int [] freeIndices, int [] spaces) {
    // Reserves random triangles in the grid and their two or three adjacent triangles.
    /*
        Use to place the player-controlled balls and the hole.
     The first reserved space will be on an edge. Use it to place the hole.
     The second reserved space will be on the opposite edge. Use it to place the first ball.
     This special treatment of the first and second spaces is to ensure that the ball initially starts far from the hole.
     */
    // Run this function before placing anything on the grid, such that objects
    //    which require 3-4 empty spaces will not be impossible to find room for later!
    // Input arguments
    /*
      numTriangles = number of triangles in the grid
     grid = array of size numTriangles which contains all Triangle objects in the hexagon
     columns = number of columns of triangles in the hexagonal grid
     freeIndices = array with numTriangles elements
     freeIndices should be populated beforehand so that each element is equal to its index.
     This array will be updated whenever any triangles become occupied during the setup phase of a level.
     spaces = array to contain the indices of the reserved triangles for 4-space objects
     The length of spaces[] is the number of 4-space positions needed.
     */
    // Quasi Return Values
    /*
      freeIndices[] will be updated such that the elements at each of the four indices which are now "occupied"
     by each 4-space object will be set equal to numTriangles.
     spaces[] will be updated to return the reserved indices for each of the spaces.length 4-space objects
     */

    // Special code for the hole and the first player-controlled ball:
    // ====================================================================================================
    int edge_ = (int) random(0, 6);  // Pick a random edge to pass to findEdge()
    spaces[0] = findEdge(numTriangles, grid, columns, edge_);  // Get a triangle on which to place the hole
    // Put the ball at the opposite edge
    switch(edge_) {
    case 0:
      edge_ = 3;
      break;
    case 1:
      edge_ = 4;
      break;
    case 2:
      edge_ = 5;
      break;
    case 3:
      edge_ = 0;
      break;
    case 4:
      edge_ = 1;
      break;
    case 5:
      edge_ = 2;
      break;
    }  // end switch(edge)
    spaces[1] = findEdge(numTriangles, grid, columns, edge_);  // Get a triangle on which to place the first ball
    // Apply the changes to freeIndices[]
    for (int i = 0; i < 2; i +=1) {
      freeIndices[spaces[i]] = numTriangles;
      // Go over adjacent triangles
      for (int j = 0; j < 3; j +=1) {
        if (grid[spaces[i]].adj[j] != numTriangles) {
          // The adjacent triangle is within the grid, so mark it as occupied
          freeIndices[grid[spaces[i]].adj[j]] = numTriangles;
        }
      }
    }

    // Placing subsequent 4-space objects
    // =======================================================================
    // The following code is similar to the reserveSpace() method
    int index;  // ID of central triangle to reserve in the 4-space area
    int inc = 0;  // Direction to search for the next index, if necessary
    boolean cannotUse = true;  // Can we place the object here?
    int adjOccupied = 0;  // Number of occupied adjacent triangles

    for (int i = 2; i < spaces.length; i +=1) {
      index = (int) random(0, numTriangles);  // ID of a random triangle

      // Choose a random direction to search for an empty triangle.
      inc = (int) random(0, 2)*2-1;  // Will be +1 or -1

        // Keep searching until finding an unoccupied triangle without any occupied adjacent triangles
      while (cannotUse) {

        // If this triangle is occupied, go search for another triangle
        while (freeIndices[index] == numTriangles) {
          index += inc;  // Go to the next index
          // If we reach a boundary of the array
          if (index == numTriangles) {
            // Start from the beginning
            index = 0;
          } 
          else if (index == -1) {
            // Go back to the end
            index = numTriangles-1;
          }
        }  // end while(freeIndices[index] == numTriangles)

        // Now we have found an unoccupied triangle.
        // Are any of its adjacent triangles occupied?
        for (int j = 0; j < 3; j +=1) {
          if (grid[index].adj[j] != numTriangles) {
            // The location is within the grid.
            if ( freeIndices[grid[index].adj[j]] == numTriangles ) {
              // This adjacent triangle is occupied.
              adjOccupied += 1;
            }
          }
        }  // end for j

        if (adjOccupied == 0) {
          // We can use this triangle.
          cannotUse = false;
        } 
        else {
          // Iterate again
          adjOccupied = 0;
          index += inc;  // Go to the next index
          // If we reach a boundary of the array
          if (index == numTriangles) {
            // Start from the beginning
            index = 0;
          } 
          else if (index == -1) {
            // Go back to the end
            index = numTriangles-1;
          }
        }
      }  // end while(cannotUse)

      // We have an index that we can use. Reserve it.
      spaces[i] = index;
      // Apply the changes to freeIndices[]
      freeIndices[index] = numTriangles;
      // Go over adjacent triangles
      for (int j = 0; j < 3; j +=1) {
        if (grid[index].adj[j] != numTriangles) {
          // The adjacent triangle is within the grid, so mark it as occupied.
          freeIndices[grid[index].adj[j]] = numTriangles;
        }
      }  // end for j

      // Preparing for the next iteration
      cannotUse = true;
      adjOccupied = 0;
    }  // end for i
  }  // end reserve4Spaces()


  public int reserveSpace(int numTriangles, int [] freeIndices) {
    // Called within other functions to find and reserve an unoccupied triangle in the grid.
    // Updates the freeIndices[] array to indicate the triangle which is now "occupied".
    // For objects that occupy more than one empty triangle, place them at positions in the array
    //     "spaces[]". This array is setup by the reserve4Spaces() method.
    // Input arguments
    /*
      freeIndices = array with numTriangles elements
     freeIndices should be populated so that each element is equal to its index.
     This array will be updated whenever any triangles become occupied during the setup phase of a level.
     */
    // Return Value: ID of the reserved triangle

    // Select a triangle to reserve
    int index = (int) random(0, numTriangles);  // ID of a random triangle
    int inc = 0;  // Direction to search for the next index, if necessary

    // Is the triangle occupied? If so, search for the next empty triangle
    if (freeIndices[index] == numTriangles) {
      // This triangle is occupied
      // Choose a random direction to search for an empty triangle
      inc = (int) random(0, 2)*2-1;  // Will be +1 or -1
      // Go search for another triangle
      while (freeIndices[index] == numTriangles) {
        index += inc;  // Go to the next index
        // If we reach a boundary of the array
        if (index == numTriangles) {
          // Start from the beginning
          index = 0;
        } 
        else if (index == -1) {
          // Go back to the end
          index = numTriangles-1;
        }
      }  // end while(freeIndices[index] == numTriangles)
    }  // end if(freeIndices[index] == numTriangles)

    // Update freeIndices[] to reserve the space
    freeIndices[index] = numTriangles;

    return index;
  }  // end reserveSpace

  public int findEdge(int numTriangles, Triangle [] grid, int columns, int edge) {
    // Returns a random index of a triangle on a given border of the hexagonal grid
    // Called within reserve4Spaces()
    // Note that, in this function, triangles are considered
    //     on a given border if they touch the border at either a corner or a side.
    // Input arguments
    /*
       grid = array of size numTriangles which contains all Triangle objects in the hexagon
     columns = number of columns of triangles in the hexagonal grid
     edge = Which edge on which to find an index?
     0 - Top Left
     1 - Left
     2 - Bottom Left
     3 - Bottom Right
     4 - Right
     5 - Top Right
     */

    // Initializing
    int firstColumn = columns+1;  // Number of triangles in the first column
    int col_ = 0;  // Column of the index to find
    int row_ = 0;  // Row of the index to find

    switch(edge) {
    case 0:
      col_ = (int) random(0, columns/2);
      if (col_ != 0) {
        row_ = (int) random(0, 2);
      } 
      else {
        row_ = 0;
      }
      break;
    case 1:
      col_ = 0;
      row_ = (int) random(1, firstColumn-1);
      break;
    case 2:
      col_ = (int) random(0, columns/2);
      if (col_ != 0) {
        row_ = (int) random(-2, 1)+2*col_+(firstColumn-1);
      } 
      else {
        row_ = 2*col_+(firstColumn-1);
      }
      break;
    case 3:
      col_ = (int) random(columns/2, columns);
      if (col_ != columns-1) {
        row_ = (int) random(-2, 1)+2*(columns-1-col_)+(firstColumn-1);
      } 
      else {
        row_ = 2*(columns-1-col_)+(firstColumn-1);
      }
      break;
    case 4:
      col_ = columns-1;
      row_ = (int) random(1, firstColumn-1);
      break;
    case 5:
      col_ = (int) random(columns/2, columns);
      if (col_ != columns-1) {
        row_ = (int) random(0, 2);
      } 
      else {
        row_ = 0;
      }
      break;
    }  // end switch(edge)

    return find(numTriangles, grid, col_, row_);
  }  // end findEdge()


  public void verifyBlocks(int numTriangles, Triangle [] grid, int [] spaces, int [] freeIndices) {
    // Checks if all balls can reach the hole, and if necessary, makes adjustments so that they can reach the hole
    // Run this function after createBlocks() and reserve4Spaces()
    // verifyBlocks() can print out descriptions of any changes that it makes, if these lines are uncommented
    // This function will need to be revised if ever there happens to be more than one hole in a level!
    //    Note that it can handle multiple player-controlled balls, however.
    // Input arguments
    /*
      numTriangles = number of triangles in the hexagonal grid
     grid[] = array containing all numTriangles Triangle objects in the hexagonal grid
     spaces = array containing the indices of the reserved triangles for 4-space objects
     The length of spaces[] is the number of 4-space objects.
     spaces[] is needed to get the triangle IDs of the hole and player-controlled balls.
     spaces[] is assumed to be in the following format:
     [0] = ID of triangle reserved for the hole
     [rest of array] = IDs of triangles reserved for player-controlled balls
     freeIndices = array with numTriangles elements.
     Unoccupied triangles are indicated by having elements corresponding to their ID equal to their ID.
     Occupied triangles are indicated by having elements corresponding to their ID equal to numTriangles.
     */

    // println("\nverifyBlocks() has started.");
    // println("  Hole is at Triangle "+spaces[0]);

    // Creating arrays to be passed to findPath()
    int holePath [] = new int[numTriangles];
    int ballPath [] = new int[numTriangles];
    int holeBarriers [] = new int[numTriangles];
    int ballBarriers [] = new int[numTriangles];

    // Check the path of each player-controlled ball
    for (int i = 1; i < spaces.length; i +=1) {

      // println("verifyBlocks() is checking the path of ball "+i+" at Triangle "+spaces[i]+"...");

      // Initializing arrays for each path
      for (int j = 0; j < numTriangles; j +=1) {
        holePath[j] = numTriangles;
        ballPath[j] = numTriangles;
        holeBarriers[j] = numTriangles;
        ballBarriers[j] = numTriangles;
      }  // end for j

      // Use findPath() to calculate the path from the current ball
      findPath(numTriangles, grid, spaces[i], ballPath, ballBarriers);

      // Check if the hole is in the path of the ball
      if (searchArray(ballPath, spaces[0]) != numTriangles) {
        // The hole is in the path of the ball
        // println("  Ball can reach hole - No Adjustments Made");
      } 
      else {
        // println("  Ball cannot reach hole - Adjusting...");

        // Calculate the path from the hole
        findPath(numTriangles, grid, spaces[0], holePath, holeBarriers);

        // Do the two paths share a common barrier?
        int stop = searchArray(ballBarriers, numTriangles);
        int barrier = numTriangles;  // Index of barrier
        for (int j = 0; j < stop; j += 1) {
          if (searchArray(holeBarriers, ballBarriers[j]) != numTriangles) {
            barrier = ballBarriers[j];  // Return this index and break the loop
            break;
          }
        }  // end for j

        if (barrier != numTriangles) {
          // There was a common barrier. Analyze it to determine how it should be changed.
          // print("  Found a common barrier. Triangle "+barrier);

          if (grid[barrier].type == 3) {
            // Teleporter - Remove it and its partner
            // print(" is a teleporter. \n  It and its partner, Triangle "+grid[barrier].link+", were changed to empty triangles.\n");
            // Update freeIndices[]
            freeIndices[barrier] = barrier;
            freeIndices[grid[barrier].link] = grid[barrier].link;
            // Removing the teleporters
            grid[barrier].type = 0;
            grid[grid[barrier].link].type = 0;
            grid[grid[barrier].link].link = numTriangles;
            grid[barrier].link = numTriangles;
          }  // end if-block of if(grid[barrier].type == 3)

          else {
            // Another kind of barrier
            // print(" is not a teleporter. \n  It was changed to an empty triangle.\n");
            grid[barrier].type = 0;
            freeIndices[barrier] = barrier;  // Update freeIndices[]
          }  // end else-block of if(grid[barrier].type == 3)
        }  // end if-block of if(barrier != numTriangles)

        else {
          // There is no common barrier. Create a teleportation pair to link this path
          // println("  No common barrier found...");
          // Create the link at the ends of the paths of the current ball and the hole.
          int ballPortal = ballPath[(searchArray(ballPath, numTriangles)-1)];  // The brackets around searchArray(ballPath, numTriangles)-1 are important, for some reason?!
          int holePortal = holePath[(searchArray(holePath, numTriangles)-1)];  // The index ends up being one too short without the brackets in some cases.
          grid[ballPortal].type = 3;
          grid[ballPortal].link = holePortal;
          grid[holePortal].type = 3;
          grid[holePortal].link = ballPortal;
          // println("  Created a teleportation link between Triangles "+ballPortal+" and "+holePortal);
          // Update freeIndices[]
          freeIndices[ballPortal] = numTriangles;
          freeIndices[holePortal] = numTriangles;
        }  // end else-block of if(barrier != numTriangles)
      }  // end else-block of if(searchArray(ballPath, spaces[0]) != numTriangles)
    }  // end for i

    // println("verifyBlocks() has finished.\n");
  }  // end verifyBlocks()


  public void findPath(int numTriangles, Triangle [] grid, int start, int [] onPath, int [] barriers) {
    // Finds all triangles that a ball could move into if it started from a given triangle
    // Used by verifyBlocks() to determine if a ball could move from one triangle to another.
    // Used by addEnemy() in the Enemy class to determine if a triangle is on a path to the hole.
    // If it encounters unpaired teleporters, this function will not work! Ensure all teleporters are paired.
    // Input arguments
    /*
        numTriangles = number of triangles in the hexagonal grid
     grid[] = array containing all numTriangles Triangle objects in the hexagonal grid
     start = ID of the triangle at the start of the path
     onPath = array of length numTriangles to contain all triangles in the path (Types 0 or 2)
     All entries should be initialized to numTriangles
     If a triangle is on the path, it will have onPath[numPath] = ID
     Later entries in the array are more likely to be further from the start of the path.
     barriers = array of length numTriangles to contain the ID numbers of barrier triangles (all types other than 0 or 2)
     If a triangle is a barrier, adjacent to the path, it will be marked by setting barrier[numBarrier] = ID
     Initialize all elements to equal numTriangles
     Triangles are indicated in this array in the order in which they are found,
     such that the later entries in the array are more likely to be far from the start of the path.
     */
    // Quasi-return values
    /*
        onPath[] and barriers[] are updated by findPath()
     */

    // Initializing tracking variables
    int checked [] = new int[numTriangles];  // Array to indicate which triangles have been checked as part of the search
    // If a triangle is checked, it will be marked by setting checked[ID] = numTriangles
    // Triangles which not been checked will have checked[ID] = 0
    int branches [] = new int[numTriangles];  // Array to contain branches of the path to check later.
    // Untouched elements will have values equal to numTriangles
    // Branches are indicated by branches[numBranch] = ID of triangle at the start of the branch.
    // Entries later in the array represent branches that are likely further from the start of the path.
    int numPath = 0;  // How many triangles (Types 0 or 2) have been found on the path so far?
    // To be incremented whenever another triangle on the path is found.       
    int numBranch = 0;  // How many branches have been found so far?
    // To be incremented whenever a branch is found.
    int checkedBranch = 0;  // How many branches have been checked so far?
    // Index of the current branch to check
    // To be incremented after a branch is checked.
    int numBarrier = 0;  // How many barriers have been found so far?
    // To be incremented whenever a barrier is found.
    int id = numTriangles;  // ID of current triangle under examination

    // Initializing array elements
    for (int i = 0; i < numTriangles; i +=1) {
      checked[i] = 0;
      branches[i] = numTriangles;
    }

    // Set the start of the path as the first branch, and check this triangle
    branches[numBranch] = start;
    onPath[numPath] = start;
    checked[start] = numTriangles;
    numPath += 1;
    numBranch += 1;

    // Initializing loop controllers and small-scope variables
    boolean moreBranches = true;  // Are there still branches to check?
    boolean noDeadEnd = true;  // Can we keep going along this branch?
    boolean noNext = true;  // Have we set the next triangle to check yet? (Determines if we find a dead end)
    int nextid = numTriangles;  // Next triangle to examine
    int adjIndex = 0;  // Current adjacent triangle to examine (index within the current triangle's adj[] array)
    int adjid = numTriangles;  // ID of current adjacent triangle under study

    // Iterate over all possible branches
    // ===========================================================================================
    while (moreBranches) {

      // Go to the start of the current branch
      id = branches[checkedBranch];

      // Search this branch of the path
      while (noDeadEnd) {

        // Check adjacent triangles
        adjIndex = (int) random(0, 3);  // Pick an adjacent triangle at random
        for (int i = 0; i < 3; i += 1, adjIndex +=1) {
          if (adjIndex > 2) {
            // Wrap around to the first adjacent triangle
            adjIndex = 0;
          }
          adjid = grid[id].adj[adjIndex];  // Give the index a shorter name

          // Examine this adjacent triangle, if it is inside the grid and has not already been checked
          if (adjid != numTriangles && checked[adjid] == 0) {
            // We have not yet checked this triangle
            if (grid[adjid].type == 0 || grid[adjid].type == 2) {
              // This triangle is on the path
              onPath[numPath] = adjid;
              numPath += 1;
              // Set it as the nextid to check, or mark it as a branch to check later
              if (noNext) {
                nextid = adjid;
                noNext = false;
              } 
              else {
                branches[numBranch] = adjid;
                numBranch += 1;
              }
            }  // end if-block of if(grid[adjid].type == 0 || grid[adjid].type == 2)

            else {
              // This triangle is a barrier (Type other than 0 or 2)
              barriers[numBarrier] = adjid;
              numBarrier += 1;
              if (grid[adjid].type == 3) {
                // This triangle is also a teleporter
                // Mark its linked triangle as both a barrier, checked and as either a branch or the next triangle to check
                barriers[numBarrier] = grid[adjid].link;
                numBarrier += 1;
                checked[grid[adjid].link] = numTriangles;
                if (noNext) {
                  nextid = grid[adjid].link;
                  noNext = false;
                } 
                else {
                  branches[numBranch] = grid[adjid].link;
                  numBranch += 1;
                }
              }  // end if(grid[adjid].type == 3)
            }  // end else-block of if(grid[adjid].type == 0 || grid[adjid].type == 2)

            checked[adjid] = numTriangles;  // Mark adjacent triangle as checked.
          }  // end if(adjid != numTriangles && checked[adjid] == 0)
        }  // end for i

        // Are we at a dead end? Do we have the next place to go?
        if (noNext) {
          noDeadEnd = false;
        }

        // If we can continue, prepare for the next iteration
        noNext = true;
        id = nextid;  // Go to the next triangle
      }  // end while(noDeadEnd)

      checkedBranch += 1;  // We are finished with this branch
      noDeadEnd = true;  // Prepare to search the next branch

      // Are we finished yet?
      if (checkedBranch == numBranch) {
        // All branches have been checked
        moreBranches = false;
      }
    }  // end while(moreBranches)
  }  // end findPath()


  public int searchArray(int [] set, int value) {
    // Search an unordered array for the first occurence of a given integer value
    // Returns the index if found. If not found, returns a value equal to the length of the array.
    for (int k = 0; k < set.length; k +=1) {
      if (set[k] == value) {
        return k;
      }
    }  // end for k
    // Value was not found in the array set[]
    return set.length;
  }  // end searchArray()
}  // end Triangle class

class Win {
  int recordFinalTime;
  boolean stopCountBlock = false;
  int copyCountBlock = 0;
  public void display() {
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
  public void press() {
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

class WinEffect {
  Star s;
  Star s2;
  Star[] stars;
  int numstars = 12;
  int r_x;
  int r_y;

  public void install()
  {
    //size(600, 400);
    s = new Star(300, 200, 10, 40, 0);
    stroke(255);
    r_x = (int)random(width);
    r_y = (int)random(height);
    s2 = new Star(r_x, r_y, 8, 200, 0);

    stars = new Star[numstars];
    for (int i = 0; i < numstars; i++)
    {
      stars[i] = new Star(random(width), random(height), 7, 40, 0);
    }

    //  s2.sz = s.sz;
    //  s2.sz += 200;
  }
  public void display()
  {
    //background(0);
    fill(0);
    strokeWeight (2);
    stroke(255,255,0);
    drawStar(s);
    drawStar(s2);

    starShine(s);
    starShine(s2);

    for (int i = 0; i < numstars; i++)
    {
      drawStar(stars[i]);
      starShine(stars[i]);
    }
    strokeWeight(1);
    stroke(0);
  }

  public void starShine(Star s)
  {
    // update the input star: increase the timer
    // and increase its rotation value
    s.spin -= 0.07f;
    s.shinetime += 0.045f;
  }

  public void drawStar(Star s)
  {
    // draw the input star on the screen

    float a, x, y, p;
    for (int i = 0; i < s.arms; i++)
    {
      a = i*2*PI/s.arms + s.spin;
      x = s.x;
      y = s.y;
      p = s.sz + s.sz/3*sin(s.shinetime);

      line(x, y, x+p*cos(a), y+p*sin(a));
    }
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Thuong_Mai_Bernard_Llanos_Ball_Drop" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
