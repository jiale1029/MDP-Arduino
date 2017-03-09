/*
 * Sensor reading class convert the data from sensors to distance in the unit of block length
 * and store the data
 * Sensor reading class also implements the logic for sensor input in simulation
 */
public class SensorReading {
	/*
	 * Data from sensor readings in unit of block length A value of -1 means
	 * that no blocks within detectable range
	 */
	private int[] front; // can take value -1, 1 or 2
	private int[] left; // can take value -1, 1 or 2
	private int right; // long range sensor, can take value -1, 1, 2, 3, 4, 5

	/*
	 * Data for position adjustment of the sensor (distance between sensors and
	 * edge in each direction)
	 */
	private int frontAdjustment = 5;
	private int leftAdjustment = 5;
	private int rightAdjustment = 13;

	/*
	 * Data for position adjustment of robot (distance between the edge of the
	 * robot and the nearest block)
	 */
	private int positionAdjustmentFront = 5;
	private int positionAdjustmentLeft = 5;
	private int positionAdjustmentRight = 5;
	/*
	 * Include arena and navigator to provide information
	 */
	private Navigator navigator;
	private Arena arena;

	public SensorReading(Navigator navigator, Arena arena) {
		this.navigator = navigator;
		this.arena = arena;
		this.front = new int[3];
		this.left = new int[2];
	}

	/*
	 * Functions for getting sensor readings in each directions
	 */
	public int[] getFrontReadings() {
		return this.front;
	}

	public int[] getLeftReadings() {
		return this.left;
	}

	public int getRightReadings() {
		return this.right;
	}

	/*
	 * Function that update the sensor readings, currently based on simulation
	 */
	public void getSensorReadings() {
		String readings = GlobalVariables.sensorInput;
		String[] readingStrings = readings.split(",");
		double[] readingFront = new double[3];
		double[] readingLeft = new double[2];
		double readingRight;
		for (int index = 0; index < 3; index++) {
			readingFront[index] = Double.parseDouble(readingStrings[index]);
		}
		for (int index = 3; index < 5; index++) {
			readingLeft[index - 3] = Double.parseDouble(readingStrings[index]);
		}
		readingRight = Double.parseDouble(readingStrings[5]);
		this.sensorDetectFront(readingFront);
		this.sensorDetectLeft(readingLeft);
		this.sensorDetectRight(readingRight);
	}

