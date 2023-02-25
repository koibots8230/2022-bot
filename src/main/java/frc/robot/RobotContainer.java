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
import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeSubsystem.SwitchIntakeDirection;
import frc.robot.subsystems.TankDriveSubsystem.SwitchDrivetrainInvert;
import frc.robot.commands.IntakeCommand;


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
    private static MiscDashboardSubsystem m_miscDashboardSubsystem = new MiscDashboardSubsystem();

    // other stuff
    private final CommandGenericHID m_driverHID = new CommandGenericHID(0);
    private final CommandGenericHID m_operatorHID = new CommandGenericHID(1);

    // Commands
    private final TankDriveSubsystem.driveMotorCommand m_driveCommand = m_tankDriveSubsystem.new driveMotorCommand(
            () -> m_driverHID.getRawAxis(1),
            () -> m_driverHID.getRawAxis(5),
            m_tankDriveSubsystem);

    private final TankDriveSubsystem.driveMotorCommand m_operatorDrive = m_tankDriveSubsystem.new driveMotorCommand(
            () -> m_operatorHID.getRawAxis(1),
            () -> m_operatorHID.getRawAxis(5),
            m_tankDriveSubsystem);

    // Drivetrain is reversed when button A is pressed on the controller:
    SwitchDrivetrainInvert m_SwitchDrivetrainInvertCommand = m_tankDriveSubsystem.new SwitchDrivetrainInvert(
            m_tankDriveSubsystem);
    Trigger invertDrivetrainTrigger = m_driverHID.button(1)
            .onTrue(m_tankDriveSubsystem.new SwitchDrivetrainInvert(m_tankDriveSubsystem));

    SendableChooser<Command> m_autoChooser = new SendableChooser<>();
    SendableChooser<String> m_driverChooser = new SendableChooser<>();

    // m_controllerType 0 -> Unrecognized
    // m_controllerType 1 -> Xbox Controller
    // m_controllerType 2 -> Playstation Controller
    // m_controllerType 3 -> Flight Joystick
    int m_controllerType;

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    private RobotContainer() {
        configureButtonBindings();

        int pairButton;
        String hidType = m_driverHID.getHID().getName();
        if (hidType.equals("")) { // Xbox Controller | Name Unknown
            m_controllerType = 1;
            pairButton = 7;
        } else if (hidType.equals("Wireless Controller")) { // PS5 | Is still called "Wireless Controller" if plugged in with a wire.
            m_controllerType = 2;
            pairButton = 7;
        } else {
            m_controllerType = 0;
            pairButton = 7;
        }


        // ==================OPERATOR CONTROLS======================================

        // Create Triggers here | Triggers should be named t_CommandName
        Trigger leftTrigger = m_operatorHID.axisGreaterThan(3, Constants.DEADZONE);
        Trigger rightTrigger = m_operatorHID.axisGreaterThan(4, Constants.DEADZONE);

        Trigger operatorDriveTrigger = m_operatorHID.axisGreaterThan(1, Constants.DEADZONE);
        operatorDriveTrigger.onTrue(m_operatorDrive);

        Trigger operatorSpeedUp = m_operatorHID.button(2);
        Trigger operatorSpeedDown = m_operatorHID.button(3);
        operatorSpeedUp.onTrue(new setSpeedCommand(true, m_tankDriveSubsystem));
        operatorSpeedDown.onTrue(new setSpeedCommand(false, m_tankDriveSubsystem));

        Trigger intakeMoveUp = m_operatorHID.axisGreaterThan(1, Constants.DEADZONE);
        Trigger intakeMoveDown = m_operatorHID.axisLessThan(1, -Constants.DEADZONE);
        intakeMoveUp.whileTrue(new InstantCommand(() -> m_intake.setRaiseIntakeSpeed(0.1), m_intake));
        intakeMoveDown.whileTrue(new InstantCommand(() -> m_intake.setRaiseIntakeSpeed(-0.1), m_intake));
        intakeMoveUp.or(intakeMoveDown).onFalse(new InstantCommand(() -> m_intake.setRaiseIntakeSpeed(0), m_intake));

        // ================DRIVER CONTROLS==========================================
        // create commands
        // 5 = left bumper
        // 6 = right bumper

        // Intake is toggled when left bumper is pressed
        Trigger flipTrigger = m_driverHID.button(5);
        flipTrigger.onTrue(m_intake.new FlipIntake(m_intake));

        // Intake runs FORWARD when right trigger is pressed
        Trigger runIntakeForwardsTrigger = m_driverHID.axisGreaterThan(3, Constants.DEADZONE);
        runIntakeForwardsTrigger.whileTrue(new IntakeCommand(m_intake, true));

        // Intake runs BACKWARD when right bumper is pressed
        Trigger runIntakeBackwardsTrigger = m_driverHID.button(6);
        runIntakeBackwardsTrigger.whileTrue(new IntakeCommand(m_intake, false));

        // Trigger to reset the controls
        Trigger resetControls = m_driverHID.button(pairButton);
        resetControls.whileTrue(new setupControls());
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
}
