package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Beto Completo", group="Linear OpMode")
public class completeBeto extends LinearOpMode {

    // Motores do chassi
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    // Motores extras (para R1 e L1)
    private DcMotor motorA, motorB;

    @Override
    public void runOpMode() {

        // Configuração do hardware (nomes iguais aos do Robot Configuration)
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRight = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backLeft = hardwareMap.get(DcMotor.class, "backLeftDrive");
        backRight = hardwareMap.get(DcMotor.class, "backRightDrive");

        motorA = hardwareMap.get(DcMotor.class, "motorA");
        motorB = hardwareMap.get(DcMotor.class, "motorB");

        // Direções (ajuste se o robô estiver invertido)
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Zera potências
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        motorA.setPower(0);
        motorB.setPower(0);

        telemetry.addLine("Aguardando start...");
        telemetry.update();

        waitForStart();

        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive()) {

            // ---- CONTROLE DO CHASSIS OMNI ----
            double y = -gamepad1.left_stick_y; // frente/trás
            double x = gamepad1.left_stick_x;  // lateral
            double rx = gamepad1.right_stick_x; // rotação

            double frontLeftPower = y + x + rx;
            double backLeftPower = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower = y + x - rx;

            // Normaliza se necessário
            double max = Math.max(Math.abs(frontLeftPower),
                    Math.max(Math.abs(backLeftPower),
                            Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))));
            if (max > 1.0) {
                frontLeftPower /= max;
                backLeftPower /= max;
                frontRightPower /= max;
                backRightPower /= max;
            }

            // Aplica potência
            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // ---- CONTROLE DOS MOTORES EXTRAS ----

            // Quando R1 é pressionado -> motorA liga
            if (gamepad1.left_bumper) {
                motorA.setPower(-1.0);
            } else {
                motorA.setPower(0);
            }

            // Quando L1 é pressionado -> sequência temporizada
            if (gamepad1.right_bumper) {
                // Executa a sequência apenas uma vez enquanto o botão estiver pressionado
                timer.reset();

                // Liga motorA imediatamente
                motorA.setPower(-1.0);

                // Espera 2 segundos e liga motorB
                while (timer.seconds() < 2 && opModeIsActive()) {
                    idle();
                }
                motorB.setPower(-1.0);

                // Espera até 5 segundos no total, depois desliga os dois
                while (timer.seconds() < 5 && opModeIsActive()) {
                    idle();
                }
                motorA.setPower(0);
                motorB.setPower(0);
            }

            telemetry.addData("Tempo", "%.1f s", timer.seconds());
            telemetry.update();
        }
    }
}