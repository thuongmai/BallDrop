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

void setup() {
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
    float t_ = vEn[type_]*(0.5-2*sqrt(3)/radiiEn[type_]);
    arrayEn.add(new Enemy(x_, y_, sideTri/radiiEn[type_], sideTri/vEn[type_], path_, triIndex, t_, type_, arrayEn.size()));

    // Enemy
    triIndex = 10;
    currentTri_Enemy = gridTri[triIndex];
    x_ = currentTri_Enemy.x;
    y_ = currentTri_Enemy.y;
    type_ = 1;
    path_ = 3;
    t_ = vEn[type_]*(0.5-2*sqrt(3)/radiiEn[type_]);
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

void draw() {
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


Triangle calculatorTriangle() {
  // Creating a Triangle used for Calculations
  float vertices_ [][] = new float [3][2];
  for (int i = 0; i < 3; i+=1) {
    for (int j = 0; j < 2; j+=1) {
      vertices_[i][j] = 0;
    }
  }
  return new Triangle(0.0, 0.0, vertices_, true, 0, 0, 0, 0, 0);
}  // end calculatorTriangle()


Enemy calculatorEnemy() {
  // Creating an Enemy used for Calculations
  return new Enemy(0, 0, 0, 0, 0, 0, 0, 0, 0);
}  // end calculatorEnemy()


void mousePressed() {
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


void keyPressed() {
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


void collisions(ArrayList enemies, Enemy ball, float jumpTime) {
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


void replaceEnemy() {
  // Attempts to create a new enemy in the current frame
  //   to replace enemies fallen into the hole

  if (newEn > 0) {
    // Choose which type of enemy to create
    int type_ = 1;  // Type of enemy to create (Initially Type 1 - Normal Enemy)
    if (level >= 3) {  // REPLACE WITH LEVEL IN WHICH THE TYPE 3 ENEMY IS INTRODUCED
      float roll = random(0, 1);
      if (roll > 0.6) {
        type_ = 3;  // Create a block-breaking enemy (Type 3)
      } 
      else if (roll > 0.2) {
        type_ = 2;  // Create a ghost enemy (Type 2)
      }
    } 
    else if (level >= 2) {  // REPLACE WITH LEVEL IN WHICH THE TYPE 2 ENEMY IS INTRODUCED
      float roll = random(0, 1);
      if (roll > 0.4) {
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


void PLAY() {
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

void tutorial () {
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

