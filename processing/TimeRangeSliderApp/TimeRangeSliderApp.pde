/*
 * Simple TimeRangerSlider example.
 * 
 * Drag handles and time range bar to move around the time range. Press SPACE to toggle animation. Press LEFT and RIGHT
 * arrow keys to step through the time range.
 *
 * (c) 2012 Till Nagel, tillnagel.com (see license.txt)
 */

TimeRangeSlider timeRangeSlider;

void setup() {
  size(800, 600);
  smooth();

  timeRangeSlider = new TimeRangeSlider(this, 100, 200, 300, 16, new DateTime(2011, 04, 11, 10, 0, 0), 
  new DateTime(2011, 04, 11, 22, 0, 0), 60 * 60);
  timeRangeSlider.setTickIntervalSeconds(60 * 30);
}

void draw() {
  background(240);

  timeRangeSlider.draw();
}

// Gets called each time the time ranger slider has changed, both by user interaction as well as by animation
void timeUpdated(DateTime startDateTime, DateTime endDateTime) {
  println("timeUpdated to " + startDateTime.toString("hh:mm") +
    " - " + endDateTime.toString("hh:mm"));
}


// Forwarding of key and mouse events

void keyPressed() {
  timeRangeSlider.onKeyPressed(key, keyCode);
}

void mouseMoved() {
  timeRangeSlider.onMoved(mouseX, mouseY);
}

void mouseDragged() {
  timeRangeSlider.onDragged(mouseX, mouseY, pmouseX, pmouseY);
}

