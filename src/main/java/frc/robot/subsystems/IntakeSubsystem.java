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


import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.util.sendable.Sendable;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax intakeMotor;
    private final RelativeEncoder intakeEncoder;
    private final double RUNNING_SPEED = .7;

    public IntakeSubsystem() {
        intakeMotor = new CANSparkMax(Constants.kIntakeMotorPort, MotorType.kBrushless);
        addChild("IntakeMotor", (Sendable) intakeMotor);
        intakeMotor.setInverted(false);
        intakeEncoder = intakeMotor.getEncoder();
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        double velocity = intakeEncoder.getVelocity();
        double current = intakeMotor.getOutputCurrent(); 
        SmartDashboard.putNumber("Intake Speed (RPM)", velocity);
        SmartDashboard.putNumber("Motor Current (A)", current);
    }

    @Override
    public void simulationPeriodic() {
    }

    public void turnOn() {
        intakeMotor.set(RUNNING_SPEED);
    }

    public void turnOn(Boolean Forwards) {
        if (Forwards){
            intakeMotor.set(RUNNING_SPEED);
        } else {
            intakeMotor.set(-RUNNING_SPEED);
        }
    }

    public void turnOff() {
        intakeMotor.set(0);
    }
}

