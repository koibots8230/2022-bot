// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

// ROBOTBUILDER TYPE: Robot.

package frc.robot;

import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.simulation.DriverStationDataJNI;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Utilities.NAVX;
import frc.robot.subsystems.IntakePositionSubsystem;
import frc.robot.subsystems.TankDriveSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in 
 * the project.
 */
public class Robot extends TimedRobot {

    private Command m_autonomousCommand;
    private RobotContainer m_robotContainer;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    @SuppressWarnings("resource")
    public void robotInit() {

        m_robotContainer = RobotContainer.getInstance();
        // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        NAVX.get().zeroYaw();
        TankDriveSubsystem.get().resetEncoders();
        HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_RobotBuilder);

        ShuffleboardTab debugTab = Shuffleboard.getTab("Debug");

        debugTab
            .addDoubleArray("Drive Voltages", TankDriveSubsystem.get()::getVoltages)
            .withWidget("Graph")
            .withPosition(0, 0)
            .withSize(3, 3);

        debugTab
            .addDouble("Yaw", NAVX.get()::getAngle)
            .withPosition(3, 0)
            .withSize(3, 3);
        
        debugTab
            .addDouble("Pitch", NAVX.get()::getRoll)
            .withWidget("Gyro")
            .withPosition(6, 0)
            .withSize(3, 3);

        debugTab
            .addDouble("Roll", NAVX.get()::getPitch)
            .withWidget("Gyro")
            .withPosition(9, 0)
            .withSize(3, 3);

        debugTab
            .addDoubleArray("Encoders", TankDriveSubsystem.get()::getEncoderPositions)
            .withWidget("Graph")
            .withPosition(0, 3)
            .withSize(3, 3);
    }

    /**
    * This function is called every robot packet, no matter the mode. Use this for items like
    * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
    *
    * <p>This runs after the mode specific periodic functions, but before
    * LiveWindow and SmartDashboard integrated updating.
    */
    @Override
    public void robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run();
    }

    /**
    * This function is called once each time the robot enters Disabled mode.
    */
    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    /**
    * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
    */
    @Override
    public void autonomousInit() {
        NAVX.get().zeroYaw();
        TankDriveSubsystem.get().resetEncoders();
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        TankDriveSubsystem.get().setCoast();
        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    /**
    * This function is called periodically during autonomous.
    */
    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void autonomousExit() {
        TankDriveSubsystem.get().resetEncoders();
    
    }

    @Override
    public void teleopInit() {
        //enable coast
        TankDriveSubsystem.get().setCoast();
        IntakePositionSubsystem.get().setCoast();
        //start camera
        //CameraServer.startAutomaticCapture();
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void teleopExit() {
        TankDriveSubsystem.get().setBrake();
        IntakePositionSubsystem.get().setBrake();
    }

    @Override
    public void testInit() {
        IntakePositionSubsystem.get().setCoast();
        TankDriveSubsystem.get().setCoast();
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll();
        System.out.println("Reset to Coast");
        DriverStationDataJNI.setEnabled(true);
    }

    /**
    * This function is called periodically during test mode.
    */
    @Override
    public void testPeriodic() {
    }
}
