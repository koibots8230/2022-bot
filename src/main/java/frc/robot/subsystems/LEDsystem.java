// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: Subsystem.

package frc.robot.subsystems;

import frc.robot.Constants;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDsystem extends SubsystemBase {
private AddressableLED strip1;
// private AddressableLED strip2;
private AddressableLEDBuffer buffer;
private boolean state = false;//cone = false, box = cube.
    
    public LEDsystem(int port1,int port2 ) {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        strip1=new AddressableLED(9);
        // strip2=new AddressableLED(port2);
        buffer= new AddressableLEDBuffer(Constants.LED_STRIP_LENGTH);//this length is the number of LEDs on each strip.
        strip1.setLength(buffer.getLength());
        // strip2.setLength(buffer.getLength());
        strip1.setData(buffer);
        // strip2.setData(buffer);
        //I'm not 100% what start actually does but the doc makes it seem needed.
        strip1.start();
        // strip2.start();
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run

    }
    public void setColor(boolean color){//cone = false, box = cube.
        if(color==state){
            return;
        }
        if(color){
            for (var i = 0; i < buffer.getLength(); i++) {
                // Sets the specified LED to the RGB values for purple
                buffer.setRGB(i, 255, 0, 255);
            }
        } else {
            for (var i = 0; i < buffer.getLength(); i++) {
                // Sets the specified LED to the RGB values for yellow
                buffer.setRGB(i, 255, 255, 0);
            }
        }
        state=color;
        strip1.setData(buffer);
        // strip2.setData(buffer);
    }
    public void turnOff(){
        for (var i = 0; i < buffer.getLength(); i++) {
            // Sets the specified LED to the RGB values for black
            buffer.setRGB(i, 0, 0, 0);
        }
        strip1.setData(buffer);
        // strip2.setData(buffer);
    }
    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

}

