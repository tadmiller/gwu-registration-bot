// hello
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeader;
import com.gargoylesoftware.htmlunit.html.HtmlImage;

import java.util.*;
import javax.activation.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.Function;
import net.sourceforge.htmlunit.corejs.javascript.NativeArray;
import net.sourceforge.htmlunit.corejs.javascript.Script;
import net.sourceforge.htmlunit.corejs.javascript.Scriptable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ranges.Range;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.OnbeforeunloadHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.TextUtil;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.impl.SelectableTextInput;
import com.gargoylesoftware.htmlunit.html.impl.SimpleRange;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.PostponedAction;
import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleSheet;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import com.gargoylesoftware.htmlunit.protocol.javascript.JavaScriptURLConnection;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class AutoRegister
{
	private String gwid;
	private String pin;
	private WebClient webClient;
	private HtmlPage banweb;
	private LinkedList<String> crns;
	
	// com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException this is when we get auto logged out
	
	public AutoRegister(String gwid, String pin) throws IOException
	{
		this.gwid = gwid;
		this.pin = pin;
		crns = getCRNs();
		
		webClient = new WebClient(BrowserVersion.FIREFOX_38);
		
		banweb = login(banweb);
		
		if (Calendar.getInstance().getTime().getHours() < 22 && Calendar.getInstance().getTime().getHours() > 6)
		{
			banweb = navigateToCRNs(banweb);
		
			banweb = registerForCRNs(banweb);
		}
		else
			System.out.println("Time is " + (Calendar.getInstance().getTime().getHours()) + ":" + Calendar.getInstance().getTime().getMinutes() + " :: Out of registration time bounds");
		
		logout(banweb);
	}
	
	public void logout(HtmlPage page) throws IOException
	{
		HtmlAnchor a = page.getAnchorByHref("twbkwbis.P_Logout");
		page = a.click();

		if (checkWord(page.asText(), "If you are finished, "))
			System.out.println("Succesfully logged out.");
	}
	
	public boolean checkWord(String toCheck, String word)
	{
		for (int i = 0; i < toCheck.length(); i++)
		{
			for (int j = 0; j < word.length(); j++)
			{
				if (toCheck.charAt(i + j) != word.charAt(j))
					break;
				
				if (j == word.length() - 1)
					return true;
			}
		}
		
		return false;
	}
	
	public HtmlPage registerForCRNs(HtmlPage page) throws IOException
	{
		ListIterator<String> i = crns.listIterator();
		
		while (i.hasNext())
		{
			registerForCRN(i.next(), page);
		}
		
		return banweb;
	}
	
	public boolean registerForCRN(String crn, HtmlPage page) throws IOException
	{
		int lecture = -1;
		int lab = -1;
		String temp = "";
		
		boolean isLecture = true;
		
		for (int j = 0; j < crn.length(); j++)
		{
			if (crn.charAt(j) == '/')
			{
				if (isLecture)
					lecture = Integer.valueOf(temp);
				else
					lab = Integer.valueOf(temp);
				
				temp = "";
				isLecture = !isLecture;
			}
			else if (crn.charAt(j) != '/')
				temp += crn.charAt(j);
		}
		
		HtmlTextInput crn1 = page.getHtmlElementById("crn_id1");
		crn1.setValueAttribute(String.valueOf(lecture));
		
		if (lab != -1)
		{
			HtmlTextInput crn2 = page.getHtmlElementById("crn_id2");
			crn2.setValueAttribute(String.valueOf(lab));
		}
		
		Iterator<DomNode> i = page.getDescendants().iterator();
		
		while(i.hasNext())
		{
			DomNode a = i.next();
			
			if (a.asText().toString().equals("Submit Changes"))
			{
				page = ((HtmlSubmitInput) a).click();
				break;
			}
		}
		
		System.out.println(page.asText());
		
		System.out.println("Attempting to register for CRN" + lecture);
		
		return false;
	}
	
	public HtmlPage navigateToCRNs(HtmlPage page) throws IOException
	{
		WebRequest request = new WebRequest(new URL("https://banweb.gwu.edu/PRODCartridge/twbkwbis.P_GenMenu?name=bmenu.P_RegMnu"));
	
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptTimeout(10000);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setTimeout(10000);
	
		page = webClient.getPage(request);
		
//		while(i.hasNext())
//		{
//			DomNode a = i.next();
//
//			if (a.getClass().toString().equals("class com.gargoylesoftware.htmlunit.html.HtmlAnchor"))
//			{
//				if (a.asText().equals("Register, Drop and/or Add Classes"))
//				{
//					page = ((HtmlAnchor) a).click();
//					break;
//				}
//			}
//		}
		
		HtmlAnchor getToTerm = page.getAnchorByText("Register, Drop and/or Add Classes");
		page = getToTerm.click();
		
		Iterator<DomNode> i = page.getDescendants().iterator();
		
		while(i.hasNext())
		{
			DomNode a = i.next();
			
			if (a.asText().toString().equals("Submit"))
			{
				page = ((HtmlSubmitInput) a).click();
				break;
			}
		}
		
		System.out.println(page.asText());
		
		return page;
	}
	
	public HtmlPage login(HtmlPage page) throws FailingHttpStatusCodeException, IOException
	{
		WebRequest request = new WebRequest(new URL("https://banweb.gwu.edu/PRODCartridge/twbkwbis.P_WWWLogin"));
		
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptTimeout(10000);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setTimeout(10000);
		
		page = webClient.getPage(request);
		
		HtmlPasswordInput gwidInput = page.getHtmlElementById("UserID");
		gwidInput.setValueAttribute(gwid);
		
		HtmlForm form = page.getFormByName("loginform");
		
		HtmlPasswordInput pinInput = form.getInputByName("PIN");
		pinInput.setValueAttribute(pin);
		
		form.fireEvent(Event.TYPE_SUBMIT);
		
		page = (HtmlPage) form.getInputByValue("Login").click();
		
		// Print out that we've successfully logged in and the name of the person
		for (int i = 0; i < page.asText().length(); i++)
		{
			if (page.asText().charAt(i) == 'W' && page.asText().charAt(i + 1) == 'e' && page.asText().charAt(i + 2) == 'l')
			{
				System.out.print("Succesfully logged in as ");
				
				i += 8;
				
				while (page.asText().charAt(++i) != ',')
					System.out.print(page.asText().charAt(i));
				
				System.out.println();
				
				break;
			}
		}
		
		return page;
	}
	
	public LinkedList<String> getCRNs()
	{
		LinkedList<String> crns = new LinkedList<String>();
		BufferedReader br = null;
		
		try
		{
			String currLine;
			
			br = new BufferedReader(new FileReader("crns.txt"));	
			
			while ((currLine = br.readLine()) != null)
			{
				if (currLine.charAt(0) != '/' && currLine.charAt(1) != '/')
				{
					String crn = "";
					
					//int i = 0;
					
					crns.add(currLine);
					
//					while (currLine.charAt(i) != '~')
//					{
//						if (currLine.charAt(i) == ' ')
//							
//							crns.add(Integer.valueOf(crn));
//						else
//							crn += currLine.charAt(i);
//						
//						i++;
//					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		ListIterator<String> i = crns.listIterator();
		
		return crns;
	}
}
