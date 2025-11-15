package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "segundo intake + shooter", group = "Linear Opmode")
public class segundointake extends LinearOpMode {

    private static final String MOTOR_NAME = "motor";
    private static final String MOTOR5_NAME = "motor5";
    private static final String MOTOR6_NAME = "motor6";

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotor controlMotor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
        DcMotor motor5 = hardwareMap.get(DcMotor.class, MOTOR5_NAME);
        DcMotor motor6 = hardwareMap.get(DcMotor.class, MOTOR6_NAME);

        controlMotor.setDirection(DcMotor.Direction.REVERSE);
        motor5.setDirection(DcMotor.Direction.FORWARD);
        motor6.setDirection(DcMotor.Direction.FORWARD);

        controlMotor.setPower(0.0);
        motor5.setPower(0.0);
        motor6.setPower(0.0);

        telemetry.addData("Status", "Ready - pressione START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            boolean l1 = gamepad1.left_bumper;  // controle do motor principal
            boolean bPressed = gamepad1.b;      // botão para acionar motor5 e motor6

            // Controle do motor principal (intake/shooter)
            if (l1) {
                controlMotor.setPower(1.0);
            } else {
                controlMotor.setPower(0.0);
            }

            // Controle dos motores 5 e 6
            if (bPressed) {
                motor5.setPower(1.0);
                sleep(2000); // espera 2 segundos
                motor6.setPower(1.0);
            } else {
                motor5.setPower(0.0);
                motor6.setPower(0.0);
            }

            telemetry.addData("L1 (motor)", l1);
            telemetry.addData("B (motor5 + motor6)", bPressed);
            telemetry.addData("Motor", controlMotor.getPower());
            telemetry.addData("Motor5", motor5.getPower());
            telemetry.addData("Motor6", motor6.getPower());
            telemetry.update();

            idle();
        }

        controlMotor.setPower(0.0);
        motor5.setPower(0.0);
        motor6.setPower(0.0);
    }
}
