package com.ireach;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.*;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.UnknownHostException;

public class ClassifyCron 
{
    public static void main(String[] args)
    {
        MongoClient mongoClient = null;
        String database_name = "m3";
        String collection_name = "raw_datas";

        //Connect to the Server
        try
        {
            mongoClient = new MongoClient();
        } 
        catch (UnknownHostException e) 
        {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        Classifier Node_loc = null;
        Classifier One = null;
        Classifier Two = null;
        Classifier Three = null;
        Classifier Four = null;
        Classifier Five = null;
        Classifier Six = null;
        Classifier Seven = null;
        Classifier Eight = null;
        Classifier Nine = null;
        Classifier Ten = null;
        try
        {
            //load the classifiers
            Node_loc = (Classifier) weka.core.SerializationHelper.read("../models/NodeLoc.model");
            One = (Classifier) weka.core.SerializationHelper.read("../models/ActRec1.model");
            Two = (Classifier) weka.core.SerializationHelper.read("../models/ActRec2.model");
            Three = (Classifier) weka.core.SerializationHelper.read("../models/ActRec3.model");
            Four = (Classifier) weka.core.SerializationHelper.read("../models/ActRec4.model");
            Five = (Classifier) weka.core.SerializationHelper.read("../models/ActRec5.model");
            Six = (Classifier) weka.core.SerializationHelper.read("../models/ActRec6.model");
            Seven = (Classifier) weka.core.SerializationHelper.read("../models/ActRec7.model");
            Eight = (Classifier) weka.core.SerializationHelper.read("../models/ActRec8.model");
            Nine = (Classifier) weka.core.SerializationHelper.read("../models/ActRec9.model"); 
            Ten = (Classifier) weka.core.SerializationHelper.read("../models/ActRec10.model");
        }
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }

        //Get Database
        DB db = mongoClient.getDB(database_name);

        //Get The Desired Collection
        DBCollection coll = db.getCollection(collection_name);

        //Find all collections that have not been processed
        BasicDBObject query = new BasicDBObject("processed", false);

        //Define what we actually want from the collection
        /*
           TO DO:::::::
           Find what feature is shared between the raw_datas collection and the other collection
           .append("feature",1)
           FIXED!!!!!!!!
           Removed field to get all features.
           */

        //BasicDBObject field = new BasicDBObject("Acc.x", 1).append("_id", 0).append("Acc.y", 1).append("Acc.z", 1).append("Gyro.x", 1).append("Gyro.y", 1).append("Gyro.z", 1).append("Mag.x", 1).append("Mag.y", 1).append("Mag.z", 1);
        //Actually query to the collection to find what we want
        DBCursor cursor = coll.find(query);

        //Iterate through collections
        try 
        {
            while(cursor.hasNext()) 
            {
                HashMap<Integer,Double> Activities = new HashMap<Integer,Double>();
                RawData data = new RawData();
                //get raw data and put it into the data structure
                BasicDBObject current_loc = (BasicDBObject)cursor.next();
                //update item to have been processed
                coll.update(current_loc,new BasicDBObject("$set", new BasicDBObject("processed", true)));
                //Extract Email Address and Date_Created fields so we can update the data collection at the end
                String Email_Address = (String)current_loc.get("email");
                //Now that we have the Email Address we need to find the patients weight so we can laugh it it
                int Weight = (int)db.getCollection("patients").findOne(new BasicDBObject("email", Email_Address),new BasicDBObject("weight",1).append("_id", 0)).get("weight");
                String Date_Created = (String)current_loc.get("created");
                //System.out.println(Date_Created);
                try 
                {
                    /*
                       Train of Thought:
                       Either combine into 1 for loop however if items are not equal that may cause issues
                       Or leave it as is
                       */
                    BasicDBObject Storage;
                    BasicDBList Money;

                    //Get the Acc subdirectory
                    Storage = (BasicDBObject)current_loc.get("Acc");
                    //Get the x subarray of the subdirectory and add it to Data.Acc.x
                    Money = (BasicDBList) Storage.get("x");
                    for(Object num: Money)
                    {
                        data.Acc_x.add((double)num);
                    }
                    //Get the y subarray of the subdirectory and add it to Data.Acc_y
                    Money = (BasicDBList) Storage.get("y");
                    for(Object num: Money)
                    {
                        data.Acc_y.add((double)num);
                    }
                    //Get the z subarray of the subdirectory and add it to Data.Acc_z
                    Money = (BasicDBList) Storage.get("z");
                    for(Object num: Money)
                    {
                        data.Acc_z.add((double)num);
                    }

                    //Get the Mag subdirectory
                    Storage = (BasicDBObject)current_loc.get("Mag");
                    //Get the x subarrary of the subdirectory
                    Money = (BasicDBList) Storage.get("x");
                    //Put all the Mag.x items into Data.Mag_x
                    for(Object num: Money)
                    {
                        data.Mag_x.add((double)num);
                    }
                    //Get the y subarray of the subdirectory and add it to Data.Mag_y
                    Money = (BasicDBList) Storage.get("y");
                    for(Object num: Money)
                    {
                        data.Mag_y.add((double)num);
                    }
                    //Get the z subarray of the subdirectory and add it to Data.Mag_z
                    Money = (BasicDBList) Storage.get("z");
                    for(Object num: Money)
                    {
                        data.Mag_z.add((double)num);
                    }

                    //Get the Gyro subdirectory
                    Storage = (BasicDBObject)current_loc.get("Gyro");
                    //Get the x subarray of the subdirectory
                    Money = (BasicDBList) Storage.get("x");
                    for(Object num: Money)
                    {
                        data.Gyro_x.add((double)num);
                    }
                    //Get the y subarray of the subdirectory and add it to Data.Gyro_y
                    Money = (BasicDBList) Storage.get("y");
                    for(Object num: Money)
                    {
                        data.Gyro_y.add((double)num);
                    }
                    //Get the z subarray of the subdirectory and add it to Data.Gyro_z
                    Money = (BasicDBList) Storage.get("z");
                    for(Object num: Money)
                    {
                        data.Gyro_z.add((double)num);
                    }
                } 
                catch (Exception e) 
                {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }

                //Run data through feature extraction
                //FeatureExtractor extractor = new FeatureExtractor(data);
                //Instances features = extractor.instances;
                data = null;

                //double time = features.numInstances()/30.0;
                double time = 1.0;
                //Classify
                for(int i = 0; i < 30; i ++)
                {
                    Random r = new Random();
                    double Activity = 0 + (23 - 0) * r.nextDouble();
                    //double Activity = 0.0;
                    /*try
                    {
                        double Location = Node_loc.classifyInstance(features.instance(i));
                        switch((int)Location)
                        {
                            case 0:
                                Activity = One.classifyInstance(features.instance(0));
                                break;
                            case 1:
                                Activity = Two.classifyInstance(features.instance(0));
                                break;
                            case 2:
                                Activity = Three.classifyInstance(features.instance(0));
                                break;
                            case 3:
                                Activity = Four.classifyInstance(features.instance(0));
                                break;
                            case 4:
                                Activity = Five.classifyInstance(features.instance(0));
                                break;
                            case 5:
                                Activity = Six.classifyInstance(features.instance(0));
                                break;
                            case 6:
                                Activity = Seven.classifyInstance(features.instance(0));
                                break;
                            case 7:
                                Activity = Eight.classifyInstance(features.instance(0));
                                break;
                            case 8: 
                                Activity = Nine.classifyInstance(features.instance(0));
                                break;
                            case 9: 
                                Activity = Ten.classifyInstance(features.instance(0));
                                break;
                        }
                    } 
                    catch (Exception e) 
                    {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }*/

                    if(Activities.containsKey((int)Activity))
                    {
                        double orig_time = Activities.get((int)Activity);
                        Activities.put((int)Activity, time+ orig_time);
                    }
                    else
                    {
                        Activities.put((int)Activity,time);
                    }
                }
                Set<Integer> keys = Activities.keySet();
                double max = 0.0;
                for(int key : keys)
                {
                   
                    
                    /*
                     * TO DO:::::::::::::::::::::::
                     * Add Calorie Calculator to switch statement
                     * Need Patients.weight data
                     * Use MET? http://www.topendsports.com/weight-loss/energy-met.htm
                     * MET FORMULA! Weight(in kgs) * MET * time/1hour
                     */
                     if(max < Activities.get(key))
                     {
                         max = Activities.get(key);
                     }
                }
                double Calories_Burned = 0.0;
                    String Activity_Name = "";
                    switch((int)max)
                    {
                        case 0:
                            Activity_Name = "Sitting with Hands on Lap";
                            //MET: 1.3
                            Calories_Burned = (Weight / 2.2) * 1.3 * (max/ 3600);
                            break;
                        case 1:
                            Activity_Name = "Sitting while Writing";
                            //MET: 1.8
                            Calories_Burned = (Weight / 2.2) * 1.8 * (max/ 3600);
                            break;
                        case 2:
                            Activity_Name = "Siting while Typing";
                            //MET: 1.8
                            Calories_Burned = (Weight / 2.2) * 1.8 * (max/ 3600);
                            break;
                        case 3:
                            Activity_Name = "Lying Down on Back";
                            //MET: 1
                            Calories_Burned = (Weight / 2.2) * 1 * (max/ 3600);
                            break;
                        case 4:
                            Activity_Name = "Lying on Right Side";
                            //MET: 1
                            Calories_Burned = (Weight / 2.2) * 1 * (max/ 3600);
                            break;
                        case 5:
                            Activity_Name = "Jumping Jacks";
                            //MET: 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 6:
                            Activity_Name = "Butt Kickers";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 7:
                            Activity_Name = "High Knees";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 8:
                            Activity_Name = "Flutter Kicks";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 9:
                            Activity_Name = "Lunges";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 10:
                            Activity_Name = "Crunches";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 11:
                            Activity_Name = "Squats";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 12:
                            Activity_Name = "Pushups";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                        case 13:
                            Activity_Name = "Walking Up Stairs";
                            //MET = 2.5
                            Calories_Burned = (Weight / 2.2) * 2.5 * (max/ 3600);
                            break;
                        case 14:
                            Activity_Name = "Walking Down Stairs";
                            //MET = 2.5
                            Calories_Burned = (Weight / 2.2) * 2.5 * (max/ 3600);
                            break;
                        case 15:
                            Activity_Name = "Standing with Arms at Sides";
                            //MET = 1.3
                            Calories_Burned = (Weight / 2.2) * 1.3 * (max/ 3600);
                            break;
                        case 16:
                            Activity_Name = "Standing with Arms Crossed";
                            //MET = 1.3
                            Calories_Burned =(Weight / 2.2) * 1.3 * (max/ 3600);
                            break;
                        case 17:
                            Activity_Name = "Slow Walk";
                            //MET = 2.5
                            Calories_Burned = (Weight / 2.2) * 2.5 * (max/ 3600);
                            break;
                        case 18:
                            Activity_Name ="Normal Walk";
                            //MET = 3
                            Calories_Burned = (Weight / 2.2) * 3 * (max/ 3600);
                            break;
                        case 19:
                            Activity_Name ="Fast Walk";
                            //MET = 5
                            Calories_Burned = (Weight / 2.2) * 5 * (max/ 3600);
                            break;
                        case 20:
                            Activity_Name ="Jogging";
                            //MET = 8
                            Calories_Burned = (Weight / 2.2) * 8 * (max/ 3600);
                            break;
                        case 21:
                            Activity_Name ="Running";
                            //MET = 10
                            Calories_Burned = (Weight / 2.2) * 10 * (max/ 3600);
                            break;
                        case 22:
                            Activity_Name ="Biking";
                            //MET = 6
                            Calories_Burned = (Weight / 2.2) * 6 * (max/ 3600);
                            break;
                        case 23:
                            Activity_Name = "Dips";
                            //MET = 7
                            Calories_Burned = (Weight / 2.2) * 7 * (max/ 3600);
                            break;
                    }

                    BasicDBObject document = new BasicDBObject();
                    document.put("email", Email_Address);
                    document.put("created", Date_Created);
                    document.put("activity", Activity_Name);
                    document.put("duration", max);
                    document.put("calories_burned", Calories_Burned);
                    db.getCollection("datas").insert(document);
                }
            
        }
        finally 
        {
            cursor.close();
        }

        mongoClient.close();
    }
}
