package com.unitbv.events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.unitbv.events.util.ServerUtilities;

public class App 
{
    public static void main( String[] args )
    {
		ExecutorService exSrv = Executors.newCachedThreadPool();
		try {
			ServerUtilities server = new ServerUtilities(9001);
			exSrv.submit(server);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
