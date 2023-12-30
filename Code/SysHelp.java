import java.io.*;
import java.io.Console;
import java.lang.ClassLoader.*;
import java.lang.Object;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Scanner;
import java.sql.*;

/*
    HELP
    ----------------------------------------------------------------------------
    Loads the help topic files into the database.
    ----------------------------------------------------------------------------
*/
public class SysHelp
{
    // Properties
    private String DBPath = "Data/help";
    private String RootDir = "../help";
    private Connection CONN = null;
    private String noneFound = "No help articles found.";


    // Constructor
    public SysHelp()
    {
        SetConnection();
    };


    // Parser
    public List<String> Parse (String input)
    {
        boolean match = false;
        List<String> output = new ArrayList<String>();

        if (input.equals("topics"))
        {
            match = true;
            output = GetHelpTopics();
            if (output.size() < 1)
            {
                output.add("No topics are defined.");
            }
        }

        if (!match) output = GetHelp(input);
        if (output.size() > 0) match = true;

        if (!match) output = GetHelpByTopic(input);
        if (output.size() > 1 && output.get(1) != noneFound) match = true;

        if (!match) output = GetHelpBySyntax(input);
        if (output.size() > 1 && output.get(1) != noneFound) match = true;

        if (!match) output = GetHelpByTags(input);
        if (output.size() > 1 && output.get(1) != noneFound) match = true;

        if (!match) output = GetHelpByContent(input);
        if (output.size() > 1 && output.get(1) != noneFound) match = true;

        return output;
    }


