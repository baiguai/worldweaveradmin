import java.io.File;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Random;

/*
    DATABASE
    ----------------------------------------------------------------------------
    Handles the game's database.
    ----------------------------------------------------------------------------
*/
public class SysDatabase
{
    /* Properties */
    //
        private static String DBPath;
        private static Connection CONN = null;
        private static int max_insert = 400;

        // One SQL Insert statement per table
        private String sql_Log = "";
        private String sql_Tests = "";
        private String sql_Variable = "";
        private String sql_Game = "";
        private String sql_Help = "";
        private String sql_Room = "";
        private String sql_Npc = "";
        private String sql_NpcTravelSet = "";
        private String sql_NpcTravel = "";
        private String sql_Object = "";
        private String sql_InitObject = "";
        private String sql_Attribute = "";
        private String sql_InitAttribute = "";
        private String sql_Event = "";
        private String sql_Command = "";
        private String sql_ActionSet = "";
        private String sql_Action = "";
        private String sql_Inventory = "";
        private String sql_LogicSet = "";
        private String sql_Logic = "";
        private String sql_MessageSet = "";
        private String sql_Message = "";

        private String sql_UnitTest = "";
        private String sql_Step = "";
        private String sql_Input = "";
        private String sql_Assert = "";
        private String sql_Eval = "";
        private String sql_Comparison = "";
        private String sql_Result = "";
        private String sql_ResultMessage = "";

        private int int_Log = 0;
        private int int_Tests = 0;
        private int int_Variable = 0;
        private int int_Game = 0;
        private int int_Help = 0;
        private int int_Room = 0;
        private int int_Npc = 0;
        private int int_NpcTravelSet = 0;
        private int int_NpcTravel = 0;
        private int int_Object = 0;
        private int int_InitObject = 0;
        private int int_Attribute = 0;
        private int int_InitAttribute = 0;
        private int int_Event = 0;
        private int int_Command = 0;
        private int int_ActionSet = 0;
        private int int_Action = 0;
        private int int_Inventory = 0;
        private int int_LogicSet = 0;
        private int int_Logic = 0;
        private int int_MessageSet = 0;
        private int int_Message = 0;

        private int int_UnitTest = 0;
        private int int_Step = 0;
        private int int_Input = 0;
        private int int_Assert = 0;
        private int int_Eval = 0;
        private int int_Comparison = 0;
        private int int_Result = 0;
        private int int_ResultMessage = 0;
    //


    // Constructor
    public SysDatabase(String dbPath)
    {
        DBPath = dbPath;
        SetConnection();
    }


