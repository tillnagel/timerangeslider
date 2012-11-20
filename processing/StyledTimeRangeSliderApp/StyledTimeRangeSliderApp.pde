/*
 * Example showing two styled TimeRangeSlider. Additionally, a play button to control one of the sliders.  
 *
 * (c) 2012 Till Nagel, tillnagel.com (see license.txt)
 *
 * Style based on Blaubarry UI Kit by Mikael Eldenberg.
 */

TimeRangeSlider timeRangeSlider1;
TimeRangeSlider timeRangeSlider2;

PImage button;
PImage playButtonNormal;
PImage playButtonActive;
PImage pauseButtonNormal;
PImage pauseButtonActive;
int buttonX = 450;
int buttonY = 187;

void setup() {
  size(800, 600);
  smooth();

  playButtonNormal = loadImage("timerange/button-play.png");
  playButtonActive = loadImage("timerange/button-play-pressed.png");
  pauseButtonNormal = loadImage("timerange/button-pause.png");
  pauseButtonActive = loadImage("timerange/button-pause-pressed.png");
  button = playButtonNormal;

  timeRangeSlider1 = new StyledTimeRangeSlider(this, 100, 200, 300, 16, new DateTime(2011, 04, 11, 10, 0, 0), 
  new DateTime(2011, 04, 11, 22, 0, 0), 60 * 30);
  timeRangeSlider2 = new StyledWeekdayTimeRangeSlider(this, 100, 300, 400, 16, 
  new DateTime(2012, 05, 21, 0, 0, 0), new DateTime(2012, 05, 27, 0, 0, 1), 60 * 60 * 24);
}

void draw() {
  background(58, 63, 66);

  image(button, buttonX, buttonY);

  timeRangeSlider1.draw();
  timeRangeSlider2.draw();
}

void mousePressed() {
  if (dist(mouseX, mouseY, buttonX + 13, buttonY + 13) < 15) {
    if (timeRangeSlider1.isPlaying()) {
      button = pauseButtonActive;
    } 
    else {
      button = playButtonActive;
    }
  }
}

void mouseReleased() {
  if (dist(mouseX, mouseY, buttonX + 13, buttonY + 13) < 15) {
    if (timeRangeSlider1.isPlaying()) {
      button = playButtonNormal;
    } 
    else {
      button = pauseButtonNormal;
    }
    timeRangeSlider1.playOrPause();
  }
}

void timeUpdated(DateTime startDateTime, DateTime endDateTime) {
  println("timeUpdated to " + startDateTime.toString("yyyy-MM-dd HH:mm") + " - "
    + endDateTime.toString("yyyy-MM-dd HH:mm"));
}

void keyPressed() {
  // Keyboard events only go to one TimeRangeSlider (of course you can do that for both)
  timeRangeSlider1.onKeyPressed(key, keyCode);
}

void mouseMoved() {
  timeRangeSlider1.onMoved(mouseX, mouseY);
  timeRangeSlider2.onMoved(mouseX, mouseY);
}

void mouseDragged() {
  timeRangeSlider1.onDragged(mouseX, mouseY, pmouseX, pmouseY);
  timeRangeSlider2.onDragged(mouseX, mouseY, pmouseX, pmouseY);
}

