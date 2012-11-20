package de.fhpotsdam;

import org.joda.time.DateTime;

import de.fhpotsdam.rangeslider.TimeRangeSlider;
import de.fhpotsdam.rangeslider.TimeRangeSliderListener;
import de.fhpotsdam.rangeslider.WhiteMultitouchTimeRangeSlider;

import processing.core.PApplet;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioPoint;
import TUIO.TuioTime;

public class MultitouchTimeRangeSliderTestApp extends PApplet implements TimeRangeSliderListener, TuioListener {

	TimeRangeSlider timeRangeSlider;

	TuioClient tuioClient;

	public void setup() {
		size(800, 600);
		smooth();

		timeRangeSlider = new WhiteMultitouchTimeRangeSlider(this, 100, 200, 500, 160, new DateTime(2011, 04, 11, 10,
				0, 0), new DateTime(2011, 04, 11, 22, 0, 0), 60 * 60);
		timeRangeSlider.setInProximityPadding(0);
		timeRangeSlider.setAlwaysShowHandles(true);

		tuioClient = new TuioClient();
		tuioClient.connect();
		tuioClient.addTuioListener(this);
		registerDispose(this);
	}

	public void draw() {
		background(30);

		timeRangeSlider.draw();

		fill(0, 100);
		for (TuioCursor tcur : tuioClient.getTuioCursors()) {
			ellipse(tcur.getScreenX(width), tcur.getScreenY(height), 20, 20);
		}
	}

	public void keyPressed() {
		// Does not forward all keyboard input. Only supports play/pause via SPACE key.
		if (key == ' ') {
			timeRangeSlider.playOrPause();
		}
	}

	@Override
	public void timeUpdated(DateTime startDateTime, DateTime endDateTime) {
		println("timeUpdated to " + startDateTime.toString("HH:mm") + " - " + endDateTime.toString("HH:mm"));
	}

	@Override
	public void addTuioCursor(TuioCursor tcur) {
		timeRangeSlider.onAdded(tcur.getScreenX(width), tcur.getScreenY(height), "tuioCursor-" + tcur.getCursorID());
	}

	@Override
	public void updateTuioCursor(TuioCursor tcur) {
		TuioPoint oldTuioPoint = tcur.getPath().get(tcur.getPath().size() - 2);
		timeRangeSlider.onDragged(tcur.getScreenX(width), tcur.getScreenY(height), oldTuioPoint.getScreenX(width),
				oldTuioPoint.getScreenY(height), "tuioCursor-" + tcur.getCursorID());
	}

	public void dispose() {
		tuioClient.disconnect();
	}

	@Override
	public void removeTuioCursor(TuioCursor tcur) {
		timeRangeSlider.onReleased(tcur.getScreenX(width), tcur.getScreenY(height), "tuioCursor-" + tcur.getCursorID());
	}

	@Override
	public void addTuioObject(TuioObject arg0) {
	}

	@Override
	public void refresh(TuioTime arg0) {
	}

	@Override
	public void removeTuioObject(TuioObject arg0) {
	}

	@Override
	public void updateTuioObject(TuioObject arg0) {
	}

}
