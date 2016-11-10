package src.algorithm;

public class USLocalization {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };

	//rotation speed
	public static int ROTATION_SPEED = 30;

	//for constructor
	private Odometer odo;
	private Navigator navigation;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;

	//constructor
	public USLocalization(Odometer odo, SampleProvider usSensor, float[] usData, LocalizationType locType,
			EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	public void doLocalization() {
		//array for the position of the robot
		double [] pos = new double [3];
		pos[0] = odo.getX();
		pos[1] = odo.getY();

		//array for setPosition method (it will only update the angle)
		boolean[] update = {false, false, true};

		//margin for the distance of wall
		final int MARGIN = 2;

		double clockwiseAngle;							//first angle
		double counterClockwiseAngle;					//second angle

		//set speed to ROTATION_SPEED
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		//--------------FALLING_EDGE-------------------
		if (locType == LocalizationType.FALLING_EDGE) {

			// rotate the robot clockwise until it sees no wall
			while (getFilteredData() < 50){
				leftMotor.forward();
				rightMotor.backward();
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() >= 50 - MARGIN ){
				leftMotor.forward();
				rightMotor.backward();
			}

			//the robot sees the wall, stop
			if(getFilteredData() < 50){
				/*
				 * Stop motor.
				 * We need to set speed to 0 before stopping the motor
				 * because stop() doesn't stop both motors at the same time
				 * and leads to error (testing hardware issue).
				 */
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);

				leftMotor.forward();
				rightMotor.forward();

				leftMotor.stop();
				rightMotor.stop();
			}

			//save the first angle
			clockwiseAngle = odo.getAng();

			//set speed to ROTATION_SPEED
			leftMotor.setSpeed(ROTATION_SPEED);
			rightMotor.setSpeed(ROTATION_SPEED);

			// switch direction (counterclockwise) and wait until it sees no wall
			while(getFilteredData() < 50){
				leftMotor.backward();
				rightMotor.forward();
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() >= 50 - MARGIN){
				leftMotor.backward();
				rightMotor.forward();
			}

			//the robot sees the wall, stop motors
			if(getFilteredData() < 50){
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);

				leftMotor.forward();
				rightMotor.forward();

				leftMotor.stop();
				rightMotor.stop();
			}

			//save the second angle
			counterClockwiseAngle = odo.getAng();

			//set speed to ROTATION_SPEED
			leftMotor.setSpeed(ROTATION_SPEED);
			rightMotor.setSpeed(ROTATION_SPEED);

			//determine the new angle (using the formula from the tutorial)
			if(clockwiseAngle > counterClockwiseAngle)
			{
				pos[2] = (odo.getAng() + (225- (clockwiseAngle + counterClockwiseAngle)/2));

			}
			else{
				pos[2] = (odo.getAng() + (45- (clockwiseAngle + counterClockwiseAngle)/2));
			}

			//update the angle
			odo.setPosition(pos, update);

			//turn to 0 degrees
			navigation.turnTo(0, true);






			//travel somewhere aroun 45d untill a line is passed
			//

			// update the odometer position (example to follow:)
			//odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});


		//RISING_EDGE
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */

			//rotate the robot clockwise until it sees a wall
			while (getFilteredData() >= 50){
				leftMotor.forward();
				rightMotor.backward();
			}

			//keep rotating until until it sees no wall
			while(getFilteredData() < 50 + MARGIN){
				leftMotor.forward();
				rightMotor.backward();
			}

			//stop motor
			leftMotor.setSpeed(0);
			rightMotor.setSpeed(0);
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.stop();
			rightMotor.stop();

			//save the first angle
			clockwiseAngle = odo.getAng();

			//set speed to ROTATION_SPEED
			leftMotor.setSpeed(ROTATION_SPEED);
			rightMotor.setSpeed(ROTATION_SPEED);

			//rotate counterclockwise until it sees a wall
			while(getFilteredData() >= 50){

				leftMotor.backward();
				rightMotor.forward();

			}

			//keep rotating until it sees no wall
			while(getFilteredData() < 50 + MARGIN){

				leftMotor.backward();
				rightMotor.forward();

			}

			//stop motor
			leftMotor.setSpeed(0);
			rightMotor.setSpeed(0);
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.stop();
			rightMotor.stop();

			//set speed to rotation speed
			leftMotor.setSpeed(ROTATION_SPEED);
			rightMotor.setSpeed(ROTATION_SPEED);

			//save the second angle
			counterClockwiseAngle = odo.getAng();

			//determine the new angle (using the formula from the tutorial)
			if(clockwiseAngle > counterClockwiseAngle)
			{
				pos[2] = (odo.getAng() + (45- (clockwiseAngle + counterClockwiseAngle)/2));

			}
			else{
				pos[2] = (odo.getAng() + (225- (clockwiseAngle + counterClockwiseAngle)/2));
			}

			//update the angle
			odo.setPosition(pos, update);


		}
	}

	//fetch sample, returns the distance detected by the US sensor
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;

		return distance;
	}


}