import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

//import com.gargoylesoftware.htmlunit.javascript.host.file.FileReader;

public class Runner
{
	public static void main(String[] args) throws IOException
	{
		String gwid = "";
		String pin = "";
		int setting = -1;

		Scanner s = new Scanner(System.in);
		Console cnsl = null;
		
		try
		{
			cnsl = System.console();

			if (cnsl != null)
			{
				char[] id = cnsl.readPassword("Enter your GWID: ");
				char[] p = cnsl.readPassword("Enter your PIN: ");
				
				for (int i = 0; i < id.length; i++)
				{
					gwid += id[i];
					
					if (i < p.length)
						pin += p[i];
				}
				
				System.out.println("=============================");
				
				System.out.println("\nYou have two options for registering for classes."
						+ "\n1. is to spam BanWeb's registration area."
						+ "\n   This WILL result in a registration hold if done for enough time."
						+ "\n2. is to manually go to each class within BanWeb and check to see if it is open, then register"
						+ "\n   This is a much safer method, and should be done instead of the first if you are using this NOT on registration day.\n");
				setting = Integer.parseInt(cnsl.readLine("Enter 1 or 2... "));
			}    
			else
			{
				System.out.println("Console not found. Exiting...");
				System.exit(0);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();      
		}
		
		System.out.println("Starting application");
		AutoRegister bot;
		
		while (true)
		{
			bot = new AutoRegister(gwid, pin);
			
			sleep();
			
			bot = null;
		}
	}
	
	public void encrypt(String s)
	{
		String encrypted = "";
		
		for (int i = 0; i < s.length(); i++)
		{
			encrypted += s.charAt(i) | 5;
		}
		
		System.out.println(encrypted);
	}
	
	public static void sleep()
	{
		double time = (Math.random()) * 100; //(Math.random() * 300 + 180) * 1000;
		System.out.println("Sleeping for " + (double)(time/1000) + " seconds");
		
		try
		{
			Thread.sleep((int)time);
		}

		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
	
//	public HashMap<Integer, LinkedList<Integer>> getCRNs()
//	{
//		HashMap<Integer, LinkedList<Integer>> crns = new HashMap<Integer, LinkedList<Integer>>();
//		
//		Scanner s = new Scanner(System.in);
//		
//		int input = 0;
//		
//		while (input != -1)
//		{
//			System.out.println("Enter a CRN: ");
//			input = s.nextInt();
//			
//		//	System.out.println("Enter possible lab  );
//		}
//		
//		return crns;
//	}
}
