import java.lang.reflect.Method;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

/**
 * A user interface component to enable selecting and animating time ranges. This TimeRangeSlider handles both mouse and
 * keyboard input, and adapts the display accordingly. Firstly, the user can select the time window, as well as the
 * length of the time range. Secondly, this component can be used to handle animation, by running step-wise through the
 * time. If a {@link TimeRangeSliderListener} has been specified, it will be notified of any time updates, whether by
 * user interaction or by animation.
 * 
 */
public class TimeRangeSlider {

  protected PApplet p;

  // Time and ranges --------------------------

  /** Start time of overall time, i.e. the full range to select sub ranges in. */
  protected DateTime startDateTime;
  /** End time of overall time, i.e. the full range to select sub ranges in. */
  protected DateTime endDateTime;

  /** Time interval of the range. */
  protected int aggregationIntervalSeconds = 60;
  /** Time interval for the animation. */
  protected int animationIntervalSeconds;
  /** Time interval for the tick marks. Also used for interaction. */
  protected int tickIntervalSeconds;

  /** Start of selected time range. */
  protected DateTime currentStartDateTime;
  /**
   	 * End of selected time range. Is automatically set via currentStartDateTime and aggregationIntervalSeconds.
   	 */
  protected DateTime currentEndDateTime;

  // Overall time range in seconds
  private int totalSeconds;
  // Time range between ticks
  private float widthPerSecond;

  // Current x position of the range start
  protected float currentStartX;
  // Current x position of the range end
  protected float currentEndX;

  // Animation speed
  protected int framesPerInterval = 10;
  // Whether slider is currently animated
  protected boolean running = false;

  // Display properties -----------------------

  /** Whether to show tick markers. */
  protected boolean showTicks = true;

  /** Shows labels for start and end times. */
  protected boolean showStartEndTimeLabels = true;
  /** Shows labels for selected time range. */
  protected boolean showTimeRangeLabels = true;
  protected float labelPadding = 8;
  protected boolean showSelectedTimeRange = true;

  // Position and dimension -------------------

  protected float x;
  protected float y;
  protected float width;
  protected float height;

  // Handles ----------------------------------

  protected boolean centeredHandle = true;
  private boolean draggedSelectedTimeRange = false;
  private boolean draggedStartHandle = false;
  private boolean draggedEndHandle = false;
  protected float startHandleX;
  protected float endHandleX;
  protected float handleWidth;
  private float handleHeight;

  protected boolean inProximity = false;
  protected float inProximityPadding = 25;
  protected boolean alwaysShowHandles = false;

  // Interaction ------------------------------

  /** Will be called if slider has been updated. */
  protected TimeRangeSliderListener listener;

  // For multitouch purposes, i.e. to allow multiple dragging at the same time
  private String startHandleId = null;
  private String endHandleId = null;
  private String timeRangeHandleId = null;
  private static final String MOUSE_ID = "mouse";

  // Event ------------------------------------
  private Method timeUpdatedMethod;

  // ------------------------------------------

