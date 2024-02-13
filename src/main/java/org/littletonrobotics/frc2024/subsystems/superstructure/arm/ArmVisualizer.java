package org.littletonrobotics.frc2024.subsystems.superstructure.arm;

import static org.littletonrobotics.frc2024.subsystems.superstructure.arm.ArmConstants.*;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.littletonrobotics.junction.Logger;

public class ArmVisualizer {
  private final Mechanism2d mechanism;
  private final MechanismRoot2d root;
  private final MechanismLigament2d arm;
  private final String key;

  public ArmVisualizer(String key, Color color) {
    this.key = key;
    mechanism = new Mechanism2d(3.0, 3.0, new Color8Bit(Color.kAntiqueWhite));
    root = mechanism.getRoot("pivot", 1.0, 0.4);
    arm = new MechanismLigament2d("arm", armLength, 20.0, 6, new Color8Bit(color));
    root.append(arm);
  }

  /** Update arm visualizer with current arm angle */
  public void update(Rotation2d angle) {
    arm.setAngle(angle);
    Logger.recordOutput("Arm/" + key + "Mechanism2d", mechanism);

    // Log 3d poses
    Pose3d pivot =
        new Pose3d(
            armOrigin.getX(), 0.0, armOrigin.getY(), new Rotation3d(0.0, -angle.getRadians(), 0.0));
    Logger.recordOutput("Arm/" + key + "3d", pivot);
  }
}