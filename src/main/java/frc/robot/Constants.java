// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public class Constants { 
    public enum XboxButtons {
        kLeftBumper(5),
        kRightBumper(6),
        kLeftStick(9),
        kRightStick(10),
        kA(1),
        kB(2),
        kX(3),
        kY(4),
        kBack(7),
        kStart(8);
    
        public final int value;
    
        XboxButtons(int value) {
          this.value = value;
        }
    }
    public enum XboxAxes {
        kLeftX(0),
        kRightX(4),
        kLeftY(1),
        kRightY(5),
        kLeftTrigger(2),
        kRightTrigger(3);
    
        public final int value;
    
        XboxAxes(int value) {
          this.value = value;
        }
    }
    public enum PS4Buttons {
        kSquare(1),
        kCross(2),
        kCircle(3),
        kTriangle(4),
        kL1(5),
        kR1(6),
        kL2(7),
        kR2(8),
        kShare(9),
        kOptions(10),
        kL3(11),
        kR3(12),
        kPS(13),
        kTouchpad(14);
    
        public final int value;
    
        PS4Buttons(int index) {
          this.value = index;
        }
    }
    public enum PS4Axes {
        kLeftX(0),
        kLeftY(1),
        kRightX(2),
        kRightY(5),
        kL2(3),
        kR2(4);
    
        public final int value;
    
        PS4Axes(int index) {
          value = index;
        }
    }
    public enum Shape{
      CUBE,
      CONE,
      NONE
    }

    public static final int kLeftMotor1Port = 15;
    public static final int kLeftMotor2Port = 14;
    public static final int kRightMotor1Port = 12;
    public static final int kRightMotor2Port = 13;
    public static final int kIntakeMotorPort = 0; // To be changed when we have an actual intake
    public static final double AUTO_SPEED = 0.15;
    public static final int kRaiseIntakeMotorPort = 2; //To be changed when we know the port of raiseIntakeMotor
    public static final int kMidtakeMotorPort = 1; // To be changed when we have an actual intake
    //PID constants for PIDCommand Setmotor:
    public static final double kp = 6e-5;
    public static final double ki = 0;
    public static final double kd = 0;
    //Slowmotion, deadzone, etc:
    public static final double slowMoFactor = 0.5;
    //for LED system
    public static final int stripLength=60;//the number of LEDs on each of the LED strips.
    // public static final int LEDPort1=0;
    // public static final int LEDPort2=0;//neither of these are known currently, but they can be uncommented once we know the port numbers.
    public static final double DEADZONE = 0.15;
}