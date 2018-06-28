class Hole {
  float xhole;
  float yhole;
  float diameter;
  color c;
  
  Hole (float x, float y, float side){
    xhole = x;
    yhole = y;
    diameter = side/sqrt(3);
    c = color (255,0,255,200);
  }
  
  void display(){
    fill (c);
    ellipse(xhole, yhole, diameter,diameter);
  }
}
  
