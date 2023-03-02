// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import java.util.function.DoubleSupplier;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

public class TankDriveSubsystem extends SubsystemBase {
    private CANSparkMax primaryRightMotor;
    private CANSparkMax secondaryRightMotor;
    private CANSparkMax primaryLeftMotor;
    private CANSparkMax secondaryLeftMotor;
    private double speedCoefficient = 1;
    private AHRS gyro = new AHRS(SPI.Port.kMXP);
    final DifferentialDrive drivetrain;
    DifferentialDriveWheelSpeeds wheelSpeeds = new DifferentialDriveWheelSpeeds();
    //Encoders:
    private final RelativeEncoder primaryRightEncoder;
    private final RelativeEncoder primaryLeftEncoder;
    private DifferentialDriveOdometry m_Odometry;
    private Pose2d OdometryPose;
    
    public TankDriveSubsystem() {
        primaryRightMotor = new CANSparkMax(Constants.RIGHT_DRIVE_MOTOR_1, MotorType.kBrushless);
        primaryRightMotor.setInverted(true);

        secondaryRightMotor = new CANSparkMax(Constants.RIGHT_DRIVE_MOTOR_2, MotorType.kBrushless);
        secondaryRightMotor.follow(primaryRightMotor);

        primaryLeftMotor = new CANSparkMax(Constants.LEFT_DRIVE_MOTOR_1, MotorType.kBrushless);

        secondaryLeftMotor = new CANSparkMax(Constants.LEFT_DRIVE_MOTOR_2, MotorType.kBrushless);
        secondaryLeftMotor.follow(primaryLeftMotor);

        drivetrain = new DifferentialDrive(primaryLeftMotor, primaryRightMotor);

        primaryRightEncoder = primaryRightMotor.getEncoder();
        primaryLeftEncoder = primaryLeftMotor.getEncoder();

        primaryLeftEncoder.setPositionConversionFactor(Constants.LEFT_ENCODER_ROTATIONS_TO_DISTANCE);
        primaryRightEncoder.setPositionConversionFactor(Constants.RIGHT_ENCODER_ROTATIONS_TO_DISTANCE);
        
        primaryLeftEncoder.setVelocityConversionFactor(Constants.LEFT_ENCODER_ROTATIONS_TO_DISTANCE);
        primaryRightEncoder.setVelocityConversionFactor(Constants.RIGHT_ENCODER_ROTATIONS_TO_DISTANCE);

        m_Odometry = new DifferentialDriveOdometry(new Rotation2d(gyro.getYaw()+180), primaryLeftEncoder.getPosition(), primaryRightEncoder.getPosition());
    }

    public DifferentialDriveWheelSpeeds getWheelSpeeds(){
        return wheelSpeeds;
    }

    public TankDriveSubsystem(boolean invertRight, boolean invertLeft) { // optional inversion of motors
        this();
        primaryRightMotor.setInverted(invertRight);
        primaryLeftMotor.setInverted(invertLeft);
    }

    public double getLeftDriveSpeed() {
        return primaryLeftEncoder.getVelocity();
    }

    public double getRightDriveSpeed() {
        return primaryRightEncoder.getVelocity();
    }

    @Override
    public void periodic() {
        wheelSpeeds = new DifferentialDriveWheelSpeeds(primaryLeftEncoder.getVelocity(), primaryRightEncoder.getVelocity());
        OdometryPose = m_Odometry.update(
            new Rotation2d(gyro.getYaw()+180),
            primaryLeftEncoder.getPosition(),
            primaryRightEncoder.getPosition());
        // This method will be called once per scheduler run
    }
    public Pose2d getOdometryPose() {
        return OdometryPose;
    }
    public void resetOdometry(Pose2d pose) {
        primaryLeftEncoder.setPosition(0);
        primaryRightEncoder.setPosition(0);
        m_Odometry.resetPosition(
            gyro.getRotation2d(), primaryLeftEncoder.getPosition(), primaryRightEncoder.getPosition(), pose);
      }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public SparkMaxPIDController getLeftPID() {
        return primaryLeftMotor.getPIDController();
    }

    public SparkMaxPIDController getRightPID() {
        return primaryRightMotor.getPIDController();
    }

    public void setMotor(double leftSpeed, double rightSpeed) {
        primaryLeftMotor.set(leftSpeed);
        primaryRightMotor.set(rightSpeed);
    }
    
    public void setSpeed(Boolean Increase){
        if (Increase) {
            //Only need increase - if it's called and Increase is false than decrease is pressed instead
            if (speedCoefficient < 1) {
                speedCoefficient += 0.05;
            }
        } else {
            if (speedCoefficient > 0){
                speedCoefficient -= 0.05;
            }
        }
    }
    
