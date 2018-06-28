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


  void enemyColor(int type, float opacity, float jumpTime) {
    // Setting enemy fill colour based on type
    switch(type) {
    case 0:
      if (jumpTime > 0 ) {
        fill(255);  // White, fully opaque
      } 
      else {
        fill(#00FFFD, opacity);  // Bright blue
      }
      break;
    case 1:
      fill(#C93737, opacity);  // Red
      break;
    case 2:
      fill(#D1439F, opacity);  // Magenta
      break;
    case 3:
      fill(#CB7C12, opacity);  // Orange
      break;
    }
  }  // end enemyColor()


  void createEnemies(int numTriangles, Triangle [] grid, ArrayList enemies, int [] numTypes, float side, float [] radiiCount, float [] vCount, int [] freeIndices, int [] spaces) {
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
        t_ = vCount[i]*(0.5-2*sqrt(3)/radiiCount[i]);

        // Use Enemy Constructor and add an element to the enemies ArrayList
        enemies.add(new Enemy(x_, y_, radii[i], speeds[i], path_, currentTri_, t_, i, enemies.size()));
      }  // end for j
    }  // end for i
  }  // end createEnemies()


  void display(ArrayList enemies, float jumpTime) {
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


  void findFreeTri(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, boolean countAllBalls) {
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
        float t_ = vCount*(0.5-2*sqrt(3)/radiiCount);
        enemies.add(new Enemy(x_, y_, r_, v_, path_, currentTri_, t_, type_, enemies.size()));
      }  // end if(isSpace)
    }  // end if(playerBalls.size != 0)
  }  // end addEnemy()


  int maxDistPosition(int numTriangles, Triangle [] grid, ArrayList playerBalls, int [] currentFreeIndices) {
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


  int moveAndBounce(int numTriangles, Triangle [] grid, ArrayList enemies, float side, float jumpTime, int hole) {
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
  
  
      void move0(Enemy ball, Triangle [] grid){
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
      
      
      void move1(Enemy ball, Triangle [] grid, float side, int hole){
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
      
      
      void move2(Enemy ball, Triangle [] grid, float side){
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
            if(ball.t >= tmid - 0.5 && ball.t < tmid + 0.5){
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
      
      
      void move3(Enemy ball, Triangle [] grid, float side, float depth){
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
      
      
      int move4(ArrayList enemies, Enemy ball, Triangle [] grid, float side, int hole){
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
      
      void move5(Enemy ball, Triangle [] grid, float side, float rotTime){
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
      
      void move6(Enemy ball, Triangle [] grid, float side){
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
      
      void move7(Enemy ball, Triangle [] grid, float side, float jumpTime, float rotTime){
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
      
      void move8(ArrayList enemies, Enemy ball){
        // Sub-Function of moveAndBounce()
        if(ball.t > -2){
          // Remove the corpse from the level
          removeEnemy(enemies, ball.id);          
        } else {
          ball.t += 1;  // Dead enemy objects have an increasing timer
        }
      }  // end move8()


  void removeEnemy(ArrayList enemies, int id) {
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
  
  
  void speedChange(ArrayList enemies, float [] vCount, boolean up){
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

