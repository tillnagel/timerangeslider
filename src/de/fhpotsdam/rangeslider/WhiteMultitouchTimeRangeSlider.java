package de.fhpotsdam.rangeslider;

import org.joda.time.DateTime;

import processing.core.PApplet;

public class WhiteMultitouchTimeRangeSlider extends TimeRangeSlider {

	float radius = 25;

	public WhiteMultitouchTimeRangeSlider(PApplet p, float x, float y, float width, float height,
			DateTime startDateTime, DateTime endDateTime, int aggregationIntervalSeconds) {
		super(p, x, y, width, height, startDateTime, endDateTime, aggregationIntervalSeconds);

		centeredHandle = false;
	}

	@Override
	protected void drawTimeLine() {
		p.stroke(255);
		p.noFill();
		p.strokeWeight(1);
		p.line(x, y, x + width, y);
	}

	@Override
	protected void drawSelectedTimeRange(boolean highlight) {
		p.noStroke();
		p.noFill();
		if (highlight) {
			p.fill(240, 140);
		} else {
			p.fill(220, 60);
		}
		p.rect(currentStartX, y - height, currentEndX - currentStartX, height + 3);
	}

	@Override
	protected void drawHandle(float x, boolean highlight, boolean start) {
		int xo = (start) ? 0 : -10;

		if (highlight) {
			p.fill(240, 140);
			p.noStroke();
			p.rect(x + xo, y - height, 10, height + 3);
		}

		p.stroke(0);
		p.strokeWeight(1);
		p.line(x + xo + 3, y - height / 2 - 5, x + xo + 3, y - height / 2 + 5);
		p.line(x + xo + 5, y - height / 2 - 7, x + xo + 5, y - height / 2 + 7);
		p.line(x + xo + 7, y - height / 2 - 5, x + xo + 7, y - height / 2 + 5);
	}

	@Override
	protected void drawStartAndEndTics() {
		p.stroke(255, 5);
		float yTop = y - height;
		float yBottom = y + 4;
		p.line(x, yTop, x, yBottom);
		p.line(x + width, yTop, x + width, yBottom);
	}

	@Override
	protected void drawTic(float tx) {
		float tyTop = y - 4;
		float tyBottom = y + 4;
		p.stroke(255, 150);
		p.line(tx, tyTop, tx, tyBottom);
	}

	@Override
	protected boolean isOverTimeRange(int checkX, int checkY) {
		float yTop = y - height;
		float yBottom = y;
		return checkX > currentStartX && checkX < currentEndX && checkY > yTop && checkY < yBottom;
	}

	@Override
	protected boolean isOverStartHandle(int checkX, int checkY) {
		float handleY = y - height / 2;
		return PApplet.dist(checkX, checkY, startHandleX, handleY) < radius;
	}

	@Override
	protected boolean isOverEndHandle(int checkX, int checkY) {
		float handleY = y - height / 2;
		return PApplet.dist(checkX, checkY, endHandleX, handleY) < radius;
	}

	@Override
	protected void drawLabel(String timeRangeLabels, int labelX, int labelY) {
		p.fill(230);
		p.text(timeRangeLabels, labelX, labelY);
	}
}
