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


import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import frc.robot.Constants;

import com.revrobotics.SparkMaxPIDController;

import java.util.function.DoubleSupplier;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax intakeMotor;
    private final RelativeEncoder intakeEncoder;

    private final CANSparkMax firstConveyer;
    private final RelativeEncoder conveyerEncoder;
    private final CANSparkMax leftStarWheelsMotor;
    private final CANSparkMax rightStarWheelsMotor;

    private final CANSparkMax raiseIntakeMotor;
    private final RelativeEncoder raiseIntakeEncoder;

    private double intakePosition; // This variable refers to the incline of the intake IN DEGREES

    private final AnalogInput topHallEffectSensor;
    private final AnalogInput bottomHallEffectSensor;
    
    private IntakeState previous_state;

    public IntakeSubsystem() {
        // Motors
        intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR, MotorType.kBrushless);
        intakeMotor.setInverted(false);
        intakeEncoder = intakeMotor.getEncoder();

        firstConveyer = new CANSparkMax(Constants.MIDTAKE_MOTOR, MotorType.kBrushless); 
        firstConveyer.setInverted(false);
        conveyerEncoder = firstConveyer.getEncoder();

        rightStarWheelsMotor = new CANSparkMax(Constants.STAR_WHEELS_MOTOR_L, MotorType.kBrushless);
        rightStarWheelsMotor.setInverted(true);
        leftStarWheelsMotor = new CANSparkMax(Constants.STAR_WHEELS_MOTOR_R, MotorType.kBrushless);
        leftStarWheelsMotor.follow(rightStarWheelsMotor, true);

        raiseIntakeMotor = new CANSparkMax(Constants.RAISE_INTAKE_MOTOR, MotorType.kBrushless);
        raiseIntakeMotor.setInverted(false);
        raiseIntakeEncoder = raiseIntakeMotor.getEncoder();
        raiseIntakeEncoder.setPosition(0);
        intakePosition = 0;

        // Hall effect sensors
        topHallEffectSensor = new AnalogInput(0); // Change port number when testing the code
        bottomHallEffectSensor = new AnalogInput(1); // Change port numer when testing the code

    }

    public void resetPosition() {
        raiseIntakeEncoder.setPosition(0);
        intakeEncoder.setPosition(0);
        conveyerEncoder.setPosition(0);
        rightStarWheelsMotor.getEncoder().setPosition(0);
    }

    enum IntakeState {
        BOTTOM,
        TOP,
        MOVE,
        CALIBRATE
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        double inVelocity = intakeEncoder.getVelocity();
        double InCurrent = intakeMotor.getOutputCurrent(); 
        SmartDashboard.putNumber("Intake Motor Speed (RPM)", inVelocity);
        SmartDashboard.putNumber("Main Battery Current (A)", InCurrent);
        double midVelocity = conveyerEncoder.getVelocity();
        double midCurrent = firstConveyer.getOutputCurrent();
        SmartDashboard.putNumber("Midtake Motor Current (A)", midCurrent);
        SmartDashboard.putNumber("Midtake Motor Speed (RPM)", midVelocity);

    }
    
    // ================================Setters================================

    public void ClearStickies() {
        raiseIntakeMotor.clearFaults();
    }

    public void setRaiseIntakeSpeed(double speed){
        raiseIntakeMotor.set(speed);
    }
    public void turnOn(Boolean Forwards) {
        if (Forwards){
            intakeMotor.set(-Constants.INTAKE_RUNNING_SPEED);
            firstConveyer.set(Constants.BELT_RUNNING_SPEED);
            rightStarWheelsMotor.set(Constants.STARS_RUNNING_SPEED);
            leftStarWheelsMotor.set(Constants.STARS_RUNNING_SPEED);
        } else {
            intakeMotor.set(Constants.INTAKE_RUNNING_SPEED);
            firstConveyer.set(-.15);
            rightStarWheelsMotor.set(-.15);
            leftStarWheelsMotor.set(-.15);
        }
    }

    public void turnOff() {
        intakeMotor.set(0);
        firstConveyer.set(0);
        rightStarWheelsMotor.set(0);
    }

    private double approximateSpeed(double Position){
        double calculated_speed = Constants.RAISE_SPEED * Math.pow(4, -(Math.abs(Position)/20));
        if (calculated_speed < 0.10) {
            return 0.10;
        }
        return calculated_speed;
    }

    // ================================Getters================================

    public double getRaiseMotorCurrent() {
        return raiseIntakeMotor.getOutputCurrent();
    }

    public double getIntakePosition() {
        return intakePosition;
    }

    public RelativeEncoder getRaiseEncoder() {
        return raiseIntakeEncoder;
    }

    public CANSparkMax getIntakeRaiseMotor() {
        return raiseIntakeMotor;
    }
    public AnalogInput getTopHallEffectSensor(){
        return topHallEffectSensor;
    }
    public AnalogInput getBottomHallEffectSensor() {
        return bottomHallEffectSensor;
    }

    public IntakeState getIntakeState() {
        if (topHallEffectSensor.getVoltage() > Constants.HALL_EFFECT_SENSOR_TRIGGERED) {
            return IntakeState.BOTTOM;
        }
        if (bottomHallEffectSensor.getVoltage() > Constants.HALL_EFFECT_SENSOR_TRIGGERED) {
            return IntakeState.TOP;
        }
        return IntakeState.MOVE;
    }

    public SparkMaxPIDController getIntakePID() {
        return rightStarWheelsMotor.getPIDController();
    }

    public SparkMaxPIDController getMidtakePID() {
        return intakeMotor.getPIDController();
    }
    
    // ================================Commands================================

    public class IntakeCommand extends CommandBase {
        private DoubleSupplier m_intakeSpeed;
        private SparkMaxPIDController m_intakePID;
        private SparkMaxPIDController m_midtakePID;
        private IntakeSubsystem m_intakeSubsystem;

        public IntakeCommand(DoubleSupplier intakeSpeed, IntakeSubsystem subsystem) {
            m_intakeSpeed = intakeSpeed;
            m_intakeSubsystem = subsystem;
            addRequirements(subsystem);
        }
        
        @Override
        public void initialize() {
            m_intakePID = m_intakeSubsystem.getIntakePID();
            m_midtakePID = m_intakeSubsystem.getMidtakePID();

            m_intakePID.setOutputRange(-1, 1);
            m_midtakePID.setOutputRange(-1, 1);

            m_intakePID.setP(6e-5);
            m_midtakePID.setP(6e-5);

            m_intakePID.setI(0);
            m_midtakePID.setI(0);

            m_intakePID.setD(0);
            m_midtakePID.setD(0);
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute() {
            // Here's the invert drivetrain invert feature:
            m_intakePID.setReference(adjustForDeadzone(m_intakeSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
            m_midtakePID.setReference(adjustForDeadzone(m_intakeSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
        }

        private double adjustForDeadzone(double in) {
            if (Math.abs(in) < Constants.DEADZONE) {
                return 0;
            }
            return in;
        }
    }

    public class MoveIntakeByEncoder extends CommandBase {
        IntakeSubsystem intake;
        boolean end = false;

        public MoveIntakeByEncoder(IntakeSubsystem _intake) {
            addRequirements(_intake);
            intake = _intake;
        }

        public void initialize() {
            intake.setRaiseIntakeSpeed(-Constants.RAISE_SPEED);
        }

        public void periodic() {
            if (intake.getRaiseEncoder().getPosition() >= 20) {
                end = true;
            } else if (intake.getRaiseMotorCurrent() >= 70) {
                end = true;
            }
        }

        public boolean isFinished() {
            return end;
        }

        public void end(boolean interrupted) {
            intake.setRaiseIntakeSpeed(0);
            resetPosition();
        }
    }

    public class FlipIntake extends CommandBase {
        IntakeSubsystem m_intake;
        boolean end = false;
        AnalogInput hallEffectSensor;
        public FlipIntake(IntakeSubsystem subsystem) {
            m_intake = subsystem;
            addRequirements(m_intake);
        }

        @Override
        public void initialize() {
            System.out.println("Intake is moving");
            previous_state = m_intake.getIntakeState();
            //use get just incase intake isnt run
            switch(previous_state){
                case TOP:
                    m_intake.setRaiseIntakeSpeed(-Constants.RAISE_SPEED);
                    hallEffectSensor = getBottomHallEffectSensor();
                    break;
                case BOTTOM:
                    m_intake.setRaiseIntakeSpeed(Constants.RAISE_SPEED);
                    hallEffectSensor = getTopHallEffectSensor();
                    break;
                case MOVE:
                    //dont use this command if we're in a move state, so just end
                    //leaving this in case we need it in future challenges
                    end = true;
                    break;
                case CALIBRATE:
                    //move down to calibrate if we dont know our position
                    m_intake.setRaiseIntakeSpeed(-Constants.RAISE_SPEED);
                    hallEffectSensor = getBottomHallEffectSensor();
                    break;
            }
        }

        @Override
        public void execute() {
            boolean CurrentOrHallEffectTriggered = (Math.abs(m_intake.getRaiseMotorCurrent()) > Constants.CURRENT_ZONE_AMPS || hallEffectSensor.getVoltage() > Constants.HALL_EFFECT_SENSOR_TRIGGERED);
            boolean EncoderPositionPassed = (Math.abs(m_intake.getRaiseEncoder().getPosition()) >= Constants.INTAKE_CHANGE_POSITION);
            if (CurrentOrHallEffectTriggered) {
                if (EncoderPositionPassed) {
                m_intake.setRaiseIntakeSpeed(0);
                end = true;
                } 
            } 
            //change speeds
            //intakePos is absolute cause we caccount for signs when settings
            double intakePos = Math.abs(intakeEncoder.getPosition());
            switch(previous_state){
                case TOP:
                    m_intake.setRaiseIntakeSpeed(-approximateSpeed(intakePos));
                    break;
                case BOTTOM:
                    m_intake.setRaiseIntakeSpeed(approximateSpeed(intakePos));
                    break;
                case CALIBRATE:
                    //move down to calibrate if we dont know our position
                    m_intake.setRaiseIntakeSpeed(-approximateSpeed(intakePos));
                    break;
                case MOVE:
                    //move 
                    break;
            }

        }

        @Override
        public boolean isFinished() {
            return end;
        }
        
        @Override
        public void end(boolean Interrupted){
            m_intake.getRaiseEncoder().setPosition(0);
        }
    }
}