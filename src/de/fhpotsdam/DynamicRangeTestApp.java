package de.fhpotsdam;

import org.joda.time.DateTime;

import processing.core.PApplet;
import de.fhpotsdam.rangeslider.DynamicRangeTimeRangeSlider;

/**
 * 
 * Copyright (c) 2012 Till Nagel, tillnagel.com
 */
public class DynamicRangeTestApp extends PApplet {

	DynamicRangeTimeRangeSlider timeRangeSlider;

	public void setup() {
		size(800, 600);
		smooth();

		timeRangeSlider = new DynamicRangeTimeRangeSlider(this, 100, 200, 300, 16,
				new DateTime(2011, 04, 11, 10, 0, 0), new DateTime(2011, 04, 11, 22, 0, 0), 60 * 10);
		timeRangeSlider.setTickIntervalSeconds(60 * 10);
	}

	public void draw() {
		background(240);

		timeRangeSlider.draw();
	}

	public void keyPressed() {
		if (key == 'i') {
			timeRangeSlider.increaseRange();
		}
		
		timeRangeSlider.onKeyPressed(key, keyCode);
	}

	// Gets called each time the time ranger slider has changed, both by user interaction as well as by animation
	public void timeUpdated(DateTime startDateTime, DateTime endDateTime) {
		println("timeUpdated to " + startDateTime.toString("hh:mm") + " - " + endDateTime.toString("hh:mm"));
	}

	public void mouseMoved() {
		timeRangeSlider.onMoved(mouseX, mouseY);
	}

	public void mouseDragged() {
		timeRangeSlider.onDragged(mouseX, mouseY, pmouseX, pmouseY);
	}

}
