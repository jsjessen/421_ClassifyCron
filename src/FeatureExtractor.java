package com.ireach;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.*;

public class FeatureExtractor
{
    public RawData data;
    public DataSource source;
    public Instances instances;

    public FeatureExtractor(RawData raw_data)
    {
        data = raw_data;

        try
        {
        source = new DataSource("file.csv");
        instances = source.getDataSet();
        }
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }
}
