package org.odk.collect.android.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Scanner;

import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSGeoFence;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.skyhookwireless.wps.*;


public class Skyhook extends IntentService {
	final static File file = new File(Environment.getExternalStorageDirectory(),"out.txt");
	final static File allowedForms = new File(Environment.getExternalStorageDirectory(),"allowedForms.txt");
	static String[] questions= {"",""};
	public Skyhook(){
		super("SkyhookGeofenceService");
	}
	public static boolean SERVICE_STATE = false;
	private XPS _xps;
    private WPSAuthentication auth;
    private NotificationManager notificationManager;
    private Context context;
    public static final int NOTIFICATION_ID = 1234567;
    public static GeoFence raja = new GeoFence(37.874705,-122.258491,100);
    public static GeoFence restaurant = new GeoFence(37.875681,-122.260095,100);
    public static GeoFence jupiter = new GeoFence(37.865473,-122.262553,100);
    public static GeoFence home = new GeoFence(37.884733,-122.278301,200);
    public static GeoFence south = new GeoFence(37.868077,-122.259207,100);
	/**
	 * @param args
	 */
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		try{ 
			FileWriter out = new FileWriter(allowedForms,false);
			out.write("");
			out.close();
		  	}catch (Exception e){//Catch exception if any
		  		System.err.println("Error: " + e.getMessage());
		  	}
		try{ 
			FileWriter out = new FileWriter(file,true);
			out.write("Service Start\n");
			out.close();
		  	}catch (Exception e){//Catch exception if any
		  		System.err.println("Error: " + e.getMessage());
		  	}
		_xps = new XPS(this);
        auth = new WPSAuthentication("hoanghw", "UC Berkeley");

        final MyRegistrationCallback _regCallback = new MyRegistrationCallback();