    // Setup the Connection
    private void SetConnection()
    {
        try
        {
            File f = new File(DBPath);
            f.delete();
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

        Functions.OutputRaw("Creating game database (" + DBPath + ")...");
        CreateDatabase();
    }


    // Create the Database
    private void CreateDatabase()
    {
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();

            // Test Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Test ( ";
            sql += "    TestName VARCHAR(100) NOT NULL, ";
            sql += "    Commands BLOB NOT NULL ";
            sql += ");";
            sql += "CREATE INDEX idx_test ON Test (TestName);";
            cmd.execute(sql);

            // Testing Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Testing ( ";
            sql += "    RoomGUID VARCHAR(100) NOT NULL, ";
            sql += "    Pass BOOL NOT NULL DEFAULT 0, ";
            sql += "    Notes BLOB, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_testing ON Testing (RoomGUID);";
            cmd.execute(sql);



            /* Unit Test Tables */
            // Unit Test Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "UnitTest ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_unittest ON UnitTest (GUID);";
            cmd.execute(sql);

            // Step Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Step ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Name VARCHAR(200), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_step ON Step (GUID);";
            cmd.execute(sql);

            // Input Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Input ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Text BLOB NOT NULL, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_input ON Step (GUID);";
            cmd.execute(sql);

            // Assert Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Assert ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Name VARCHAR(200), ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_assert ON Assert (GUID);";
            cmd.execute(sql);

            // Eval Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Eval ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Pass BOOL NOT NULL DEFAULT 1, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_eval ON Eval (GUID);";
            cmd.execute(sql);

            // Comparison Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Comparison ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Operand VARCHAR(100) NOT NULL, ";
            sql += "    CheckText BLOB NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_comparison ON Comparison (GUID);";
            cmd.execute(sql);

            // Result Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Result ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_result ON Result (GUID);";
            cmd.execute(sql);

            // Result Message Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "ResultMessage ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Fail BOOL NOT NULL DEFAULT 1, ";
            sql += "    Text BLOB NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_resultmessage ON ResultMessage (GUID);";
            cmd.execute(sql);



            // Log Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Log ( ";
            sql += "    LogType VARCHAR(100) NOT NULL, ";
            sql += "    FilePath BLOB NOT NULL, ";
            sql += "    Message BLOB, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_log ON Log (LogType);";
            cmd.execute(sql);

            // Variable Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Variable ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Value VARCHAR(600) NOT NULL, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += "); ";
            sql += "CREATE INDEX idx_var ON Variable (GUID);";
            cmd.execute(sql);

            // Game Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Game ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Name VARCHAR(200) NOT NULL, ";
            sql += "    Build VARCHAR(100) NOT NULL, ";
            sql += "    InitialLocation VARCHAR(100) NOT NULL, ";
            sql += "    AdminPass VARCHAR(100), ";
            sql += "    StatsGuids BLOB, ";
            sql += "    Splash BLOB, ";
            sql += "    Credits BLOB ";
            sql += ");";
            sql += "CREATE INDEX idx_game ON Game (GUID);";
            cmd.execute(sql);

            // Player Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Player ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Name VARCHAR(200) NOT NULL, ";
            sql += "    Location VARCHAR(100) NOT NULL, ";
            sql += "    ArmedWeapon VARCHAR(100), ";
            sql += "    Points BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_player ON Player (GUID);";
            cmd.execute(sql);

            // Player Notes Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "PlayerNote ( ";
            sql += "    PlayerGUID VARCHAR(100) NOT NULL, ";
            sql += "    Note BLOB NOT NULL, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_playernote ON PlayerNote (PlayerGUID);";
            cmd.execute(sql);

            // NPC Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Npc ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Name VARCHAR(200) NOT NULL, ";
            sql += "    Meta VARCHAR(400), ";
            sql += "    Location VARCHAR(100) NOT NULL, ";
            sql += "    Inherit VARCHAR(100), ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_npc ON Npc (GUID);";
            cmd.execute(sql);

            // NPC Travel
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "NpcTravelSet ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    NpcGUID VARCHAR(100) NOT NULL, ";
            sql += "    WaitMinute BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Mode VARCHAR(100), ";
            sql += "    GoingForward BOOL NOT NULL DEFAULT 1, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_npctravelset ON NpcTravelSet (GUID, NpcGUID);";
            cmd.execute(sql);

            // NPC Travel
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "NpcTravelSetIndex ( ";
            sql += "    NpcTravelSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    NpcTravelSetIndex BIGINT NOT NULL DEFAULT 0, ";
            sql += "    GoingForward BOOL NOT NULL DEFAULT 1, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_npctravelsetindex ON NpcTravelSetIndex (NpcTravelSetGUID);";
            cmd.execute(sql);

            sql =  "";
            sql += "CREATE TABLE ";
            sql += "NpcTravel ( ";
            sql += "    NpcTravelSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    Location VARCHAR(200), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_npctravel ON NpcTravel (NpcTravelSetGUID);";
            cmd.execute(sql);

            // Inventory Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Inventory ( ";
            sql += "    PlayerGUID VARCHAR(100) NOT NULL, ";
            sql += "    ObjectGUID VARCHAR(100) NOT NULL, ";
            sql += "    ObjectName VARCHAR(200) NOT NULL, ";
            sql += "    ObjectCount BIGINT NOT NULL DEFAULT 1, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_inventory ON Inventory (PlayerGUID, ObjectGUID);";
            cmd.execute(sql);

            // InitObject Table
            // Used for resetting a game
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "InitObject ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100), ";
            sql += "    Chance BIGINT, ";
            sql += "    Label VARCHAR(200) NOT NULL, ";
            sql += "    Meta VARCHAR(400), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Inherit VARCHAR(100), ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_initobject ON InitObject (GUID, ParentGUID, ParentType);";
            cmd.execute(sql);

            // Object Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Object ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100), ";
            sql += "    Chance BIGINT, ";
            sql += "    Label VARCHAR(200) NOT NULL, ";
            sql += "    Meta VARCHAR(400), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Inherit VARCHAR(100), ";
            sql += "    Count BIGINT NOT NULL DEFAULT 1, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_object ON Object (GUID, ParentGUID, ParentType, Type);";
            cmd.execute(sql);

            // Room Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Room ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Label VARCHAR(200), ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_room ON Room (GUID);";
            cmd.execute(sql);

            // InitAttribute Table
            // Used for resetting a game
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "InitAttribute ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100), ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Value BLOB, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_initattribute ON InitAttribute (GUID, Type, ParentGUID, ParentType);";
            cmd.execute(sql);

            // Attribute Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Attribute ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100), ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Value BLOB, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_attribute ON Attribute (GUID, Type);";
            cmd.execute(sql);

            // MessageSet Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "MessageSet ( ";
            sql += "    GUID VARCHAR(100), ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    RepeatType VARCHAR(50) NOT NULL DEFAULT 'repeat', ";
            sql += "    TemplateMessageSetGUID VARCHAR(100), ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_messageset ON MessageSet (GUID, ParentGUID, ParentType);";
            cmd.execute(sql);

            // Player MessageSetIndex Table
            sql = "";
            sql += "CREATE TABLE ";
            sql += "PlayerMessageSetIndex ( ";
            sql += "    PlayerGUID VARCHAR(100) NOT NULL, ";
            sql += "    MessageSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    MessageSetIndex BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_playermessagesetindex ON PlayerMessageSetIndex (PlayerGUID, MessageSetGUID);";
            cmd.execute(sql);

            // Message Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Message ( ";
            sql += "    MessageSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    Output BLOB, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_message ON Message (MessageSetGUID);";
            cmd.execute(sql);

            // Event Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Event ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100) NOT NULL, ";
            sql += "    InitialTime BIGINT NOT NULL DEFAULT 0, ";
            sql += "    IntervalMinutes BIGINT NOT NULL DEFAULT -1, ";
            sql += "    RepeatCount BIGINT NOT NULL DEFAULT -1, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_event ON Event (GUID, ParentGUID, ParentType, Type);";
            cmd.execute(sql);

            // Player EventFired Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "PlayerEventFired ( ";
            sql += "    PlayerGUID VARCHAR(100) NOT NULL, ";
            sql += "    EventGUID VARCHAR(100) NOT NULL, ";
            sql += "    EventFired BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_playereventfired ON PlayerEventFired (PlayerGUID, EventGUID);";
            cmd.execute(sql);

            // Player ActionFired Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "PlayerActionSetFired ( ";
            sql += "    PlayerGUID VARCHAR(100) NOT NULL, ";
            sql += "    ActionSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    ActionSetFired BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_playeractionsetfired ON PlayerActionSetFired (PlayerGUID, ActionSetGUID);";
            cmd.execute(sql);

            // Command Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Command ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Syntax VARCHAR(200) NOT NULL, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 1, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_command ON Command (GUID, ParentGUID, ParentType, Syntax);";
            cmd.execute(sql);

            // ActionSet Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "ActionSet ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    RepeatCount BIGINT NOT NULL DEFAULT -1, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_actionset ON ActionSet (GUID, ParentGUID, ParentType);";
            cmd.execute(sql);

            // Action Table
            /*
                - Repeat : if -1, action is never disabled
                           otherwise, only fires specified
                           amount of times then is disabled.
            */
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Action ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100) NOT NULL, ";
            sql += "    Source VARCHAR(200), ";
            sql += "    NewValue BLOB, ";
            sql += "    Repeat BIGINT NOT NULL DEFAULT -1, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_action ON Action (GUID, ParentGUID, Type);";
            cmd.execute(sql);

            // Action Fired
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "ActionFired ( ";
            sql += "    ActionGUID VARCHAR(100) NOT NULL, ";
            sql += "    ActionFired BIGINT NOT NULL DEFAULT 0, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_actionfired ON ActionFired (ActionGUID);";
            cmd.execute(sql);

            // LogicSet Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "LogicSet ( ";
            sql += "    GUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentGUID VARCHAR(100) NOT NULL, ";
            sql += "    ParentType VARCHAR(100) NOT NULL, ";
            sql += "    Operand VARCHAR(25) NOT NULL DEFAULT 'and', ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FailEvent VARCHAR(100), ";
            sql += "    FailMessage BLOB, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_logicset ON LogicSet (GUID, ParentGUID, ParentType);";
            cmd.execute(sql);

            // Logic Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Logic ( ";
            sql += "    LogicSetGUID VARCHAR(100) NOT NULL, ";
            sql += "    Type VARCHAR(100) NOT NULL, ";
            sql += "    Source VARCHAR(200), ";
            sql += "    SourceValue BLOB, ";
            sql += "    NewValue BLOB, ";
            sql += "    Operand VARCHAR(20) NOT NULL, ";
            sql += "    Eval BLOB, ";
            sql += "    Sort BIGINT NOT NULL DEFAULT 0, ";
            sql += "    FileName VARCHAR(200), ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_logic ON Logic (LogicSetGUID, Type);";
            cmd.execute(sql);

            // Help Table
            sql =  "";
            sql += "CREATE TABLE ";
            sql += "Help ( ";
            sql += "    Syntax VARCHAR(200) NOT NULL, ";
            sql += "    Title VARCHAR(200), ";
            sql += "    Topic BLOB, ";
            sql += "    Deleted BOOL NOT NULL DEFAULT 0 ";
            sql += ");";
            sql += "CREATE INDEX idx_help ON Help (Syntax, Title, Topic);";
            cmd.execute(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /* Insertion Methods */

    // Insert UnitTest
    public void Insert_UnitTest(String guid)
    {
        guid = Functions.SqlCleanup(guid);

        if (int_UnitTest >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_UnitTest.equals("")) sql_UnitTest += " UNION ";
        sql_UnitTest += "SELECT ";
        sql_UnitTest += "    '" + guid + "' ";

        int_UnitTest++;
    }

    // Insert Step
    public void Insert_Step(String guid, String parentGuid, String name, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);
        name = Functions.SqlCleanup(name);

        if (int_Step >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Step.equals("")) sql_Step += " UNION ";
        sql_Step += "SELECT ";
        sql_Step += "    '" + guid + "', ";
        sql_Step += "    '" + parentGuid + "', ";
        sql_Step += "    '" + name + "', ";
        sql_Step += "    " + sort + " ";

        int_Step++;
    }

    // Insert Input
    public void Insert_Input(String guid, String parentGuid, int sort, String text)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);
        text = Functions.SqlCleanup(text);

        if (int_Input >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Input.equals("")) sql_Input += " UNION ";
        sql_Input += "SELECT ";
        sql_Input += "    '" + guid + "', ";
        sql_Input += "    '" + parentGuid + "', ";
        sql_Input += "    " + sort + ", ";
        sql_Input += "    '" + text + "' ";