	/*
	 * Functions that get sensor reading in each direction, currently based on
	 * simulation
	 */
	public void detectFront() {
		int cur_height = this.navigator.getHeight();
		int cur_width = this.navigator.getWidth();
		for (int index = 0; index < 3; index++) {
			switch (navigator.getCurDirection()) {
			case Direction.NORTH:
				if (!arena.withinRange(cur_height + 2, cur_width - 1 + index)
						|| (this.arena.sensorGetState(cur_height + 2, cur_width - 1 + index) == BlockState.BLOCKED))
					front[index] = 1;
				else {
					if (!arena.withinRange(cur_height + 3, cur_width - 1 + index)
							|| (this.arena.sensorGetState(cur_height + 3, cur_width - 1 + index) == BlockState.BLOCKED))
						front[index] = 2;
					else {
						if (!arena.withinRange(cur_height + 4, cur_width - 1 + index) || (this.arena
								.sensorGetState(cur_height + 4, cur_width - 1 + index) == BlockState.BLOCKED))
							front[index] = 3;
						else
							front[index] = -1;
					}
				}
				break;
			case Direction.SOUTH:
				if (!arena.withinRange(cur_height - 2, cur_width + 1 - index)
						|| (this.arena.sensorGetState(cur_height - 2, cur_width + 1 - index) == BlockState.BLOCKED))
					front[index] = 1;
				else {
					if (!arena.withinRange(cur_height - 3, cur_width + 1 - index)
							|| (this.arena.sensorGetState(cur_height - 3, cur_width + 1 - index) == BlockState.BLOCKED))
						front[index] = 2;
					else {
						if (!arena.withinRange(cur_height - 4, cur_width + 1 - index) || (this.arena
								.sensorGetState(cur_height - 4, cur_width + 1 - index) == BlockState.BLOCKED))
							front[index] = 3;
						else
							front[index] = -1;
					}

				}
				break;
			case Direction.WEST:
				if (!arena.withinRange(cur_height - 1 + index, cur_width - 2)
						|| (this.arena.sensorGetState(cur_height - 1 + index, cur_width - 2) == BlockState.BLOCKED))
					front[index] = 1;
				else {
					if (!arena.withinRange(cur_height - 1 + index, cur_width - 3)
							|| (this.arena.sensorGetState(cur_height - 1 + index, cur_width - 3) == BlockState.BLOCKED))
						front[index] = 2;
					else {
						if (!arena.withinRange(cur_height - 1 + index, cur_width - 4) || (this.arena
								.sensorGetState(cur_height - 1 + index, cur_width - 4) == BlockState.BLOCKED))
							front[index] = 3;
						else
							front[index] = -1;
					}
				}
				break;
			case Direction.EAST:
				if (!arena.withinRange(cur_height + 1 - index, cur_width + 2)
						|| (this.arena.sensorGetState(cur_height + 1 - index, cur_width + 2) == BlockState.BLOCKED))
					front[index] = 1;
				else {
					if (!arena.withinRange(cur_height + 1 - index, cur_width + 3)
							|| (this.arena.sensorGetState(cur_height + 1 - index, cur_width + 3) == BlockState.BLOCKED))
						front[index] = 2;
					else {
						if (!arena.withinRange(cur_height + 1 - index, cur_width + 4) || (this.arena
								.sensorGetState(cur_height + 1 - index, cur_width + 4) == BlockState.BLOCKED))
							front[index] = 3;
						else
							front[index] = -1;
					}
				}
				break;
			}
		}
	}

	public void sensorDetectFront(double distances[]) {
		for (int index = 0; index < 3; index++) {
			double blockNum;
			if (index != 1)
				blockNum = (distances[index] - this.frontAdjustment - this.positionAdjustmentFront) / 10.0 + 1;
			else
				blockNum = (distances[index] - 2 - this.positionAdjustmentFront) / 10.0 + 1;
			int blockInt = (int) (blockNum + 0.5);
			if (blockInt > 2 || blockInt < 1) {
				this.front[index] = -1;
			} else {
				this.front[index] = blockInt;
			}
		}
	}

	public void detectLeft() {
		int cur_height = this.navigator.getHeight();
		int cur_width = this.navigator.getWidth();
		for (int index = 0; index < 2; index++) {
			switch (navigator.getCurDirection()) {
			case Direction.NORTH:
				if (!arena.withinRange(cur_height + 1 - index, cur_width - 2)
						|| (this.arena.sensorGetState(cur_height + 1 - index, cur_width - 2) == BlockState.BLOCKED))
					left[index] = 1;
				else {
					if (!arena.withinRange(cur_height + 1 - index, cur_width - 3)
							|| (this.arena.sensorGetState(cur_height + 1 - index, cur_width - 3) == BlockState.BLOCKED))
						left[index] = 2;
					else
						left[index] = -1;
				}
				break;
			case Direction.SOUTH:
				if (!arena.withinRange(cur_height - 1 + index, cur_width + 2)
						|| (this.arena.sensorGetState(cur_height - 1 + index, cur_width + 2) == BlockState.BLOCKED))
					left[index] = 1;
				else {
					if (!arena.withinRange(cur_height - 1 + index, cur_width + 3)
							|| (this.arena.sensorGetState(cur_height - 1 + index, cur_width + 3) == BlockState.BLOCKED))
						left[index] = 2;
					else
						left[index] = -1;
				}
				break;
			case Direction.WEST:
				if (!arena.withinRange(cur_height - 2, cur_width - 1 + index)
						|| (this.arena.sensorGetState(cur_height - 2, cur_width - 1 + index) == BlockState.BLOCKED))
					left[index] = 1;
				else {
					if (!arena.withinRange(cur_height - 3, cur_width - 1 + index)
							|| (this.arena.sensorGetState(cur_height - 3, cur_width - 1 + index) == BlockState.BLOCKED))
						left[index] = 2;
					else
						left[index] = -1;
				}
				break;
			case Direction.EAST:
				if (!arena.withinRange(cur_height + 2, cur_width + 1 - index)
						|| (this.arena.sensorGetState(cur_height + 2, cur_width + 1 - index) == BlockState.BLOCKED))
					left[index] = 1;
				else {
					if (!arena.withinRange(cur_height + 3, cur_width + 1 - index)
							|| (this.arena.sensorGetState(cur_height + 3, cur_width + 1 - index) == BlockState.BLOCKED))
						left[index] = 2;
					else
						left[index] = -1;
				}
				break;
			}
		}
	}

