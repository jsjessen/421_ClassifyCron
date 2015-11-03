package com.ireach;

import java.util.List;
import java.util.ArrayList;

public class RawData
{
    public List<Double> Acc_x;
    public List<Double> Acc_y;
    public List<Double> Acc_z;
    public List<Double> Gyro_x;
    public List<Double> Gyro_y;
    public List<Double> Gyro_z;
    public List<Double> Mag_x;
    public List<Double> Mag_y;
    public List<Double> Mag_z;

    public RawData()
    {
        Acc_x = new ArrayList<Double>();
        Acc_y = new ArrayList<Double>();
        Acc_z = new ArrayList<Double>();
        Gyro_x = new ArrayList<Double>();
        Gyro_y = new ArrayList<Double>();
        Gyro_z = new ArrayList<Double>();
        Mag_x = new ArrayList<Double>();
        Mag_y = new ArrayList<Double>();
        Mag_z = new ArrayList<Double>();
    }
}
