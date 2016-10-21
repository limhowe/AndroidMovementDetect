package com.quandary.quandary.filter;

public class LowPassFilterSmoothing
{
	// Constants for the low-pass filters
	private float timeConstant = 0.18f;
	private float alpha = 0.9f;
	private float dt = 0;

	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float startTime = 0;

	private int count = 0;

	// Gravity and linear accelerations components for the
	// Wikipedia low-pass filter
	private float[] output = new float[]
	{ 0, 0, 0 };

	// Raw accelerometer data
	private float[] input = new float[]
	{ 0, 0, 0 };

	/**
	 * Add a sample.
	 * 
	 * @param acceleration
	 *            The acceleration data.
	 * @return Returns the output of the filter.
	 */
	public float[] addSamples(float[] acceleration)
	{
		// Initialize the start time.
		if (startTime == 0)
		{
			startTime = System.nanoTime();
		}

		timestamp = System.nanoTime();

		// Get a local copy of the sensor values
		System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);

		// Find the sample period (between updates) and convert from
		// nanoseconds to seconds. Note that the sensor delivery rates can
		// individually vary by a relatively large time frame, so we use an
		// averaging technique with the number of sensor updates to
		// determine the delivery rate.
		dt = 1 / (count++ / ((timestamp - startTime) / 1000000000.0f));

		alpha = timeConstant / (timeConstant + dt);

		if (count > 5)
		{
			output[0] = alpha * output[0] + (1 - alpha) * input[0];
			output[1] = alpha * output[1] + (1 - alpha) * input[1];
			output[2] = alpha * output[2] + (1 - alpha) * input[2];
		}

		//return a copy of output so that it will not be modified outside
		float[] result = new float[3];
		System.arraycopy(output, 0, result, 0, output.length);

		return result;
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
		dt = 0;
		alpha = 0;
	}
}
