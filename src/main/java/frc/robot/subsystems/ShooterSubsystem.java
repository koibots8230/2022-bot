package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.DoubleSupplier;

import org.photonvision.EstimatedRobotPose;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
  public static Pose3d Bot3d = null;
  public static boolean VariablesDefined = false;
  public static Translation3d Closest = new Translation3d(0, 0, 0);
  public static double ClosestDistance = 0;
  private int count = 0;
  private int ShootLevel = 2;
  private double xDistance;
  private double yDistance;
  private double distance;
  private Translation2d Spot = new Translation2d(0, 0);

  private CANSparkMax shooterMotor1;
  private CANSparkMax shooterMotor2;

  public ShooterSubsystem() {
    shooterMotor1 = new CANSparkMax(Constants.kSHOOTER_MOTOR_PORT_1, MotorType.kBrushless);
    shooterMotor2 = new CANSparkMax(Constants.kSHOOTER_MOTOR_PORT_2, MotorType.kBrushless);
  }

  public void SetShooter(double Speed) {
    shooterMotor1.set(Speed);
    shooterMotor2.set(-Speed);
  }

  @Override
  public void periodic() {
    // TODO: What if we want to turn this off?
    if (count == 10) { // TODO: Make this a constant that we can adjust as part of the class
      count = 0;
      // Optional<EstimatedRobotPose> BotThing = VisionSubsystem.photonPoseEstimator.update();
      // if (BotThing.isPresent()) {
        //Bot3d = BotThing.get().estimatedPose;
        Bot3d = new Pose3d(0.0, 0.0, 0.0, new Rotation3d(0.0, 0.0, 0.0));
        VariablesDefined = true;
        for (int a = 0; a < 3; a++) {
          if (ShootLevel == 2) {
            Spot = Constants.MIDDLE_SPOTS.get(a).toTranslation2d();
          } else {
            Spot = Constants.HIGH_SPOTS.get(a).toTranslation2d();
          }
          xDistance = Bot3d.getX() - Spot.getX();
          yDistance = Bot3d.getY() - Spot.getY();
          distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
          if (distance < ClosestDistance) {
            ClosestDistance = distance;
            Closest = Constants.MIDDLE_SPOTS.get(a-1);
          }
        }
      //} else {
        //VariablesDefined = false;
      //}
    } else {
      count++;
    }
  }

  public class CommunityShotCommand extends CommandBase {
    private DoubleSupplier m_Trigger;

    public CommunityShotCommand(DoubleSupplier Trigger, ShooterSubsystem m_ShooterSubsystem) {
      m_Trigger = Trigger;
      addRequirements(m_ShooterSubsystem);
    }

    @Override
    public void initialize() {
      if (m_Trigger.getAsDouble() > Constants.DEADZONE) {
        SetShooter(.75);
      }
    }

    @Override
    public void end(boolean interrupted) {
      SetShooter(0);
    }
  }

  @Override
  public void simulationPeriodic() {

  }

  // Put methods for controlling this subsystem
  // here. Call these from Commands.
}