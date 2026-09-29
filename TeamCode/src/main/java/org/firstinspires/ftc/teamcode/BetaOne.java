package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This OpMode also includes controls for auxiliary motors (motorA and motorB) and a CRServo (continuous rotation servo).
 * - Left bumper: Controls motorA
 * - Right bumper: Controls motorB
 * - A button: CRServo at maximum speed (forward rotation)
 * - Right joystick Y: Controls CRServo speed (forward/backward continuous rotation) when A button is not pressed
 */

@TeleOp(name="BetaOne", group="Linear OpMode")
public class BetaOne extends LinearOpMode {

    // Declare OpMode members for drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    // Declare OpMode members for auxiliary motors
    private DcMotor motorA = null;
    private DcMotor motorB = null;
    private DcMotor motorC = null;

    // Declare OpMode members for continuous rotation servo
    private CRServo servo = null;

    // Declare OpMode members for timing
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_motor");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_motor");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_motor");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_motor");

        motorA = hardwareMap.get(DcMotor.class, "motorA");
        motorB = hardwareMap.get(DcMotor.class, "motorB");
        motorC = hardwareMap.get(DcMotor.class, "motorC");

        servo = hardwareMap.get(CRServo.class, "servo");

        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        // Configure drive motors: Set to run without encoders and brake when power is zero
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Configure auxiliary motors
        motorA.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorA.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorA.setPower(0);

        motorB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorB.setPower(0);

        motorC.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorC.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorC.setPower(0);

        // Initialize CRServo to stopped (0.0 = stopped for most CRServos)
        servo.setPower(0.0);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Aguardando start...");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Dead zone constant: ignore small joystick values to prevent drift
        final double deadZone = 0.20;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Apply dead zone: ignore small joystick values to prevent drift
            if (Math.abs(axial) < deadZone) axial = 0.0;
            if (Math.abs(lateral) < deadZone) lateral = 0.0;
            if (Math.abs(yaw) < deadZone) yaw = 0.0;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            // Control auxiliary motor A with left bumper
            if (gamepad1.left_bumper) {
                motorA.setPower(-1.0);
            } else {
                motorA.setPower(0);
            }


            // Control auxiliary motor B with right bumper
            if (gamepad1.right_bumper) {
                motorB.setPower(-1.0);
            } else {
                motorB.setPower(0.0);
            }

            // Control motor C with button B
            if (gamepad1.b) {
                motorC.setPower(-1.0);
            } else {
                motorC.setPower(0.0);
            }

            // Control CRServo: Button A = maximum speed, otherwise stopped
            // For CRServo: 0.0 = stopped, 1.0 = max speed one direction, -1.0 = max speed other direction
            if (gamepad1.a) {
                // Button A pressed: maximum speed forward
                servo.setPower(1.0);
            } else {
                // Button A not pressed: stopped
                servo.setPower(0.0);
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Motor A", motorA.getPower() != 0 ? "ON" : "OFF");
            telemetry.addData("Motor B", motorB.getPower() != 0 ? "ON" : "OFF");
            telemetry.addData("Motor C", motorC.getPower() != 0 ? "ON" : "OFF");
            telemetry.addData("CRServo Power", "%.2f", servo.getPower());
            telemetry.update();
        }
    }
}