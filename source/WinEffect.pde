class WinEffect {
  Star s;
  Star s2;
  Star[] stars;
  int numstars = 12;
  int r_x;
  int r_y;

  void install()
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
  void display()
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

  void starShine(Star s)
  {
    // update the input star: increase the timer
    // and increase its rotation value
    s.spin -= 0.07;
    s.shinetime += 0.045;
  }

  void drawStar(Star s)
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

