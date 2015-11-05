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
    // Model Information
    // TODO: Change on server (or have as input arg?)
    private final static String modelDir = "models/"; 
    private final static String locationModelName = "NodeLoc"; //.model
    private final static String activityModelName = "ActRec"; //#.model

    // Database information
    private MongoClient mongoClient = null;
    private final static String database_name = "m3";
    private final static String collection_name = "raw_datas";
    private DB db;
    private DBCollection coll;
    private BasicDBObject query;
    private DBCursor cursor = coll.find(query);

    // User data
    private String Email_Address;
    private int Weight;
    private String Date_Created;

    // Sensor data
    private final static String sensors[] = { "Acc", "Mag", "Gyro" };
    private final static String axes[] = { "x", "y", "z" };
    private List<List<Double>> data = 
        new ArrayList<List<Double>>(sensors.length * axes.length); 
    private Instances features;

    // Classifiers
    private Classifier locationClassifier = null;
    private int numActivityClassifiers = 10;
    private Classifier[] activityClassifiers = 
        new Classifier[numActivityClassifiers];
    private List<Activity> activities = 
        new ArrayList<Activity>(Activity.names.length);

    ClassifyCron()
    {
        // Initialize activities
        for(int i = 0; i < Activity.names.length; i++)
            activities.add(new Activity(i));
    }

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
           Find what feature is shared between the raw_datas collection 
           and the other collection
           .append("feature",1)
           FIXED!!!!!!!!
           Removed field to get all features.

           BasicDBObject field = new BasicDBObject("Acc.x", 1).
           append("_id", 0).
           append("Acc.y", 1).
           append("Acc.z", 1).
           append("Gyro.x", 1).
           append("Gyro.y", 1).
           append("Gyro.z", 1).
           append("Mag.x", 1).
           append("Mag.y", 1).
           append("Mag.z", 1);

           Actually query to the collection to find what we want
           cursor = coll.find(query);
           */
    }

    private void getData()
    {
        // Get raw data and put it into the data structure
        BasicDBObject current_loc = (BasicDBObject)cursor.next();

        // Update item to have been processed
        coll.update(current_loc,
                new BasicDBObject("$set", new BasicDBObject("processed", true)));

        // Extract Email Address and Date_Created fields so we can update the data collection at the end
        Email_Address = (String)current_loc.get("email");

        // Now that we have the Email Address we need to find the patients weight so we can laugh it it
        Weight = (int)db.getCollection("patients").findOne(
                new BasicDBObject("email", Email_Address),
                new BasicDBObject("weight",1).append("_id", 0)).get("weight");

        Date_Created = (String)current_loc.get("created");
        //System.out.println(Date_Created);

        try 
        {
            for(int s = 0; s < sensors.length; s++)
            {
                // Get sensor subdir
                BasicDBObject Storage = (BasicDBObject)current_loc.get(sensors[s]);
                for(int a = 0; a < axes.length; a++)
                {
                    // Get axis subarray
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
            locationClassifier = (Classifier) weka.core.SerializationHelper.
                read(modelDir + locationModelName + ".model");

            for(int i = 0; i < numActivityClassifiers; i++)
                activityClassifiers[i] = ((Classifier)weka.core.SerializationHelper.
                        read(modelDir + activityModelName + i + ".model"));
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

        // Classify
        double time = features.numInstances() / 30.0; // why 30?
        for(int i = 0; i < features.numInstances(); i ++)
        {
            int activityCode;
            try
            {
                int Location = (int)locationClassifier.
                    classifyInstance(features.instance(i));

                activityCode = (int)activityClassifiers[Location].
                    classifyInstance(features.instance(0));
            } 
            catch (Exception e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(activityCode < activities.size())
                activities.get(activityCode).updateDuration(time);
        }

        for(Activity activ: activities)
        {
            activ.updateCalories(Weight);

            BasicDBObject document = new BasicDBObject();
            document.put("email", Email_Address);
            document.put("created", Date_Created);
            document.put("activity", activ.name);
            document.put("duration", activ.duration);
            document.put("calories_burned", activ.calories);
            db.getCollection("data").insert(document);
        }
    }
}
