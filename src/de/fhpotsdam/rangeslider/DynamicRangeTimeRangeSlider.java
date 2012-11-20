package de.fhpotsdam.rangeslider;

import org.joda.time.DateTime;

import processing.core.PApplet;

public class DynamicRangeTimeRangeSlider extends TimeRangeSlider {

	public DynamicRangeTimeRangeSlider(PApplet p, float x, float y, float width, float height, DateTime startDateTime,
			DateTime endDateTime, int aggregationIntervalSeconds) {
		super(p, x, y, width, height, startDateTime, endDateTime, aggregationIntervalSeconds);
	}

	public void increaseRange() {
		aggregationIntervalSeconds += tickIntervalSeconds;
		updateAnimationStep();
	}

	public void decreaseRange() {
		aggregationIntervalSeconds -= tickIntervalSeconds;
		updateAnimationStep();
	}
}
