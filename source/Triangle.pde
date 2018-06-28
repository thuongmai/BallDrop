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


  int numTriangles(int columns) {
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


  float createGrid(int numTriangles, Triangle [] grid, int columns, float x1, float y1, float y2) {
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


  void  display (int numTriangles, Triangle [] grid, float side, int numTeleport, int hole, Enemy ball) {
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
      stroke(#00FF63);  // Bright green-blue
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
          color ac = color(255,255,0);  // Active colour
          color ic = color(150);  // Inactive colour
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
  

      void fractalBreak(float ax, float ay, float bx, float by, float cx, float cy , int num){
        // Sub-function of the display() method
        // Draw a fractal pattern of triangles, given the coordinates of a triangle,
        //   and the number of fractal levels to draw.
        if(num == 0){
          // No code to execute
        } else {
          stroke(#D8E4FF);  // Bluish-White
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

  boolean inside(int index, Triangle [] grid, float x, float y) {
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

  int nearest(int numTriangles, Triangle [] grid, float x, float y) {
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

  void playerBlock(int numTriangles, Triangle [] grid, ArrayList enemies, int hole, int [] currentFreeIndices, float x, float y) { //change here
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


  void adjacent(int numTriangles, Triangle [] grid, int columns) {
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


  int find(int numTriangles, Triangle [] grid, int col_, int row_) {
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


  void collisionSet(int numTriangles, Triangle [] grid) {
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


  void createBlocks(int numTriangles, Triangle [] grid, int [] freeIndices, int [] numTypes) {
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


  void reserve4Spaces(int numTriangles, Triangle [] grid, int columns, int [] freeIndices, int [] spaces) {
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


  int reserveSpace(int numTriangles, int [] freeIndices) {
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

  int findEdge(int numTriangles, Triangle [] grid, int columns, int edge) {
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


  void verifyBlocks(int numTriangles, Triangle [] grid, int [] spaces, int [] freeIndices) {
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


  void findPath(int numTriangles, Triangle [] grid, int start, int [] onPath, int [] barriers) {
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


  int searchArray(int [] set, int value) {
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

