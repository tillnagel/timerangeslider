import java.lang.reflect.Method;

import org.joda.time.DateTime;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class MillisPointSlider extends TimeRangeSlider {

	
	protected int aggregationIntervalMilliseconds;
	protected int animationIntervalMilliseconds;
	protected float tickIntervalMilliseconds;
	protected TimePointSliderListener plistener;
	protected TickPointSliderListener tlistener;
	private int totalMillis;
	private float widthPerMillis;
	private DateTime currentDateTime;
	private Integer currentTick = 1;
	private boolean draggedHandle = false;
	private float handleHeight;
	private float currentX;
	private float handleX;
	private Method timeUpdatedMethod, tickUpdatedMethod;
	private static final String MOUSE_ID = "mouse";

	public MillisPointSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime, DateTime endDateTime, int aggregationIntervalMilliseconds) {

		super();
		framesPerInterval = 5;
		this.p = p;
	  //   if (p instanceof TimePointSliderListener) {
			// this.plistener = (TimePointSliderListener) p;
	  //   }
	     if (p instanceof TickPointSliderListener) {
			this.tlistener = (TickPointSliderListener) p;
	    }
	    FontManager.getInstance(p);

	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;

	    this.startDateTime = startDateTime;
	    this.endDateTime = endDateTime;
	    currentDateTime = startDateTime;
	    //this.currentStartDateTime = startDateTime;
	    //this.currentEndDateTime = this.currentStartDateTime.plusMillis(aggregationIntervalMilliseconds);

	    this.aggregationIntervalMilliseconds = aggregationIntervalMilliseconds;
	    this.tickIntervalMilliseconds = 33.333f;
	    this.animationIntervalMilliseconds = aggregationIntervalMilliseconds;

	    handleWidth = 8;
	    handleHeight = height;

	    //totalSeconds = Seconds.secondsBetween(startDateTime, endDateTime).getSeconds();
	    totalMillis = endDateTime.getMillisOfDay()-startDateTime.getMillisOfDay();

	    System.out.println(totalMillis);
	    widthPerMillis = width / totalMillis;

	    // event hook
	    try {
	      // timeUpdatedMethod = p.getClass().getMethod("timeUpdated", new Class[] { 
	      //   DateTime.class
	      // });
	      tickUpdatedMethod = p.getClass().getMethod("tickUpdated", new Class[] {
			Integer.class
	      });
	    } 
	    catch (Exception e) {
	    }



	}

	//@Override
	public void setTickIntervalMilliseconds(int tickIntervalMS) {
		this.tickIntervalMilliseconds = tickIntervalMS;
		this.animationIntervalMilliseconds = tickIntervalMS;

	}

	//@Override
	public void setAnimationIntervalMilliseconds(int animationIntervalMS) {
		this.animationIntervalMilliseconds = animationIntervalMS;

	}

	@Override
	public void draw() {

	    if ((p.frameCount % framesPerInterval == 0) && running) {
	      nextAnimationStep();
	    }
	    p.stroke(255);
	    drawTimeLine();
	    drawStartAndEndTics();
	    int i = 0;
	    // Show tics
	    if (showTicks) {
	      float distancePerTic = widthPerMillis* tickIntervalMilliseconds;
	      for (float tx = x; tx < x + width; tx += distancePerTic) {
	        drawTic(tx,i);
	        i++;
	      }
	    }
	    //System.out.println(i);
	    //int currentStartSeconds = Seconds.secondsBetween(startDateTime, currentStartDateTime).getSeconds();
	    //int currentEndSeconds = Seconds.secondsBetween(startDateTime, currentEndDateTime).getSeconds();
	    int currentSeconds = currentDateTime.getMillisOfDay()-startDateTime.getMillisOfDay();

	    //currentStartX = x + widthPerSecond * currentStartSeconds;
	    //currentEndX = x + widthPerSecond * currentEndSeconds;
	    currentX = x+ widthPerMillis * currentSeconds;

	    // Show selected time range
	    // if (showSelectedTimeRange) {
	    //   drawSelectedTimeRange(draggedSelectedTimeRange);
	    // }

	    // Show handles to change time range
	    handleX = currentX;
	    //startHandleX = currentStartX;
	    //endHandleX = currentEndX;
	    if (centeredHandle) {
	      //startHandleX -= handleWidth / 2;
	      //endHandleX -= handleWidth / 2;
			handleX -= handleWidth / 2;
	    }

	    //if (inProximity || alwaysShowHandles) {
	      //drawHandle(startHandleX, draggedStartHandle, true);
	      //drawHandle(endHandleX, draggedEndHandle, false);
			drawHandle(handleX, draggedHandle, false);
	    //}

	    // Show labels for selected time range
	    if (showTimeRangeLabels) {
	      //drawTimeRangeLabels();
			drawHandleLabel();
	    }
	    // Show labels for complete time
	    if (showStartEndTimeLabels) {
			drawStartEndTimeLabels();
	    }
	}

	// @Override
	// protected void drawSelectedTimeRange(boolean highlight) {

	// }

	@Override
	protected void drawHandle(float x, boolean highlight, boolean start) {
	    p.fill(250, 220);
	    if (highlight) {
	      p.stroke(140, 20, 20, 150);
	    } 
	    else {
	      p.stroke(140);
	    }

	    float handleY = y - height / 2;
	    p.rect(handleX, handleY, handleWidth, handleHeight);
	    p.line(handleX + 3, handleY + 4, handleX + 3, handleY - 90);
	    p.line(handleX + 5, handleY + 4, handleX + 5, handleY - 90);
	}

	@Override
	protected void drawStartEndTimeLabels() {
		PFont font = FontManager.getInstance().getLabelFont();
	    p.fill(0, 200);
	    p.textFont(font);

	    String startTimeLabel = startDateTime.toString("HH:mm:ss");
	    int startLabelX = (int) (x - p.textWidth(startTimeLabel) - labelPadding);
	    int labelY = (int) (y + font.getSize() / 2 - 3);
	    p.text(startTimeLabel, startLabelX, labelY);

	    String endTimeLabel = endDateTime.toString("HH:mm:ss");
	    int endLabelX = (int) (x + width + labelPadding);
	    p.text(endTimeLabel, endLabelX, labelY);

	}


	//@Override
	protected void drawTic(float tx, int counter) {
		float tyTop;
		float tyBottom;
		//large tic every minute
		if(counter%1800==0){
			tyTop = y - 20;
			tyBottom = y +4;
			p.stroke(255);
		}
		else if(counter%30==0){
			//System.out.println("test");
		    tyTop = y - 10;
		    tyBottom = y + 4;
		    p.stroke(110, 50);			
		}

		else{
			tyTop = y;
			tyBottom = y+4;
			p.stroke(30, 50);
		}

	    p.line(tx, tyTop, tx, tyBottom);
	}

	@Override
	protected void drawStartAndEndTics() {

	    float yTop = y - height / 2;
		float yBottom = y + height / 2;
		p.line(x, yTop, x, yBottom);
		p.line(x + width, yTop, x + width, yBottom);
	}

	// @Override
	// protected void drawTimeRangeLabels() {

	// }
	protected void drawHandleLabel(){
	    //String timeRangeLabel = currentStartDateTime.toString("HH:mm") + " - " + currentEndDateTime.toString("HH:mm");
	    String timeRangeLabel = currentDateTime.toString("HH:mm:ss.SSS");
	    PFont font = FontManager.getInstance().getMiniLabelFont();
	    p.textFont(font);
	    //int labelX = (int) (currentStartX + (currentEndX - currentStartX) / 2 - p.textWidth(timeRangeLabel) / 2);
	    int labelX = (int) (currentX - p.textWidth(timeRangeLabel) / 2);
	    //int labelY = (int) (y + font.getSize() + labelPadding / 2);
	    int labelY = (int) (y-100);
	    drawLabel(timeRangeLabel, labelX, labelY);

	}

	@Override
	protected void drawLabel(String timeRangeLabels, int labelX, int labelY) {
		p.fill(66);
		p.text(timeRangeLabels, labelX, labelY);
	}

	@Override
	protected void drawTimeLine() {
		p.stroke(0);
		p.noFill();
		p.line(x, y, x + width, y);
	}

	@Override
	public void nextAnimationStep() {
		currentDateTime = currentDateTime.plusMillis(animationIntervalMilliseconds);
		currentTick+=1;
		if(currentDateTime.isAfter(endDateTime)){
			currentDateTime = endDateTime;
		}
		if(currentTick>(totalMillis-1)){
			currentTick=totalMillis-1;
		}
		updateAnimationStep();

	}

	@Override
	public void previousAnimationStep() {
		currentDateTime = currentDateTime.minusMillis(animationIntervalMilliseconds);
		currentTick-=1;
		if(currentDateTime.isBefore(startDateTime)){
			currentDateTime = startDateTime;
		}
		if(currentTick<0){
			currentTick=0;
		}
		updateAnimationStep();

	}

	// @Override
	// public void nextInterval() {
	//	currentDateTime = currentDateTime.plusMillis(aggregationIntervalMilliseconds);
	//	updateAnimationStep();
	// }

	// @Override
	// public void previousInterval() {
	//	currentDateTime = currentDateTime.minusMillis(aggregationIntervalMilliseconds);
	//	updateAnimationStep();
	// }

	@Override
	protected void updateAnimationStep() {
		//System.out.println("test");
		if(plistener != null){
			plistener.timeUpdated(currentDateTime);
			//System.out.println("test");
		}
		if(tlistener != null){
			tlistener.tickUpdated(currentTick);
		}
		else if(tickUpdatedMethod != null){
			try{
				// System.out.println("test2");
				// timeUpdatedMethod.invoke(p, new Object[] {
				//	currentDateTime
				// });
				tickUpdatedMethod.invoke(p, new Object[] {
					currentTick
				});
			}
			catch (Exception e) {
				System.err.println("Disabling timeUpdatedMethod()");
				e.printStackTrace();
				timeUpdatedMethod = null;
			}
		}
	}

	@Override
	public void onDragged(float checkX, float checkY, float oldX, float oldY, String id) {
		float widthPerTic = widthPerMillis*tickIntervalMilliseconds;
		//running = false;

		int currentSeconds = currentDateTime.getMillisOfDay()-startDateTime.getMillisOfDay();
		currentX = x + widthPerMillis * currentSeconds;

		if(draggedHandle) {
			float tx = PApplet.constrain(checkX, x, x+width);
			//if positive, drag was forward. if negative, drag was backward.
			tx = Math.round((tx-currentX)/widthPerTic)*widthPerTic;
			int millis = Math.round(tx/widthPerMillis);
			if(Math.abs(millis)>=tickIntervalMilliseconds) {
				if(millis<0){
					currentDateTime = currentDateTime.minusMillis(millis*(-1));
					//currentTick-=Math.round((tx-currentX*-1)/widthPerTic);
					currentTick +=Math.round(millis/tickIntervalMilliseconds);
					if(currentTick<0)
						currentTick = 0;
					updateAnimationStep();
				}
				else{
					currentDateTime = currentDateTime.plusMillis(millis);
					//currentTick+=Math.round((tx-currentX)/widthPerTic);
					currentTick += Math.round(millis/tickIntervalMilliseconds);
					if(currentTick>(totalMillis-1))
						currentTick = totalMillis-1;
					updateAnimationStep();
				}
			}

		}
	}

	@Override
	public void onAdded(int checkX, int checkY, String id) {

		if (isOverHandle(checkX, checkY)){
			draggedHandle=true;
		}

	}

	@Override
	public void onReleased(int checkX, int checkY, String id) {
		running = true;
		draggedHandle=false;

	}
	@Override
	public void onMoved(int checkX, int checkY) {
		inProximity = checkX > x - inProximityPadding && checkX < x + width + inProximityPadding
	 && checkY > y - height / 2 - inProximityPadding && checkY < y + height / 2 + inProximityPadding;

		draggedHandle= isOverHandle(checkX,checkY);
		onAdded(checkX,checkY, MOUSE_ID);	
	}
	protected boolean isOverHandle(int checkX, int checkY) {

		float handleY = y-height/2;
		return checkX > handleX && checkX < handleX +handleWidth && checkY > handleY && checkY < handleY + handleHeight;		

	}

	public void setFrameInterval(int newint){

		framesPerInterval = newint;
	} 

	public void resetSlider(){
		running = false;
	    currentDateTime = startDateTime;
	    currentTick = 1;
	}

}
