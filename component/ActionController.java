/**
 * The ActionController class acts as a finite state
 * machine for the code 
 */

package component;

import algorithm.ClawController;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.TimerListener;

public class ActionController implements TimerListener{
	private final static EV3LargeRegulatedMotor clawLift = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private final static EV3LargeRegulatedMotor clawClose = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	
	private final int CLAW_LIFT_FULL = 775;
	private final int CLAW_LIFT_ONE_BLOCK = -700;
	private final int CLAW_LIFT_TWO_BLOCK = 600;
	private final int CLAW_LIFT_THREE_BLOCK = 500;
	
	
	public ActionController()
	{
		
	}
	
	public void getWifiInfo()
	{
		
	}
	
	/*
	 * Setup the odometer
	 */
	public void startOdometer()
	{
		
	}
	
	/*
	 * Checks if the 
	 */
	public boolean isTimeShort()
	{
		
		return false;
	}
	
	/*
	 * Uses the Wifi info to get the job assigned 
	 */
	public boolean jobAssigned()
	{
		return false;
		
	}
	
	/*
	 * Goes back to the starting position when the time is almost up
	 */
	public void goToStart()
	{
		
	}

	@Override
	public void timedOut() {
		// TODO Auto-generated method stub
		startOdometer();

		ClawController claw = new ClawController(clawLift, clawClose);

		claw.grab();
		claw.lift(CLAW_LIFT_FULL);
		while (Button.waitForAnyPress() != Button.ID_LEFT)
			;
		claw.lift(CLAW_LIFT_ONE_BLOCK);
		claw.release();
		claw.lift(CLAW_LIFT_ONE_BLOCK - CLAW_LIFT_FULL);
	}
}
