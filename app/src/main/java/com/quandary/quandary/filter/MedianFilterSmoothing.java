package com.quandary.quandary.filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;


public class MedianFilterSmoothing
{
	private static final String tag = MedianFilterSmoothing.class
			.getSimpleName();

	private float timeConstant = 1;
	private float startTime = 0;
	private float timestamp = 0;
	private float hz = 0;

	private int count = 0;
	// The size of the mean filters rolling window.
	private int filterWindow = 20;

	private boolean dataInit;

	private ArrayList<LinkedList<Number>> dataLists;

	/**
	 * Initialize a new MeanFilter object.
	 */
	public MedianFilterSmoothing()
	{
		dataLists = new ArrayList<LinkedList<Number>>();
		dataInit = false;
	}

	public void setTimeConstant(float timeConstant)
	{
		this.timeConstant = timeConstant;
	}

	public void reset()
	{
		startTime = 0;
		timestamp = 0;
		count = 0;
		hz = 0;
	}

	/**
	 * Filter the data.
	 *
	 * @return the filtered output data.
	 */
	public float[] addSamples(float[] data)
	{
		// Initialize the start time.
		if (startTime == 0)
		{
			startTime = System.nanoTime();
		}

		timestamp = System.nanoTime();

		// Find the sample period (between updates) and convert from
		// nanoseconds to seconds. Note that the sensor delivery rates can
		// individually vary by a relatively large time frame, so we use an
		// averaging technique with the number of sensor updates to
		// determine the delivery rate.
		hz = (count++ / ((timestamp - startTime) / 1000000000.0f));

		filterWindow = (int) (hz * timeConstant);

		for (int i = 0; i < data.length; i++)
		{
			// Initialize the data structures for the data set.
			if (!dataInit)
			{
				dataLists.add(new LinkedList<Number>());
			}

			dataLists.get(i).addLast(data[i]);

			if (dataLists.get(i).size() > filterWindow)
			{
				dataLists.get(i).removeFirst();
			}
		}

		dataInit = true;

		float[] medians = new float[dataLists.size()];

		for (int i = 0; i < dataLists.size(); i++)
		{
			medians[i] = (float) getMedian(dataLists.get(i));
		}

		return medians;
	}

	/**
	 * Get the mean of the data set.
	 * 
	 * @param data
	 *            the data set.
	 * @return the mean of the data set.
	 */
	private float getMedian(List<Number> data)
	{
		double[] values = new double[data.size()];

		for (int i = 0; i < values.length; i++)
		{
			values[i] = data.get(i).floatValue();
		}

		return (float) StatUtils.percentile(values, 50);
	}

}
