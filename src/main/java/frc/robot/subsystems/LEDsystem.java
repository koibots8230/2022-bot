// RobotBuilder Version: 5.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package frc.robot.subsystems;

import frc.robot.Constants;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class LEDsystem extends SubsystemBase {
private AddressableLED strip;
private AddressableLEDBuffer buffer;
private Constants.color currentcolor= Constants.color.BLACK;
private boolean runningPattern = false;//are we running the moving pattern?
private Constants.moving currentPattern=Constants.moving.NONE;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    
    /**
    *
    */
    //constructor
    public LEDsystem(int port) {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        strip=new AddressableLED(port);
        buffer = new AddressableLEDBuffer(Constants.LED_STRIP_LENGTH);
        //defines how many LEDs are on the strip
        strip.setLength(buffer.getLength());
        strip.setData(buffer);
        // I'm not 100% what start actually does but the doc makes it seem needed.
        strip.start();
    }
    //static patterns:
    private final int[][]redAlliance={{255,165,0},{255,0,255},{255,0,0}};
    private final int[][]blueAlliance={{255,165,0},{255,0,255},{0,0,255}};
    //set LEDs static edition:
    public void setColor(Constants.color color){
        if(color==currentcolor&&!runningPattern){//no need to set the LEDs if we're just trying to set them to what they are.
            return;
        }
        switch(color){
            case PURPLE:
            setLEDs(255,0,255);
            break;
            case YELLOW:
            setLEDs(255,255,0);
            break;
            case BLACK:
            setLEDs(0, 0, 0);
            break;
            case GREEN:
            setLEDs(0,255,0);
            break;
            case ALLYR:
            setPattern(redAlliance);
            break;
            case ALLYB:
            setPattern(blueAlliance);
            break;
            case ORANGE:
            setLEDs(255,155,0);
            break;
            default:
            System.err.println("WARNING: attempted to set LEDs to magenta.");
            return;
        }
        currentcolor=color;
        runningPattern=false;
        strip.setData(buffer);
    }
    public void setLEDs(int R, int G, int B){//sets all the LEDs to the given RGB color
        for (var i = 0; i < buffer.getLength(); i++) {
            buffer.setRGB(i, R, G, B);
        }
    }
    public void setColor(Constants.moving pattern){
        if(pattern==currentPattern&&runningPattern){//no need to set the LEDs if we're just trying to set them to what they are.
            return;
        }
        switch(pattern){
            case DOT1:
            D1Start();
            break;
            case BAR:
            D1Start();//uses the same array, so no need to make a new function.
            break;
            case ALLYR:
            slidingPatternStart(redAlliance);
            break;
            case ALLYB:
            slidingPatternStart(blueAlliance);
            break;
            case NONE:
            //If a case doesn't have a specific function to handle it, don't put a break
            default:
            System.err.println("WARNING: Attempted to set a moving pattern without a proper pattern function.");
            return;
        }
        setLEDs(0, 0, 0);//clear LEDs
        currentPattern=pattern;
        runningPattern=true;
        //don't set data cause we can't change it yet.
    }
    public void setPattern(int[][] pattern){//the length of the inner arrays is always 3.
        if(Constants.LED_STRIP_LENGTH/pattern.length!=(double)Constants.LED_STRIP_LENGTH/pattern.length){
            //TL;DR of this if statement: is the number of LEDs divisible by the length of the pattern.
            System.err.println("WARNING: Pattern can not be displayed properly on LEDs");
            return;//might want to change this to throw an exception and have the original command catch it.
        }
        for (var i = 0; i < buffer.getLength(); i++) {//think this will work.
            int[] colors=pattern[i%(pattern.length)];
            buffer.setRGB(i, colors[0], colors[1], colors[2]);
        }
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation
        if(runningPattern){
            allPeriodics();
        }
    }
    
    @Override
    public void periodic() {
        // This method will be called once per scheduler run when IRL
        if(runningPattern){
            allPeriodics();
        }
        //do nothing if not running pattern.
    }
    public void allPeriodics(){
        //So that I only have to edit one function to make changes to the periodic functions
        switch(currentPattern){
            case DOT1:
            D1Period();
            break;
            case BAR:
            BarPeriod();
            break;
            case ALLYR:
            slidePeriod();
            break;
            case ALLYB:
            slidePeriod();
            break;
            case NONE:
            //If a case doesn't have a specific function to handle it, don't put a break
            default:
            System.err.println("WARNING: Attempted to execute a moving pattern without a proper pattern function");
            runningPattern=!runningPattern;//to stop the print message spaming.
        }
    }
    //moving pattern code

    //start pattern functions

    private int[]patternValues={};//use this to hold values needed to properly display moving patterns.
    public void D1Start(){//"start" functions set the array so that it can hold the values needed for the pattern.
        patternValues=new int[2];//appearently {0,0} is an array constant, and you can only use that to init an array.
        patternValues[0]=0;//timer, so that the dot doesn't move every
        patternValues[1]=0;
    }
    private int[][]slidingPatternState={};//needed for sliding patterns
    public void slidingPatternStart(int[][] pattern){//this start will be used by multiple functions.
        patternValues=new int[1];
        patternValues[0]=0;//timer
        slidingPatternState=pattern;
    }

    //start periodic functions

    public void D1Period(){//Period functions are the functions that handle the movement of the pattern.
        if(patternValues[0]<10){//periodic runs every 20 miliseconds
            patternValues[0]++;
            return;
        } else {
            patternValues[0]=0;
            buffer.setRGB(patternValues[1], 0, 0, 0);
            patternValues[1]++;
            if(!(patternValues[1]<buffer.getLength())){//is the value outside of the LED strip?
                patternValues[1]=0;
            }
            buffer.setRGB(patternValues[1], 255, 0, 0);
            strip.setData(buffer);//Don't forget to actually write the changes.
        }
    }
    public void BarPeriod(){
        if(patternValues[0]<10){//periodic runs every 20 miliseconds
            patternValues[0]++;
            return;
        } else {//first LED is off first time around, but after it goes through all the LEDs, it works. 
            patternValues[0]=0;
            patternValues[1]++;
            if(!(patternValues[1]<buffer.getLength())){//is the value outside of the LED strip?
                patternValues[1]=0;
                setLEDs(0,0,0);
            }
            buffer.setRGB(patternValues[1], 255, 0, 0);
            strip.setData(buffer);//Don't forget to actually write the changes.
        }
    }
    public void slidePeriod(){//for static patterns that slide.
        if(patternValues[0]<20){//periodic runs every 20 miliseconds
            patternValues[0]++;
            return;
        } else {
            patternValues[0]=0;
            int[] holdOverflow = slidingPatternState[slidingPatternState.length-1];//should be the last one.
            for(int i=slidingPatternState.length-1;i>0;i--){
                slidingPatternState[i]=slidingPatternState[i-1];
            }
            slidingPatternState[0]=holdOverflow;
            setPattern(slidingPatternState);
            strip.setData(buffer);//Don't forget to actually write the changes.
        }
    }

    //misc functions:

    public LEDsystem get(){
        return this;
    }

    //command(s)

    public class setLedColor extends CommandBase {
        private final LEDsystem sys;
        private final Constants.color setColor;
        private final Constants.moving movingPattern;
    
        public setLedColor(LEDsystem strips, Constants.color hold) {
            sys = strips;
            movingPattern = Constants.moving.NONE;
            setColor = hold;
            addRequirements(sys);
        }
        public setLedColor(LEDsystem strips, Constants.moving hold) {
            sys = strips;
            setColor = Constants.color.NONE;
            movingPattern = hold;
            addRequirements(sys);
        }
    
        // Called when the command is initially scheduled.
        @Override
        public void initialize() {
            if(setColor!=Constants.color.NONE){
                sys.setColor(setColor);
            } else {
                sys.setColor(movingPattern);
            }
        }
    
        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute() {
            // check for if one of the triggers is active
            // sys.setColor(setColor);
        }
    
        // Called once the command ends or is interrupted.
        @Override
        public void end(boolean interrupted) {
        }
    
        // Returns true when the command should end.
        @Override
        public boolean isFinished() {
            return true;
        }
    
        @Override
        public boolean runsWhenDisabled() {
            return false;
        }
    }
    
}


