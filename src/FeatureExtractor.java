package com.ireach;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.*;

public class FeatureExtractor
{
    public RawData data;
    public DataSource source;
    public Instances instances;

    public FeatureExtractor(RawData data)
    {
       // I appologize for the lack of commenting, but I had to do this last minute
					   //storage class for features
					   RawData features = null;
					   for(int i = 0; i < data.Acc_x.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Acc_x.get(j));
							   squares += data.Acc_x.get(j) * data.Acc_x.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Acc_x.add(minimum);
						   features.Acc_x.add(maximum);
						   features.Acc_x.add(range);
						   features.Acc_x.add(mean);
						   features.Acc_x.add(median);
						   features.Acc_x.add(amplitude);
						   features.Acc_x.add(std_dev);
						   features.Acc_x.add(variance);
						   features.Acc_x.add(rms);
					   }
					   for(int i = 0; i < data.Acc_y.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Acc_y.get(j));
							   squares += data.Acc_y.get(j) * data.Acc_y.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Acc_y.add(minimum);
						   features.Acc_y.add(maximum);
						   features.Acc_y.add(range);
						   features.Acc_y.add(mean);
						   features.Acc_y.add(median);
						   features.Acc_y.add(amplitude);
						   features.Acc_y.add(std_dev);
						   features.Acc_y.add(variance);
						   features.Acc_y.add(rms);
					   }
					   for(int i = 0; i < data.Acc_z.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Acc_z.get(j));
							   squares += data.Acc_z.get(j) * data.Acc_z.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Acc_z.add(minimum);
						   features.Acc_z.add(maximum);
						   features.Acc_z.add(range);
						   features.Acc_z.add(mean);
						   features.Acc_z.add(median);
						   features.Acc_z.add(amplitude);
						   features.Acc_z.add(std_dev);
						   features.Acc_z.add(variance);
						   features.Acc_z.add(rms);
					   }
					   for(int i = 0; i < data.Gyro_x.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Gyro_x.get(j));
							   squares += data.Gyro_x.get(j) * data.Gyro_x.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Gyro_x.add(minimum);
						   features.Gyro_x.add(maximum);
						   features.Gyro_x.add(range);
						   features.Gyro_x.add(mean);
						   features.Gyro_x.add(median);
						   features.Gyro_x.add(amplitude);
						   features.Gyro_x.add(std_dev);
						   features.Gyro_x.add(variance);
						   features.Gyro_x.add(rms);
					   }
					   for(int i = 0; i < data.Gyro_y.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Gyro_y.get(j));
							   squares += data.Gyro_y.get(j) * data.Gyro_y.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Gyro_y.add(minimum);
						   features.Gyro_y.add(maximum);
						   features.Gyro_y.add(range);
						   features.Gyro_y.add(mean);
						   features.Gyro_y.add(median);
						   features.Gyro_y.add(amplitude);
						   features.Gyro_y.add(std_dev);
						   features.Gyro_y.add(variance);
						   features.Gyro_y.add(rms);
					   }
					   for(int i = 0; i < data.Gyro_z.size(); i += 150)
					   {
						   double maximum = 0;
						   double minimum = 1000;
						   double range = 0;
						   double mean = 0;
						   double median = 0;
						   double amplitude = 0;
						   double std_dev = 0;
						   double variance = 0;
						   double rms = 0;
						   double squares = 0;
						   List<Double> partial_list = null;
						   for(int j = i ; j< i + 200; j++)
						   {
							   partial_list.add(data.Gyro_z.get(j));
							   squares += data.Gyro_z.get(j) * data.Gyro_z.get(j);
						   }
						   //sort list
						   Collections.sort(partial_list);
						   //get the minimum a.k.a. the first item
						   minimum = partial_list.get(0);
						   //get the maximum a.k.a. the last item
						   maximum = partial_list.get(partial_list.size());
						   //get sum for mean
						   double sum = partial_list.stream().mapToDouble(Double::doubleValue).sum();
						   //get actual Mean
						   mean = sum / (double)partial_list.size();
						   //get range
						   range = maximum - mean;
						   //get amplitude
						   amplitude = maximum - minimum;
						   //get median
						   if(partial_list.size() % 2 == 0)
						   {
							   median = partial_list.get(partial_list.size()/2);
						   }
						   else
						   {
							   median = (partial_list.get(partial_list.size()/2) + partial_list.get(partial_list.size()/2 -1))/2;
						   }
						   //get Variance
						   variance = (squares/ partial_list.size()) - (mean * mean);
						   //get standard deviation
						   std_dev = Math.sqrt(variance);
						   //get RMS
						   rms = Math.sqrt(squares/ partial_list.size());
						   //Now load features into an instance or a FeatureExtractor
						   features.Gyro_z.add(minimum);
						   features.Gyro_z.add(maximum);
						   features.Gyro_z.add(range);
						   features.Gyro_z.add(mean);
						   features.Gyro_z.add(median);
						   features.Gyro_z.add(amplitude);
						   features.Gyro_z.add(std_dev);
						   features.Gyro_z.add(variance);
						   features.Gyro_z.add(rms);
					   }
    }
}
