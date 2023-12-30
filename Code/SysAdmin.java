import java.io.*;
import java.io.Console;
import java.lang.ClassLoader.*;
import java.lang.Object;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Scanner;

/*
    ADMIN
    ----------------------------------------------------------------------------
    The Admin tool creates various game definition stubs.
    ----------------------------------------------------------------------------
*/
public class SysAdmin
{
    public String activePath = "Games/";

    // Parses the user's input and routes execution to the Listener
    public void Parse_Admin(String input)
    {
        boolean handled = false;
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

        handled = PathHandling(input);

        // Room Template
        if (!handled && cmd.equals("create"))
        {
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("room"))
            {
                Listener("createroom");
            }
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("object"))
            {
                Listener("createobject");
            }
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("event"))
            {
                Listener("createevent");
            }
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("command"))
            {
                Listener("createcommand");
            }
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("message"))
            {
                Listener("createmessage");
            }
            if (!handled && paramsArr.length > 0 && paramsArr[0].equals("logic"))
            {
                Listener("createlogic");
            }
        }
    }




    public void Listener(String action)
    {
        String input = "";
        Scanner scan = new Scanner(System.in);
        String prompt = "";

        switch (action)
        {
            case "createroom":
                System.out.print("Enter <alias>, <label>, <filename> >> ");
                break;

            case "createobject":
                System.out.print("Enter <alias>, <label>, <type>, <meta>, <location>, <parent type> >> ");
                break;

            case "createevent":
                System.out.print("Enter <type>, <message> >> ");
                break;
            case "createcommand":
                System.out.print("Enter <syntax>, <message> >> ");
                break;
            case "createmessage":
                System.out.print("Enter <message> >> ");
                break;
            case "createlogic":
                System.out.print("Enter <attribute|location>, <source>, <source value>, <operand> >> ");
                break;
        }

        try
        {
            input = scan.nextLine();
            if (CallListener(action, input)) Listener(action);
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
        }
    }

    public boolean CallListener(String action, String input)
    {
        boolean call = true;
        boolean match = false;
        String[] arr = input.split(",");
        List<String> contents = new ArrayList<String>();

        if (action.equals("createroom"))
        {
            if (arr.length != 3)
            {
                Functions.Output("To create a new room use: <alias>, <label>, <filename>");
                return call;
            }

            String alias = arr[0].trim();
            String label = arr[1].trim();
            String file = arr[2].trim();

            contents.add("{room alias=" + alias + ", label=" + label);
            contents.add("    {evt, type=enter");
            contents.add("        {mset");
            contents.add("            [msg");
            contents.add("                .");
            contents.add("            ]");
            contents.add("        }");
            contents.add("    }");
            contents.add("");
            contents.add("    {cmd, syntax=look|l");
            contents.add("        {aset");
            contents.add("            {act, type=event, newvalue=look }");
            contents.add("        }");
            contents.add("    }");
            contents.add("}");

            Functions.WriteToFile(activePath + file, contents);

            Functions.Output("Room created.");
            call = false;
        }

        if (action.equals("createobject"))
        {
            if (arr.length != 6)
            {
                Functions.Output("To create a new object use: <alias>, <label>, <type>, <meta>, <location>, <parent type>");
                return call;
            }

            String alias = arr[0].trim();
            String label = arr[1].trim();
            String type = arr[2].trim();
            String meta = arr[3].trim();
            String location = arr[4].trim();
            String parent = arr[5].trim();

            contents.add("{object, alias=" + alias + ", label=" + label + ", type=" + type + ", meta=" + meta + ", location=" + location + ", parent_type=" + parent);
            contents.add("    {evt, type=enter|look");
            contents.add("        {mset");
            contents.add("            [msg");
            contents.add("                .");
            contents.add("            ]");
            contents.add("        }");
            contents.add("    }");
            contents.add("");
            contents.add("    {evt, type=examine");
            contents.add("        {mset");
            contents.add("            [msg");
            contents.add("                .");
            contents.add("            ]");
            contents.add("        }");
            contents.add("    }");
            contents.add("");
            contents.add("    {cmd, syntax=examine");
            contents.add("        {aset");
            contents.add("            {act, type=object_event, newvalue=examine }");
            contents.add("        }");
            contents.add("    }");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        if (action.equals("createevent"))
        {
            if (arr.length != 2)
            {
                Functions.Output("To create a new Event use: <type>, <message>");
                return call;
            }
            String type = arr[0].trim();
            String message = arr[1].trim();

            contents.add("{event type=" + type);
            contents.add("    {mset");
            contents.add("       [msg");
            contents.add("          " + message);
            contents.add("       ]");
            contents.add("    }");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        if (action.equals("createcommand"))
        {
            if (arr.length != 2)
            {
                Functions.Output("To create a new Command use: <syntax>, <message>");
                return call;
            }
            String syntax = arr[0].trim();
            String message = arr[1].trim();

            contents.add("{cmd syntax=" + syntax);
            contents.add("    {mset");
            contents.add("        [msg");
            contents.add("           " + message);
            contents.add("        ]");
            contents.add("    }");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        if (action.equals("createmessage"))
        {
            if (arr.length != 1)
            {
                Functions.Output("To create a new Message use: <message>");
                return call;
            }
            String message = arr[0].trim();

            contents.add("{mset");
            contents.add("    [msg");
            contents.add("        " + message);
            contents.add("    ]");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        if (action.equals("createobject"))
        {
            if (arr.length != 1)
            {
                Functions.Output("To create a new Message use: <message>");
                return call;
            }
            String message = arr[0].trim();

            contents.add("{mset");
            contents.add("    [msg");
            contents.add("        " + message);
            contents.add("    ]");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        if (action.equals("createlogic"))
        {
            if (arr.length != 4)
            {
                Functions.Output("To create a new Logic block use: <attribute|location>, <source>, <source value>, <operand>");
                return call;
            }
            String type = arr[0].trim();
            String source = arr[1].trim();
            String sourceValue = arr[2].trim();
            String operand = arr[3].trim();

            contents.add("{lset");
            contents.add("    [@eval");
            contents.add("        " + type + "@" + source + operand + sourceValue);
            contents.add("    ]");
            contents.add("}");

            Functions.Output(contents);
            call = false;
        }

        return call;
    };

    public boolean PathHandling(String input)
    {
        boolean output = false;

        if (!output && input.equals("pwd"))
        {
            output = true;
            Functions.Output(activePath);
        }

        if (!output && input.equals("ls"))
        {
            List<String> f = Functions.ListDirContents(activePath);
            for (String cf : f)
            {
                Functions.OutputRaw(cf);
            }
        }

        if (!output && input.length() >= 4)
        {
            if (input.substring(0, 4).equals("cd /"))
            {
                output = true;
                activePath = input.substring(4);
                if (!activePath.substring(activePath.length()-1).equals("/"))
                {
                    activePath += "/";
                }
                Functions.Output(activePath);
            }
        }

        if (!output && input.length() >= 3)
        {
            if (!output && input.equals("cd .."))
            {
                output = true;
                // clear out the last / safely
                activePath = activePath.substring(0, activePath.lastIndexOf("/"));
                activePath = activePath.substring(0, activePath.lastIndexOf("/"));
                Functions.Output(activePath);
            }
            if (!output && input.substring(0, 3).equals("cd "))
            {
                output = true;
                activePath += input.substring(3);
                if (!activePath.substring(activePath.length()-1).equals("/"))
                {
                    activePath += "/";
                }
                Functions.Output(activePath);
            }
        }

        return output;
    }
}
