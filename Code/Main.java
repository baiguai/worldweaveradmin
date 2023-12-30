/*
    MAIN CLASS
    ----------------------------------------------------------------------------
    The primary class - creates the instance of the parser.
    ----------------------------------------------------------------------------
*/
public class Main
{
    // Properties
    private SysParser cparser;


    public static void main(String[] args)
    {
        String input = "";

        // Command line actions
        if (args.length > 0)
        {
            // Macros
            if (args.length == 2 && args[0].toLowerCase().trim().equals("-m"))
            {
                input = "macro " + args[1].trim();
            }
        }

        System.out.println(input);

        Main m = new Main();
        m.CParser().Listener(input);
    };





    // GET/SET
    public SysParser CParser() { if (cparser == null) cparser = new SysParser(); return cparser; }
};