    public double[] getEncoderPositions(){
        //get the in between of both encoders
        return (new double[] {primaryLeftEncoder.getPosition(),primaryRightEncoder.getPosition()});
    }

    public void setMotorVoltage(double leftVoltage, double rightVoltage) {
        primaryRightMotor.setVoltage(rightVoltage);
        primaryLeftMotor.setVoltage(leftVoltage);
        drivetrain.feed();
    }
    
    public class driveMotorCommand extends CommandBase {
        private DoubleSupplier m_rightSpeed;
        private DoubleSupplier m_leftSpeed;
        private SparkMaxPIDController m_rightPID;
        private SparkMaxPIDController m_leftPID;
        private TankDriveSubsystem m_DriveSubsystem;

        public driveMotorCommand(DoubleSupplier rightSpeed, DoubleSupplier leftSpeed, TankDriveSubsystem subsystem) {
            m_rightSpeed = rightSpeed;
            m_leftSpeed = leftSpeed;
            m_DriveSubsystem = subsystem;
            addRequirements(subsystem);
        }
        
        @Override
        public void initialize() {
            m_rightPID = m_DriveSubsystem.getRightPID();
            m_leftPID = m_DriveSubsystem.getLeftPID();

            m_rightPID.setOutputRange(-1, 1);
            m_leftPID.setOutputRange(-1, 1);

            m_rightPID.setP(6e-5);
            m_leftPID.setP(6e-5);

            m_rightPID.setI(0);
            m_leftPID.setI(0);

            m_rightPID.setD(0);
            m_leftPID.setD(0);
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute() {
            // Here's the invert drivetrain invert feature:
            m_leftPID.setReference(adjustForDeadzone(m_leftSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
            m_rightPID.setReference(adjustForDeadzone(m_rightSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
        }

        private double adjustForDeadzone(double in) {
            if (Math.abs(in) < Constants.DEADZONE) {
                return 0;
            }
            double sign = (in < 0) ? -.25 : .25;
            return sign*in*in;
        }
    }

    public class driveDistanceCommand extends CommandBase {
        private double m_rightSpeed;
        private double m_leftSpeed;
        private SparkMaxPIDController m_rightPID;
        private SparkMaxPIDController m_leftPID;
        private TankDriveSubsystem m_DriveSubsystem;
        private double[] m_initialPositions;
        private boolean hasReachedEnd;
        private double m_encoderLimit;


        //Direction should be from -1 to 1 to indicate direction; 0 is Balanced, -1 is full left, 1 is full right
        public driveDistanceCommand(double leftSpeed, double rightSpeed, double encoder_limit, TankDriveSubsystem subsystem) {
            m_DriveSubsystem = subsystem;
            m_encoderLimit = encoder_limit;
            m_leftSpeed = leftSpeed;
            m_rightSpeed = rightSpeed;
            addRequirements(subsystem);
        }
        
        @Override
        public void initialize() {

            m_initialPositions = m_DriveSubsystem.getEncoderPositions();
            hasReachedEnd = false;

            m_rightPID = m_DriveSubsystem.getRightPID();
            m_leftPID = m_DriveSubsystem.getLeftPID();

            m_rightPID.setOutputRange(-1, 1);
            m_leftPID.setOutputRange(-1, 1);

            m_rightPID.setP(6e-5);
            m_leftPID.setP(6e-5);

            m_rightPID.setI(0);
            m_leftPID.setI(0);

            m_rightPID.setD(0);
            m_leftPID.setD(0);
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute() {
            // Here's the invert drivetrain invert feature:
            m_leftPID.setReference((-m_leftSpeed), CANSparkMax.ControlType.kDutyCycle);
            m_rightPID.setReference((-m_rightSpeed), CANSparkMax.ControlType.kDutyCycle);

            // check if we have reached the end
            double[] current_positions = m_DriveSubsystem.getEncoderPositions();
            double l_dif = (current_positions[0] - m_initialPositions[0]);
            double r_dif = (current_positions[1] - m_initialPositions[1]); 

            if (Math.abs(l_dif + r_dif)>= m_encoderLimit) {
                System.out.println("Reached end condition for DriveDistance");
                hasReachedEnd = true;
            }
        }
        
        @Override 
        public boolean isFinished() {
            return hasReachedEnd;
        }

        @Override 
        public void end(boolean isInterrupted){
            m_leftPID.setReference(0, CANSparkMax.ControlType.kDutyCycle);
            m_rightPID.setReference(0, CANSparkMax.ControlType.kDutyCycle);
        }
    }


}