        _xps.registerUser(auth, null, _regCallback);
        SERVICE_STATE = true;
        
        
//		final WPSGeoFence home = new WPSGeoFence(37.884733,-122.278301, 170, WPSGeoFence.Type.WPS_GEOFENCE_ENTER, 2000);
//		final WPSGeoFence davisHall = new WPSGeoFence(37.874705,-122.258491, 160, WPSGeoFence.Type.WPS_GEOFENCE_ENTER, 2000);
//		
//        final WPSGeoFence.Handle handleHome = _xps.setGeoFence(home, new MyGeoFenceCallback());
//        if (handleHome == null){
//            //System.err.println("setGeoFence() failed");
//        	try{ 
//    			FileWriter out = new FileWriter(file,true);
//    			out.write("setGeoFence Home failed\n");
//    			out.close();
//    		  	}catch (Exception e){//Catch exception if any
//    		  		System.err.println("Error: " + e.getMessage());
//    		  	}
//        	SERVICE_STATE = false;
//        }
//        final WPSGeoFence.Handle handleDavis = _xps.setGeoFence(davisHall, new MyGeoFenceCallback());
//        if (handleDavis == null){
//            //System.err.println("setGeoFence() failed");
//        	try{ 
//    			FileWriter out = new FileWriter(file,true);
//    			out.write("setGeoFence DavisHall failed\n");
//    			out.close();
//    		  	}catch (Exception e){//Catch exception if any
//    		  		System.err.println("Error: " + e.getMessage());
//    		  	}
//        	SERVICE_STATE = false;
//        }
        
        
        _xps.getXPSLocation(auth,(int)15,40,new MyPeriodicLocationCallback());
        
	}
	private class MyRegistrationCallback implements RegistrationCallback{
    	public void handleSuccess()
    	{
    		// Indicates that registration has been successful
    	}

    	public WPSContinuation handleError(final WPSReturnCode error)
    	{
    		// Indicates that registration has failed, along with the error code.
    		// Return continue to keep trying, stop to give up.
    		return WPSContinuation.WPS_CONTINUE;  
    	}

    	public void done()
    	{
    		// Indicates that registration is completed.  
    		// If you call abort() during registration, done() will be
    		// called without either handle method being called.	  
    	}
    }
	/*private class MyGeoFenceCallback implements GeoFenceCallback{

		@Override
		public WPSContinuation handleGeoFence(WPSGeoFence geofence, WPSLocation location) {
			// TODO Auto-generated method stub
			String status="";
			if (geofence.getType() == WPSGeoFence.Type.WPS_GEOFENCE_ENTER)
	        {
	            status="entering";
	            String question = null;
	            switch (geofence.getRadius()){
	            	case 170: question="Q1";
	            	case 160: question="Q2";
	            }
	            
	            if (question != null)
	            	try{ 
	        			FileWriter out = new FileWriter(allowedForms,true);
	        			out.write(question);
	        			out.write("\n");
	        			out.close();
	    			  	}catch (Exception e){//Catch exception if any
	    			  		System.err.println("Error: " + e.getMessage());
	    			  	}
	            notifyNewQuestions();
	            
	        }
	        else // geofence.getType() == WPSGeoFence.Type.WPS_GEOFENCE_LEAVE
	        {
	            status="leaving";
	        }

	        final String pattern = " geofence" + " - " + status + " - "
	                               + " ({0,number,#.######},"
	                               + " {1,number,#.######}"
	                               + " +/-{2, number,#}m)"
	                               + " at {3,number,#.######},"
	                               + " {4,number,#.######}"
	                               + " +/-{5, number,#}m";
	        try{ 
    			FileWriter out = new FileWriter(file,true);
    			out.write(MessageFormat.format(pattern,
                        geofence.getLatitude(),
                        geofence.getLongitude(),
                        geofence.getRadius(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getHPE()));
    			out.write("\n");
    			out.close();
			  	}catch (Exception e){//Catch exception if any
			  		System.err.println("Error: " + e.getMessage());
			  	}	  

	        return WPSContinuation.WPS_CONTINUE;
		}
		
	}*/
	private class MyPeriodicLocationCallback implements WPSPeriodicLocationCallback{

		@Override
		public void done() {
			// TODO Auto-generated method stub
			SERVICE_STATE = false;
			try{ 
    			FileWriter out = new FileWriter(file,true);
    			out.write("PeriodicLocationCallback Stopped!\n");
    			out.close();
			  	}catch (Exception e){//Catch exception if any
			  		System.err.println("Error: " + e.getMessage());
			  	}	
		    Thread thread = new Thread() {
		    	public void run() {
		    		SERVICE_STATE = true;
		            _xps.getXPSLocation(auth,6,40,new MyPeriodicLocationCallback());
		    		}
		    	};
			thread.start();
		}

		@Override
		public WPSContinuation handleError(WPSReturnCode arg0) {
			// TODO Auto-generated method stub
			return WPSContinuation.WPS_CONTINUE;
		}

		@Override
		public WPSContinuation handleWPSPeriodicLocation(WPSLocation location) {
			// TODO Auto-generated method stub
			
			double lat=location.getLatitude();
			double lon=location.getLongitude();
			
			if (raja.isInGeoFence(lat,lon)){
				if (!Arrays.asList(questions).contains("Q1")){
					questions[0]="Q1";
					updateAllowedForms();
					notifyNewQuestions("Q1");
				}	
			}
			else if (questions[0]!="") {
					questions[0] = "";
					updateAllowedForms();
					notifyLeaving("Q1");
				}
			
			if (jupiter.isInGeoFence(lat, lon)){
				if (!Arrays.asList(questions).contains("Q2")){
					questions[1]="Q2";
					updateAllowedForms();
					notifyNewQuestions("Q2");		
				}
			}
			else if (questions[1]!="") {
				questions[1] = "";
				updateAllowedForms();
				notifyLeaving("Q2");
			}
			
//			final String pattern = " periodicReport" 
//                    + " at {0,number,#.######},"
//                    + " {1,number,#.######}"
//                    + " +/-{2, number,#}m";
//			try{ 
//				FileWriter out = new FileWriter(file,true);
//				out.write(MessageFormat.format(pattern,
//			         location.getLatitude(),
//			         location.getLongitude(),
//			         location.getHPE()));
//				out.write("\n");
//				out.close();
//				}catch (Exception e){//Catch exception if any
//					System.err.println("Error: " + e.getMessage());
//				}	
			return WPSContinuation.WPS_CONTINUE;
		}
	}
	
/*	private void notifyNewQuestions() {
		context = this.getApplicationContext();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = android.R.drawable.star_on;
		CharSequence tickerText = "New question(s) posted!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		
		CharSequence contentTitle = "ODK Alert";
		String question = "";
        try {
            Scanner scanner = new Scanner(allowedForms);
            while (scanner.hasNextLine()) {
                question += scanner.nextLine()+" ";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		CharSequence contentText = "Please answer new question: "+question;
		Intent notificationIntent = new Intent(this, FormChooserList.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}*/
	
	private void notifyNewQuestions(String question) {
		context = this.getApplicationContext();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = android.R.drawable.star_on;
		CharSequence tickerText = "New question(s) posted!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		
		CharSequence contentTitle = "ODK Alert";
		CharSequence contentText = "You enter the GeoFence. Please answer new question: "+question;
		
		Intent notificationIntent = new Intent(this, FormChooserList.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private void notifyLeaving(String question) {
		context = this.getApplicationContext();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = android.R.drawable.star_on;
		CharSequence tickerText = "You have left the GeoFence!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		
		CharSequence contentTitle = "ODK Alert";
		CharSequence contentText = "You left the GeoFence! Please ignore question: "+question;
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private static void updateAllowedForms(){	
		try{ 
			FileWriter out = new FileWriter(allowedForms,false);
			out.write(questions[0]+"\n"+questions[1]+"\n");
			out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}	
	}
}
