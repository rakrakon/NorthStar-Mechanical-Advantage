package org.littletonrobotics.frc2024.subsystems.superstructure.flywheels;

import static org.littletonrobotics.frc2024.subsystems.superstructure.SuperstructureConstants.FlywheelConstants.*;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.frc2024.util.LoggedTunableNumber;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;

public class Flywheels extends SubsystemBase {
    private static final LoggedTunableNumber kP = new LoggedTunableNumber("Flywheels/kP", gains.kP());
    private static final LoggedTunableNumber kI = new LoggedTunableNumber("Flywheels/kI", gains.kI());
    private static final LoggedTunableNumber kD = new LoggedTunableNumber("Flywheels/kD", gains.kD());
    private static final LoggedTunableNumber kS = new LoggedTunableNumber("Flywheels/kS", gains.kS());
    private static final LoggedTunableNumber kV = new LoggedTunableNumber("Flywheels/kV", gains.kV());
    private static final LoggedTunableNumber kA = new LoggedTunableNumber("Flywheels/kA", gains.kA());
    private static final LoggedTunableNumber shooterTolerance =
            new LoggedTunableNumber("Flywheels/ToleranceRPM", shooterToleranceRPM);

    private final FlywheelsIO io;
    private final FlywheelsIOInputsAutoLogged inputs = new FlywheelsIOInputsAutoLogged();

    private Double leftSetpointRpm = null;
    private Double rightSetpointRPM = null;

    public Flywheels(FlywheelsIO io) {
        System.out.println("[Init] Creating Shooter");
        this.io = io;
    }

    @Override
    public void periodic() {
        // check controllers
        LoggedTunableNumber.ifChanged(hashCode(), pid -> io.setPID(pid[0], pid[1], pid[2]), kP, kI, kD);
        LoggedTunableNumber.ifChanged(
                hashCode(), kSVA -> io.setFF(kSVA[0], kSVA[1], kSVA[2]), kS, kV, kA);

        io.updateInputs(inputs);
        Logger.processInputs("Flywheels", inputs);

        if (DriverStation.isDisabled()) {
            io.stop();
            leftSetpointRpm = null;
            rightSetpointRPM = null;
        } else if (leftSetpointRpm != null && rightSetpointRPM != null) {
            // Run to setpoint
            io.runVelocity(leftSetpointRpm, rightSetpointRPM);
        }

        Logger.recordOutput(
                "Flywheels/LeftSetpointRPM", leftSetpointRpm != null ? leftSetpointRpm : 0.0);
        Logger.recordOutput(
                "Flywheels/RightSetpointRPM", rightSetpointRPM != null ? rightSetpointRPM : 0.0);
        Logger.recordOutput("Flywheels/LeftRPM", inputs.leftVelocityRpm);
        Logger.recordOutput("Flywheels/RightRPM", inputs.rightVelocityRpm);
    }

    public void setSetpointRpm(double leftRpm, double rightRpm) {
        leftSetpointRpm = leftRpm;
        rightSetpointRPM = rightRpm;
    }

    public void runVolts(double leftVolts, double rightVolts) {
        leftSetpointRpm = null;
        rightSetpointRPM = null;
        io.runVolts(leftVolts, rightVolts);
    }

    public void runLeftCharacterizationVolts(double volts) {
        io.runCharacterizationLeftVolts(volts);
    }

    public void runRightCharacterizationVolts(double volts) {
        io.runCharacterizationRightVolts(volts);
    }

    public double getLeftCharacterizationVelocity() {
        return inputs.leftVelocityRpm;
    }

    public double getRightCharacterizationVelocity() {
        return inputs.rightVelocityRpm;
    }

    @AutoLogOutput(key = "Shooter/AtSetpoint")
    public boolean atSetpoint() {
        return leftSetpointRpm != null
                && rightSetpointRPM != null
                && Math.abs(inputs.leftVelocityRpm - leftSetpointRpm) <= shooterTolerance.get()
                && Math.abs(inputs.rightVelocityRpm - rightSetpointRPM) <= shooterTolerance.get();
    }
}