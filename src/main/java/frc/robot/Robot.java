// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
/*wolissss */

public class Robot extends TimedRobot {
  
/******************************* CHASSIS *********************************/

CANSparkMax chassis_right_front = new CANSparkMax(1, MotorType.kBrushless); 
CANSparkMax chassis_right_back = new CANSparkMax(2, MotorType.kBrushless); 
CANSparkMax chassis_left_front = new CANSparkMax(3, MotorType.kBrushless); 
CANSparkMax chassis_left_back = new CANSparkMax(4, MotorType.kBrushless); 

AHRS navx = new AHRS (SPI.Port.kMXP); 

RelativeEncoder encoder_chassis_right = chassis_right_front.getEncoder(); 
RelativeEncoder encoder_chassis_left = chassis_left_front.getEncoder(); 

DifferentialDrive chassis = new DifferentialDrive(chassis_right_front, chassis_left_front); 


/******************************* WRIST *************************************/

Solenoid solenoid_wrist = new Solenoid(PneumaticsModuleType.REVPH, 1); 

/******************************* INTAKE *************************************/

Solenoid solenoid_intake = new Solenoid(PneumaticsModuleType.REVPH, 2); 

CANSparkMax motor_intake_right = new CANSparkMax(5, MotorType.kBrushless); 
CANSparkMax motor_intake_left = new CANSparkMax(6, MotorType.kBrushless); 

/******************************* ELEVATOR ********************************/

CANSparkMax motor_elevator_right = new CANSparkMax(7, MotorType.kBrushless); 
CANSparkMax motor_elevator_left = new CANSparkMax(8, MotorType.kBrushless); 

RelativeEncoder encoder_elevator = motor_elevator_right.getEncoder(); 

/***************************JOYSTICKS *****************************************/

Joystick controlDriver = new Joystick(0);
Joystick controlPlacer = new Joystick(1); 

/****************************** VARIABLES ******************************/

  boolean isTank = true; 
  boolean middleStage = false; 
  boolean upperStage = false; 
  boolean wristUp = true;  


  @Override
  public void robotInit() {

    chassis_right_back.follow(chassis_right_front); 
    chassis_left_back.follow(chassis_left_front); 

    encoder_chassis_right.setPosition(0);
    encoder_chassis_left.setPosition(0);

    navx.reset();

    solenoid_wrist.set(true); /* true es arriba */
    solenoid_intake.set(false); /* true es abierta */

    motor_intake_right.setInverted(true); 
    motor_intake_left.setInverted(! motor_intake_right.getInverted() );
    motor_intake_left.follow(motor_intake_right); 

    encoder_elevator.setPosition(0);
    motor_elevator_right.setInverted(true);
    motor_elevator_left.setInverted(! motor_elevator_right.getInverted() ); 
    motor_elevator_left.follow(motor_elevator_right); 

    isTank = true; 

   

  }

  public void stages() {

    double armPosition = encoder_elevator.getPosition();

    if (middleStage == true) {

      if (armPosition > 21) {
        motor_elevator_right.set(-0.6);
      } else if (armPosition < 21) {
        motor_elevator_right.set(0.6);
      }
                /*Drop it */
      if (solenoid_wrist.get() == true ) {
        solenoid_wrist.set(false);
      }
      if (solenoid_intake.get() == false) {
        solenoid_intake.set(true);
      }

    } else if (middleStage == false) {

      if (armPosition < 42) {
        motor_elevator_right.set(0.6);
      }
              /*Drop it */
      if (solenoid_wrist.get() == true ) {
        solenoid_wrist.set(false);
      }
      if (solenoid_intake.get() == false) {
        solenoid_intake.set(true);
      }
     
    } else {
      motor_elevator_right.set(0);
    }

  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {

    /* ARCADE OR TANK */

    if (controlDriver.getRawButton(1)) {
      isTank= true;
    } else if (controlDriver.getRawButton(2)){
      isTank = false; 
    }

    if (isTank== true) {
      chassis.tankDrive( controlDriver.getRawAxis(1) * .7 , controlDriver.getRawAxis(4) * .7);
    } else {
      chassis.arcadeDrive( controlDriver.getRawAxis(1) * .7, controlDriver.getRawAxis(4) * .7);
    }
  
    /* INTAKE */

    if (controlPlacer.getRawButton(5)) {

      if (encoder_elevator.getPosition() > 0) {
        motor_elevator_right.set(-0.6);
      }
      if (solenoid_wrist.get() == false) {
        solenoid_wrist.set(true);
      }
      if (solenoid_intake.get() == false) {
        solenoid_intake.set(true);
      }

      motor_intake_right.set(.8);

    } 

    else {
      motor_intake_right.set(0);
    } 

    /* WRIST UP */

    if (control.getRawButton(1)) {

      solenoid_wrist.set (true); 

    }

    /* PLACE */

    if (controlPlacer.getRawButton(7)) {
      middleStage = true; 
      stages();
    }
    else if (controlPlacer.getRawButton(8)) {
      middleStage = false; 
      stages();
    }


  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}