  /**
   	 * Creates a TimeRangeSlider.
   	 * 
   	 * @param p
   	 *            The PApplet. Optionally, this is also a TimeRangeSlider.
   	 * @param x
   	 *            The x position of this UI.
   	 * @param y
   	 *            The y position of this UI.
   	 * @param width
   	 *            The width of this UI.
   	 * @param height
   	 *            The height of this UI.
   	 * @param startDateTime
   	 *            The overall start time of this slider.
   	 * @param endDateTime
   	 *            The overall end time of this slider.
   	 * @param aggregationIntervalSeconds
   	 *            The number of seconds for every interval. Will be used for the tics as well. Use
   	 *            {@link #setTickIntervalSeconds(int)} for different intervals.
   	 */
  public TimeRangeSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime, 
  DateTime endDateTime, int aggregationIntervalSeconds) {
    this.p = p;
    if (p instanceof TimeRangeSliderListener) {
      this.listener = (TimeRangeSliderListener) p;
    }
    FontManager.getInstance(p);

    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.currentStartDateTime = startDateTime;
    this.currentEndDateTime = this.currentStartDateTime.plusSeconds(aggregationIntervalSeconds);

    this.aggregationIntervalSeconds = aggregationIntervalSeconds;
    this.tickIntervalSeconds = aggregationIntervalSeconds;
    this.animationIntervalSeconds = aggregationIntervalSeconds;

    handleWidth = 8;
    handleHeight = height;

    totalSeconds = Seconds.secondsBetween(startDateTime, endDateTime).getSeconds();
    widthPerSecond = width / totalSeconds;

    // event hook
    try {
      timeUpdatedMethod = p.getClass().getMethod("timeUpdated", new Class[] { 
        DateTime.class, DateTime.class
      }
      );
    } 
    catch (Exception e) {
    }
  }

  public void setTickIntervalSeconds(int tickIntervalSeconds) {
    this.tickIntervalSeconds = tickIntervalSeconds;
    this.animationIntervalSeconds = tickIntervalSeconds;
  }

  public void setAnimationIntervalSeconds(int animationIntervalSeconds) {
    this.animationIntervalSeconds = animationIntervalSeconds;
  }

  /**
   	 * Draws this TimeRangeSlider.
   	 */
  public void draw() {
    if ((p.frameCount % framesPerInterval == 0) && running) {
      nextAnimationStep();
    }

    drawTimeLine();
    drawStartAndEndTics();

    // Show tics
    if (showTicks) {
      float distancePerTic = widthPerSecond * tickIntervalSeconds;
      for (float tx = x; tx < x + width; tx += distancePerTic) {
        drawTic(tx);
      }
    }

    int currentStartSeconds = Seconds.secondsBetween(startDateTime, currentStartDateTime).getSeconds();
    int currentEndSeconds = Seconds.secondsBetween(startDateTime, currentEndDateTime).getSeconds();
    currentStartX = x + widthPerSecond * currentStartSeconds;
    currentEndX = x + widthPerSecond * currentEndSeconds;

    // Show selected time range
    if (showSelectedTimeRange) {
      drawSelectedTimeRange(draggedSelectedTimeRange);
    }

    // Show handles to change time range
    startHandleX = currentStartX;
    endHandleX = currentEndX;
    if (centeredHandle) {
      startHandleX -= handleWidth / 2;
      endHandleX -= handleWidth / 2;
    }

    if (inProximity || alwaysShowHandles) {
      drawHandle(startHandleX, draggedStartHandle, true);
      drawHandle(endHandleX, draggedEndHandle, false);
    }

    // Show labels for selected time range
    if (showTimeRangeLabels) {
      drawTimeRangeLabels();
    }
    // Show labels for complete time
    if (showStartEndTimeLabels) {
      drawStartEndTimeLabels();
    }
  }

  protected void drawStartEndTimeLabels() {
    PFont font = FontManager.getInstance().getLabelFont();
    p.fill(0, 200);
    p.textFont(font);

    String startTimeLabel = startDateTime.toString("HH:mm");
    int startLabelX = (int) (x - p.textWidth(startTimeLabel) - labelPadding);
    int labelY = (int) (y + font.getSize() / 2 - 3);
    p.text(startTimeLabel, startLabelX, labelY);

    String endTimeLabel = endDateTime.toString("HH:mm");
    int endLabelX = (int) (x + width + labelPadding);
    p.text(endTimeLabel, endLabelX, labelY);
  }

  protected void drawTimeRangeLabels() {
    String timeRangeLabel = currentStartDateTime.toString("HH:mm") + " - " + currentEndDateTime.toString("HH:mm");
    PFont font = FontManager.getInstance().getMiniLabelFont();
    p.textFont(font);
    int labelX = (int) (currentStartX + (currentEndX - currentStartX) / 2 - p.textWidth(timeRangeLabel) / 2);
    int labelY = (int) (y + font.getSize() + labelPadding / 2);
    drawLabel(timeRangeLabel, labelX, labelY);
  }

  protected void drawLabel(String timeRangeLabels, int labelX, int labelY) {
    p.fill(66);
    p.text(timeRangeLabels, labelX, labelY);
  }

  protected void drawTic(float tx) {
    float tyTop = y - 4;
    float tyBottom = y + 4;
    p.stroke(0, 50);
    p.line(tx, tyTop, tx, tyBottom);
  }

  protected void drawStartAndEndTics() {
    float yTop = y - height / 2;
    float yBottom = y + height / 2;
    p.line(x, yTop, x, yBottom);
    p.line(x + width, yTop, x + width, yBottom);
  }

  protected void drawSelectedTimeRange(boolean highlight) {
    float yTop = y - height / 2;
    if (highlight) {
      p.fill(200, 66, 66, 200);
    } 
    else {
      p.fill(66, 200);
    }
    p.rect(currentStartX, yTop + height / 4, currentEndX - currentStartX, height / 2);
  }

  protected void drawTimeLine() {
    p.stroke(0);
    p.noFill();
    p.line(x, y, x + width, y);
  }

  protected void drawHandle(float handleX, boolean highlight, boolean start) {
    p.fill(250, 220);
    if (highlight) {
      p.stroke(140, 20, 20, 150);
    } 
    else {
      p.stroke(140);
    }

    float handleY = y - height / 2;
    p.rect(handleX, handleY, handleWidth, handleHeight);
    p.line(handleX + 3, handleY + 4, handleX + 3, handleY + 12);
    p.line(handleX + 5, handleY + 4, handleX + 5, handleY + 12);
  }

  // --------------------------------------------------------------

  /**
   	 * Goes to next animations step, i.e. slides the time by animationIntervalSeconds.
   	 */
  public void nextAnimationStep() {
    currentStartDateTime = currentStartDateTime.plusSeconds(animationIntervalSeconds);
    if (currentStartDateTime.isAfter(endDateTime.minusSeconds(aggregationIntervalSeconds))) {
      currentStartDateTime = startDateTime;
    }
    updateAnimationStep();
  }

  /**
   	 * Goes to previous animations step, i.e. slides the time by -animationIntervalSeconds.
   	 */
  public void previousAnimationStep() {
    currentStartDateTime = currentStartDateTime.minusSeconds(animationIntervalSeconds);
    if (currentStartDateTime.isBefore(startDateTime)) {
      currentStartDateTime = endDateTime.minusSeconds(aggregationIntervalSeconds);
    }
    updateAnimationStep();
  }

  /**
   	 * Goes to next interval step, i.e. slides the time by aggregationIntervalSeconds.
   	 */
  public void nextInterval() {
    currentStartDateTime = currentStartDateTime.plusSeconds(aggregationIntervalSeconds);
    if (currentStartDateTime.isAfter(endDateTime.minusSeconds(aggregationIntervalSeconds))) {
      currentStartDateTime = startDateTime;
    }
    updateAnimationStep();
  }

  /**
   	 * Goes to previous interval step, i.e. slides the time by -aggregationIntervalSeconds.
   	 */
  public void previousInterval() {
    currentStartDateTime = currentStartDateTime.minusSeconds(aggregationIntervalSeconds);
    if (currentStartDateTime.isBefore(startDateTime)) {
      currentStartDateTime = endDateTime.minusSeconds(aggregationIntervalSeconds);
    }
    updateAnimationStep();
  }

  protected void updateAnimationStep() {
    currentEndDateTime = currentStartDateTime.plusSeconds(aggregationIntervalSeconds);

    // Two event mechanisms: Listener or Reflection
    if (listener != null) {
      // Call implemented method of listener

      // FIXME timeUpdated is called too often from TimeRangeSlider (even if not updated)
      listener.timeUpdated(currentStartDateTime, currentEndDateTime);
    } 
    else if (timeUpdatedMethod != null) {
      // Call method of applet if implemented
      try {
        timeUpdatedMethod.invoke(p, new Object[] { 
          currentStartDateTime, currentEndDateTime
        }
        );
      } 
      catch (Exception e) {
        System.err.println("Disabling timeUpdatedMethod()");
        e.printStackTrace();
        timeUpdatedMethod = null;
      }
    }
  }

  public void playOrPause() {
    running = !running;
  }

  public void play() {
    running = true;
  }

  public void pause() {
    running = false;
  }

  // Interactions -------------------------------------------------

  public void onMoved(int checkX, int checkY) {
    inProximity = checkX > x - inProximityPadding && checkX < x + width + inProximityPadding
      && checkY > y - height / 2 - inProximityPadding && checkY < y + height / 2 + inProximityPadding;

    // Checks whether the main selector is moved
    draggedSelectedTimeRange = isOverTimeRange(checkX, checkY);

    draggedStartHandle = isOverStartHandle(checkX, checkY);
    draggedEndHandle = isOverEndHandle(checkX, checkY);

    onAdded(checkX, checkY, MOUSE_ID);
  }

  protected boolean isOverTimeRange(int checkX, int checkY) {
    float handlePadding = (centeredHandle) ? handleWidth / 2 : handleWidth;
    float yTop = y - height / 2;
    float yBottom = y + height / 2;
    return checkX > currentStartX + handlePadding && checkX < currentEndX - handlePadding && checkY > yTop
      && checkY < yBottom;
  }

  protected boolean isOverStartHandle(int checkX, int checkY) {
    float handleY = y - height / 2;
    return checkX > startHandleX && checkX < startHandleX + handleWidth && checkY > handleY
      && checkY < handleY + handleHeight;
  }

  protected boolean isOverEndHandle(int checkX, int checkY) {
    float handleY = y - height / 2;
    return checkX > endHandleX && checkX < endHandleX + handleWidth && checkY > handleY
      && checkY < handleY + handleHeight;
  }

  public void onAdded(int checkX, int checkY, String id) {
    // Allow only one interaction at a time; either dragging handles OR timeRange.

    if (isOverStartHandle(checkX, checkY) && !draggedSelectedTimeRange) {
      draggedStartHandle = true;
      startHandleId = id;
    }

    if (isOverEndHandle(checkX, checkY) && !draggedSelectedTimeRange) {
      draggedEndHandle = true;
      endHandleId = id;
    }

    if (isOverTimeRange(checkX, checkY) && !draggedStartHandle && !draggedEndHandle) {
      draggedSelectedTimeRange = true;
      timeRangeHandleId = id;
    }
  }

  public void onReleased(int checkX, int checkY, String id) {
    if (id.equals(startHandleId)) {
      draggedStartHandle = false;
      startHandleId = null;
    }
    if (id.equals(endHandleId)) {
      draggedEndHandle = false;
      endHandleId = null;
    }
    if (id.equals(timeRangeHandleId)) {
      draggedSelectedTimeRange = false;
      timeRangeHandleId = null;
    }
  }

  public void onDragged(float checkX, float checkY, float oldX, float oldY) {
    onDragged(checkX, checkY, oldX, oldY, MOUSE_ID);
  }

  public void onDragged(float checkX, float checkY, float oldX, float oldY, String id) {

    float widthPerTic = widthPerSecond * tickIntervalSeconds;
    // float widthPerTic = widthPerSecond * aggregationIntervalSeconds;

    int currentStartSeconds = Seconds.secondsBetween(startDateTime, currentStartDateTime).getSeconds();
    int currentEndSeconds = Seconds.secondsBetween(startDateTime, currentEndDateTime).getSeconds();
    currentStartX = x + widthPerSecond * currentStartSeconds;
    currentEndX = x + widthPerSecond * currentEndSeconds;

    if (draggedEndHandle && id.equals(endHandleId)) {
      float tx = PApplet.constrain(checkX, x, x + width);
      tx = Math.round((tx - currentStartX) / widthPerTic) * widthPerTic;
      int seconds = Math.round(tx / widthPerSecond);
      // Update if larger than first tick, and different to prev value
      if (seconds >= tickIntervalSeconds && seconds != aggregationIntervalSeconds) {
        // if (seconds >= aggregationIntervalSeconds && seconds != aggregationIntervalSeconds) {
        aggregationIntervalSeconds = seconds;
        updateAnimationStep();
      }
    }

    if (draggedStartHandle && id.equals(startHandleId)) {
      float tx = PApplet.constrain(checkX, x, x + width);
      tx = Math.round((currentEndX - tx) / widthPerTic) * widthPerTic;
      int seconds = Math.round(tx / widthPerSecond);
      if (seconds >= tickIntervalSeconds && seconds != aggregationIntervalSeconds) {
        // if (seconds >= aggregationIntervalSeconds && seconds != aggregationIntervalSeconds) {
        aggregationIntervalSeconds = seconds;
        currentStartDateTime = currentEndDateTime.minusSeconds(aggregationIntervalSeconds);
        updateAnimationStep();
      }
    }

    if (draggedSelectedTimeRange && timeRangeHandleId != null && timeRangeHandleId.equals(id)) {
      // TODO tn, Oct 7, 2011: Move slider correctly if borders are hit (use onClick and
      // onRelease)

      checkX = Math.round(checkX / widthPerTic) * widthPerTic;
      oldX = Math.round(oldX / widthPerTic) * widthPerTic;
      float diffX = checkX - oldX;

      if (currentStartX + diffX < x || currentEndX + diffX > x + width) {
        diffX = 0;
      }

      int seconds = Math.round(diffX / widthPerSecond);
      if (Math.abs(seconds) >= tickIntervalSeconds) {
        // if (Math.abs(seconds) >= aggregationIntervalSeconds) {
        currentStartDateTime = currentStartDateTime.plusSeconds(seconds);
        updateAnimationStep();
      }
    }
  }

  public void onKeyPressed(char key, int keyCode) {
    if (key == ' ') {
      playOrPause();
    }
    if (key == PConstants.CODED) {
      if (keyCode == PConstants.LEFT) {
        previousAnimationStep();
      }
      if (keyCode == PConstants.RIGHT) {
        nextAnimationStep();
      }
    }
  }

  public DateTime getCurrentDateTime() {
    return currentStartDateTime;
  }

  public DateTime getCurrentStartDateTime() {
    return currentStartDateTime;
  }

  public DateTime getCurrentEndDateTime() {
    return currentEndDateTime;
  }

  public void setShowTicks(boolean showTicks) {
    this.showTicks = showTicks;
  }

  public void setShowStartEndTimeLabels(boolean showStartEndTimeLabels) {
    this.showStartEndTimeLabels = showStartEndTimeLabels;
  }

  public void setShowTimeRangeLabels(boolean showTimeRangeLabels) {
    this.showTimeRangeLabels = showTimeRangeLabels;
  }

  public void setShowSelectedTimeRange(boolean showSelectedTimeRange) {
    this.showSelectedTimeRange = showSelectedTimeRange;
  }

  public void setInProximityPadding(float inProximityPadding) {
    this.inProximityPadding = inProximityPadding;
  }

  public void setAlwaysShowHandles(boolean alwaysShowHandles) {
    this.alwaysShowHandles = alwaysShowHandles;
  }

  public boolean isPlaying() {
    return running;
  }
}

