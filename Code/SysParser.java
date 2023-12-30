import java.io.*;
import java.io.Console;
import java.lang.ClassLoader.*;
import java.lang.Object;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Scanner;

/*
    parser
    ----------------------------------------------------------------------------
    ----------------------------------------------------------------------------
*/
public class SysParser
{
    // Properties
    private boolean initialized = false;
    private SysHelp help = null;
    private String History = "";
    List<String> historyList = new ArrayList<String>();
    public SysAdmin admin = new SysAdmin();


    // Main Listener
    //  Accepts the player's input and calls the CallListener
    //  sending it the input, and checking for an escapement.
    public void Listener()
    {
        Listener("");
    }
    public void Listener(String input)
    {
        Scanner scan = new Scanner(System.in);

        // Initialize the help class
        SysHelp h = GetHelp();

        if (input.equals(""))
        {
            if (!initialized)
            {
                initialized = true;
                // Build Welcome
                Functions.Output(HomeScreen());
            }

            System.out.print(">> ");

            try
            {
                input = scan.nextLine();
                if (CallListener(input)) Listener();
            }
            catch (Exception ex)
            {
                //ex.printStackTrace();
            }
        }
        else
        {
            System.out.println("Running macro: " + input);
            CallListener(input);
        }
    }

    // Listener Caller
    //  Calls the parser, sending in the input and returning
    //  a boolean, allowing for an escapement.
    public boolean CallListener(String input)
    {
        boolean call = true;
        boolean match = false;
        Method method = null;
        String[] arr = input.split(" ");
        String cmd = input;
        if (arr.length > 1)
        {
            cmd = arr[0];
        }
        String params = "";
        if (arr.length > 1)
        {
            cmd = arr[0];
            params = Functions.ArrayToString(arr, 1, " ");
        }
        String[] paramsArr = null;
        if (!params.equals("")) paramsArr = params.split(",");
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<String> output = new ArrayList<String>();
        String tmp = "";


        // Help
        if (!match && Functions.Match(cmd, "helpindex"))
        {
            output = GetHelp().GetHelpIndex(params.trim());
            if (output.size() > 0)
            {
                Functions.ClearConsole();
                AddHistory(input);
                match = true;
            }
        }

        if (!match && Functions.Match(cmd, "help|?|helpindex"))
        {
            if (params.equals(""))
            {
                output = GetHelp().Parse("");
            }
            else
            {
                output = GetHelp().Parse(params.trim());
            }
            if (output.size() > 0)
            {
                Functions.ClearConsole();
                AddHistory(input);
                match = true;
            }
        }

        // History
        if (!match && Functions.Match(cmd, "hi"))
        {
            match = true;
            GetHistory(params.trim());
        }

        
        // Exit
        if (!match && Functions.Match(input, "exit"))
        {
            match = true;
            call = false;
        }

        // List Macros
        if (!match && Functions.Match(cmd, "macros"))
        {
            match = true;
            AddHistory("macros");
            ListMacros();
        }

        // <macro> <macro file>
        if (!match && Functions.Match(cmd, "macro"))
        {
            if (paramsArr.length < 1)
            {
                Functions.Output("Be sure to specify a valid macro name.");
            }
            else
            {
                match = true;
                AddHistory(cmd + " " + paramsArr[0].trim());
                RunMacro(paramsArr[0].trim());
            }
        }

        // <build> <output file name>, <root dir>
        if (!match && Functions.Match(input, "build"))
        {
            match = true;
            AddHistory(input);
            if (paramsArr == null || paramsArr.length != 2)
            {
                Functions.Output("To build a game use:\nbuild <output file name (one word)>, <root path>");
                return call;
            }

            SysNrmnParser np = new SysNrmnParser(paramsArr[0].trim(), paramsArr[1].trim());
            Functions.Output("Building Game: " + params);
            np.ParseDefinitions();

            CopyGame(paramsArr[0].trim());

            np.Clear();
            np = null;
            System.gc();
        }

        // <copyfile> <input file name>, <output file>
        if (!match && Functions.Match(input, "copyfile"))
        {
            match = true;
            AddHistory(input);
            if (paramsArr == null || paramsArr.length != 2)
            {
                Functions.Output("To copy a file use:\ncopyfile <source file>, <destination file>");
                return call;
            }

            try
            {
                Functions.CopyFile(paramsArr[0].trim(), paramsArr[1].trim());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Functions.Output("Source: " + paramsArr[0].trim() + ", Dest: " + paramsArr[1].trim());
            }

            Functions.Output("File: " + paramsArr[0].trim() + " copied to: " + paramsArr[1].trim());
        }

        if (!match)
        {
            admin.Parse_Admin(input);
        }



        if (match && output.size() > 0)
        {
            Functions.Output(output);
        }

        return call;
    };


