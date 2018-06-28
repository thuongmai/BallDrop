
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

