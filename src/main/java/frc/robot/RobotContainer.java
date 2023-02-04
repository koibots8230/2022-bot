/* RobotBuilder Version: 5.0

This file was generated by RobotBuilder. It contains sections of
code that are automatically generated and assigned by robotbuilder.
These sections will be updated in the future when you export to
Java from RobotBuilder. Do not put any code or make any change in
the blocks indicating autogenerated code or it will be lost on an
update. Deleting the comments indicating the section will prevent
it from being updated in the future.

ROBOTBUILDER TYPE: RobotContainer. */

package frc.robot;

import frc.robot.commands.*;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.io.File;
import java.util.Map;
import java.util.Scanner;
import edu.wpi.first.wpilibj.Filesystem;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot
 * (including subsystems, commands, and button mappings) should be declared
 * here.
 */
public class RobotContainer {

  private static RobotContainer m_robotContainer = new RobotContainer();
  // private static MiscDashboardSubsystem m_miscDashboardSubsystem = new MiscDashboardSubsystem();


  // Subsystems
  public final TankDriveSubsystem m_tankDriveSubsystem = new TankDriveSubsystem();
  // public final ClawSubsytem m_clawSubsytemBase = new ClawSubsytem(); | Not on
  // robot
  // public final IntakeSubsystem m_intake = new IntakeSubsystem(); | Not on robot
  // public final VisionSubsystem m_VisionSubsystem = new VisionSubsystem();

  // Joysticks
  private final CommandGenericHID m_driverHID = new CommandGenericHID(0);

  // Commands

  private final TankDriveSubsystem.driveMotorCommand m_driveCommand = m_tankDriveSubsystem.new driveMotorCommand(
      () -> m_driverHID.getRawAxis(0),
      () -> m_driverHID.getRawAxis(5),
      m_tankDriveSubsystem);
  
  SendableChooser<Command> m_autoChooser = new SendableChooser<>();
  SendableChooser<String> m_driverChooser = new SendableChooser<>();

  /*
   * m_controllerType 0 -> Unrecognized
   * m_controllerType 1 -> Xbox Controller
   * m_controllerType 2 -> Playstation Controller
   * m_controllerType 3 -> Flight Joystick
   */
  int m_controllerType;

  // Triggers

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  private RobotContainer() {
    // m_driverChooser.setDefaultOption("DEFAULT", null);
    // m_driverChooser.addOption("Caleb", null);
    buildShuffleboard();
    configureButtonBindings();

    int pairButton;
    String hidType = m_driverHID.getHID().getName();
    if (hidType.equals("")) { // Xbox Controller | Name Unknown
      m_controllerType = 1;
      pairButton = 7;
    } else if (hidType.equals("Wireless Controller")) { // PS5 | Is still called "Wireless Controller" if plugged in
                                                        // with a wire.
      m_controllerType = 2;
      pairButton = 7;
    } else {
      m_controllerType = 0;
      pairButton = 7;
    }

    Trigger resetControls = m_driverHID.button(pairButton)
        .whileTrue(new setupControls());

  }

  class setupControls extends CommandBase {
    @Override
    public void execute() {
      configureButtonBindings();
    }

    @Override
    public boolean isFinished() {
      return true;
    }
  }

  private void configureButtonBindings() {
    m_tankDriveSubsystem.setDefaultCommand(m_driveCommand);
  }

  public static RobotContainer getInstance() {
    return m_robotContainer;
  }

  public CommandGenericHID getController() {
    return m_driverHID;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // The selected command will be run in autonomous
    return m_autoChooser.getSelected();
  }

  public void buildDriverTab() {
    ShuffleboardTab battery = Shuffleboard.getTab("SmartDashboard");
    battery.add("Battery Voltage", MiscDashboardSubsystem.getBatteryVoltage())
    .withPosition(0, 0).withWidget(BuiltInWidgets.kDial).withProperties(Map.of("min", 10, "max", 16));

    ShuffleboardTab batteryAlert = Shuffleboard.getTab("SmartDashboard");
    batteryAlert.add("Battery Alert", MiscDashboardSubsystem.getBatteryVoltageAlert())
    .withPosition(1, 0).withWidget(BuiltInWidgets.kBooleanBox);
  }

  private void buildShuffleboard() {
    buildDriverTab();
  }

}
