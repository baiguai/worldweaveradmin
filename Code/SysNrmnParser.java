import java.io.File;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Random;

/*
    NRMN PARSER
    ----------------------------------------------------------------------------
    ----------------------------------------------------------------------------
*/
public class SysNrmnParser
{
    // Properties
    private boolean VERBOSE = false;
    private boolean DEBUG = false;
    private SysDatabase DB;
    private static String RootPath = "";
    private static String HelpDefPath = "Config/HelpDefs/";
    private static boolean GameDefined = false;
    private SysNorman norman = null;
    public SysNorman GetNorman() { if (norman == null) norman = new SysNorman(); return norman; }
    public void SetNorman(SysNorman val) { norman = val; }


    // Constructor
    public SysNrmnParser (String fileName, String rootPath)
    {
        // Set the root and DB paths
        RootPath = "Games/" + rootPath + "/";
        DB = new SysDatabase("Data/Games/" + fileName);

        // Utilize the verbose setting
        String vb = Functions.GetSetting("Config/Global.config", "verbose", "false");
        if (vb.equalsIgnoreCase("true")) VERBOSE = true;
        else VERBOSE = false;
        String db = Functions.GetSetting("Config/Global.config", "debug", "false");
        if (db.equalsIgnoreCase("true")) DEBUG = true;
        else DEBUG = false;
    }

    // Clear out the object
    public void Clear()
    {
        norman.Clear();
        DB = null;
    }

    // Parse Game Definitions
    public void ParseDefinitions()
    {
        GameDefined = false;

        DB.ClearAllSql();

        // Define the Objects
        Functions.OutputRaw("Parsing game objects...");
        ParseFiles(RootPath);

        // Parse the Logs
        Functions.OutputRaw("Parsing logs...");
        ParseLogFiles(RootPath);

        // Parse the Tests
        Functions.OutputRaw("Parsing tests...");
        ParseTestFiles(RootPath);

        // Parse the Variables
        Functions.OutputRaw("Parsing variables...");
        ParseVariableFiles(RootPath);

        // Parse the System Help Files
        Functions.OutputRaw("Parsing help topics...");
        ParseHelpFiles(HelpDefPath);

        DB.ExecuteAllSql();

        DB.MessageTemplateFix();

        DB.Inheritance();

        DB.ReplaceVariables();

        PostParseFiles(RootPath);

        ParseSplash(RootPath);
        ParseCredits(RootPath);

        Functions.Output("Finished successfully.");
    }

    // Parse Tests
    private void ParseTestFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (VERBOSE) Functions.OutputRaw("Parsing tests " + rootDir);

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    if (VERBOSE) Functions.OutputRaw("Parsing file: " + fullPath);

                    SetNorman(null);
                    GetNorman().ProcessNormanFile(fullPath);

