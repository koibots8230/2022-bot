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
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.networktables.GenericEntry;

import java.util.function.DoubleSupplier;

import com.kauailabs.navx.frc.AHRS;

import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem.CommunityShotCommand;

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

  // Subsystems
  public final TankDriveSubsystem m_tankDriveSubsystem = new TankDriveSubsystem();
  public final IntakeSubsystem m_intake = new IntakeSubsystem();

  public final ShooterSubsystem m_ShooterSubsystem = new ShooterSubsystem();

  //LED system 
  private final LEDsystem LEDstrips = new LEDsystem(Constants.LEDPort1);//addressable LED only works from one port.
  //public final VisionSubsystem m_VisionSubsystem = new VisionSubsystem(m_sideChooser.getSelected());

  // public final VisionSubsystem m_VisionSubsystem = new
  // VisionSubsystem(m_sideChooser.getSelected());
  private MiscDashboardSubsystem m_miscDashboardSubsystem = new MiscDashboardSubsystem(m_intake, m_ShooterSubsystem, m_tankDriveSubsystem);

  // Controlers
  private final CommandXboxController m_driverHID = new CommandXboxController(0);
  private final CommandPS4Controller m_operatorHID = new CommandPS4Controller(1);

  // Teleop Drive
  private DoubleSupplier leftDriveTrain = () -> m_driverHID.getLeftY();
  private DoubleSupplier rightDriveTrain = () -> m_driverHID.getRightY();

  private final TankDriveSubsystem.driveMotorCommand m_driveCommand = m_tankDriveSubsystem.new driveMotorCommand(
      rightDriveTrain,
      leftDriveTrain,
      m_tankDriveSubsystem);

  // Shuffleboard
  SendableChooser<Integer> m_autoChooser;
  SendableChooser<Boolean> m_customAuto;
  SendableChooser<Integer> m_customChooser;
  SendableChooser<Double> m_intakeSpeedChooser;
  // SendableChooser<Boolean> m_sideChooser;

  GenericEntry autobal_leftSpeed; // = m_autotab.add("autobal leftSpeed", Constants.AUTO_LEFT_SPEED).getEntry();
  GenericEntry autobal_rightSpeed; // = m_autotab.add("autobal rightSpeed", Constants.AUTO_RIGHT_SPEED).getEntry();
  GenericEntry shoot_leftSpeed; // = m_autotab.add("shoot leftSpeed", Constants.SHOOT_LEFT_SPEED).getEntry();
  GenericEntry shoot_rightSpeed;// = m_autotab.add("shoot rightSpeed", Constants.SHOOT_RIGHT_SPEED).getEntry();
  GenericEntry shoot_time; // = m_autotab.add("Shoot time", Constants.SHOOT_SECONDS).getEntry();
  GenericEntry autobal_limit;
  GenericEntry shoot_limit;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  private void configureButtonBindings() {
    m_tankDriveSubsystem.setDefaultCommand(m_driveCommand);

    // Create Triggers here | Triggers should be named t_CommandName

    // ======================================Operator Controls======================================

    // Create Triggers here | Triggers should be named t_CommandName

    Trigger operatorSpeedUp = m_operatorHID.cross();
    Trigger operatorSpeedDown = m_operatorHID.circle();

    Trigger slowMode = m_operatorHID.triangle();
    slowMode.onTrue(new InstantCommand(() -> m_tankDriveSubsystem.SlowDrive()));
    slowMode.onFalse(new InstantCommand(() -> m_tankDriveSubsystem.UnslowDrive()));

    // Shooting
    Trigger shootL2 = m_operatorHID.L1();
    Trigger shootL3 = m_operatorHID.R1();

    shootL2.whileTrue(m_ShooterSubsystem.new LevelShootCommand(m_ShooterSubsystem, 2));
    shootL3.whileTrue(m_ShooterSubsystem.new LevelShootCommand(m_ShooterSubsystem, 3));

    // Manual Intake Up/Down
    Trigger intakeMoveUp = m_operatorHID.axisLessThan(PS4Controller.Axis.kLeftY.value, -.3);
    Trigger intakeMoveDown = m_operatorHID.axisGreaterThan(PS4Controller.Axis.kLeftY.value, .3);
    //intakeMoveUp.whileTrue(new IntakeManualCommand(m_intake, true));
    //intakeMoveDown.whileTrue(new IntakeManualCommand(m_intake, false));

    Trigger clearButton = m_operatorHID.circle();

    clearButton.whileTrue(new InstantCommand(() -> m_intake.ClearStickies(), m_intake));

    // ======================================DRIVER CONTROLS======================================
    // create commands
    // 5 = left bumper
    // 6 = right bumper

    // Community Shot
    Trigger shootTrigger = m_driverHID.axisGreaterThan(XboxController.Axis.kLeftTrigger.value, Constants.DEADZONE);
    CommunityShotCommand com_shot_cmd = m_ShooterSubsystem.new CommunityShotCommand(m_ShooterSubsystem);
    shootTrigger.whileTrue(com_shot_cmd);

    // Flip Intake
    // Trigger flipTrigger = m_driverHID.leftBumper();
    // flipTrigger.onTrue(m_intake.new FlipIntake(m_intake));

    Trigger runIntakeForwardsTrigger = m_driverHID.rightTrigger(Constants.DEADZONE);
    runIntakeForwardsTrigger.whileTrue(new IntakeCommand(m_intake, true));

    // Reverse Intake/Midtake/Shooter
    Trigger runIntakeBackwardsTrigger = m_driverHID.rightBumper();
    runIntakeBackwardsTrigger.whileTrue(new IntakeCommand(m_intake, false)
        .alongWith(Commands.runEnd(
            () -> m_ShooterSubsystem.SetShooter(-.1),
            () -> m_ShooterSubsystem.SetShooter(0),
            m_ShooterSubsystem)));

    // Slow Shooter
    Trigger runInShooterSlowly = m_operatorHID.square();
    runInShooterSlowly.whileTrue(new IntakeCommand(m_intake, true)
        .alongWith(Commands.runEnd(
            () -> m_ShooterSubsystem.SetShooter(.2),
            () -> m_ShooterSubsystem.SetShooter(0),
            m_ShooterSubsystem)));

  }

  public static RobotContainer getInstance() {
    return m_robotContainer;
  }

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  private RobotContainer() {
    // choosing what auto
    m_autoChooser = new SendableChooser<Integer>();

    m_autoChooser.addOption("Shoot -> Move", 0);
    m_autoChooser.addOption("Shoot -> Autobalance", 1);
    m_autoChooser.addOption("DO NOTHING", 2);
    m_autoChooser.addOption("Exit Community + Balance", 3);
    m_autoChooser.addOption("Only Shoot Move", 4);
    m_customAuto = new SendableChooser<Boolean>();
    // integer representes which auto we get
    m_customChooser = new SendableChooser<Integer>();
    // chosing parameters of auto
    ShuffleboardTab m_autotab = Shuffleboard.getTab("Auto");
    m_customAuto.setDefaultOption("Default autos", false);
    m_customAuto.addOption("Custom auto", true);

    m_customChooser.addOption("Shoot->Move", 0);
    m_customChooser.addOption("Shoot->Autobalance", 1);

    autobal_leftSpeed = m_autotab.add("autobal leftSpeed", Constants.AUTO_LEFT_SPEED).getEntry();
    autobal_rightSpeed = m_autotab.add("autobal rightSpeed", Constants.AUTO_RIGHT_SPEED).getEntry();
    shoot_leftSpeed = m_autotab.add("shoot leftSpeed", Constants.SHOOT_LEFT_SPEED).getEntry();
    shoot_rightSpeed = m_autotab.add("shoot rightSpeed", Constants.SHOOT_RIGHT_SPEED).getEntry();
    shoot_time = m_autotab.add("Shoot time", Constants.SHOOT_SECONDS).getEntry();
    autobal_limit = m_autotab.add("ShootAutobalance Encoder Limit", Constants.AUTOBALANCE_MOVE_LIMIT).getEntry();
    shoot_limit = m_autotab.add("ShootMove EncoderLimit", Constants.SHOOT_MOVE_LIMIT).getEntry();

    m_autoChooser.addOption(("NO AUTO"), null);
    configureButtonBindings();
    ShuffleboardTab m_shuffleboard = Shuffleboard.getTab("Main");
    m_shuffleboard.add(m_autoChooser);
    m_autotab.add(m_customAuto);
    m_autotab.add(m_autoChooser);
    m_shuffleboard.addNumber("Encoder Left", () -> m_tankDriveSubsystem.getEncoderPositions()[0]);
    m_shuffleboard.addNumber("Encoder Right", () -> m_tankDriveSubsystem.getEncoderPositions()[1]);

  }

  public CommandGenericHID getController() {
    return m_driverHID;
  }

  public void ResetPositions() {
    m_intake.resetPosition();
  }

  public TankDriveSubsystem getDrive() {
    return m_tankDriveSubsystem;
  }
  public LEDsystem getLEDs(){
    return LEDstrips;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // check if custom auto
    // by defualt is set to false
    if (m_customAuto.getSelected()) {

      switch (m_customChooser.getSelected()) {
        // break statements unecessary due to return function
        case (0):
          return new shootMove(m_tankDriveSubsystem, m_ShooterSubsystem, m_intake,
              Constants.SHOOT_SECONDS,
              Constants.SHOOT_MOVE_LIMIT,
              Constants.SHOOT_LEFT_SPEED,
              Constants.SHOOT_RIGHT_SPEED);
        case (1):
          AHRS m_gyro = new AHRS(SPI.Port.kMXP);
          return new shootAutobalance(m_tankDriveSubsystem, m_ShooterSubsystem,
              Constants.SHOOT_SECONDS,
              Constants.AUTOBALANCE_MOVE_LIMIT,
              Constants.AUTO_LEFT_SPEED,
              Constants.AUTO_RIGHT_SPEED,
              m_gyro,
              m_intake);
      }
      // if nothing seletcted
      return null;
    } else {
      switch (m_autoChooser.getSelected()) {
        case (0):
          return new shootMove(m_tankDriveSubsystem, m_ShooterSubsystem, m_intake,
              Constants.SHOOT_SECONDS,
              Constants.SHOOT_MOVE_LIMIT,
              Constants.SHOOT_LEFT_SPEED,
              Constants.SHOOT_RIGHT_SPEED);
        case (1):
          AHRS m_gyro = new AHRS(SPI.Port.kMXP);
          return new shootAutobalance(m_tankDriveSubsystem, m_ShooterSubsystem,
              Constants.SHOOT_SECONDS,
              Constants.AUTOBALANCE_MOVE_LIMIT,
              Constants.AUTO_LEFT_SPEED,
              Constants.AUTO_RIGHT_SPEED,
              m_gyro,
              m_intake);
        case (2):
          return null;
        case (3):
          AHRS n_gyro = new AHRS(SPI.Port.kMXP);
          return new CommunityBalance(
              m_tankDriveSubsystem,
              m_ShooterSubsystem,
              Constants.SHOOT_SECONDS,
              Constants.AUTOBALANCE_MOVE_LIMIT,
              Constants.AUTO_LEFT_SPEED,
              Constants.AUTO_RIGHT_SPEED,
              n_gyro,
              m_intake);
        case (4):
            return new ShootMoveOnly(m_tankDriveSubsystem, m_ShooterSubsystem, m_intake,
            Constants.SHOOT_SECONDS,
            Constants.SHOOT_MOVE_LIMIT,
            Constants.SHOOT_LEFT_SPEED,
            Constants.SHOOT_RIGHT_SPEED); 
            }
      return null;
    }
  }
}