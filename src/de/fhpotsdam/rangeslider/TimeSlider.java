package de.fhpotsdam.rangeslider;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import processing.core.PApplet;
import processing.core.PFont;
import de.fhpotsdam.utils.FontManager;

/**
 * A simple TimeSlider.
 * 
 */
public class TimeSlider extends TimeRangeSlider {

	public boolean showCurrentTimeLabel = true;
	boolean isSingleSlider = true;

	public TimeSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime,
			DateTime endDateTime, int tickIntervalSeconds) {
		super(p, x, y, width, height, startDateTime, endDateTime, 1);
		this.tickIntervalSeconds = tickIntervalSeconds;
		this.animationIntervalSeconds = tickIntervalSeconds;

		this.currentEndDateTime = endDateTime;
		FontManager.getInstance(p);
	}

	/**
	 * Draws this TimeRangeSlider.
	 */
	public void draw() {
		update();

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
		currentStartX = x + widthPerSecond * currentStartSeconds;
		currentEndX = currentStartX;

		// Show handles to change time range
		startHandleX = currentStartX;
		if (centeredHandle) {
			startHandleX -= handleWidth / 2;
		}

		if (inProximity || alwaysShowHandles) {
			drawHandle(startHandleX, draggedStartHandle, true);
		}

		// Show labels for current time
		if (showCurrentTimeLabel) {
			drawCurrentTimeLabel();
		}
		// Show labels for complete time
		if (showStartEndTimeLabels) {
			drawStartEndTimeLabels();
		}
	}

	public void nextAnimationStep() {
		currentStartDateTime = currentStartDateTime.plusSeconds(animationIntervalSeconds);
		if (currentStartDateTime.isAfter(endDateTime)) {
			currentStartDateTime = startDateTime;
		}
		updateAnimationStep();
	}

	public void nextInterval() {
		currentStartDateTime = currentStartDateTime.plusSeconds(aggregationIntervalSeconds);
		// if (currentStartDateTime.isAfter(endDateTime.minusSeconds(tickIntervalSeconds))) {
		if (currentStartDateTime.isAfter(endDateTime)) {
			currentStartDateTime = startDateTime;
		}
		updateAnimationStep();
	}

	public void previousAnimationStep() {
		currentStartDateTime = currentStartDateTime.minusSeconds(animationIntervalSeconds);
		if (currentStartDateTime.isBefore(startDateTime)) {
			currentStartDateTime = endDateTime.minusSeconds(0);
		}
		updateAnimationStep();
	}

	/**
	 * Goes to previous interval step, i.e. slides the time by -aggregationIntervalSeconds.
	 */
	public void previousInterval() {
		currentStartDateTime = currentStartDateTime.minusSeconds(aggregationIntervalSeconds);
		if (currentStartDateTime.isBefore(startDateTime)) {
			currentStartDateTime = endDateTime.minusSeconds(0);
		}
		updateAnimationStep();
	}

	protected void updateAnimationStep() {
		// currentEndDateTime = currentStartDateTime.plusSeconds(aggregationIntervalSeconds);
		fireAnimationStepListeners();
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getAggregationIntervalSeconds() {
		return aggregationIntervalSeconds;
	}

	public float getCurrentStartX() {
		return currentStartX;
	}

	public float getCurrentEndX() {
		return currentEndX;
	}

	public float getStartHandleX() {
		return startHandleX;
	}

	public float getEndHandleX() {
		return endHandleX;
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

	protected void drawCurrentTimeLabel() {
		String currentTimeLabel = getCurrentTimeLabel();
		PFont font = FontManager.getInstance().getLabelFont();
		p.textFont(font);
		int labelX = (int) (currentStartX + (currentEndX - currentStartX) / 2 - p.textWidth(currentTimeLabel) / 2);
		int labelY = (int) (y + font.getSize() + labelPadding / 2);
		drawLabel(currentTimeLabel, labelX, labelY);
	}

	protected String getCurrentTimeLabel() {
		return currentStartDateTime.toString(timeLabelFormat);
	}

	// --------------------------------------------------------------

	// protected void updateAnimationStep() {
	// currentEndDateTime = endDateTime.plus(0);
	// fireAnimationStepListeners();
	// }

	public void setCurrentDateTime(DateTime newCurrentDateTime) {
		setCurrentStartDateTime(newCurrentDateTime);
	}

	protected boolean isOverEndHandle(int checkX, int checkY) {
		return false;
	}

	protected boolean isOverTimeRange(int checkX, int checkY) {
		return false;
	}

}