                    ParseTests(fullPath, null);
                }
                else
                {
                    ParseTestFiles(fullPath + "/");
                }
            }
        }
    }

    // Parse Variables
    private void ParseVariableFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (VERBOSE) Functions.OutputRaw("Parsing variables " + rootDir);

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    if (VERBOSE) Functions.OutputRaw("Parsing file: " + fullPath);

                    SetNorman(null);
                    GetNorman().ProcessNormanFile(fullPath);

                    ParseVariables(fullPath, null);
                }
                else
                {
                    ParseVariableFiles(fullPath + "/");
                }
            }
        }
    }

    // Parse Log Files
    private void ParseLogFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (VERBOSE) Functions.OutputRaw("Parsing logs " + rootDir);

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    if (VERBOSE) Functions.OutputRaw("Parsing file: " + fullPath);

                    SetNorman(null);
                    GetNorman().ProcessNormanFile(fullPath);

                    ParseLog(fullPath, null);
                }
                else
                {
                    ParseLogFiles(fullPath + "/");
                }
            }
        }
    }

    // Parse Help Files
    private void ParseHelpFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (VERBOSE) Functions.OutputRaw("Parsing files in " + rootDir);

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    if (VERBOSE) Functions.OutputRaw("Parsing file: " + rootDir + fName);

                    SetNorman(null);
                    GetNorman().ProcessNormanFile(fullPath);

                    ParseHelp();
                }
                else
                {
                    ParseHelpFiles(fullPath + "/");
                }
            }
        }
    }

    // Parse Files
    private void ParseFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (VERBOSE) Functions.OutputRaw("Parsing files in " + rootDir);

        if (fileList != null)
        {
            for (File f : fileList)
            {
                if (!GameDefined)
                {
                    fName = f.getName();
                    fullPath = rootDir + fName;

                    if (!f.isDirectory())
                    {
                        if (VERBOSE) Functions.OutputRaw("Parsing file " + fName);

                        if (!GameDefined && fName.equalsIgnoreCase("game.nrmn"))
                        {
                            SetNorman(null);
                            GetNorman().ProcessNormanFile(fullPath);

                            ParseGame();
                        }
                    }
                }
            }
        }

        if (!GameDefined)
        {
            Functions.OutputRaw("A single game node must be defined.");
            return;
        }

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    if (VERBOSE) Functions.OutputRaw("Parsing file: " + fullPath);

                    try
                    {
                        SetNorman(null);
                        GetNorman().ProcessNormanFile(fullPath);

                        ParsePlaceholderObjects();
                        ParseRooms();
                        ParseRootObjects();
                        ParseNpcs();
                        ParseFight();
                        ParseLinks();
                        ParseHelp();
                        ParseUnitTests();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    ParseFiles(fullPath + "/");
                }
            }
        }
    }

    // Post Parse Files
    private void PostParseFiles(String rootDir)
    {
        File[] fileList = new File(rootDir).listFiles();
        String fName = "";
        String fullPath = "";

        if (fileList != null)
        {
            for (File f : fileList)
            {
                fName = f.getName();
                fullPath = rootDir + fName;

                if (!f.isDirectory())
                {
                    try
                    {
                        SetNorman(null);
                        GetNorman().ProcessNormanFile(fullPath);

                        PostParseElements(null);
                    }
                    catch (Exception ex) {}
                }
                else
                {
                    PostParseFiles(fullPath + "/");
                }
            }
        }
    }

    // Post Parsing
    private void PostParseElements(SysNorman.Element parentElem)
    {
        String parentGuid = "";
        String parentType = "";
        int sort = 0;
        List<SysNorman.Element> elems = null;

        try
        {
            if (parentElem == null) elems = GetNorman().GetElements();
            else elems = parentElem.GetElements();

            for (SysNorman.Element elem : elems)
            {
                // Handle Sorting
                ParseSort(elem);

                // Handle child elements
                PostParseElements(elem);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Splash
    private void ParseSplash(String rootDir)
    {
        List<String> data = Functions.ReadFile(rootDir + "splash.txt", true);
        String content = "";

        if (data.size() < 1) return;

        for (String s : data)
        {
            if (!content.equals("")) content += "\n";
            content += s;
        }

        DB.Update_Splash(content);
    }

    // Parse Credits
    private void ParseCredits(String rootDir)
    {
        List<String> data = Functions.ReadFile(rootDir + "credits.txt", true);
        String content = "";

        if (data.size() < 1) return;

        for (String s : data)
        {
            if (!content.equals("")) content += "\n";
            content += s;
        }

        DB.Update_Credits(content);
    }


    // Parse Game
    private void ParseGame()
    {
        String name = "";
        String initialLocation = "";
        String guid = Functions.GetGUID();
        String statsGuid = "";
        String adminPass = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("game"))
                {
                    name = elem.GetProperty("name");
                    initialLocation = elem.GetProperty("initial_location");
                    statsGuid = elem.GetProperty("stats_aliases");
                    adminPass = elem.GetProperty("admin_password");

                    // Insert Game
                    DB.Insert_Game(guid, name, initialLocation, statsGuid, adminPass);

                    // Parse Game Attributes
                    ParseAttributes(guid, "game", elem, guid);

                    // Parse Events
                    ParseEvents(guid, "game", elem, guid);

                    // Parse Commands
                    ParseCommands(guid, "game", elem, guid);

                    // Parse Objects
                    ParseObjects(guid, "game", elem);

                    ParsePlayerAttributes(elem);

                    GameDefined = true;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Player Attributes (for player generation)
    private void ParsePlayerAttributes(SysNorman.Element elem)
    {
        String guid = "";

        try
        {
            for (SysNorman.Element plElem : elem.GetElements())
            {
                if (plElem.GetElementName().equals("player"))
                {
                    ParseAttributes(guid, "player", plElem, guid);
                    ParseObjects(guid, "player", plElem);
                    ParseCommands(guid, "player", plElem, guid);
                    ParseEvents(guid, "player", plElem, guid);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }


    // Parse Fight
    private void ParseFight()
    {
        String guid = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("fight"))
                {
                    if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    // Parse Fight Commands
                    ParseCommands(guid, "fight", elem, guid);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Help
    private void ParseHelp()
    {
        String syntax = "";
        String title = "";
        String topic = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("help"))
                {
                    syntax = elem.GetProperty("syntax");
                    title = elem.GetProperty("title");

                    for (SysNorman.Element conElem : elem.GetElements())
                    {
                        if (conElem.GetElementName().equals("topic"))
                        {
                            topic = conElem.GetContent();
                        }
                    }

                    // Insert Room
                    DB.Insert_Help(syntax, title, topic);

                    if (VERBOSE) Functions.OutputRaw("Help Parsed: " + title);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Tests
    private void ParseTests(String docPath, SysNorman.Element parentElem)
    {
        String testName = "";
        String commands = "";
        List<SysNorman.Element> elems = new ArrayList<SysNorman.Element>();

        if (parentElem != null)
        {
            elems = parentElem.GetElements();
        }
        else
        {
            elems = GetNorman().GetElements();
        }

        try
        {
            for (SysNorman.Element elem : elems)
            {
                if (elem.GetElementName().equals("test"))
                {
                    testName = elem.GetProperty("name");

                    for (SysNorman.Element tstElem : elem.GetElements())
                    {
                        if (tstElem.GetElementName().equals("commands"))
                        {
                            commands = tstElem.GetContent();
                        }
                    }

                    // Insert Log
                    DB.Insert_Test(testName, commands);

                    if (VERBOSE) Functions.OutputRaw("Test Parsed: " + testName);
                }
                else
                {
                    // Find logs recursively
                    ParseTests(docPath, elem);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Variables
    private void ParseVariables(String docPath, SysNorman.Element parentElem)
    {
        String alias = "";
        String value = "";
        List<SysNorman.Element> elems = new ArrayList<SysNorman.Element>();

        if (parentElem != null)
        {
            elems = parentElem.GetElements();
        }
        else
        {
            elems = GetNorman().GetElements();
        }

        try
        {
            for (SysNorman.Element elem : elems)
            {
                if (elem.GetElementName().equals("variable") || elem.GetElementName().equals("var"))
                {
                    alias = elem.GetProperty("alias");
                    value = elem.GetProperty("value");

                    // Insert Variable
                    DB.Insert_Variable(alias, value);

                    if (VERBOSE) Functions.OutputRaw("Variable Parsed: " + alias);
                }
                else
                {
                    // Find logs recursively
                    ParseVariables(docPath, elem);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Log
    private void ParseLog(String docPath, SysNorman.Element parentElem)
    {
        String type = "";
        String message = "";
        List<SysNorman.Element> elems = new ArrayList<SysNorman.Element>();

        if (parentElem != null)
        {
            elems = parentElem.GetElements();
        }
        else
        {
            elems = GetNorman().GetElements();
        }

        try
        {
            for (SysNorman.Element elem : elems)
            {
                if (elem.GetElementName().equals("log"))
                {
                    type = elem.GetProperty("type");

                    for (SysNorman.Element lgElem : elem.GetElements())
                    {
                        if (lgElem.GetElementName().equals("text"))
                        {
                            message = lgElem.GetContent();
                        }
                    }

                    // Insert Log
                    DB.Insert_Log(type, docPath, message);

                    if (VERBOSE) Functions.OutputRaw("Log Parsed: " + type);
                }
                else
                {
                    // Find logs recursively
                    ParseLog(docPath, elem);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }




    // Parse Rooms
    private void ParseRooms()
    {
        String guid = "";
        String label = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (!elem.GetElementName().equals("room")) continue;

                if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
                else guid = Functions.GetGUID();

                label = elem.GetProperty("label");

                DB.Insert_Room(guid, label, elem.GetFilePath());

                if (VERBOSE) Functions.OutputRaw("Room Parsed: " + guid);

                // Parse Room Attributes
                ParseAttributes(guid, "room", elem, guid);

                // Parse Objects
                ParseObjects(guid, "room", elem);

                // Parse Events
                ParseEvents(guid, "room", elem, guid);

                // Parse Commands
                ParseCommands(guid, "room", elem, guid);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse NPCs
    private void ParseNpcs()
    {
        String guid = Functions.GetGUID();
        String name = "";
        String meta = "";
        String location = "";
        String inherit = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("npc"))
                {
                    if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!elem.GetProperty("meta").equals("")) meta = elem.GetProperty("meta");
                    name = elem.GetProperty("name");
                    location = elem.GetProperty("location");
                    if (!elem.GetProperty("inherit").equals("")) inherit = elem.GetProperty("inherit");

                    // Insert NPC
                    DB.Insert_Npc(guid, name, meta, location, inherit, sort);

                    if (VERBOSE) Functions.OutputRaw("NPC Parsed: " + guid);

                    // Parse NPC Attributes
                    ParseAttributes(guid, "npc", elem, guid);

                    // Parse NPC Objects
                    ParseObjects(guid, "npc", elem);

                    // Parse NPC Events
                    ParseEvents(guid, "npc", elem, guid);

                    // Parse NPC Commands
                    ParseCommands(guid, "npc", elem, guid);

                    // Parse NPC Travel Set
                    ParseNpcTravelSets(guid, elem);

                    sort = (sort + 1);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse NPC Travel Sets
    private void ParseNpcTravelSets(String parentGuid, SysNorman.Element elem)
    {
        String guid = "";
        int waitMinute = 0;
        String mode = "repeat";

        try
        {
            for (SysNorman.Element trvElem : elem.GetElements())
            {
                if (trvElem.GetElementName().equals("travelset") || trvElem.GetElementName().equals("tset"))
                {
                    if (!trvElem.GetProperty("alias").equals("")) guid = trvElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!DB.Unique_Alias(parentGuid, guid))
                    {
                        Functions.Output("Alias not unique (ParseNpcTravelSets): " + guid);
                        System.exit(1);
                    }

                    try
                    {
                        if (!trvElem.GetProperty("wait_minutes").equals("")) waitMinute = Integer.parseInt(trvElem.GetProperty("wait_minutes"));
                    }
                    catch (Exception ex) {}

                    if (!trvElem.GetProperty("mode").equals("")) mode = trvElem.GetProperty("mode");

                    // Insert the TravelSet
                    DB.Insert_NpcTravelSet(guid, parentGuid, waitMinute, mode);

                    if (VERBOSE) Functions.OutputRaw("NPC Travel Set Parsed: " + guid);

                    // Parse NPC Attributes
                    ParseAttributes(guid, "travelset", trvElem, elem.GetProperty("alias"));

                    // Parse the Travel Nodes
                    ParseNpcTravel(guid, trvElem);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse NPC Travel
    private void ParseNpcTravel(String npcTravelSetGuid, SysNorman.Element elem)
    {
        String location = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element trvElem : elem.GetElements())
            {
                if (trvElem.GetElementName().equals("travel") || trvElem.GetElementName().equals("trv"))
                {
                    if (!trvElem.GetProperty("location").equals("")) location = trvElem.GetProperty("location");

                    // Insert the TravelSet
                    DB.Insert_NpcTravel(npcTravelSetGuid, location, sort);

                    if (VERBOSE) Functions.OutputRaw("NPC Travel Parsed for TravelSet: " + npcTravelSetGuid + ", Location: " + location);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Links
    private void ParseLinks()
    {
        String guid = "";
        String pType = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (!elem.GetElementName().equals("link")) continue;

                if (!elem.GetProperty("parent").equals("")) guid = elem.GetProperty("parent");
                if (!elem.GetProperty("parent_type").equals("")) pType = elem.GetProperty("parent_type");

                // Object Link
                if (pType.equals("object"))
                {
                    // Parse Logic Sets
                    ParseLogicSets(guid, pType, elem, guid);
                    // Parse MessageSets
                    ParseMessageSets(guid, pType, elem, guid);
                }

                // Parse Room Attributes
                ParseAttributes(guid, pType, elem, guid);
                // Parse Objects
                ParseObjects(guid, pType, elem);
                // Parse Events
                ParseEvents(guid, pType, elem, guid);
                // Parse Commands
                ParseCommands(guid, pType, elem, guid);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Placeholder Objects
    private void ParsePlaceholderObjects()
    {
        String guid = Functions.GetGUID();

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("object") || elem.GetElementName().equals("obj"))
                {
                    if (!elem.GetProperty("type").equals("message")) break;

                    if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    DB.Insert_Object("", "", guid, "message", -1, "", "", 0, elem.GetFilePath(), "");

                    ParseMessageSets(guid, "object", elem, guid);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Root Level Object Nodes
    private void ParseRootObjects()
    {
        String parentGuid = "";
        String parentType = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (elem.GetElementName().equals("object") || elem.GetElementName().equals("obj"))
                {
                    if (!elem.GetProperty("location").equals(""))
                    {
                        parentGuid = elem.GetProperty("location");
                        parentType = elem.GetProperty("parent_type");
                        if (parentType.equals("")) parentType = "room";

                        sort++;
                    }

                    ParseObjectNode(parentGuid, parentType, elem, sort);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Objects
    private void ParseObjects(String parentGuid, String parentType, SysNorman.Element elem)
    {
        int sort = 0;

        try
        {

            for (SysNorman.Element objElem : elem.GetElements())
            {
                if (objElem.GetElementName().equals("object") || objElem.GetElementName().equals("obj"))
                {
                    ParseObjectNode(parentGuid, parentType, objElem, sort);
                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void ParseObjectNode(String parentGuid, String parentType, SysNorman.Element elem, int sort)
    {
        try
        {
            String guid = "";
            String type = "";
            int chance = -1;
            String label = "";
            String meta = "";
            String inherit = "";
            int custSort = sort;

            if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
            else guid = Functions.GetGUID();

            if (!DB.Unique_Alias(parentGuid, guid))
            {
                Functions.Output("Alias not unique (ParseObjectNode): " + guid);
                System.exit(1);
            }

            if (!elem.GetProperty("meta").equals("")) meta = elem.GetProperty("meta");
            if (!elem.GetProperty("inherit").equals("")) inherit = elem.GetProperty("inherit");
            type = elem.GetProperty("type");
            if (!elem.GetProperty("chance").equals("")) chance = Integer.parseInt(elem.GetProperty("chance"));
            label = elem.GetProperty("label");

            if (!elem.GetProperty("sort").equals("")) 
            {
                try
                {
                    custSort = Integer.parseInt(elem.GetProperty("sort"));
                    sort = custSort;
                }
                catch (Exception ex) {}
            }

            // Insert Object
            DB.Insert_Object(parentGuid, parentType, guid, type, chance, label, meta, sort, elem.GetFilePath(), inherit);

            // Insert into Inventory
            if (parentType.equals("player"))
            {
                DB.Insert_Inventory("", guid, label, 1);
            }

            if (VERBOSE) Functions.OutputRaw("Object Parsed: " + guid);

            // Parse Logic Sets
            ParseLogicSets(guid, "object", elem, guid);

            // Parse Object Attributes
            ParseAttributes(guid, "object", elem, guid);

            // Parse Events
            ParseEvents(guid, "object", elem, guid);

            // Parse Commands
            ParseCommands(guid, "object", elem, guid);

            // Parse MessageSets
            ParseMessageSets(guid, "object", elem, guid);

            // Parse Objects
            ParseObjects(guid, "object", elem);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Attributes
    private void ParseAttributes(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = Functions.GetGUID();
        String value = "";
        String type = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element attElem : elem.GetElements())
            {
                type = "";
                value = "";

                if (attElem.GetElementName().equals("attribute") || attElem.GetElementName().equals("attr"))
                {
                    if (!attElem.GetProperty("alias").equals("")) guid = attElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!attElem.GetProperty("type").equals("")) type = attElem.GetProperty("type");
                    value = attElem.GetProperty("value").replace("{self}", self);

                    // Insert Attribute
                    DB.Insert_Attribute(parentGuid, parentType, guid, type, value, sort);

                    if (VERBOSE) Functions.OutputRaw("Attribute Parsed: " + guid);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Events
    private void ParseEvents(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = "";
        String type = "";
        int intervalMinutes = -1;
        int repeat = -1;
        int sortAttrib = 0;
        int sort = 0;

        try
        {
            for (SysNorman.Element evElem : elem.GetElements())
            {
                if (evElem.GetElementName().equals("event") || evElem.GetElementName().equals("evt"))
                {
                    if (!evElem.GetProperty("alias").equals("")) guid = evElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!DB.Unique_Alias(parentGuid, guid))
                    {
                        Functions.Output("Alias not unique (ParseEvents): " + guid);
                        System.exit(1);
                    }

                    type = evElem.GetProperty("type");

                    if (!evElem.GetProperty("interval_minutes").equals("")) intervalMinutes = Integer.parseInt(evElem.GetProperty("interval_minutes"));

                    try
                    {
                        if (!evElem.GetProperty("repeat").equals("")) repeat = Integer.parseInt(evElem.GetProperty("repeat"));
                    }
                    catch (Exception ex)
                    {
                        Functions.Output("An Event's repeat property must be a numeric value (Event type: " + type + ").");
                        System.exit(1);
                    }

                    if (!evElem.GetProperty("sort").equals(""))
                    {
                        try
                        {
                            sortAttrib = Integer.parseInt(evElem.GetProperty("sort"));
                        }
                        catch (Exception ex) {}
                    }
                    else
                    {
                        sortAttrib = sort;
                    }

                    // Insert the Event
                    DB.Insert_Event(parentGuid, parentType, guid, type, intervalMinutes, repeat, evElem.GetFilePath(), sortAttrib);

                    if (VERBOSE) Functions.OutputRaw("Event Parsed: " + guid);

                    sort++;

                    // Parse MessageSets
                    ParseMessageSets(guid, "event", evElem, self);
                    // Parse Logic Sets
                    ParseLogicSets(guid, "event", evElem, self);
                    // Parse Action Sets
                    ParseActionSets(guid, "event", evElem, self);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Commands
    private void ParseCommands(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = "";
        String syntax = "";
        int sort = 1;

        try
        {
            for (SysNorman.Element cmdElem : elem.GetElements())
            {
                if (!cmdElem.GetElementName().equals("command") && !cmdElem.GetElementName().equals("cmd")) continue;

                if (!cmdElem.GetProperty("alias").equals("")) guid = cmdElem.GetProperty("alias");
                else guid = Functions.GetGUID();

                if (!DB.Unique_Alias(parentGuid, guid))
                {
                    Functions.Output("Alias not unique (ParseCommands): " + guid);
                    System.exit(1);
                }

                syntax = cmdElem.GetProperty("syntax");

                if (syntax.equals(""))
                {
                    Functions.Output("The Command syntax cannot be blank (Parent: " + parentGuid + ", type: " + parentType + ", elem: " + elem + ")");
                    System.exit(1);
                }

                try
                {
                    if (!cmdElem.GetProperty("sort").equals("")) sort = Integer.parseInt(cmdElem.GetProperty("sort"));
                }
                catch (Exception ex)
                {
                    sort = 1;
                }

                // Insert the Command
                DB.Insert_Command(parentGuid, parentType, guid, syntax, cmdElem.GetFilePath(), sort);

                if (VERBOSE) Functions.OutputRaw("Command Parsed: " + guid);

                // Parse Logic Sets
                ParseLogicSets(guid, "command", cmdElem, self);

                // Parse Action Sets
                ParseActionSets(guid, "command", cmdElem, self);

                // Parse MessageSets
                ParseMessageSets(guid, "command", cmdElem, self);

                sort++;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse ActionSets
    private void ParseActionSets(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = "";
        int repeat = -1;
        int sort = 0;

        try
        {
            // Parse ActionSets
            for (SysNorman.Element actElem : elem.GetElements())
            {
                // Single Actions can exist outside of ActionSets.
                // Create a wrapper ActionSet around the Action - for the DB.
                if (actElem.GetElementName().equals("action") || actElem.GetElementName().equals("act"))
                {
                    guid = Functions.GetGUID();
                    DB.Insert_ActionSet(parentGuid, parentType, guid, repeat, sort);

                    // Parse the Action item
                    ParseActionItem(guid, actElem, self);

                    sort++;
                }


                if (!actElem.GetElementName().equals("actionset") && !actElem.GetElementName().equals("aset")) continue;

                if (!actElem.GetProperty("alias").equals("")) guid = actElem.GetProperty("alias");
                else guid = Functions.GetGUID();

                if (!DB.Unique_Alias(parentGuid, guid))
                {
                    Functions.Output("Alias not unique (ParseActionSets): " + guid);
                    System.exit(1);
                }

                try
                {
                    if (!actElem.GetProperty("repeat").equals("")) repeat = Integer.parseInt(actElem.GetProperty("repeat"));
                }
                catch(Exception ex)
                {
                    Functions.Output("(ParseActionSets) Bad integer conversion from: " + actElem.GetProperty("repeat"));
                    System.exit(1);
                }

                // Insert the ActionSet
                DB.Insert_ActionSet(parentGuid, parentType, guid, repeat, sort);

                if (VERBOSE) Functions.OutputRaw("Action Set Parsed: " + guid);

                sort++;

                // Parse Logic Sets
                ParseLogicSets(guid, "actionset", actElem, self);

                // Parse the Actions
                ParseActions(guid, actElem, self);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Actions
    private void ParseActions(String actionSetGuid, SysNorman.Element elem, String self)
    {
        try
        {
            for (SysNorman.Element actElem : elem.GetElements())
            {
                if (actElem.GetElementName().equals("action") || actElem.GetElementName().equals("act"))
                {
                    ParseActionItem(actionSetGuid, actElem, self);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    private void ParseActionItem(String actionSetGuid, SysNorman.Element actElem, String self)
    {
        String guid = "";
        String type = "";
        String source = "";
        String newValue = "";
        int repeat = -1;
        int sortAttrib = 0;
        int sort = 0;

        try
        {
            if (!actElem.GetProperty("alias").equals("")) guid = actElem.GetProperty("alias");
            else guid = Functions.GetGUID();

            if (!DB.Unique_Alias(actionSetGuid, guid))
            {
                Functions.Output("Alias not unique (ParseActions): " + guid);
                System.exit(1);
            }

            type = actElem.GetProperty("type");
            source = actElem.GetProperty("source").replace("{self}", self);

            if (Functions.Match(type, "event"))
            {
                newValue = actElem.GetProperty("value");
            }
            else
            {
                newValue = actElem.GetProperty("newvalue").replace("{self}", self);
            }

            if (!actElem.GetProperty("sort").equals(""))
            {
                try
                {
                    sortAttrib = Integer.parseInt(actElem.GetProperty("sort"));
                }
                catch (Exception ex) {}
            }
            else
            {
                sortAttrib = sort;
            }

            if (!actElem.GetProperty("repeat").equals(""))
            {
                try
                {
                    repeat = Integer.parseInt(actElem.GetProperty("repeat"));
                }
                catch (Exception ex)
                {
                    repeat = -1;
                }
            }

            // Insert the Action
            DB.Insert_Action(actionSetGuid, guid, type, source, newValue, repeat, sortAttrib);

            // Parse Logic Sets
            ParseLogicSets(guid, "action", actElem, self);

            // Parse MessageSets
            ParseMessageSets(guid, "action", actElem, self);

            if (VERBOSE) Functions.OutputRaw("Action Parsed: " + guid);

            sort++;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse LogicSets
    private void ParseLogicSets(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = "";
        int sort = 0;
        int sortEval = 0;
        SysNorman.Element failElem = null;
        String operand = "";
        String failEvent = "";
        String failMsg = "";
        String eval = "";
        boolean hasChildren = false;

        try
        {
            for (SysNorman.Element lgElem : elem.GetElements())
            {
                guid = "";

                if (lgElem.GetElementName().equals("logicset") || lgElem.GetElementName().equals("lset"))
                {
                    failMsg = "";
                    hasChildren = false;

                    for (SysNorman.Element flElem : lgElem.GetElements())
                    {
                        if (flElem.GetElementName().equals("fail"))
                        {
                            if (!failMsg.equals("")) failMsg += "\n";
                            failMsg += flElem.GetContent();
                        }
                    }

                    if (!lgElem.GetProperty("operand").equals("")) operand = lgElem.GetProperty("operand");
                    else operand = "and";

                    if (!lgElem.GetProperty("alias").equals("")) guid = lgElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!lgElem.GetProperty("fail_event").equals("")) failEvent = lgElem.GetProperty("fail_event");

                    if (!DB.Unique_Alias(parentGuid, guid))
                    {
                        Functions.Output("Alias not unique (ParseLogicSets): " + guid);
                        System.exit(1);
                    }

                    // Insert the LogicSet
                    DB.Insert_LogicSet(parentGuid, parentType, guid, failEvent, failMsg, operand, sort);

                    if (VERBOSE) Functions.OutputRaw("Logic Set Parsed: " + guid);

                    // Parse the lset level Evals
                    for (SysNorman.Element evElem : lgElem.GetElements())
                    {
                        if (evElem.GetElementName().equals("eval"))
                        {
                            hasChildren = true;

                            eval = evElem.GetContent().replace("{self}", self);

                            // Insert a Logic Block w/ the Eval in it
                            DB.Insert_Logic(guid, "", "", "", "", "", eval, sortEval);
                            sortEval++;

                            if (VERBOSE) Functions.OutputRaw("Logic Set Eval Parsed: " + eval);
                        }
                    }

                    sort++;

                    // Parse the Logic Blocks
                    if (!hasChildren)
                    {
                        hasChildren = ParseLogic(guid, lgElem, self);
                    }

                    if (!hasChildren)
                    {
                        Functions.Output("LogicSet missing an eval or logic child component. File: " + elem.GetFilePath());
                        System.exit(1);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Logic Blocks
    private boolean ParseLogic(String logicSetGuid, SysNorman.Element elem, String self)
    {
        boolean output = false;
        String type = "";
        String source = "";
        String sourceValue = "";
        String newValue = "";
        String operand = "";
        String eval = "";
        int sort = 0;
        boolean err = false;

        try
        {
            for (SysNorman.Element lgElem : elem.GetElements())
            {
                if (lgElem.GetElementName().equals("logic") || lgElem.GetElementName().equals("lgc"))
                {
                    output = true;

                    for (SysNorman.Element evElem : lgElem.GetElements())
                    {
                        if (evElem.GetElementName().equals("eval"))
                        {
                            eval = evElem.GetContent().replace("{self}", self);
                        }
                    }

                    // There is no EVAL string - so collect the properties
                    if (eval.equals(""))
                    {
                        type = lgElem.GetProperty("type");
                        source = lgElem.GetProperty("source").replace("{self}", self);
                        sourceValue = lgElem.GetProperty("sourcevalue").replace("{self}", self);

                        if (type.equals("attribute") && lgElem.GetProperty("operand").equals(""))
                        {
                            Functions.Output("Missing operand attribute (" + type + ", " + source + ", " + sourceValue + ").");
                            err = true;
                        }
                        else
                        {
                            operand = lgElem.GetProperty("operand");
                        }

                        if (err)
                        {
                            break;
                        }
                    }

                    // Insert the Logic block
                    DB.Insert_Logic(logicSetGuid, type, source, sourceValue, newValue, operand, eval, sort);

                    if (VERBOSE) Functions.OutputRaw("Logic Parsed for Logic Set: " + logicSetGuid + ", Type: " + type + ", Source: " + source + ", Source Value: " + sourceValue + ", Operand: " + operand);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }

        return output;
    }

    // Parse Message Sets
    private void ParseMessageSets(String parentGuid, String parentType, SysNorman.Element elem, String self)
    {
        String guid = "";
        String repeatType = "none";
        String templateId = "";
        String output = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element msgElem : elem.GetElements())
            {
                // Single Messages can reside outside of MessageSets.
                // Check for these first, and create a MessageSet wrapper
                // around them for the DB.
                if (msgElem.GetElementName().equals("message") || msgElem.GetElementName().equals("msg"))
                {
                    guid = Functions.GetGUID();
                    // Insert the MessageSet wrapper
                    DB.Insert_MessageSet(parentGuid, parentType, guid, repeatType, templateId, sort);

                    // Create the Message itself
                    output = msgElem.GetContent().replace("{self}", self);

                    // Insert the Message
                    DB.Insert_Message(guid, output, sort);

                    if (VERBOSE) Functions.OutputRaw("Message Parsed for Message Set: " + guid + ", Output: " + output);

                    sort++;
                }


                // Now handle the MessageSets
                if (msgElem.GetElementName().equals("messageset") || msgElem.GetElementName().equals("mset"))
                {
                    guid = Functions.GetGUID();

                    if (!msgElem.GetProperty("alias").equals("")) guid = msgElem.GetProperty("alias");

                    if (!DB.Unique_Alias(parentGuid, guid))
                    {
                        Functions.Output("Alias not unique (Parse MessageSets): " + guid);
                        System.exit(1);
                    }

                    if (!msgElem.GetProperty("repeat").equals("")) repeatType = msgElem.GetProperty("repeat");
                    if (!msgElem.GetProperty("message_alias").equals("")) templateId = msgElem.GetProperty("message_alias");

                    // Insert the MessageSet
                    DB.Insert_MessageSet(parentGuid, parentType, guid, repeatType, templateId, sort);

                    if (VERBOSE) Functions.OutputRaw("Message Set Parsed: " + guid);

                    sort++;

                    // Parse Game Attributes
                    ParseAttributes(guid, "messageset", msgElem, guid);

                    // Parse Logic Sets
                    ParseLogicSets(guid, "messageset", msgElem, self);

                    // Parse the Messages
                    ParseMessages(guid, msgElem, self);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Messages
    private void ParseMessages(String messageSetGuid, SysNorman.Element elem, String self)
    {
        String output = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element msgElem : elem.GetElements())
            {
                if (msgElem.GetElementName().equals("message") || msgElem.GetElementName().equals("msg"))
                {
                    output = msgElem.GetContent().replace("{self}", self);

                    // Insert the Logic block
                    DB.Insert_Message(messageSetGuid, output, sort);

                    if (VERBOSE) Functions.OutputRaw("Message Parsed for Message Set: " + messageSetGuid + ", Output: " + output);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Sort
    private void ParseSort(SysNorman.Element elem)
    {
        String itms = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element sortElem : elem.GetElements())
            {
                if (sortElem.GetElementName().equals("sort"))
                {
                    for (SysNorman.Element itmElem : sortElem.GetElements())
                    {
                        // Objects
                        if (itmElem.GetElementName().equals("object") || itmElem.GetElementName().equals("obj"))
                        {
                            itms = itmElem.GetContent();

                            String[] guids = itms.split(System.getProperty("line.separator"));

                            // Update the Sort
                            DB.Update_Sort("Object", guids);
                            DB.Update_Sort("Npc", guids);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }




    /* Unit Testing Elements */

    // Parse Unit Tests
    private void ParseUnitTests()
    {
        String guid = "";

        try
        {
            for (SysNorman.Element elem : GetNorman().GetElements())
            {
                if (!elem.GetElementName().equals("unit")) continue;

                if (!elem.GetProperty("alias").equals("")) guid = elem.GetProperty("alias");
                else guid = Functions.GetGUID();

                DB.Insert_UnitTest(guid);

                if (VERBOSE) Functions.OutputRaw("UnitTest Parsed: " + guid);

                // Parse Steps
                ParseSteps(guid, elem);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Steps
    private void ParseSteps(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        String name = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element stElem : parentElem.GetElements())
            {
                guid = "";
                name = "";

                if (stElem.GetElementName().equals("step"))
                {
                    if (!stElem.GetProperty("alias").equals("")) guid = stElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    name = stElem.GetProperty("name");

                    // Insert Step
                    DB.Insert_Step(guid, parentGuid, name, sort);

                    if (VERBOSE) Functions.OutputRaw("Step Parsed: " + guid);

                    // Parse Inputs
                    ParseInputs(guid, stElem);

                    // Parse Asserts
                    ParseAsserts(guid, stElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Asserts
    /**
        Asserts group together various comparisons and their pass/fail messages
    */
    private void ParseAsserts(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        String name = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element asElem : parentElem.GetElements())
            {
                guid = "";
                name = "";

                if (asElem.GetElementName().equals("assert"))
                {
                    if (!asElem.GetProperty("alias").equals("")) guid = asElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    if (!asElem.GetProperty("name").equals("")) name = asElem.GetProperty("name");

                    // Insert Assert
                    DB.Insert_Assert(guid, name, parentGuid, sort);

                    if (VERBOSE) Functions.OutputRaw("Assert Parsed: " + guid);

                    // Parse Inputs
                    ParseInputs(guid, asElem);

                    // Parse Evals
                    ParseEvals(guid, asElem);

                    // Parse Results
                    ParseResults(guid, asElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Inputs
    private void ParseInputs(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        String text = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element inElem : parentElem.GetElements())
            {
                guid = "";
                text = "";

                if (inElem.GetElementName().equals("input"))
                {
                    if (!inElem.GetProperty("alias").equals("")) guid = inElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    // Get the text element
                    for (SysNorman.Element txElem : inElem.GetElements())
                    {
                        if (txElem.GetElementName().equals("text"))
                        {
                            text += txElem.GetContent();
                        }
                    }

                    // Insert Input
                    DB.Insert_Input(guid, parentGuid, sort, text);

                    if (VERBOSE) Functions.OutputRaw("Input Parsed: " + guid);

                    // Parse Inputs
                    ParseEvals(guid, inElem);

                    // Parse Results
                    ParseResults(guid, inElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Evals
    private void ParseEvals(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        int pass = 1;
        int sort = 0;

        try
        {
            for (SysNorman.Element evElem : parentElem.GetElements())
            {
                guid = "";
                pass = 1;

                if (evElem.GetElementName().equals("eval"))
                {
                    if (!evElem.GetProperty("alias").equals("")) guid = evElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    // Insert Eval
                    DB.Insert_Eval(guid, parentGuid, (pass == 1), sort);

                    if (VERBOSE) Functions.OutputRaw("Eval Parsed: " + guid);

                    // Parse Comparisons
                    ParseComparisons(guid, evElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Results
    private void ParseResults(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        int sort = 0;

        try
        {
            for (SysNorman.Element resElem : parentElem.GetElements())
            {
                guid = "";

                if (resElem.GetElementName().equals("result"))
                {
                    if (!resElem.GetProperty("alias").equals("")) guid = resElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    // Insert Result
                    DB.Insert_Result(guid, parentGuid, sort);

                    if (VERBOSE) Functions.OutputRaw("Result Parsed: " + guid);

                    // Parse Inputs
                    ParseResultMessages(guid, resElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Result Messages
    private void ParseResultMessages(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        int fail = 0;
        String text = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element resElem : parentElem.GetElements())
            {
                guid = "";
                fail = 0;
                text = "";

                if (resElem.GetElementName().equals("fail"))
                {
                    fail = 1;
                }
                if (resElem.GetElementName().equals("pass"))
                {
                    fail = 0;
                }

                if (resElem.GetElementName().equals("pass") ||
                    resElem.GetElementName().equals("fail"))
                {
                    if (!resElem.GetProperty("alias").equals("")) guid = resElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    for (SysNorman.Element mElem : resElem.GetElements())
                    {
                        if (mElem.GetElementName().equals("text"))
                        {
                            text += mElem.GetContent();
                        }
                    }

                    // Insert Result
                    DB.Insert_ResultMessage(guid, parentGuid, (fail == 1), text, sort);

                    if (VERBOSE) Functions.OutputRaw("Result Message Parsed: " + guid);

                    // Parse Inputs
                    ParseInputs(guid, resElem);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    // Parse Comparisons
    /**
        Comparisons are always made against the output of a game command.
    */
    private void ParseComparisons(String parentGuid, SysNorman.Element parentElem)
    {
        String guid = Functions.GetGUID();
        String operand = "";
        String checkText = "";
        int sort = 0;

        try
        {
            for (SysNorman.Element cpElem : parentElem.GetElements())
            {
                guid = "";
                operand = "";
                checkText = "";

                if (cpElem.GetElementName().equals("compare"))
                {
                    if (!cpElem.GetProperty("alias").equals("")) guid = cpElem.GetProperty("alias");
                    else guid = Functions.GetGUID();

                    operand = cpElem.GetProperty("operand");

                    // Get the text
                    for (SysNorman.Element txElem : cpElem.GetElements())
                    {
                        if (txElem.GetElementName().equals("text"))
                        {
                            checkText += txElem.GetContent();
                        }
                    }

                    // Insert Comparison
                    DB.Insert_Comparison(guid, parentGuid, operand, checkText, sort);

                    if (VERBOSE) Functions.OutputRaw("Comparison Parsed: " + guid);

                    sort++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
