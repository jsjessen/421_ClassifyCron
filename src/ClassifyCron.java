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
import static java.util.concurrent.TimeUnit.SECONDS;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.classifiers.*;

import java.net.UnknownHostException;

public class ClassifyCron 
{
    private MongoClient mongoClient = null;
    private String database_name = "m3";
    private String collection_name = "raw_datas";
    private DB db;
    private DBCollection coll;
    private BasicDBObject query;
    private DBCursor cursor = coll.find(query);

    private String Email_Address;
    private int Weight;
    private String Date_Created;

    private String sensors[] = {"Acc", "Mag", "Gyro"};
    private String axes[] = {"x", "y", "z"};
    private List<List<Double>> data = new ArrayList<List<Double>>(sensors.length * axes.length);


    private Classifier locationClassifier = null;
    private int numActivityClassifiers = 10;
    private Classifier[] activityClassifiers = new Classifier[numActivityClassifiers];

    private Instances features;

    public static void main(String[] args)
    {
        ClassifyCron job = new ClassifyCron();
        job.connectDB();

        //Iterate through collections
        try 
        {
            while(job.cursor.hasNext()) 
                job.classify();
        }
        finally 
        {
            job.cursor.close();
        }

        job.mongoClient.close();
    }

    private void connectDB()
    {
        //Connect to the Server
        try
        {
            mongoClient = new MongoClient();
        } 
        catch (UnknownHostException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Get Database
        db = mongoClient.getDB(database_name);

        //Get The Desired Collection
        coll = db.getCollection(collection_name);

        //Find all collections that have not been processed
        query = new BasicDBObject("processed", false);

        //Define what we actually want from the collection
        /*
           TODO
           Find what feature is shared between the raw_datas collection and the other collection
           .append("feature",1)
           FIXED!!!!!!!!
           Removed field to get all features.
           */

        //BasicDBObject field = new BasicDBObject("Acc.x", 1).append("_id", 0).append("Acc.y", 1).append("Acc.z", 1).append("Gyro.x", 1).append("Gyro.y", 1).append("Gyro.z", 1).append("Mag.x", 1).append("Mag.y", 1).append("Mag.z", 1);
        //Actually query to the collection to find what we want
        cursor = coll.find(query);
    }

    private void getData()
    {
        // Get raw data and put it into the data structure
        BasicDBObject current_loc = (BasicDBObject)cursor.next();

        // Update item to have been processed
        coll.update(current_loc,new BasicDBObject("$set", new BasicDBObject("processed", true)));

        // Extract Email Address and Date_Created fields so we can update the data collection at the end
        Email_Address = (String)current_loc.get("email");

        // Now that we have the Email Address we need to find the patients weight so we can laugh it it
        Weight = (int)db.getCollection("patients").findOne(new BasicDBObject("email", Email_Address),new BasicDBObject("weight",1).append("_id", 0)).get("weight");

        Date_Created = (String)current_loc.get("created");
        //System.out.println(Date_Created);

        try 
        {
            for(int s = 0; s < sensors.length; s++)
            {
                // Get sensor's subdir
                BasicDBObject Storage = (BasicDBObject)current_loc.get(sensors[s]);
                for(int a = 0; a < axes.length; a++)
                {
                    // Get the axis subarray
                    BasicDBList dbList = (BasicDBList)Storage.get(axes[a]);
                    for(Object num: dbList)
                        data.get(s*a).add((double)num);
                }
            }
        } 
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void extractFeatures()
    {
        getData();
    }

    private void getClassifiers()
    {
        try
        {
            //load the classifiers
            locationClassifier = (Classifier) weka.core.SerializationHelper.read("models/NodeLoc.model");

            for(int i = 0; i < numActivityClassifiers; i++)
                activityClassifiers[i] = ((Classifier)weka.core.SerializationHelper.read("models/ActRec" + i + ".model"));
        }
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void classify()
    {
        extractFeatures();
        //clearData();

        getClassifiers();

        HashMap<Integer, Double> Activities = new HashMap<Integer, Double>();

        // Classify
        double time = features.numInstances() / 30.0; // why 30?
        for(int i = 0; i < features.numInstances(); i ++)
        {
            double activityCode = 0.0;
            try
            {
                int Location = (int)locationClassifier.classifyInstance(features.instance(i));
                activityCode = activityClassifiers[Location].classifyInstance(features.instance(0));
            } 
            catch (Exception e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(Activities.containsKey((int)activityCode))
            {
                double orig_time = Activities.get((int)activityCode);
                Activities.put((int)activityCode, time + orig_time);
            }
            else
            {
                Activities.put((int)activityCode, time);
            }
        }

        Set<Integer> keys = Activities.keySet();
        for(int key : keys)
        {
            String Activity_Name = "";
            double Calories_Burned = 0.0;
            /*
             * TO DO:::::::::::::::::::::::
             * Add Calorie Calculator to switch statement
             * Need Patients.weight data
             * Use MET? http://www.topendsports.com/weight-loss/energy-met.htm
             * MET FORMULA! Weight(in kgs) * MET * time/1hour
             */
            switch(key)
            {
                case 0:
                    Activity_Name = "Sitting with Hands on Lap";
                    //MET: 1.3
                    Calories_Burned = (Weight / 2.2) * 1.3 * (Activities.get(key)/ 3600);
                    break;
                case 1:
                    Activity_Name = "Sitting while Writing";
                    //MET: 1.8
                    Calories_Burned = (Weight / 2.2) * 1.8 * (Activities.get(key)/ 3600);
                    break;
                case 2:
                    Activity_Name = "Siting while Typing";
                    //MET: 1.8
                    Calories_Burned = (Weight / 2.2) * 1.8 * (Activities.get(key)/ 3600);
                    break;
                case 3:
                    Activity_Name = "Lying Down on Back";
                    //MET: 1
                    Calories_Burned = (Weight / 2.2) * 1 * (Activities.get(key)/ 3600);
                    break;
                case 4:
                    Activity_Name = "Lying on Right Side";
                    //MET: 1
                    Calories_Burned = (Weight / 2.2) * 1 * (Activities.get(key)/ 3600);
                    break;
                case 5:
                    Activity_Name = "Jumping Jacks";
                    //MET: 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 6:
                    Activity_Name = "Butt Kickers";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 7:
                    Activity_Name = "High Knees";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 8:
                    Activity_Name = "Flutter Kicks";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 9:
                    Activity_Name = "Lunges";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 10:
                    Activity_Name = "Crunches";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 11:
                    Activity_Name = "Squats";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 12:
                    Activity_Name = "Pushups";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
                case 13:
                    Activity_Name = "Walking Up Stairs";
                    //MET = 2.5
                    Calories_Burned = (Weight / 2.2) * 2.5 * (Activities.get(key)/ 3600);
                    break;
                case 14:
                    Activity_Name = "Walking Down Stairs";
                    //MET = 2.5
                    Calories_Burned = (Weight / 2.2) * 2.5 * (Activities.get(key)/ 3600);
                    break;
                case 15:
                    Activity_Name = "Standing with Arms at Sides";
                    //MET = 1.3
                    Calories_Burned = (Weight / 2.2) * 1.3 * (Activities.get(key)/ 3600);
                    break;
                case 16:
                    Activity_Name = "Standing with Arms Crossed";
                    //MET = 1.3
                    Calories_Burned =(Weight / 2.2) * 1.3 * (Activities.get(key)/ 3600);
                    break;
                case 17:
                    Activity_Name = "Slow Walk";
                    //MET = 2.5
                    Calories_Burned = (Weight / 2.2) * 2.5 * (Activities.get(key)/ 3600);
                    break;
                case 18:
                    Activity_Name ="Normal Walk";
                    //MET = 3
                    Calories_Burned = (Weight / 2.2) * 3 * (Activities.get(key)/ 3600);
                    break;
                case 19:
                    Activity_Name ="Fast Walk";
                    //MET = 5
                    Calories_Burned = (Weight / 2.2) * 5 * (Activities.get(key)/ 3600);
                    break;
                case 20:
                    Activity_Name ="Jogging";
                    //MET = 8
                    Calories_Burned = (Weight / 2.2) * 8 * (Activities.get(key)/ 3600);
                    break;
                case 21:
                    Activity_Name ="Running";
                    //MET = 10
                    Calories_Burned = (Weight / 2.2) * 10 * (Activities.get(key)/ 3600);
                    break;
                case 22:
                    Activity_Name ="Biking";
                    //MET = 6
                    Calories_Burned = (Weight / 2.2) * 6 * (Activities.get(key)/ 3600);
                    break;
                case 23:
                    Activity_Name = "Dips";
                    //MET = 7
                    Calories_Burned = (Weight / 2.2) * 7 * (Activities.get(key)/ 3600);
                    break;
            }

            BasicDBObject document = new BasicDBObject();
            document.put("email", Email_Address);
            document.put("created", Date_Created);
            document.put("activity", Activity_Name);
            document.put("duration", Activities.get(key));
            document.put("calories_burned", Calories_Burned);
            db.getCollection("data").insert(document);
        }
    }

}
