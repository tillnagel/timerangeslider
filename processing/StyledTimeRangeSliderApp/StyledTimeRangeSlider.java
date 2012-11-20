import org.joda.time.DateTime;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class StyledTimeRangeSlider extends TimeRangeSlider {

	PImage timelineLeftImage;
	PImage timelineRightImage;
	PImage timelineTileImage;

	PImage timerangeTileImage;
	PImage timerangeTileHiImage;

	PImage timerangeHandleImage;
	PImage timerangeHandleHiImage;

	public StyledTimeRangeSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime,
			DateTime endDateTime, int aggregationIntervalSeconds) {
		super(p, x, y, width, height, startDateTime, endDateTime, aggregationIntervalSeconds);
		alwaysShowHandles = true;
		showTicks = false;
		showTimeRangeLabels = true;
		handleWidth = 20;

		timelineLeftImage = p.loadImage("timerange/timeline-left.png");
		timelineRightImage = p.loadImage("timerange/timeline-right.png");
		timelineTileImage = p.loadImage("timerange/timeline-tile.png");
		timerangeTileImage = p.loadImage("timerange/timerange-tile-blue.png");
		timerangeTileHiImage = p.loadImage("timerange/timerange-tile-orange.png");
		timerangeHandleImage = p.loadImage("timerange/timerange-handle-blue.png");
		timerangeHandleHiImage = p.loadImage("timerange/timerange-handle-orange.png");
	}

	protected void drawStartEndTimeLabels() {
		PFont font = FontManager.getInstance().getMiniLabelFont();
		p.fill(230, 200);
		p.textFont(font);

		labelPadding = 12;
		String startTimeLabel = startDateTime.toString("HH:mm");
		int startLabelX = (int) (x - p.textWidth(startTimeLabel) - labelPadding);
		int labelY = (int) (y + font.getSize() / 2 - 2);
		p.text(startTimeLabel, startLabelX, labelY);

		String endTimeLabel = endDateTime.toString("HH:mm");
		int endLabelX = (int) (x + width + labelPadding);
		p.text(endTimeLabel, endLabelX, labelY);
	}

	@Override
	protected void drawTimeLine() {
		int leftWidth = timelineLeftImage.width;
		int rightWidth = timelineRightImage.width;
		int halfHeight = timelineLeftImage.height / 2;

		p.image(timelineTileImage, x, y - halfHeight, width, timelineTileImage.height);
		p.image(timelineLeftImage, x - leftWidth, y - halfHeight);
		p.image(timelineRightImage, x + width, y - halfHeight);
	}

	protected void drawTimeLineRoundedCornerAsEnds() {
		int leftWidth = timelineLeftImage.width;
		int rightWidth = timelineRightImage.width;
		int sideWidths = leftWidth + rightWidth;
		int halfHeight = timelineLeftImage.height / 2;

		p.image(timelineTileImage, x + leftWidth, y - halfHeight, width - sideWidths, timelineTileImage.height);
		p.image(timelineLeftImage, x, y - halfHeight);
		p.image(timelineRightImage, x + width - rightWidth, y - halfHeight);
	}

	@Override
	protected void drawSelectedTimeRange(boolean highlight) {
		PImage tileImage;
		if (highlight) {
			tileImage = timerangeTileHiImage;
		} else {
			tileImage = timerangeTileImage;
		}

		p.image(tileImage, currentStartX, y - tileImage.height / 2, currentEndX - currentStartX, tileImage.height);
	}

	@Override
	protected void drawHandle(float x, boolean highlight, boolean start) {
		PImage handleImage;
		if (highlight) {
			handleImage = timerangeHandleHiImage;
		} else {
			handleImage = timerangeHandleImage;
		}

		int offset = 0; // start ? 5 : -5;
		p.image(handleImage, x + offset, y - handleImage.height / 2);
	}

	@Override
	protected void drawStartAndEndTics() {
		// No start and end tics
	}

	@Override
	protected void drawTimeRangeLabels() {
		String timeRangeLabel = currentStartDateTime.toString("HH:mm") + " - " + currentEndDateTime.toString("HH:mm");

		labelPadding = 20;
		PFont font = FontManager.getInstance().getMiniLabelFont();
		p.textFont(font);
		int labelX = (int) (currentStartX + (currentEndX - currentStartX) / 2 - p.textWidth(timeRangeLabel) / 2);
		int labelY = (int) (y + font.getSize() + labelPadding / 2);
		p.fill(230, 200);
		p.text(timeRangeLabel, labelX, labelY);
	}

	//
	// @Override
	// protected boolean isOverTimeRange(int checkX, int checkY) {
	// float yTop = y - height;
	// float yBottom = y;
	// return checkX > currentStartX && checkX < currentEndX && checkY > yTop && checkY < yBottom;
	// }
	//
	// @Override
	// protected boolean isOverStartHandle(int checkX, int checkY) {
	// float handleY = y - height / 2;
	// return PApplet.dist(checkX, checkY, startHandleX, handleY) < radius;
	// }
	//
	// @Override
	// protected boolean isOverEndHandle(int checkX, int checkY) {
	// float handleY = y - height / 2;
	// return PApplet.dist(checkX, checkY, endHandleX, handleY) < radius;
	// }

}
