import org.joda.time.DateTime;

import processing.core.PApplet;
import processing.core.PFont;

public class StyledWeekdayTimeRangeSlider extends StyledTimeRangeSlider {

	public StyledWeekdayTimeRangeSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime,
			DateTime endDateTime, int aggregationIntervalSeconds) {
		super(p, x, y, width, height, startDateTime, endDateTime, aggregationIntervalSeconds);
		showTicks = true;

		timelineLeftImage = p.loadImage("timerange/timelineBig-left.png");
		timelineRightImage = p.loadImage("timerange/timelineBig-right.png");
		timelineTileImage = p.loadImage("timerange/timelineBig-tile.png");
	}

	protected void drawStartEndTimeLabels() {
		PFont font = FontManager.getInstance().getMiniLabelFont();
		//p.fill(24, 27, 28);
		p.fill(34, 39, 40);
		p.textFont(font);

		int xOffset = 0;
		float xStep = (width + 2) / 6;
		for (DateTime dateTime = startDateTime.plus(0); dateTime.isBefore(endDateTime); dateTime = dateTime
				.plusSeconds(tickIntervalSeconds)) {
			// Only draw labels for non-selected ticks. 
			if (!dateTime.isEqual(currentStartDateTime) && !dateTime.isEqual(currentEndDateTime)) {
				String timeLabel = dateTime.toString("EE");
				int startLabelX = (int) (x - p.textWidth(timeLabel) / 2 + xOffset);
				int labelY = (int) (y + font.getSize() / 2 + 21);
				p.text(timeLabel, startLabelX, labelY);
			}
			xOffset += xStep;
		}
	}

	@Override
	protected void drawStartAndEndTics() {
		// No start and end tics
	}

	@Override
	protected void drawTimeRangeLabels() {
		String startLabel = currentStartDateTime.toString("EE");
		String endLabel = currentEndDateTime.toString("EE");

		labelPadding = 32;
		PFont font = FontManager.getInstance().getMiniLabelFont();
		p.textFont(font);
		p.fill(230, 200);

		int startLabelX = (int) (currentStartX - p.textWidth(startLabel) / 2);
		int labelY = (int) (y + font.getSize() + labelPadding / 2);
		p.text(startLabel, startLabelX, labelY);

		int endLabelX = (int) (currentEndX - p.textWidth(endLabel) / 2);
		p.text(endLabel, endLabelX, labelY);
	}

	@Override
	protected void drawTic(float ticX) {
		int tx = (int) ticX;
		int tyTop = (int) y + 6;
		int tyBottom = (int) y + 12;
		p.stroke(37, 40, 43);
		p.line(tx, tyTop, tx, tyBottom);
		p.stroke(50, 54, 58);
		p.line(tx + 1, tyTop, tx + 1, tyBottom);
	}

}
