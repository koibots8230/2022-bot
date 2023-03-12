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

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Utilities.NAVX;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.CANSparkMax.IdleMode;

public class TankDriveSubsystem extends SubsystemBase {
    private static TankDriveSubsystem m_TankDriveSubsystem = new TankDriveSubsystem();

    private CANSparkMax primaryRightMotor;
    private CANSparkMax secondaryRightMotor;
    private CANSparkMax primaryLeftMotor;
    private CANSparkMax secondaryLeftMotor;

    DifferentialDrive drivetrain;
    DifferentialDriveWheelSpeeds wheelSpeeds = new DifferentialDriveWheelSpeeds();

    private final SparkMaxAbsoluteEncoder leftAbsoluteEncoder;
    private final SparkMaxAbsoluteEncoder rightAbsoluteEncoder;

    private DifferentialDriveOdometry m_Odometry;

    private double speedCoefficient = Constants.DRIVE_SPEED_COEFFICIENT;

    public TankDriveSubsystem() {

        primaryRightMotor = new CANSparkMax(Constants.RIGHT_DRIVE_MOTOR_1, MotorType.kBrushless);
        secondaryRightMotor = new CANSparkMax(Constants.RIGHT_DRIVE_MOTOR_2, MotorType.kBrushless);
        primaryLeftMotor = new CANSparkMax(Constants.LEFT_DRIVE_MOTOR_1, MotorType.kBrushless);
        secondaryLeftMotor = new CANSparkMax(Constants.LEFT_DRIVE_MOTOR_2, MotorType.kBrushless);

        primaryRightMotor.setInverted(true);

        secondaryRightMotor.follow(primaryRightMotor);
        secondaryLeftMotor.follow(primaryLeftMotor);

        leftAbsoluteEncoder = primaryLeftMotor.getAbsoluteEncoder(Type.kDutyCycle);
        rightAbsoluteEncoder = primaryRightMotor.getAbsoluteEncoder(Type.kDutyCycle);

        leftAbsoluteEncoder.setPositionConversionFactor(Constants.DRIVE_ROTATIONS_TO_DISTANCE);
        rightAbsoluteEncoder.setPositionConversionFactor(Constants.DRIVE_ROTATIONS_TO_DISTANCE);

        m_Odometry = new DifferentialDriveOdometry(new Rotation2d(Math.toRadians(NAVX.get().getAngle())),
                leftAbsoluteEncoder.getPosition(), rightAbsoluteEncoder.getPosition());
    }

    public static TankDriveSubsystem get() {
        return m_TankDriveSubsystem;
    }

    public Pose2d getRobotPose() {
        return m_Odometry.getPoseMeters();
    }

    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        return new DifferentialDriveWheelSpeeds(leftAbsoluteEncoder.getVelocity(), rightAbsoluteEncoder.getVelocity());
    }

    @Override
    public void periodic() {
        if (NAVX.get().getRoll() > 1) {
            m_Odometry.update(NAVX.get().getRotation2d(), // TODO: May not adjust for alliance offset
            leftAbsoluteEncoder.getPosition() * Math.cos(Math.toRadians(NAVX.get().getRoll())),
            rightAbsoluteEncoder.getPosition() * Math.cos(Math.toRadians(NAVX.get().getRoll())));
        }
        m_Odometry.update(NAVX.get().getRotation2d(),
        leftAbsoluteEncoder.getPosition(),
        rightAbsoluteEncoder.getPosition());
    }

    public void resetOdometry(Pose2d pose) {
        m_Odometry = new DifferentialDriveOdometry(pose.getRotation(), leftAbsoluteEncoder.getPosition(),
                rightAbsoluteEncoder.getPosition(), pose);
        NAVX.get().setAngleAdjustment(pose.getRotation().getDegrees());
    }

    public double[] getEncoderPositions() {
        // get the in between of both encoders
        return (new double[] { leftAbsoluteEncoder.getPosition(), rightAbsoluteEncoder.getPosition() });
    }

    public void setVoltage(double leftVoltage, double rightVoltage) {
        primaryLeftMotor.setVoltage(leftVoltage);
        primaryRightMotor.setVoltage(rightVoltage);
    }

    public void setMotor(double leftSpeed, double rightSpeed) {
        primaryLeftMotor.set(leftSpeed);
        primaryRightMotor.set(rightSpeed);
    }

    public void SlowDrive() {
        speedCoefficient = .33;
    }

    public void UnslowDrive() {
        speedCoefficient = 1;
    }

    public void setMotorVoltage(double leftVoltage, double rightVoltage) {
        primaryRightMotor.setVoltage(rightVoltage);
        primaryLeftMotor.setVoltage(leftVoltage);
        drivetrain.feed();
    }

    public SparkMaxAbsoluteEncoder getLeftAbsoluteEncoder() {
        return leftAbsoluteEncoder;
    }

    public SparkMaxAbsoluteEncoder getRightAbsoluteEncoder() {
        return rightAbsoluteEncoder;
    }

    public void setBrake() {
        primaryLeftMotor.setIdleMode(IdleMode.kBrake);
        primaryRightMotor.setIdleMode(IdleMode.kBrake);
        secondaryLeftMotor.setIdleMode(IdleMode.kBrake);
        secondaryRightMotor.setIdleMode(IdleMode.kBrake);
    }

    public void setCoast() {
        primaryLeftMotor.setIdleMode(IdleMode.kCoast);
        primaryRightMotor.setIdleMode(IdleMode.kCoast);
        secondaryLeftMotor.setIdleMode(IdleMode.kCoast);
        secondaryRightMotor.setIdleMode(IdleMode.kCoast);
    }

    // ================================Commands================================

    public class driveMotorCommand extends CommandBase {
        private DoubleSupplier m_rightSpeed;
        private DoubleSupplier m_leftSpeed;

        public driveMotorCommand(DoubleSupplier rightSpeed, DoubleSupplier leftSpeed) {
            m_rightSpeed = rightSpeed;
            m_leftSpeed = leftSpeed;
            addRequirements(TankDriveSubsystem.this);
        }

        @Override
        public void initialize() {

        }

        @Override
        public void execute() {
            TankDriveSubsystem.this.setMotor(
                    adjustForDeadzone(m_leftSpeed.getAsDouble()) * speedCoefficient,
                    adjustForDeadzone(m_rightSpeed.getAsDouble()) * speedCoefficient);
        }

        private double adjustForDeadzone(double in) {
            if (Math.abs(in) < Constants.DEADZONE) {
                return 0;
            }
            double sign = (in < 0) ? -Constants.MAX_DRIVETRAIN_SPEED : Constants.MAX_DRIVETRAIN_SPEED;
            return sign * (in * in);
        }
    }
}