        int_Input++;
    }

    // Insert Assert
    public void Insert_Assert(String guid, String name, String parentGuid, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        name = Functions.SqlCleanup(name);
        parentGuid = Functions.SqlCleanup(parentGuid);

        if (int_Assert >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Assert.equals("")) sql_Assert += " UNION ";
        sql_Assert += "SELECT ";
        sql_Assert += "    '" + guid + "', ";
        sql_Assert += "    '" + name + "', ";
        sql_Assert += "    '" + parentGuid + "', ";
        sql_Assert += "    " + sort + " ";

        int_Assert++;
    }

    // Insert Eval
    public void Insert_Eval(String guid, String parentGuid, boolean pass, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);

        int passVal = 0;
        if (pass) passVal = 1;

        if (int_Eval >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Eval.equals("")) sql_Eval += " UNION ";
        sql_Eval += "SELECT ";
        sql_Eval += "    '" + guid + "', ";
        sql_Eval += "    '" + parentGuid + "', ";
        sql_Eval += "    " + passVal + ", ";
        sql_Eval += "    " + sort + " ";

        int_Eval++;
    }

    // Insert Comparison
    public void Insert_Comparison(String guid, String parentGuid, String operand, String checkText, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);
        operand = Functions.SqlCleanup(operand);
        checkText = Functions.SqlCleanup(checkText);

        if (int_Comparison >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Comparison.equals("")) sql_Comparison += " UNION ";
        sql_Comparison += "SELECT ";
        sql_Comparison += "    '" + guid + "', ";
        sql_Comparison += "    '" + parentGuid + "', ";
        sql_Comparison += "    '" + operand + "', ";
        sql_Comparison += "    '" + checkText + "', ";
        sql_Comparison += "    " + sort + " ";

        int_Comparison++;
    }

    // Insert Result
    public void Insert_Result(String guid, String parentGuid, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);

        if (int_Result >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Result.equals("")) sql_Result += " UNION ";
        sql_Result += "SELECT ";
        sql_Result += "    '" + guid + "', ";
        sql_Result += "    '" + parentGuid + "', ";
        sql_Result += "    " + sort + " ";

        int_Result++;
    }

    // Insert ResultMessage
    public void Insert_ResultMessage(String guid, String parentGuid, boolean fail, String text, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        parentGuid = Functions.SqlCleanup(parentGuid);
        text = Functions.SqlCleanup(text);
        int failVal = 0;
        if (fail) failVal = 1;

        if (int_ResultMessage >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_ResultMessage.equals("")) sql_ResultMessage += " UNION ";
        sql_ResultMessage += "SELECT ";
        sql_ResultMessage += "    '" + guid + "', ";
        sql_ResultMessage += "    '" + parentGuid + "', ";
        sql_ResultMessage += "    " + failVal + ", ";
        sql_ResultMessage += "    '" + text + "', ";
        sql_ResultMessage += "    " + sort + " ";

        int_ResultMessage++;
    }

    // Update Sort
    public void Update_Sort(String type, String[] guids)
    {
        String sql = "";

        for (int i = 0; i < guids.length; i++)
        {
            try
            {
                Statement cmd = CONN.createStatement();

                sql =  "";
                sql += "UPDATE ";
                sql += "    " + type + " ";
                sql += "SET ";
                sql += "    Sort = " + (i + 1) + " ";
                sql += "WHERE 1 = 1 ";
                sql += "    AND Deleted = 0 ";
                sql += "    AND GUID = '" + guids[i].trim() + "' ";
                sql += ";";

                cmd.execute(sql);

                cmd.close();
            }
            catch (Exception ex)
            {
                System.out.println("sql: \n" + sql);
                ex.printStackTrace();
            }
        }
    }





    // Insert Game
    public void Insert_Game(String guid, String name, String initialLocation, String statsGuid, String adminPass)
    {
        guid = Functions.SqlCleanup(guid);
        name = Functions.SqlCleanup(name);
        initialLocation = Functions.SqlCleanup(initialLocation);
        adminPass = Functions.SqlCleanup(adminPass);

        if (!sql_Game.equals("")) sql_Game += " UNION ";
        sql_Game += "SELECT ";
        sql_Game += "    '" + guid + "', ";
        sql_Game += "    '" + name + "', ";
        sql_Game += "    '" + Functions.GetDate() + "', ";
        sql_Game += "    '" + initialLocation + "', ";
        sql_Game += "    '" + statsGuid + "', ";
        sql_Game += "    '" + adminPass + "' ";
    }

    // Insert Help 
    public void Insert_Help(String syntax, String title, String topic)
    {
        syntax = Functions.SqlCleanup(syntax);
        title = Functions.SqlCleanup(title);
        topic = Functions.SqlCleanup(topic);

        if (int_Help >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Help.equals("")) sql_Help += " UNION ";
        sql_Help += "SELECT ";
        sql_Help += "    '" + syntax + "', ";
        sql_Help += "    '" + title + "', ";
        sql_Help += "    '" + topic + "' ";

        int_Help++;
    }

    // Insert Room
    public void Insert_Room(String guid, String label, String filePath)
    {
        guid = Functions.SqlCleanup(guid);
        label = Functions.SqlCleanup(label);
        filePath = Functions.SqlCleanup(filePath);

        if (int_Room >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Room.equals("")) sql_Room += " UNION ";
        sql_Room += "SELECT ";
        sql_Room += "    '" + guid + "', ";
        sql_Room += "    '" + label + "', ";
        sql_Room += "    '" + filePath + "' ";

        int_Room++;
    }

    // Insert NPC
    public void Insert_Npc(String guid, String name, String meta, String location, String inherit, int sort)
    {
        guid = Functions.SqlCleanup(guid);
        name = Functions.SqlCleanup(name);
        meta = Functions.SqlCleanup(meta);
        location = Functions.SqlCleanup(location);
        inherit = Functions.SqlCleanup(inherit);

        if (int_Npc >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Npc.equals("")) sql_Npc += " UNION ";
        sql_Npc += "SELECT ";
        sql_Npc += "    '" + guid + "', ";
        sql_Npc += "    '" + name + "', ";
        sql_Npc += "    '" + meta + "', ";
        sql_Npc += "    '" + location + "', ";
        sql_Npc += "    '" + inherit + "', ";
        sql_Npc += "    " + sort;

        int_Npc++;
    }

    // Insert TravelSet
    public void Insert_NpcTravelSet(
        String guid,
        String npcGuid,
        int waitMinute,
        String mode
    )
    {
        guid = Functions.SqlCleanup(guid);
        npcGuid = Functions.SqlCleanup(npcGuid);
        mode = Functions.SqlCleanup(mode);

        if (int_NpcTravelSet >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_NpcTravelSet.equals("")) sql_NpcTravelSet += " UNION ";
        sql_NpcTravelSet += "SELECT ";
        sql_NpcTravelSet += "    '" + guid + "', ";
        sql_NpcTravelSet += "    '" + npcGuid + "', ";
        sql_NpcTravelSet += "    " + waitMinute + ", ";
        sql_NpcTravelSet += "    '" + mode + "' ";

        int_NpcTravelSet++;
    }

    // Insert Travel
    public void Insert_NpcTravel(
        String npcTravelSetGuid,
        String location,
        int sort
    )
    {
        npcTravelSetGuid = Functions.SqlCleanup(npcTravelSetGuid);
        location = Functions.SqlCleanup(location);

        if (int_NpcTravel >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_NpcTravel.equals("")) sql_NpcTravel += " UNION ";
        sql_NpcTravel += "SELECT ";
        sql_NpcTravel += "    '" + npcTravelSetGuid + "', ";
        sql_NpcTravel += "    '" + location + "', ";
        sql_NpcTravel += "    " + sort + " ";

        int_NpcTravel++;
    }

    // Insert Object
    public void Insert_Object(
        String parentGuid,
        String parentType,
        String guid,
        String type,
        int chance,
        String label,
        String meta,
        int sort,
        String fileName,
        String inherit)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        type = Functions.SqlCleanup(type);
        label = Functions.SqlCleanup(label);
        fileName = Functions.SqlCleanup(fileName);
        inherit = Functions.SqlCleanup(inherit);

        if (int_Object >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Object.equals("")) sql_Object += " UNION ";
        sql_Object += "SELECT ";
        sql_Object += "    '" + guid + "', ";
        sql_Object += "    '" + parentGuid + "', ";
        sql_Object += "    '" + parentType + "', ";
        sql_Object += "    '" + type + "', ";
        sql_Object += "    " + chance + ", ";
        sql_Object += "    '" + label + "', ";
        sql_Object += "    '" + meta + "', ";
        sql_Object += "    " + sort + ", ";
        sql_Object += "    '" + fileName + "', ";
        sql_Object += "    '" + inherit + "' ";

        if (!sql_InitObject.equals("")) sql_InitObject += " UNION ";
        sql_InitObject += "SELECT ";
        sql_InitObject += "    '" + guid + "', ";
        sql_InitObject += "    '" + parentGuid + "', ";
        sql_InitObject += "    '" + parentType + "', ";
        sql_InitObject += "    '" + type + "', ";
        sql_InitObject += "    " + chance + ", ";
        sql_InitObject += "    '" + label + "', ";
        sql_InitObject += "    '" + meta + "', ";
        sql_InitObject += "    " + sort + ", ";
        sql_InitObject += "    '" + fileName + "', ";
        sql_InitObject += "    '" + inherit + "' ";

        int_Object++;
    }

    // Insert Attribute
    public void Insert_Attribute(String parentGuid, String parentType, String guid, String type, String value, int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        type = Functions.SqlCleanup(type);
        value = Functions.SqlCleanup(value);

        if (int_Attribute >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Attribute.equals("")) sql_Attribute += " UNION ";
        sql_Attribute += "SELECT ";
        sql_Attribute += "    '" + guid + "', ";
        sql_Attribute += "    '" + type + "', ";
        sql_Attribute += "    '" + parentGuid + "', ";
        sql_Attribute += "    '" + parentType + "', ";
        sql_Attribute += "    '" + value + "', ";
        sql_Attribute += "    " + sort + " ";

        if (!sql_InitAttribute.equals("")) sql_InitAttribute += " UNION ";
        sql_InitAttribute += "SELECT ";
        sql_InitAttribute += "    '" + guid + "', ";
        sql_InitAttribute += "    '" + type + "', ";
        sql_InitAttribute += "    '" + parentGuid + "', ";
        sql_InitAttribute += "    '" + parentType + "', ";
        sql_InitAttribute += "    '" + value + "', ";
        sql_InitAttribute += "    " + sort + " ";

        int_Attribute++;
    }

    // Insert Event
    public void Insert_Event(
        String parentGuid,
        String parentType,
        String guid,
        String type,
        int intervalMinutes,
        int repeat,
        String fileName,
        int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        type = Functions.SqlCleanup(type);
        fileName = Functions.SqlCleanup(fileName);

        if (int_Event >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Event.equals("")) sql_Event += " UNION ";
        sql_Event += "SELECT ";
        sql_Event += "    '" + guid + "', ";
        sql_Event += "    '" + parentGuid + "', ";
        sql_Event += "    '" + parentType + "', ";
        sql_Event += "    '" + type + "', ";
        sql_Event += "    " + intervalMinutes + ", ";
        sql_Event += "    " + repeat + ", ";
        sql_Event += "    '" + fileName + "', ";
        sql_Event += "    " + sort + " ";

        int_Event++;
    }

    // Insert Command
    public void Insert_Command(String parentGuid, String parentType, String guid, String syntax, String fileName, int sort)
    {
        if (syntax.equals("")) return;

        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        syntax = Functions.SqlCleanup(syntax);
        fileName = Functions.SqlCleanup(fileName);

        if (!sql_Command.equals("")) sql_Command += " UNION ";
        sql_Command += "SELECT ";
        sql_Command += "    '" + guid + "', ";
        sql_Command += "    '" + parentGuid + "', ";
        sql_Command += "    '" + parentType + "', ";
        sql_Command += "    '" + syntax + "', ";
        sql_Command += "    '" + fileName + "', ";
        sql_Command += "    " + sort + " ";
    }

    // Insert ActionSet
    public void Insert_ActionSet(
        String parentGuid,
        String parentType,
        String guid,
        int repeat,
        int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);

        if (!sql_ActionSet.equals("")) sql_ActionSet += " UNION ";
        sql_ActionSet += "SELECT '" + guid + "', '" + parentGuid + "', '" + parentType + "', " + repeat + ", " + sort + " ";
    }

    // Insert Action
    public void Insert_Action(String parentGuid, String guid, String type, String source, String newValue, int repeat, int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        type = Functions.SqlCleanup(type);
        newValue = Functions.SqlCleanup(newValue);

        if (!sql_Action.equals("")) sql_Action += " UNION ";
        sql_Action += "SELECT ";
        sql_Action += "    '" + guid + "', ";
        sql_Action += "    '" + parentGuid + "', ";
        sql_Action += "    '" + type + "', ";
        sql_Action += "    '" + source + "', ";
        sql_Action += "    '" + newValue + "', ";
        sql_Action += "     " + repeat + ", ";
        sql_Action += "    " + sort + " ";
    }

    // Insert Inventory 
    public void Insert_Inventory(
        String playerGuid,
        String objectGuid,
        String objectName,
        int objectCount
    )
    {
        playerGuid = Functions.SqlCleanup(playerGuid);
        objectGuid = Functions.SqlCleanup(objectGuid);
        objectName = Functions.SqlCleanup(objectName);

        if (!sql_Inventory.equals("")) sql_Inventory += " UNION ";
        sql_Inventory += "SELECT ";
        sql_Inventory += "    '" + playerGuid + "', ";
        sql_Inventory += "    '" + objectGuid + "', ";
        sql_Inventory += "    '" + objectName + "', ";
        sql_Inventory += "    " + objectCount + " ";
    }

    // Insert Log
    public void Insert_Log(String logType, String filePath, String message)
    {
        logType = Functions.SqlCleanup(logType);
        filePath = Functions.SqlCleanup(filePath);
        message = Functions.SqlCleanup(message);

        if (int_Log >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Log.equals("")) sql_Log += " UNION ";
        sql_Log += "SELECT ";
        sql_Log += "    '" + logType + "', ";
        sql_Log += "    '" + filePath + "', ";
        sql_Log += "    '" + message + "' ";

        int_Log++;
    }

    // Insert LogicSet
    public void Insert_LogicSet(String parentGuid, String parentType, String guid, String failEvent, String failMessage, String operand, int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        operand = Functions.SqlCleanup(operand);
        failMessage = Functions.SqlCleanup(failMessage.trim());

        if (int_LogicSet >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_LogicSet.equals("")) sql_LogicSet += " UNION ";
        sql_LogicSet += "SELECT ";
        sql_LogicSet += "    '" + guid + "', ";
        sql_LogicSet += "    '" + parentGuid + "', ";
        sql_LogicSet += "    '" + parentType + "', ";
        sql_LogicSet += "    '" + operand + "', ";
        sql_LogicSet += "    " + sort + ", ";
        sql_LogicSet += "    '" + failEvent + "', ";
        sql_LogicSet += "    '" + failMessage + "' ";

        int_LogicSet++;
    }

    // Insert Logic
    public void Insert_Logic(
        String logicSetGuid,
        String type,
        String source,
        String sourceValue,
        String newValue,
        String operand,
        String eval,
        int sort)
    {
        logicSetGuid = Functions.SqlCleanup(logicSetGuid);
        type = Functions.SqlCleanup(type);
        source = Functions.SqlCleanup(source);
        sourceValue = Functions.SqlCleanup(sourceValue);
        newValue = Functions.SqlCleanup(newValue);
        eval = Functions.SqlCleanup(eval);
        operand = Functions.SqlCleanup(operand);

        if (int_Logic >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Logic.equals("")) sql_Logic += " UNION ";
        sql_Logic += "SELECT ";
        sql_Logic += "'" + logicSetGuid + "', ";
        sql_Logic += "'" + type + "', ";
        sql_Logic += "'" + source + "', ";
        sql_Logic += "'" + sourceValue + "', ";
        sql_Logic += "'" + newValue + "', ";
        sql_Logic += "'" + operand + "', ";
        sql_Logic += "'" + eval + "', ";
        sql_Logic += sort + " ";

        int_Logic++;
    }

    // Insert MessageSet
    public void Insert_MessageSet(String parentGuid, String parentType, String guid, String repeatType, String templateMessageSetGuid, int sort)
    {
        parentGuid = Functions.SqlCleanup(parentGuid);
        parentType = Functions.SqlCleanup(parentType);
        guid = Functions.SqlCleanup(guid);
        repeatType = Functions.SqlCleanup(repeatType);
        templateMessageSetGuid = Functions.SqlCleanup(templateMessageSetGuid);

        if (int_MessageSet >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_MessageSet.equals("")) sql_MessageSet += " UNION ";
        sql_MessageSet += "SELECT  ";
        sql_MessageSet += "'" + guid + "', ";
        sql_MessageSet += "'" + parentGuid + "', ";
        sql_MessageSet += "'" + parentType + "', ";
        sql_MessageSet += "'" + repeatType + "', ";
        sql_MessageSet += "'" + templateMessageSetGuid + "', ";
        sql_MessageSet += sort + " ";

        int_MessageSet++;
    }

    // Insert Message
    public void Insert_Message(String messageSetGuid, String output, int sort)
    {
        messageSetGuid = Functions.SqlCleanup(messageSetGuid);
        output = Functions.SqlCleanup(output);

        if (int_Message >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Message.equals("")) sql_Message += " UNION ";
        sql_Message += "SELECT '" + messageSetGuid + "', '" + output + "', " + sort + " ";

        int_Message++;
    }

    // Insert Test
    public void Insert_Test(String testName, String commands)
    {
        testName = Functions.SqlCleanup(testName);
        commands = Functions.SqlCleanup(commands);

        if (int_Tests >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Tests.equals("")) sql_Tests += " UNION ";
        sql_Tests += "SELECT '" + testName + "', '" + commands + "' ";

        int_Tests++;
    }

    // Insert Variable
    public void Insert_Variable(String guid, String value)
    {
        guid = Functions.SqlCleanup(guid);
        value = Functions.SqlCleanup(value);

        if (int_Variable >= max_insert)
        {
            ExecuteAllSql();
        }

        if (!sql_Variable.equals("")) sql_Variable += " UNION ";
        sql_Variable += "SELECT '" + guid + "', '" + value + "' ";

        int_Variable++;
    }

    // Update the splash screen
    public void Update_Splash(String content)
    {
        Functions.OutputRaw("Updating the splash screen...");
        String sql = "";

        try
        {
            sql =  "";
            sql += "UPDATE ";
            sql += "    Game ";
            sql += "SET ";
            sql += "    Splash = '" + Functions.SqlCleanup(content) + "' ";
            sql += ";";

            Statement cmd = CONN.createStatement();

            cmd.execute(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Output("ERROR! \n" + sql);
        }
    }

    // Update the credits screen
    public void Update_Credits(String content)
    {
        Functions.OutputRaw("Updating the credits screen...");
        String sql = "";

        try
        {
            sql =  "";
            sql += "UPDATE ";
            sql += "    Game ";
            sql += "SET ";
            sql += "    Credits = '" + Functions.SqlCleanup(content) + "' ";
            sql += ";";

            Statement cmd = CONN.createStatement();

            cmd.execute(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Output("ERROR! \n" + sql);
        }
    }

    public void ClearAllSql()
    {
        sql_Log = "";
        sql_Tests = "";
        sql_Game = "";
        sql_Help = "";
        sql_Room = "";
        sql_Npc = "";
        sql_NpcTravelSet = "";
        sql_NpcTravel = "";
        sql_Object = "";
        sql_InitObject = "";
        sql_Attribute = "";
        sql_InitAttribute = "";
        sql_Event = "";
        sql_Command = "";
        sql_ActionSet = "";
        sql_Action = "";
        sql_Inventory = "";
        sql_LogicSet = "";
        sql_Logic = "";
        sql_MessageSet = "";
        sql_Message = "";

        sql_UnitTest = "";
        sql_Step = "";
        sql_Input = "";
        sql_Assert = "";
        sql_Eval = "";
        sql_Comparison = "";
        sql_Result = "";
        sql_ResultMessage = "";

        int_Log = 0;
        int_Tests = 0;
        int_Game = 0;
        int_Help = 0;
        int_Room = 0;
        int_Npc = 0;
        int_NpcTravelSet = 0;
        int_NpcTravel = 0;
        int_Object = 0;
        int_InitObject = 0;
        int_Attribute = 0;
        int_InitAttribute = 0;
        int_Event = 0;
        int_Command = 0;
        int_ActionSet = 0;
        int_Action = 0;
        int_Inventory = 0;
        int_LogicSet = 0;
        int_Logic = 0;
        int_MessageSet = 0;
        int_Message = 0;

        int_UnitTest = 0;
        int_Step = 0;
        int_Input = 0;
        int_Assert = 0;
        int_Eval = 0;
        int_Comparison = 0;
        int_Result = 0;
        int_ResultMessage = 0;
    }

    public void ExecuteAllSql()
    {
        Functions.OutputRaw("Executing Database Inserts...");
        try
        {
            if (!sql_NpcTravelSet.equals("")) sql_NpcTravelSet = "INSERT INTO NpcTravelSet (GUID, NpcGUID, WaitMinute, Mode) " + sql_NpcTravelSet + ";";
            if (!sql_NpcTravel.equals("")) sql_NpcTravel = "INSERT INTO NpcTravel (NpcTravelSetGUID, Location, Sort) " + sql_NpcTravel + ";";
            if (!sql_Log.equals("")) sql_Log = "INSERT INTO Log (LogType, FilePath, Message) " + sql_Log + ";";
            if (!sql_Tests.equals("")) sql_Tests = "INSERT INTO Test (TestName, Commands) " + sql_Tests + ";";
            if (!sql_Variable.equals("")) sql_Variable = "INSERT INTO Variable (GUID, Value) " + sql_Variable + ";";
            if (!sql_Game.equals("")) sql_Game = "INSERT INTO Game (GUID, Name, Build, InitialLocation, StatsGuids, AdminPass) " + sql_Game + ";";
            if (!sql_Help.equals("")) sql_Help = "INSERT INTO Help (Syntax, title, Topic) " + sql_Help + ";";
            if (!sql_Room.equals("")) sql_Room = "INSERT INTO Room (GUID, Label, FileName) " + sql_Room + ";";
            if (!sql_Npc.equals("")) sql_Npc = "INSERT INTO Npc (GUID, Name, Meta, Location, Inherit, Sort) " + sql_Npc + ";";
            if (!sql_Object.equals("")) sql_Object = "INSERT INTO Object (GUID, ParentGUID, ParentType, Type, Chance, Label, Meta, Sort, FileName, Inherit) " + sql_Object + ";";
            if (!sql_InitObject.equals("")) sql_InitObject = "INSERT INTO InitObject (GUID, ParentGUID, ParentType, Type, Chance, Label, Meta, Sort, FileName, Inherit) " + sql_InitObject + ";";
            if (!sql_Attribute.equals("")) sql_Attribute = "INSERT INTO Attribute (GUID, Type, ParentGUID, ParentType, Value, Sort) " + sql_Attribute + ";";
            if (!sql_InitAttribute.equals("")) sql_InitAttribute = "INSERT INTO InitAttribute (GUID, Type, ParentGUID, ParentType, Value, Sort) " + sql_InitAttribute + ";";
            if (!sql_Event.equals("")) sql_Event = "INSERT INTO Event (GUID, ParentGUID, ParentType, Type, IntervalMinutes, RepeatCount, FileName, Sort) " + sql_Event + ";";
            if (!sql_Command.equals("")) sql_Command = "INSERT INTO Command (GUID, ParentGUID, ParentType, Syntax, FileName, Sort) " + sql_Command + ";";
            if (!sql_ActionSet.equals("")) sql_ActionSet = "INSERT INTO ActionSet (GUID, ParentGUID, ParentType, RepeatCount, Sort) " + sql_ActionSet + ";";
            if (!sql_Action.equals("")) sql_Action = "INSERT INTO Action (GUID, ParentGUID, Type, Source, NewValue, Repeat, Sort) " + sql_Action + ";";
            if (!sql_Inventory.equals("")) sql_Inventory = "INSERT INTO Inventory (PlayerGUID, ObjectGUID, ObjectName, ObjectCount) " + sql_Inventory + ";";
            if (!sql_LogicSet.equals("")) sql_LogicSet = "INSERT INTO LogicSet (GUID, ParentGUID, ParentType, Operand, Sort, FailEvent, FailMessage) " + sql_LogicSet + ";";
            if (!sql_Logic.equals("")) sql_Logic = "INSERT INTO Logic (LogicSetGUID, Type, Source, SourceValue, NewValue, Operand, Eval, Sort) " + sql_Logic + ";";
            if (!sql_MessageSet.equals("")) sql_MessageSet = "INSERT INTO MessageSet (GUID, ParentGUID, ParentType, RepeatType, TemplateMessageSetGUID, Sort) " + sql_MessageSet + ";";
            if (!sql_Message.equals("")) sql_Message = "INSERT INTO Message (MessageSetGUID, Output, Sort) " + sql_Message + ";";

            if (!sql_UnitTest.equals("")) sql_UnitTest = "INSERT INTO UnitTest (GUID) " + sql_UnitTest + ";";
            if (!sql_Step.equals("")) sql_Step = "INSERT INTO Step (GUID, ParentGUID, Name, Sort) " + sql_Step + ";";
            if (!sql_Input.equals("")) sql_Input = "INSERT INTO Input (GUID, ParentGUID, Sort, Text) " + sql_Input + ";";
            if (!sql_Assert.equals("")) sql_Assert = "INSERT INTO Assert (GUID, Name, ParentGUID, Sort) " + sql_Assert + ";";
            if (!sql_Eval.equals("")) sql_Eval = "INSERT INTO Eval (GUID, ParentGUID, Pass, Sort) " + sql_Eval + ";";
            if (!sql_Comparison.equals("")) sql_Comparison = "INSERT INTO Comparison (GUID, ParentGUID, Operand, CheckText, Sort) " + sql_Comparison + ";";
            if (!sql_Result.equals("")) sql_Result = "INSERT INTO Result (GUID, ParentGUID, Sort) " + sql_Result + ";";
            if (!sql_ResultMessage.equals("")) sql_ResultMessage = "INSERT INTO ResultMessage (GUID, ParentGUID, Fail, Text, Sort) " + sql_ResultMessage + ";";

            Statement cmd = CONN.createStatement();

            if (!sql_Log.equals("")) cmd.execute(sql_Log);
            if (!sql_Tests.equals("")) cmd.execute(sql_Tests);
            if (!sql_Game.equals("")) cmd.execute(sql_Game);
            if (!sql_Help.equals("")) cmd.execute(sql_Help);
            if (!sql_Room.equals("")) cmd.execute(sql_Room);
            if (!sql_Npc.equals("")) cmd.execute(sql_Npc);
            if (!sql_NpcTravelSet.equals("")) cmd.execute(sql_NpcTravelSet);
            if (!sql_NpcTravel.equals("")) cmd.execute(sql_NpcTravel);
            if (!sql_Object.equals("")) cmd.execute(sql_Object);
            if (!sql_InitObject.equals("")) cmd.execute(sql_InitObject);
            if (!sql_Attribute.equals("")) cmd.execute(sql_Attribute);
            if (!sql_InitAttribute.equals("")) cmd.execute(sql_InitAttribute);
            if (!sql_Event.equals("")) cmd.execute(sql_Event);
            if (!sql_Command.equals("")) cmd.execute(sql_Command);
            if (!sql_ActionSet.equals("")) cmd.execute(sql_ActionSet);
            if (!sql_Action.equals("")) cmd.execute(sql_Action);
            if (!sql_Inventory.equals("")) cmd.execute(sql_Inventory);
            if (!sql_LogicSet.equals("")) cmd.execute(sql_LogicSet);
            if (!sql_Logic.equals("")) cmd.execute(sql_Logic);
            if (!sql_MessageSet.equals("")) cmd.execute(sql_MessageSet);
            if (!sql_Message.equals("")) cmd.execute(sql_Message);

            if (!sql_UnitTest.equals("")) cmd.execute(sql_UnitTest);
            if (!sql_Step.equals("")) cmd.execute(sql_Step);
            if (!sql_Input.equals("")) cmd.execute(sql_Input);
            if (!sql_Assert.equals("")) cmd.execute(sql_Assert);
            if (!sql_Eval.equals("")) cmd.execute(sql_Eval);
            if (!sql_Comparison.equals("")) cmd.execute(sql_Comparison);
            if (!sql_Result.equals("")) cmd.execute(sql_Result);
            if (!sql_ResultMessage.equals("")) cmd.execute(sql_ResultMessage);

            cmd.close();

            ClearAllSql();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    // Unique Alias Check
    public boolean Unique_Alias(String parentAlias, String alias)
    {
        boolean unique = true;
        int count = 0;

        if (count < 1) count += GetCount(alias, "Object");
        if (count < 1) count += GetCount(alias, "Room");
        if (count < 1) count += GetCount(parentAlias, alias, "Attribute");
        if (count < 1) count += GetCount(alias, "MessageSet");
        if (count < 1) count += GetCount(alias, "Event");
        if (count < 1) count += GetCount(alias, "Command");
        if (count < 1) count += GetCount(alias, "ActionSet");
        if (count < 1) count += GetCount(alias, "LogicSet");

        if (count > 0) unique = false;

        return unique;
    }
    public boolean Attribute_Exists(String attributeAlias)
    {
        String sql = "";
        boolean exists = false;
        int count = 0;

        count = GetCount(attributeAlias, "Attribute");

        if (count > 0) exists = true;

        return exists;
    }
    public int GetCount (String alias, String table)
    {
        String sql = "";
        int count = 0;

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            // Object
            if (count < 1)
            {
                sql =  "";
                sql += "SELECT ";
                sql += "    COUNT(*) RecCount ";
                sql += "FROM ";
                sql += "    " + table + " ";
                sql += "WHERE 1 = 1 ";
                sql += "    AND Deleted = 0 ";
                sql += "    AND GUID = '" + alias + "' ";
                sql += ";";

                rs = cmd.executeQuery(sql);

                while (rs.next())
                {
                    count = rs.getInt("RecCount");
                    break;
                }

                rs.close();
                cmd.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return count;
    }
    public int GetCount (String parentAlias, String alias, String table)
    {
        String sql = "";
        int count = 0;

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            // Object
            if (count < 1)
            {
                sql =  "";
                sql += "SELECT ";
                sql += "    COUNT(*) RecCount ";
                sql += "FROM ";
                sql += "    " + table + " ";
                sql += "WHERE 1 = 1 ";
                sql += "    AND Deleted = 0 ";
                sql += "    AND ParentGUID = '" + parentAlias + "' ";
                sql += "    AND GUID = '" + alias + "' ";
                sql += ";";

                rs = cmd.executeQuery(sql);

                while (rs.next())
                {
                    count = rs.getInt("RecCount");
                    break;
                }

                rs.close();
                cmd.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return count;
    }

    /* MESSAGE TEMPLATE METHODS */
    public void MessageTemplateFix()
    {
        Functions.Output("Performing Message Template Parsing...");

        List<List<String>> msgs = new ArrayList<List<String>>();
        List<String> tmp = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    mset.GUID, ";
            sql += "    m.Output ";
            sql += "FROM ";
            sql += "    MessageSet mset ";
            sql += "    INNER JOIN Message m ";
            sql += "        ON m.MessageSetGUID = mset.TemplateMessageSetGUID ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND mset.Deleted = 0 ";
            sql += "    AND m.Deleted = 0 ";
            sql += "    AND mset.TemplateMessageSetGUID != '' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                tmp = new ArrayList<String>();
                tmp.add(rs.getString("GUID"));
                tmp.add(rs.getString("Output"));
                msgs.add(tmp);
            }

            rs.close();
            cmd.close();

            if (msgs.size() > 0)
            {
                GetMessageAttributes(msgs);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Error(sql);
        }
    }
    public void GetMessageAttributes(List<List<String>> msgs)
    {
        List<List<String>> attrs = new ArrayList<List<String>>();
        List<String> tmp = new ArrayList<String>();
        String sql = "";
        String inString = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    ParentGUID, ";
            sql += "    GUID, ";
            sql += "    Value ";
            sql += "FROM ";
            sql += "    Attribute ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID IN ( ";

            for (List<String> msg : msgs)
            {
                if (!inString.equals("")) inString += ", ";
                inString += "'" + msg.get(0).trim() + "'";
            }

            sql += inString;
            sql += ") ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                tmp = new ArrayList<String>();
                tmp.add(rs.getString("ParentGUID"));
                tmp.add(rs.getString("GUID"));
                tmp.add(rs.getString("Value"));
                attrs.add(tmp);
            }

            rs.close();
            cmd.close();

            FixTemplateText(attrs, msgs);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Error(sql);
        }
    }
    public void FixTemplateText(List<List<String>> attrs, List<List<String>> msgs)
    {
        String sql = "";
        String sqlIns = "";
        int attrParent = 0;
        int attrAlias = 1;
        int attrValue = 2;
        int msgAlias = 0;
        int msgText = 1;
        int sort = 0;

        // Iterate through the messages
        for (List<String> msg : msgs)
        {
            // Iterate through the attributes, replacing the alias string with the value string
            for (List<String> attr : attrs)
            {
                if (attr.get(attrParent).trim().equals(msg.get(msgAlias).trim()))
                {
                    msg.set(msgText, msg.get(msgText).replace(attr.get(attrAlias), attr.get(attrValue)));
                }
            }
        }

        // Create the insert unions
        for (List<String> msg : msgs)
        {
            if (!sqlIns.equals("")) sqlIns += " UNION ";
            sqlIns += "SELECT '" + Functions.Encode(msg.get(msgAlias)) + "', '" + Functions.Encode(msg.get(msgText)) + "', " + sort + " ";
            sort++;
        }

        try
        {
            Statement cmd = CONN.createStatement();

            sql =  "";
            sql += "INSERT INTO ";
            sql += "Message ( ";
            sql += "    MessageSetGUID, ";
            sql += "    Output, ";
            sql += "    Sort ";
            sql += ") ";
            sql += sqlIns + " ";
            sql += ";";

            cmd.execute(sql);
            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Output("ERROR! " + sql);
        }
    }

    /* VARIABLE METHODS */
    public void ReplaceVariables()
    {
        Functions.Output("Replacing variables...");

        List<List<String>> vars = new ArrayList<List<String>>();
        String sql = "";

        vars = GetVariables();

        try
        {
            Statement cmd = CONN.createStatement();

            // Iterate through the variables
            for (List<String> var : vars)
            {
                sql = "";

                sql =  "";
                sql += "UPDATE ";
                sql += "    Variable ";
                sql += "SET ";
                sql += "    Value = REPLACE(Value, '@" + var.get(0).trim() + "@', '" + var.get(1).trim() + "') ";
                sql += ";";

                cmd.execute(sql);

                sql =  "";
                sql += "UPDATE ";
                sql += "    Message ";
                sql += "SET ";
                sql += "    Output = REPLACE(Output, '@" + var.get(0).trim() + "@', '" + var.get(1).trim() + "') ";
                sql += ";";

                cmd.execute(sql);
            }

            cmd.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Functions.Output("ERROR! " + sql);
        }
    }

    public List<List<String>> GetVariables()
    {
        List<List<String>> output = new ArrayList<List<String>>();
        List<String> tmp = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID, ";
            sql += "    Value ";
            sql += "FROM ";
            sql += "    Variable ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                tmp = new ArrayList<String>();
                tmp.add(rs.getString("GUID"));
                tmp.add(rs.getString("Value"));
                output.add(tmp);
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

    /* INHERITANCE METHODS */
    public void Inheritance()
    {
        Functions.Output("Performing Inheritance...");

        // Clear all SQL strings
        ClearAllSql();

        // Do the Inheritance
        Inherit_Npc();
        Inherit_Object();

        // Ensure all SQL statements were called
        ExecuteAllSql();
    }

    public void Inherit_Object()
    {
        Functions.Output("Object inheritance...");

        List<List<String>> objs = new ArrayList<List<String>>();
        List<String> tmp = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ParentGuid, ";
            sql += "    Inherit InheritGuid ";
            sql += "FROM ";
            sql += "    Object ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Inherit != '' ";
            sql += "    AND Inherit != GUID ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                tmp = new ArrayList<String>();
                tmp.add(rs.getString("InheritGuid"));
                tmp.add(rs.getString("ParentGuid"));
                objs.add(tmp);
            }

            rs.close();
            cmd.close();

            for (List<String> obj : objs)
            {
                Inherit_Commands(obj.get(0), obj.get(1));
                Inherit_Attributes(obj.get(0), obj.get(1));
                Inherit_MessageSets(obj.get(0), obj.get(1));
                Inherit_Events(obj.get(0), obj.get(1));
                Inherit_Objects(obj.get(0), obj.get(1));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Npc()
    {
        List<List<String>> npcs = new ArrayList<List<String>>();
        List<String> tmp = new ArrayList<String>();
        String sql = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ParentGuid, ";
            sql += "    Inherit InheritGuid ";
            sql += "FROM ";
            sql += "    Npc ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND Inherit != '' ";
            sql += "    AND Inherit != GUID ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                tmp = new ArrayList<String>();
                tmp.add(rs.getString("InheritGuid"));
                tmp.add(rs.getString("ParentGuid"));
                npcs.add(tmp);
            }

            rs.close();
            cmd.close();

            for (List<String> npc : npcs)
            {
                Inherit_Commands(npc.get(0), npc.get(1));
                Inherit_Attributes(npc.get(0), npc.get(1));
                Inherit_Events(npc.get(0), npc.get(1));
                Inherit_Objects(npc.get(0), npc.get(1));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Events(String existParent, String newParent)
    {
        List<String> events = new ArrayList<String>();
        String sql = "";
        String newEventGuid = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            // FIRST GET THE EVENTS
            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    Event ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                events.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existEvent : events)
            {
                newEventGuid = Functions.GetGUID();


                if (int_Event >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Event.equals("")) sql_Event += " UNION ";
                sql_Event += "SELECT ";
                sql_Event += "    '" + newEventGuid + "', ";
                sql_Event += "    '" + newParent + "', ";
                sql_Event += "    ParentType, ";
                sql_Event += "    Type, ";
                sql_Event += "    IntervalMinutes, ";
                sql_Event += "    RepeatCount, ";
                sql_Event += "    -(Sort) ";
                sql_Event += "FROM ";
                sql_Event += "    Event ";
                sql_Event += "WHERE 1 = 1 ";
                sql_Event += "    AND Deleted = 0 ";
                sql_Event += "    AND GUID = '" + existEvent + "' ";

                int_Event++;


                Inherit_MessageSets(existEvent, newEventGuid);
                Inherit_LogicSets(existEvent, newEventGuid);
                Inherit_ActionSets(existEvent, newEventGuid);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_LogicSets(String existParent, String newParent)
    {
        List<String> lsets = new ArrayList<String>();
        String sql = "";
        String newLset = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    LogicSet ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                lsets.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existLset : lsets)
            {
                newLset = Functions.GetGUID();


                if (int_LogicSet >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_LogicSet.equals("")) sql_LogicSet += " UNION ";
                sql_LogicSet += "SELECT ";
                sql_LogicSet += "    '" + newLset + "', ";
                sql_LogicSet += "    '" + newParent + "', ";
                sql_LogicSet += "    ParentType, ";
                sql_LogicSet += "    Operand, ";
                sql_LogicSet += "    -(Sort), ";
                sql_LogicSet += "    FailEvent, ";
                sql_LogicSet += "    FailMessage ";
                sql_LogicSet += "FROM ";
                sql_LogicSet += "    LogicSet ";
                sql_LogicSet += "WHERE 1 = 1 ";
                sql_LogicSet += "    AND Deleted = 0 ";
                sql_LogicSet += "    AND GUID = '" + existLset + "' ";

                int_LogicSet++;


                Inherit_LogicBlocks(existLset, newLset);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_LogicBlocks(String existParent, String newParent)
    {
        List<String> lgcs = new ArrayList<String>();
        String sql = "";
        String newLgc = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    ROWID GUID ";
            sql += "FROM ";
            sql += "    Logic ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND LogicSetGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                lgcs.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existLgc : lgcs)
            {
                newLgc = Functions.GetGUID();


                if (int_Logic >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Logic.equals("")) sql_Logic += " UNION ";
                sql_Logic += "SELECT ";
                sql_Logic += "    '" + newParent + "', ";
                sql_Logic += "    Type, ";
                sql_Logic += "    Source, ";
                sql_Logic += "    SourceValue, ";
                sql_Logic += "    NewValue, ";
                sql_Logic += "    Operand, ";
                sql_Logic += "    Eval, ";
                sql_Logic += "    -(Sort) ";
                sql_Logic += "FROM ";
                sql_Logic += "    Logic ";
                sql_Logic += "WHERE 1 = 1 ";
                sql_Logic += "    AND Deleted = 0 ";
                sql_Logic += "    AND ROWID = '" + existLgc + "' ";

                int_Logic++;


            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_MessageSets(String existParent, String newParent)
    {
        List<String> msets = new ArrayList<String>();
        String sql = "";
        String newMset = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    MessageSet ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                msets.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existMset : msets)
            {
                newMset = Functions.GetGUID();


                if (int_MessageSet >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_MessageSet.equals("")) sql_MessageSet += " UNION ";
                sql_MessageSet += "SELECT ";
                sql_MessageSet += "    '" + newMset + "', ";
                sql_MessageSet += "    '" + newParent + "', ";
                sql_MessageSet += "    ParentType, ";
                sql_MessageSet += "    RepeatType, ";
                sql_MessageSet += "    TemplateMessageSetGUID, ";
                sql_MessageSet += "    -(Sort) ";
                sql_MessageSet += "FROM ";
                sql_MessageSet += "    MessageSet ";
                sql_MessageSet += "WHERE 1 = 1 ";
                sql_MessageSet += "    AND Deleted = 0 ";
                sql_MessageSet += "    AND GUID = '" + existMset + "' ";

                int_MessageSet++;


                Inherit_Messages(existMset, newMset);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Messages(String existParent, String newParent)
    {
        List<String> msgs = new ArrayList<String>();
        String sql = "";
        String newMsg = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    ROWID GUID ";
            sql += "FROM ";
            sql += "    Message ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND MessageSetGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                msgs.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existMsg : msgs)
            {
                newMsg = Functions.GetGUID();


                if (int_Message >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Message.equals("")) sql_Message += " UNION ";
                sql_Message += "SELECT ";
                sql_Message += "    '" + newParent + "', ";
                sql_Message += "    Output, ";
                sql_Message += "    -(Sort) ";
                sql_Message += "FROM ";
                sql_Message += "    Message ";
                sql_Message += "WHERE 1 = 1 ";
                sql_Message += "    AND Deleted = 0 ";
                sql_Message += "    AND ROWID = '" + existMsg + "' ";

                int_Message++;


            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_ActionSets(String existParent, String newParent)
    {
        List<String> asets = new ArrayList<String>();
        String sql = "";
        String newAset = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    ActionSet ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                asets.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existAset : asets)
            {
                newAset = Functions.GetGUID();


                if (int_ActionSet >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_ActionSet.equals("")) sql_ActionSet += " UNION ";
                sql_ActionSet += "SELECT ";
                sql_ActionSet += "    '" + newAset + "', ";
                sql_ActionSet += "    '" + newParent + "', ";
                sql_ActionSet += "    ParentType, ";
                sql_ActionSet += "    RepeatCount, ";
                sql_ActionSet += "    -(Sort) ";
                sql_ActionSet += "FROM ";
                sql_ActionSet += "    ActionSet ";
                sql_ActionSet += "WHERE 1 = 1 ";
                sql_ActionSet += "    AND Deleted = 0 ";
                sql_ActionSet += "    AND GUID = '" + existAset + "' ";

                int_ActionSet++;


                Inherit_LogicSets(existAset, newAset);
                Inherit_Actions(existAset, newAset);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Actions(String existParent, String newParent)
    {
        List<String> acts = new ArrayList<String>();
        String sql = "";
        String newAct = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    Action ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                acts.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existAct : acts)
            {
                newAct = Functions.GetGUID();


                if (int_Action >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Action.equals("")) sql_Action += " UNION ";
                sql_Action += "SELECT ";
                sql_Action += "    '" + newAct + "', ";
                sql_Action += "    '" + newParent + "', ";
                sql_Action += "    Type, ";
                sql_Action += "    Source, ";
                sql_Action += "    NewValue, ";
                sql_Action += "    Repeat, ";
                sql_Action += "    -(Sort) ";
                sql_Action += "FROM ";
                sql_Action += "    Action ";
                sql_Action += "WHERE 1 = 1 ";
                sql_Action += "    AND Deleted = 0 ";
                sql_Action += "    AND GUID = '" + existAct + "' ";

                int_Action++;


                Inherit_LogicSets(existAct, newAct);
                Inherit_MessageSets(existAct, newAct);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Commands(String existParent, String newParent)
    {
        List<String> cmds = new ArrayList<String>();
        String sql = "";
        String newCmd = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            // FIRST GET THE EVENTS
            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    Command ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                cmds.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existCmd : cmds)
            {
                newCmd = Functions.GetGUID();


                if (int_Command >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Command.equals("")) sql_Command += " UNION ";
                sql_Command += "SELECT ";
                sql_Command += "    '" + newCmd + "', ";
                sql_Command += "    '" + newParent + "', ";
                sql_Command += "    ParentType, ";
                sql_Command += "    Syntax, ";
                sql_Command += "    -(Sort) ";
                sql_Command += "FROM ";
                sql_Command += "    Command ";
                sql_Command += "WHERE 1 = 1 ";
                sql_Command += "    AND Deleted = 0 ";
                sql_Command += "    AND GUID = '" + existCmd + "' ";

                int_Command++;


                Inherit_ActionSets(existCmd, newCmd);
                Inherit_MessageSets(existCmd, newCmd);
                Inherit_LogicSets(existCmd, newCmd);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Attributes(String existParent, String newParent)
    {
        String sql = "";

        try
        {
            if (int_Attribute >= max_insert)
            {
                ExecuteAllSql();
            }

            if (!sql_Attribute.equals("")) sql_Attribute += " UNION ";
            sql_Attribute += "SELECT ";
            sql_Attribute += "    GUID, ";
            sql_Attribute += "    Type, ";
            sql_Attribute += "    '" + newParent + "', ";
            sql_Attribute += "    ParentType, ";
            sql_Attribute += "    Value, ";
            sql_Attribute += "    -(Sort) ";
            sql_Attribute += "FROM ";
            sql_Attribute += "    Attribute ";
            sql_Attribute += "WHERE 1 = 1 ";
            sql_Attribute += "    AND Deleted = 0 ";
            sql_Attribute += "    AND ParentGUID = '" + existParent + "' ";

            int_Attribute++;


            if (int_InitAttribute >= max_insert)
            {
                ExecuteAllSql();
            }

            if (!sql_InitAttribute.equals("")) sql_InitAttribute += " UNION ";
            sql_InitAttribute += "SELECT ";
            sql_InitAttribute += "    GUID, ";
            sql_InitAttribute += "    Type, ";
            sql_InitAttribute += "    '" + newParent + "', ";
            sql_InitAttribute += "    ParentType, ";
            sql_InitAttribute += "    Value, ";
            sql_InitAttribute += "    -(Sort) ";
            sql_InitAttribute += "FROM ";
            sql_InitAttribute += "    Attribute ";
            sql_InitAttribute += "WHERE 1 = 1 ";
            sql_InitAttribute += "    AND Deleted = 0 ";
            sql_InitAttribute += "    AND ParentGUID = '" + existParent + "' ";

            int_InitAttribute++;


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Inherit_Objects(String existParent, String newParent)
    {
        List<String> objs = new ArrayList<String>();
        String sql = "";
        String newObj = "";

        try
        {
            Statement cmd = CONN.createStatement();
            ResultSet rs;

            // FIRST GET THE OBJECTS
            sql =  "";
            sql += "SELECT ";
            sql += "    GUID ";
            sql += "FROM ";
            sql += "    Object ";
            sql += "WHERE 1 = 1 ";
            sql += "    AND Deleted = 0 ";
            sql += "    AND ParentGUID = '" + existParent + "' ";
            sql += ";";

            rs = cmd.executeQuery(sql);

            while (rs.next())
            {
                objs.add(rs.getString("GUID"));
            }

            rs.close();
            cmd.close();

            for (String existObj : objs)
            {
                newObj = Functions.GetGUID();


                if (int_Object >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_Object.equals("")) sql_Object += " UNION ";
                sql_Object += "SELECT ";
                sql_Object += "    '" + newObj + "', ";
                sql_Object += "    '" + newParent + "', ";
                sql_Object += "    ParentType, ";
                sql_Object += "    Type, ";
                sql_Object += "    Chance, ";
                sql_Object += "    Label, ";
                sql_Object += "    Meta, ";
                sql_Object += "    -(Sort), ";
                sql_Object += "    Inherit ";
                sql_Object += "FROM ";
                sql_Object += "    Object ";
                sql_Object += "WHERE 1 = 1 ";
                sql_Object += "    AND Deleted = 0 ";
                sql_Object += "    AND GUID = '" + existObj + "' ";

                int_Object++;


                if (int_InitObject >= max_insert)
                {
                    ExecuteAllSql();
                }

                if (!sql_InitObject.equals("")) sql_InitObject += " UNION ";
                sql_InitObject += "SELECT ";
                sql_InitObject += "    '" + newObj + "', ";
                sql_InitObject += "    '" + newParent + "', ";
                sql_InitObject += "    ParentType, ";
                sql_InitObject += "    Type, ";
                sql_InitObject += "    Chance, ";
                sql_InitObject += "    Label, ";
                sql_InitObject += "    Meta, ";
                sql_InitObject += "    -(Sort), ";
                sql_InitObject += "    Inherit ";
                sql_InitObject += "FROM ";
                sql_InitObject += "    Object ";
                sql_InitObject += "WHERE 1 = 1 ";
                sql_InitObject += "    AND Deleted = 0 ";
                sql_InitObject += "    AND GUID = '" + existObj + "' ";

                int_InitObject++;


                Inherit_Events(existObj, newObj);
                Inherit_Commands(existObj, newObj);
                Inherit_Attributes(existObj, newObj);
                Inherit_MessageSets(existObj, newObj);
                // Inherit_Objects(existObj, newObj);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
