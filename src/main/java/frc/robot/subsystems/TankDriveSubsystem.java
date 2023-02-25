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
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPRamseteCommand;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
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
    
    private boolean m_inverted; // This boolean determines if the drivetrain is inverted.
    

    public TankDriveSubsystem() {
        primaryRightMotor = new CANSparkMax(Constants.kRightMotor1Port, MotorType.kBrushless);

        secondaryRightMotor = new CANSparkMax(Constants.kRightMotor2Port, MotorType.kBrushless);
        secondaryRightMotor.follow(primaryRightMotor);

        primaryLeftMotor = new CANSparkMax(Constants.kLeftMotor1Port, MotorType.kBrushless);

        secondaryLeftMotor = new CANSparkMax(Constants.kLeftMotor2Port, MotorType.kBrushless);
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
        
        m_inverted = false;
    }

    public TankDriveSubsystem(boolean invertRight, boolean invertLeft) { // optional inversion of motors
        this();
        primaryRightMotor.setInverted(invertRight);
        primaryLeftMotor.setInverted(invertLeft);
    }

    private boolean isInverted = false;

    public void setInverted(boolean invertRight, boolean invertLeft, boolean invertJS) {
        isInverted = invertJS;
    }

    public boolean getInverted() {
        return isInverted;
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

    public boolean getInverted() {
        return m_inverted;
    }

    public void setInverted(boolean invert) {
        m_inverted = invert;
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

    public void setMotorVoltage(double leftVoltage, double rightVoltage) {
        primaryRightMotor.setVoltage(rightVoltage);
        primaryLeftMotor.setVoltage(leftVoltage);
        drivetrain.feed();
    }

    public Command followTrajectoryCommand(PathPlannerTrajectory traj, boolean isFirstPath) {
        return new SequentialCommandGroup(
            new InstantCommand(() -> {
                if (isFirstPath) {
                    this.resetOdometry(traj.getInitialPose());
                }
            }),
            new PPRamseteCommand(
                traj, 
                this::getOdometryPose, 
                new RamseteController(), 
                new SimpleMotorFeedforward(Constants.ksVolts, Constants.kvVoltSecondsPerMeter, Constants.kaVoltSecondsSquaredPerMeter),
                Constants.kDriveKinematics, 
                this::getWheelSpeeds, 
                new PIDController(Constants.kPDriveVel, 0, 0),
                new PIDController(Constants.kPDriveVel, 0, 0),
                this::setMotorVoltage,
                true,
                this
                )
        );
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

            m_rightPID.setP(Constants.kp);
            m_leftPID.setP(Constants.kp);

            m_rightPID.setI(Constants.ki);
            m_leftPID.setI(Constants.ki);

            m_rightPID.setD(Constants.kd);
            m_leftPID.setD(Constants.kd);
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute() {
            // Here's the invert drivetrain invert feature:
            if (m_inverted) {
                m_leftPID.setReference(adjustForDeadzone(m_leftSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
                m_rightPID.setReference(adjustForDeadzone(m_rightSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
            } else {
                // Basically, It takes the negative of the desired speed as the setpoint and runs the PID loop:
                m_leftPID.setReference(-adjustForDeadzone(m_leftSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
                m_rightPID.setReference(-adjustForDeadzone(m_rightSpeed.getAsDouble()), CANSparkMax.ControlType.kDutyCycle);
            }
        }

        private double adjustForDeadzone(double in) {
            if (Math.abs(in) < Constants.DEADZONE) {
                return 0;
            }
            double sign = (int) Math.signum(in);
            double out = Math.abs(sign);
            out *= (1 / 1 - Constants.DEADZONE);
            out *= sign * out;
            return out;
        }
    }

    public class SwitchDrivetrainInvert extends CommandBase { // Switches the drivetrain between inverted and NOT inverted:
        private TankDriveSubsystem m_TankDriveSubsystem;
        public SwitchDrivetrainInvert(TankDriveSubsystem subsystem) {
            m_TankDriveSubsystem = subsystem;
            addRequirements(m_TankDriveSubsystem);
        }

        @Override
        public void execute() {
            // If drivetrain is inverted, it will become not inverted. if it isn't inverted, it'll be inverted:
            m_TankDriveSubsystem.setInverted(!m_TankDriveSubsystem.getInverted());
            SmartDashboard.putBoolean("Is drivetrain inverted?", m_TankDriveSubsystem.getInverted());
        }

        @Override 
        public boolean isFinished() {
            return true;
        }
        
    }

}