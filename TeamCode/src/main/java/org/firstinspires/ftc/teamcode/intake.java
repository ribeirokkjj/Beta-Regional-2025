package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "intake", group = "Linear Opmode")
public class intake extends LinearOpMode {

    private static final String MOTOR_NAME = "motor";

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor controlMotor = hardwareMap.get(DcMotor.class, MOTOR_NAME);

        controlMotor.setDirection(DcMotor.Direction.REVERSE);
        controlMotor.setPower(0.0);

        telemetry.addData("Status", "Ready - pressione START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean l1 = gamepad1.left_bumper;  // L1

            if (l1) {
                controlMotor.setPower(1.0); // gira no máximo enquanto L1 está pressionado
            } else {
                controlMotor.setPower(0.0); // para quando soltar
            }

            telemetry.addData("L1 (motor)", l1);
            telemetry.addData("Motor power", controlMotor.getPower());
            telemetry.update();

            idle();
        }

        controlMotor.setPower(0.0);
    }
}
