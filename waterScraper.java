import java.io.*;
import javax.swing.*;
import java.net.*;

public class waterScraper
    {    
    public final static int PLAIN_TEXT = 0;
    public final static int IN_TAG = 1;
    public final static int IN_SINGLE_QUOTE = 2;
    public final static int IN_DOUBLE_QUOTE = 3;
    
    static int charCount = 0;
    static int state = PLAIN_TEXT;
    
    static int tokenCount = 0;
    
    /*
     // stupid way
     
    static String currentToken = "";
    static void addCharacter(int c)
	{
	currentToken = currentToken + (char) c;
	}

    static void processToken()
	{
	if (!currentToken.equals("<b>River:</b>"))
	    {
		System.err.println("TOKEN: ###" + currentToken + "###");
	    tokenCount++;
	    currentToken = "";
	    }
	}
    */
    
    static StringBuffer currentToken = new StringBuffer();
    static void addCharacter(int c)
	{
	currentToken.append( (char) c );
	}

    static void processTokens()
	{
	if (currentToken.length() > 0)
	    {
	    tokenCount = tokenCount + 1;
	    currentToken = new StringBuffer();
	    }
	}
    
    
    static int countTokens(InputStream html) throws IOException
	{
	while (true)
	    {
	    int c = html.read();
	    if (c < 0) break;  // at EOF
	    switch(state)
		{
		case PLAIN_TEXT:
		    if (c == '<')
			{
			processTokens();
			state = IN_TAG;
			}
		    addCharacter(c);
		    break;
		case IN_TAG:
		    addCharacter(c);
		    if (c == '>')
			{
			processTokens();
			state = PLAIN_TEXT;
			}
		    else if (c == '"')
			state = IN_DOUBLE_QUOTE;
		    else if (c == '\'')
			state = IN_SINGLE_QUOTE;
		    break;
		case IN_DOUBLE_QUOTE:
		    addCharacter(c);
		    if (c == '"')
			state = IN_TAG;
		    break;
		case IN_SINGLE_QUOTE:
		    addCharacter(c);
		    if (c == '"')
			state = IN_TAG;
		    break;
		default:
		    throw new RuntimeException("Um, I wasn't supposed to be here.");
		}
	    if (++charCount % 1000 == 0) System.out.println(charCount);
	    }
	processTokens();  // get the last one out
	return tokenCount;
	}
    
    public static void main(String[] args) throws IOException
	{
	String url = JOptionPane.showInputDialog("Enter URL");
	InputStream content = (InputStream) (new URL(url).openConnection().getContent());
	System.out.println("Number of tokens: " + countTokens(content));
	System.out.println("Current token is: " + currentToken);
	}
    }