    // Get Help Topics
    public List<String> GetHelpTopics()
    {
        String firstAlpha = "";

        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT DISTINCT Topic ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "ORDER BY ";
            sql += "    Topic ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                if (output.size() == 0) output.add("Help Topics:\n");

                if (rs.getString("Topic") != null && !rs.getString("Topic").equals(""))
                {
                    output.add("help " + rs.getString("Topic"));
                }
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    // Get Help Index
    public List<String> GetHelpIndex(String input)
    {
        String firstAlpha = "";

        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Syntax ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            if (input.length() > 0)
            {
                sql += "    AND Title LIKE '" + input.substring(0, 1) + "%' ";
            }
            sql += "ORDER BY ";
            sql += "    Title ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                if (output.size() == 0) output.add("Help Index\n");

                if (!firstAlpha.toLowerCase().equals(rs.getString("Title").substring(0, 1).toLowerCase()))
                {
                    output.add("\n");
                    firstAlpha = rs.getString("Title").substring(0, 1).toUpperCase();
                    output.add(firstAlpha + ":");
                }

                output.add(rs.getString("Title") + " (" + rs.getString("Syntax") + ")");
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }


    // Get Help By Strict Syntax
    public List<String> GetHelp(String input)
    {
        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Content, Related, Topic ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Syntax = '" + input + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                if (output.size() == 0) output.add("Help: " + rs.getString("Title") + "\n");
                if (rs.getString("Topic") != null && rs.getString("Topic").length() != 0) output.add("Topic: " + rs.getString("Topic") + "\n");
                output.add(rs.getString("Content"));
                if (!rs.getString("Related").equals(""))
                {
                    output.add("\nRelated:");
                    output.add(rs.getString("Related"));
                }
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    // Get Help By Syntax
    public List<String> GetHelpBySyntax(String input)
    {
        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Syntax ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Syntax = '" + input + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            output.add("Help articles matching '" + input + "': ");

            while (rs.next())
            {
                output.add("To view help article: " + rs.getString("Title") + " use: help " + rs.getString("Syntax"));
            }

            if (output.size() == 1)
            {
                output.add(noneFound);
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    // Get Help By Topic
    public List<String> GetHelpByTopic(String input)
    {
        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Syntax, Content, Related, Topic ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Topic = '" + input + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            output.add("Help topic: " + input + "\n");

            while (rs.next())
            {
                output.add("To view help article: " + rs.getString("Title") + " use: help " + rs.getString("Syntax"));
            }

            if (output.size() == 1)
            {
                output.add(noneFound);
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    // Get Help By Tags
    public List<String> GetHelpByTags(String input)
    {
        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Syntax ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Tags LIKE '%" + input + "%' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            output.add("Searching help tags for '" + input + "': ");

            while (rs.next())
            {
                output.add("To view help topic: " + rs.getString("Title") + " use: help " + rs.getString("Syntax"));
            }

            if (output.size() == 1)
            {
                output.add(noneFound);
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    // Get Help By Content
    public List<String> GetHelpByContent(String input)
    {
        List<String> output = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT Title, Syntax ";
            sql += "FROM ";
            sql += "    Help ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Content LIKE '%" + input + "%' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            output.add("Searching help articles for '" + input + "': ");

            while (rs.next())
            {
                output.add("To view help article: " + rs.getString("Title") + " use: help " + rs.getString("Syntax"));
            }

            if (output.size() == 1)
            {
                output.add(noneFound);
            }

            rs.close();
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }




    // Load the help system
    public void LoadHelp(String parent)
    {
        String dir = "";
        if (parent.equals("")) dir = RootDir + "/" + parent;
        else dir = parent;

        File[] fileList = new File(dir).listFiles();

        if (fileList != null)
        {
            for (File f : fileList)
            {
                if (!f.isDirectory())
                {
                    LoadFile(dir + "/" + f.getName());
                }
                else
                {
                    LoadHelp(dir + "/" + f.getName());
                }
            }
        }
    }

    // Load Help File
    public void LoadFile(String path)
    {
        boolean handled = false;
        List<String> related = new ArrayList<String>();
        List<String> globalvars = GetGlobalVars();
        String[] var = null;

        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(path);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            String title = "";
            String syntax = "";
            String tags = "";
            String content = "";
            String rel = "";
            String topic = "";
            int docLine = 1;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null)
            {
                handled = false;

                // Replace the global vars
                if (!strLine.equals(""))
                {
                    for (String v : globalvars)
                    {
                        var = v.split("=");
                        if (var.length == 2)
                        {
                            strLine = strLine.replace("%" + var[0] + "%", var[1]);
                        }
                    }
                }

                // Get the special syntax items
                if (!handled && strLine.length() >= 8 && strLine.toLowerCase().substring(0, 8).equals(">>title="))
                {
                    handled = true;
                    title = strLine.substring(8);
                }
                if (!handled && strLine.length() >= 9 && strLine.toLowerCase().substring(0, 9).equals(">>syntax="))
                {
                    handled = true;
                    syntax = strLine.substring(9);
                }
                if (!handled && strLine.length() >= 7 && strLine.toLowerCase().substring(0, 7).equals(">>tags="))
                {
                    handled = true;
                    tags = strLine.substring(7);
                }
                if (!handled && strLine.length() >= 8 && strLine.toLowerCase().substring(0, 8).equals(">>topic="))
                {
                    handled = true;
                    topic = strLine.substring(8);
                }
                if (!handled && strLine.length() >= 10 && strLine.toLowerCase().substring(0, 10).equals(">>related="))
                {
                    handled = true;
                    related.add(strLine.substring(10));
                }

                // Get the help article content
                if (!handled)
                {
                    if (!content.equals("")) content += "\n";
                    content += strLine;
                }

                docLine++;
            }
            //Close the input stream
            in.close();

            // Builde the related links
            for (String s : related)
            {
                if (!rel.equals("")) rel += "\n";
                rel += s;
            }

            // Store the Help File
            StoreFile(title, syntax, tags, content, rel, topic);

            Functions.OutputRaw("Help article created - " + title);
        }
        catch (Exception e)
        {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }


    // Store the help file
    public void StoreFile(String title, String syntax, String tags, String content, String related, String topic)
    {
        String sql = "";

        title = Functions.SqlCleanup(title);
        syntax = Functions.SqlCleanup(syntax);
        tags = Functions.SqlCleanup(tags);
        content = Functions.SqlCleanup(content);
        related = Functions.SqlCleanup(related);
        topic = Functions.SqlCleanup(topic);

        try
        {
            Statement cmd = CONN.createStatement();

            sql =  "";
            sql += "INSERT INTO ";
            sql += "Help ( ";
            sql += "    Title, ";
            sql += "    Syntax, ";
            sql += "    Tags, ";
            sql += "    Content, ";
            sql += "    Related, ";
            sql += "    Topic ";
            sql += ") ";
            sql += "SELECT ";
            sql += "    '" + title + "', ";
            sql += "    '" + syntax + "', ";
            sql += "    '" + tags + "', ";
            sql += "    '" + content + "', ";
            sql += "    '" + related + "', ";
            sql += "    '" + topic + "' ";
            sql += ";";

            cmd.execute(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // Get the global variables
    public List<String> GetGlobalVars()
    {
        String path = "global.config";
        List<String> output = new ArrayList<String>();
        List<String> vars = Functions.ReadFile("global.config");
        String[] row = null;

        for (String s : vars)
        {
            if (!s.equals(""))
            {
                row = s.split("=");
                if (row.length == 2)
                {
                    output.add(s);
                }
            }
        }

        return output;
    }



    // Create the Database
    private void CreateDatabase()
    {
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();

            // Help Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Help ( ";
            sql += "    Title VARCHAR(200) NOT NULL, ";
            sql += "    Syntax VARCHAR(200) NOT NULL, ";
            sql += "    Tags VARCHAR(500), ";
            sql += "    Content BLOB, ";
            sql += "    Related BLOB, ";
            sql += "    Topic BLOB, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            
            cmd.execute(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    };


    // Setup the Connection
    private void SetConnection()
    {
        boolean newDb = false;

        try
        {
            File f = new File(DBPath);
            if (!f.exists())
            {
                Functions.OutputRaw("Creating help database (" + DBPath + ")...");
                newDb = true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            Class.forName("org.sqlite.JDBC");
            CONN = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        if (newDb)
        {
            CreateDatabase();
            LoadHelp("");
        }
    };
}