	public void sensorDetectLeft(double distances[]) {
		for (int index = 0; index < 2; index++) {

			double blockNum;
			if (index == 0)
				blockNum = (distances[index] - this.leftAdjustment - this.positionAdjustmentLeft) / 10.0 + 1;
			else
				blockNum = (distances[index] - 2 - this.positionAdjustmentLeft) / 10.0 + 1;
			int blockInt = (int) (blockNum + 0.5);
			if (blockInt > 2 || blockInt < 1) {
				this.left[index] = -1;
			} else {
				this.left[index] = blockInt;
			}
		}
	}

	public void detectRight() {
		int cur_height = navigator.getHeight();
		int cur_width = navigator.getWidth();

		int direction = navigator.getCurDirection();
		boolean block_detected;
		switch (direction) {
		case Direction.NORTH:
			block_detected = false;
			for (int distance = 1; distance < 5; distance++) {
				if (!arena.withinRange(cur_height, cur_width + 1 + distance)
						|| (this.arena.sensorGetState(cur_height, cur_width + 1 + distance) == BlockState.BLOCKED)) {
					block_detected = true;
					this.right = distance;
					break;
				}
			}
			if (!block_detected)
				this.right = -1;
			break;
		case Direction.SOUTH:
			block_detected = false;
			for (int distance = 1; distance < 5; distance++) {
				if (!arena.withinRange(cur_height, cur_width - 1 - distance)
						|| (this.arena.sensorGetState(cur_height, cur_width - 1 - distance) == BlockState.BLOCKED)) {
					block_detected = true;
					this.right = distance;
					break;
				}
			}
			if (!block_detected)
				this.right = -1;
			break;
		case Direction.WEST:
			block_detected = false;
			for (int distance = 1; distance < 5; distance++) {
				if (!arena.withinRange(cur_height + 1 + distance, cur_width)
						|| (this.arena.sensorGetState(cur_height + 1 + distance, cur_width) == BlockState.BLOCKED)) {
					block_detected = true;
					this.right = distance;
					break;
				}
			}
			if (!block_detected)
				this.right = -1;
			break;
		case Direction.EAST:
			block_detected = false;
			for (int distance = 1; distance < 5; distance++) {
				if (!arena.withinRange(cur_height - 1 - distance, cur_width)
						|| (this.arena.sensorGetState(cur_height - 1 - distance, cur_width) == BlockState.BLOCKED)) {
					block_detected = true;
					this.right = distance;
					break;
				}
			}
			if (!block_detected)
				this.right = -1;
			break;
		}
	}

	public void sensorDetectRight(double distance) {
		double blockNum = (distance - this.rightAdjustment - this.positionAdjustmentRight) / 10.0 + 1; 
		int blockInt = (int) (blockNum + .2 ); //removed + .5
		if (blockInt > 4 || blockInt < 1) {
			this.right = -1;
		} else {
			this.right = blockInt;
		}
	}

}
