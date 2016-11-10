package algorithm;

import component.ActionController;
import component.Odometer;
import component.USPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread{

	//Speed variables
	
	final static int ACCELERATION = 3000, FORWARD_SPEED = 200, ROTATION_SPEED = 100;
	final static int TRACK = 0000;
	final static int RADIUS = 0000;
	final static int DIST_ERR = 0000;
	
	private Odometer odometer;
	private ActionController control;
	private USPoller usPoller;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	
	public Navigation(Odometer odometer, ActionController control, USPoller usPoller){
		
		this.odometer = odometer;
		this.control = control;
		this.usPoller = usPoller;
		
		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}
	
	public void run(){
		//do something while true (thread states inside)
	}
	
	//in degrees
	public double wrapAngle(double angle){

		if (angle < 0)
		{ angle += 360;	}

		if (angle >= 360)
		{ angle -= 360; }

		return angle;
	}
	
	//in rads to rotations needed
	public int convertAngle( double radius, double width, double angle){

		return (int)((width * angle / 2.0) / (radius));
	}

	//in rads
	public double calculateAngle(double initial_x, double initial_y, double final_x, double final_y){

		double delta_x = (final_x - initial_x);
		double delta_y = (final_y - initial_y);

		if (delta_x < 0){

			if (delta_y > 0)
				{ return Math.atan(delta_y / delta_x) + Math.PI; }

			else
				{ return Math.atan(delta_y / delta_x) - Math.PI; }
		}

		else
			{ return Math.atan(delta_y / delta_x); }
	}

	//in rads
	public void turnTo(double target_angle){

		control.stopMotors();
		control.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);

		double delta_theta = Math.toDegrees(target_angle - odometer.getAng());
		int turning_angle =convertAngle(RADIUS, TRACK, delta_theta);

		double adjusted_theta = 360 - delta_theta;
		int adjusted_turning_angle = convertAngle(RADIUS, TRACK, adjusted_theta);


		if (-180 < delta_theta && delta_theta < 180){

			rightMotor.rotate(-turning_angle, true);
			leftMotor.rotate(turning_angle, false);
		}

		else if (delta_theta < -180){

			rightMotor.rotate(-adjusted_turning_angle, true);
			leftMotor.rotate(adjusted_turning_angle, false);
		}

		else{

			rightMotor.rotate(adjusted_turning_angle, true);
			leftMotor.rotate(-adjusted_turning_angle, false);
		}
	}
	
	public void travelTo(double destination_x, double destination_y){

		turnTo(calculateAngle(odometer.getX(), odometer.getY(), destination_x, destination_y));
		control.setSpeeds(FORWARD_SPEED, FORWARD_SPEED);

		while ((Math.abs(odometer.getX() - destination_x) > DIST_ERR) || (Math.abs(odometer.getY() - destination_y) > DIST_ERR)){

			leftMotor.forward();
			rightMotor.backward();
		}
		
		control.stopMotors();
	}

	//might have to convert all methods to work in degrees
}