    // Add to History
    private void AddHistory(String input)
    {
        History = input;
        historyList.add(input);

        if (historyList.size() > 20)
        {
            historyList.remove(9);
        }
    }

    private void GetHistory(String input)
    {
        if (input.trim() == "")
        {
            ListHistory();
        }
        else
        {
            try
            {
                CallListener(historyList.get(Integer.parseInt(input)-1));
            }
            catch(NumberFormatException e)
            {
                ListHistory();
            }  
        }
    }

    private void ListHistory()
    {
        String hist = "";
        int ix = 1;

        for (String s : historyList)
        {
            if (hist != "")
            {
                hist += "\n";
            }
            hist += ix + " " + s;
            ix++;
        }

        Functions.Output(hist);
    }


    private void CopyGame(String fileName)
    {
        String clientPath = Functions.GetSetting("Config/Global.config", "client", "Client/Data/Games/");
        String compPath = "Data/Games/";

        try
        {
            Functions.Output("Installing the game in the Client tool...");
            Functions.CopyFile(compPath + fileName, clientPath + fileName);
        }
        catch (IOException ex)
        {
            // Do nothing, the author will have to manually copy the game over
        }
    }


    private void RunMacro(String fileName)
    {
        String macrosPath = Functions.GetSetting("Config/Global.config", "macros", "Data/Macros/");
        List<String> mac = Functions.ReadFile(macrosPath + fileName);

        if (mac.size() > 0)
        {
            for (String s : mac)
            {
                CallListener(s);
            }
        }
    }

    private void ListMacros()
    {
        String macrosPath = Functions.GetSetting("Config/Global.config", "macros", "Data/Macros/");
        List<String> files = Functions.ListFiles(macrosPath);

        if (files.size() > 0) Functions.Output("\nMacros:");
        else Functions.Output("\nNo available macros.");

        for (String s : files)
        {
            Functions.OutputRaw(s);
        }

        Functions.Output("");
    }


    // UI
    public String HomeScreen()
    {
        String output = "";

        output += "                      __    __                                      \n";
        output += " _      ______  _____/ /___/ /  _      _____  ____ __   _____  _____\n";
        output += "| | /| / / __ \\/ ___/ / __  /  | | /| / / _ \\/ __ `/ | / / _ \\/ ___/\n";
        output += "| |/ |/ / /_/ / /  / / /_/ /   | |/ |/ /  __/ /_/ /| |/ /  __/ /    \n";
        output += "|__/|__/\\____/_/  /_/\\__,_/    |__/|__/\\___/\\__,_/ |___/\\___/_/     \n";
        output += "                                                                    \n";
        output += "\n\n";
        output += "                            Game Builder";
        output += "\n\n";
        output += "                        For help type:  help";
        output += "\n";
        output += "                          Quit type:  exit";

        List<String> issues = new ArrayList<String>();
        issues = Functions.ReadFileRaw("../current_issues.txt");
        output += "\n\n\n\n";
        for (String l : issues)
        {
            output += l + "\n";
        }

        return output;
    };





    // GET/SET
    public SysHelp GetHelp() { if (help == null) help = new SysHelp(); return help; }
    public void SetHelp(SysHelp val) { help = val; }
};
