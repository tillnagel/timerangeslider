package de.fhpotsdam;

import org.joda.time.DateTime;

import de.fhpotsdam.rangeslider.TimeRangeSlider;

import processing.core.PApplet;

/**
 * Simple TimeRangerSlider example.
 * 
 * Press SPACE to toggle animation. Drag handles and time range bar to move around the time range. Press LEFT and RIGHT
 * arrow keys to step through the time range.
 * 
 * Copyright (c) 2012 Till Nagel, tillnagel.com
 */
public class TimeRangeSliderTestApp extends PApplet {

	TimeRangeSlider timeRangeSlider;

	public void setup() {
		size(800, 600);
		smooth();

		timeRangeSlider = new TimeRangeSlider(this, 100, 200, 300, 16, new DateTime(2011, 04, 11, 10, 0, 0),
				new DateTime(2011, 04, 11, 22, 0, 0), 60 * 10);
		timeRangeSlider.setTickIntervalSeconds(60 * 60);
	}
	
	public static void main(String[] args) {
		String[] params = new String[] { "de.fhpotsdam.TimeRangeSliderTestApp" };
		PApplet.main(params);
	}

	public void draw() {
		background(240);

		timeRangeSlider.draw();
	}

	public void keyPressed() {
		timeRangeSlider.onKeyPressed(key, keyCode);
		
		if (key == 'a') {
			// sets new start, keeps range
			timeRangeSlider.setCurrentStartDateTime(new DateTime(2011, 04, 11, 11, 30, 0));
		}
		if (key == 'b') {
			// set new end date, keeps range
			timeRangeSlider.setCurrentEndDateTime(new DateTime(2011, 04, 11, 20, 0, 0));
		}
		if (key == 'c') {
			// sets new start and end date, changes range
			timeRangeSlider.setCurrentRange(new DateTime(2011, 04, 11, 12, 00, 0), new DateTime(2011, 04, 11, 17, 0, 0));
		}
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
