package com.ireach;

public class Activity
{
    public final static String names[] = 
    { 
        "Sitting with Hands on Lap",
        "Sitting while Writing",
        "Siting while Typing",
        "Lying Down on Back",
        "Lying on Right Side",
        "Jumping Jacks",
        "Butt Kickers",
        "High Knees",
        "Flutter Kicks",
        "Lunges",
        "Crunches",
        "Squats",
        "Pushups",
        "Walking Up Stairs",
        "Walking Down Stairs",
        "Standing with Arms at Sides",
        "Standing with Arms Crossed",
        "Slow Walk",
        "Normal Walk",
        "Fast Walk",
        "Jogging",
        "Running",
        "Biking",
        "Dips" 
    };

    public final static double mets[] = 
    { 
        1.3, 
        1.8, 1.8,  
        1, 1,  
        7, 7, 7, 7, 7, 7, 7, 7,  
        2.5, 2.5,  
        1.3, 1.3,  
        2.5,  
        3, 
        5, 
        8, 
        10, 
        6, 
        7 
    };

    private final static double poundsPerKilogram = 2.2;
    private final static int secondsPerHour = 60 * 60;

    public final String name;
    public final double met;
    public double duration;
    public double calories;

    Activity(int code)
    {
        name = names[code];
        met = mets[code];
        duration = 0.0;
        calories = 0.0;
    }

    public void updateDuration(double dur)
    {
        duration += dur;
    }

    public void updateCalories(int weight)
    {
        // Use MET? http://www.topendsports.com/weight-loss/energy-met.htm
        // MET FORMULA! Weight(in kgs) * MET * time/1hour
        calories = (weight / poundsPerKilogram) * met * (duration / secondsPerHour);
    }
}
