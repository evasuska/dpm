package algorithm;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class ClawController {

	EV3LargeRegulatedMotor clawLift, clawClose;
	
	private final int CLAW_CLOSE_ANGLE = 145;
	
	public ClawController(EV3LargeRegulatedMotor clawLift, EV3LargeRegulatedMotor clawClose) {
		this.clawLift = clawLift;
		this.clawClose = clawClose;
	}
	
	/*
	 * Closes the claw 
	 */
	public void grab()
	{
		clawClose.rotate(-CLAW_CLOSE_ANGLE, false);
	}
	
	/*
	 * Opens the claw
	 */
	public void release()
	{
		clawClose.rotate(CLAW_CLOSE_ANGLE, false);
	}
	
	/*
	 * Lift the claw to the desired angle
	 */
	public void lift(int angle)
	{
		clawLift.rotate(angle, false);
	}
}
