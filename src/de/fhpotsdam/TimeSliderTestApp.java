package de.fhpotsdam;

import org.joda.time.DateTime;

import processing.core.PApplet;
import de.fhpotsdam.rangeslider.TimeSlider;

public class TimeSliderTestApp extends PApplet {

	TimeSlider timeSlider;

	public void setup() {
		size(800, 600);
		smooth();

		timeSlider = new TimeSlider(this, 100, 200, 300, 16, new DateTime(2011, 04, 11, 10, 0, 0), new DateTime(2011,
				04, 11, 22, 0, 0), 60 * 20);
		timeSlider.setAlwaysShowHandles(true);
		timeSlider.setCurrentRange(new DateTime(2011, 04, 11, 10, 0, 0), new DateTime(2011, 04, 11, 22, 0, 0));
	}

	public static void main(String[] args) {
		String[] params = new String[] { "de.fhpotsdam.TimeSliderTestApp" };
		PApplet.main(params);
	}

	public void draw() {
		background(140);

		timeSlider.draw();
		
		fill(255, 200);
		rect(0, 0, 400, 120);
		fill(0);
		text("currentStart: " + timeSlider.getCurrentStartDateTime().toString("HH:mm:ss"), 10, 30);
		text("currentEnd: " + timeSlider.getCurrentEndDateTime().toString("HH:mm:ss"), 10, 50);
		text("endDateTime: " + timeSlider.getEndDateTime().toString("HH:mm:ss"), 10, 70);
		text("aggregationIntervalSeconds: " + timeSlider.getAggregationIntervalSeconds(), 10, 90);
//		text("currStartX: " + timeSlider.getCurrentStartX(), 10, 110);
//		text("currEndX: " + timeSlider.getCurrentEndX(), 10, 130);
		text("startHandleX: " + timeSlider.getStartHandleX(), 10, 110);
		text("endHandleX: " + timeSlider.getEndHandleX(), 10, 130);
		
		
	}

	public void keyPressed() {
		timeSlider.onKeyPressed(key, keyCode);

		if (key == 'a') {
			// sets new start, keeps range
			timeSlider.setCurrentDateTime(new DateTime(2011, 04, 11, 11, 30, 0));
		}
	}

	// Gets called each time the time ranger slider has changed, both by user interaction as well as by animation
	public void timeUpdated(DateTime startDateTime, DateTime endDateTime) {
		println("timeUpdated to " + startDateTime.toString("hh:mm") + " - " + endDateTime.toString("hh:mm"));
	}

	public void mouseMoved() {
		timeSlider.onMoved(mouseX, mouseY);
	}

	public void mouseDragged() {
		timeSlider.onDragged(mouseX, mouseY, pmouseX, pmouseY);
	}

}
