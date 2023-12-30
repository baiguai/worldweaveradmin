import java.io.*;
import java.util.*;
import java.util.Random;
import java.io.IOException;
import java.util.Enumeration;
import java.text.SimpleDateFormat;

public class SysNorman
{
    private List<Element> elements = null;
    public List<Element> GetElements() { if (elements == null) elements = new ArrayList<Element>(); return elements; }
    public void SetElements(List<Element> val) { elements = val; }
    public void AddElement(Element val) { GetElements().add(val); }
    boolean VERBOSE = false;
    boolean DEBUG = false;




    /* FILE PARSING */
        /* KICK OFF METHOD */
        public void ProcessNormanFile(String filePath)
        {
            // Utilize the verbose setting
            List<String> src = ReadFile(filePath);
            String srcRow = "";
            int endLine = 0;

            for (int ix = 0; ix < src.size(); ix++)
            {
                boolean handled = false;

                srcRow = src.get(ix).trim();

                if (!handled && IsElementStart(srcRow))
                {
                    handled = true;
                    endLine = GetElementEnd(src, ix);
                    ProcessElement(src, null, ix, endLine, "", filePath);
                    if (DEBUG) System.out.println("Root Element New Start Line " + endLine);
                    ix = endLine;
                }

                if (!handled && IsContentStart(srcRow))
                {
                    handled = true;
                    endLine = GetContentEnd(src, ix);
                    ProcessContent(src, null, ix, endLine, "", filePath);
                    if (DEBUG) System.out.println("Root Content New Start Line " + endLine);
                    ix = endLine;
                }
            }
        }

        public void ProcessElement(List<String> src, Element parentElement, int startLine, int endLine, String indent, String filePath)
        {
            String srcRow = "";
            String rowCleaned = "";
            String indentString = "  ";
            Element elem = new Element();
            int subEndLine = 0;

            elem.SetFilePath(filePath);

            for (int ix = startLine; ix <= endLine; ix++)
            {
                boolean handled = false;
                boolean oneLiner = false;

                // Get the row
                srcRow = src.get(ix).trim();

                // First line logic
                if (ix == startLine)
                {
                    if (VERBOSE) System.out.println(indent + "Element Row " + ix + " : " + srcRow);

                    try
                    {
                        // Cleaned up string
                        rowCleaned = srcRow.substring(1);
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Substring Error in ProcessElement. Row " + ix + " : " + srcRow);
                    }

                    // One liner logic
                    if (rowCleaned.length() > 0 && IsOneLiner(srcRow))
                    {
                        oneLiner = true;
                        try
                        {
                            rowCleaned = rowCleaned.substring(0, rowCleaned.length() - 1);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("Substring Error - rowCleaned - in ProcessElement. Row " + ix + " : " + rowCleaned);
                        }

                        if (DEBUG) System.out.println(indent + "Element Cleaned Row " + ix + " : " + rowCleaned);
                    }

                    // Process the element name and properties
                    rowCleaned = StringEncode(rowCleaned);
                    String[] props = null;
                    try
                    {
                        props = rowCleaned.split(",");
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Split Error - stringEncode - in ProcessElement. Row " + ix + " : " + rowCleaned);
                    }
                    if (props.length < 2)
                    {
                        try
                        {
                            elem.SetElementName(props[0]);
                            if (VERBOSE) System.out.println(indent + "Element Set Name - no properties. Line " + ix + " : " + props[0]);
                        }
                        catch (Exception ex)
                        {
                            System.out.println("Element - SetElementName Error. Row " + ix + " - " + props[0]);
                        }
                    }
                    else
                    {
                        elem.SetElementName(props[0]);
                        if (VERBOSE) System.out.println(indent + "Element Set Name. Line " + ix + " : " + props[0]);
                        for (int pix = 1; pix < props.length; pix++)
                        {
                            String[] prop = null;
                            try
                            {
                                prop = props[pix].trim().split("=", 2);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("Element Property Split Error. Row " + ix + " - " + props[pix]);
                            }
                            ProcessProperty(elem, prop);
                        }

                        if (VERBOSE) System.out.println(indent + "Element - Adding Properties - length : " + (props.length - 1));
                    }

                    if (oneLiner)
                    {
                        if (VERBOSE) System.out.println(indent + "Element - Adding One Line Element. Line " + ix + " : " + elem.GetElementName());
                        DoAddElement(parentElement, elem);
                        return;
                    }
                }
                else
                {
                    // Other line logic
                    if (ix < endLine)
                    {
                        // New Element
                        if (!handled && IsElementStart(srcRow))
                        {
                            subEndLine = GetElementEnd(src, ix);
                            ProcessElement(src, elem, ix, subEndLine, indent + indentString, filePath);
                            handled = true;
                            if (DEBUG) System.out.println(indent + "Element New Start Line " + subEndLine);
                            ix = subEndLine;
                        }

                        // New Content
                        if (!handled && IsContentStart(srcRow))
                        {
                            subEndLine = GetContentEnd(src, ix);
                            ProcessContent(src, elem, ix, subEndLine, indent + indentString, filePath);
                            handled = true;
                            if (DEBUG) System.out.println(indent + "Element Content New Start Line " + subEndLine);
                            ix = subEndLine;
                        }

                        // Properties
                        if (!handled &&
                            !IsContentStart(srcRow) &&
                            srcRow.indexOf("=") >= 0)
                        {
                            String[] prop = null;

                            try
                            {
                                prop = srcRow.split("=", 2);
                                ProcessProperty(elem, prop);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("Element Property Line Split Error. Row " + ix + " - " + srcRow);
                            }

                            handled = true;
                            if (VERBOSE) System.out.println(indent + "Element - Property Added: " + prop[0]);
                        }
                    }
                    else
                    {
                        DoAddElement(parentElement, elem);
                    }
                }
            }
        }

        public void ProcessContent(List<String> src, Element parentElement, int startLine, int endLine, String indent, String filePath)
        {
            String srcRow = "";
            String rowCleaned = "";
            String indentString = "  ";
            Element elem = new Element();
            String content = "";
            String wrapMode = "none";

            elem.SetFilePath(filePath);

            if (startLine == endLine)
            {
                ProcessSingleLineContent(src, parentElement, startLine, indent, filePath);
                return;
            }

            for (int ix = startLine; ix <= endLine; ix++)
            {
                boolean handled = false;

                // Get the row
                srcRow = StringEncode(src.get(ix).trim());

                // First line logic
                if (ix == startLine)
                {
                    if (VERBOSE) System.out.println(indent + "Content Row " + ix + " : " + srcRow);

                    try
                    {
                        // Cleaned up string
                        rowCleaned = srcRow.substring(1);

                        if (rowCleaned.substring(0, 1).equals("@"))
                        {
                            rowCleaned = rowCleaned.substring(1);
                            wrapMode = "manual";
                        }
                        else
                        {
                            wrapMode = "none";
                        }
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Substring Error in ProcessElement. Row " + ix + " : " + srcRow);
                    }

                    elem.SetElementName(StringDecode(rowCleaned));
                    if (VERBOSE) System.out.println(indent + "Set Content Name " + ix + " : " + rowCleaned);
                }
                else
                {
                    if (ix < endLine)
                    {
                        if (DEBUG) System.out.println(indent + "Content " + ix + " : " + srcRow);

                        // Other line logic
                        if (!content.equals(""))
                        {
                            switch (wrapMode)
                            {
                                case "manual":
                                    // Do Nothing
                                    break;

                                case "none":
                                default:
                                    content += " ";
                                    break;
                            }
                        }

                        switch (wrapMode)
                        {
                            case "manual":
                                // Clear out any unwanted spaces
                                // Add line breaks
                                content = content.trim() + "\n" + StringDecode(srcRow.trim());
                                break;

                            case "none":
                            default:
                                // Be sure to leave the line breaks in the string
                                content += StringDecode(srcRow);
                                break;
                        }
                    }
                    else
                    {
                        elem.SetContent(content);
                        DoAddElement(parentElement, elem);
                    }
                }
            }
        }

        public void ProcessSingleLineContent(List<String> src, Element parentElement, int startLine, String indent, String filePath)
        {
            String srcRow = "";
            String rowCleaned = "";
            String indentString = "  ";
            Element elem = new Element();
            String content = "";
            String wrapMode = "none";

            elem.SetFilePath(filePath);

            boolean handled = false;

            // Get the row
            srcRow = src.get(startLine).trim();
            srcRow = srcRow.substring(1);
            srcRow = srcRow.substring(0, srcRow.length()-1);
            String[] arr = srcRow.split("==", 2);

            if (arr.length == 2)
            {
                elem.SetElementName(arr[0].trim());
                elem.SetContent(arr[1].trim());
                DoAddElement(parentElement, elem);
            }
        }

        public String GenerateReport(String indent, Element parentElem)
        {
            String output = "";

            if (GetElements().size() < 1)
            {
                return "No elements to report on.";
            }

            if (parentElem == null)
            {
                for (Element e : GetElements())
                {
                    if (!output.equals("")) output += "\n";
                    else  output += "\n\nREPORT:\n\n";
                    output += "Element: " + e.GetElementName();

                    output += GenerateReport(indent += "  ", e);
                }
            }
            else
            {
                for (Element e : parentElem.GetElements())
                {
                    if (!output.equals("")) output += "\n";
                    else  output += "\n\nREPORT:\n\n";
                    output += indent + "Element: " + e.GetElementName();

                    output += GenerateReport(indent += "  ", e);
                }
            }

            return output;
        }


        /* ADD THE ELEMENT */
        public void DoAddElement(Element parentElement, Element childElement)
        {
            if (parentElement == null)
            {
                AddElement(childElement);
            }
            else
            {
                parentElement.AddElement(childElement);
            }
        }


        /* PROCESS PROPERTY */
        public boolean ProcessProperty(Element elem, String[] list)
        {
            boolean output = false;
            List<String> prop = new ArrayList<String>();

            try
            {
                for (int ix = 0; ix < list.length; ix++)
                {
                    prop.add(StringDecode(list[ix].trim()));
                }
            }
            catch (Exception ex)
            {
                System.out.println("Prop Add Error in ProcessProperty");
            }

            // If we have two items, it's a property
            try
            {
                if (prop.size() == 1) prop.add("");
                if (prop.size() == 2)
                {
                    elem.AddProperty(prop);
                    output = true;
                }
            }
            catch (Exception ex)
            {
                System.out.println("Adding Property Error in ProcessProperty");
            }

            return output;
        }


    /* HELPER METHODS */
        /* IS ELEMENT START */
        public boolean IsElementStart(String curRow)
        {
            boolean output = false;

            try
            {
                if (curRow.trim().substring(0, 1).equals("{")) output = true;
            }
            catch (Exception ex)
            {
                System.out.println("Substring error in IsElementStart : " + curRow);
            }

            return output;
        }

        /* IS ELEMENT END */
        public boolean IsElementEnd(String curRow)
        {
            boolean output = false;

            try
            {
                if (curRow.trim().equals("}")) output = true;
            }
            catch (Exception ex)
            {
                System.out.println("Substring error in IsElementEnd : " + curRow);
            }

            return output;
        }

        /* IS CONTENT START */
        public boolean IsContentStart(String curRow)
        {
            boolean output = false;

            try
            {
                if (curRow.substring(0, 1).equals("[")) output = true;
            }
            catch (Exception ex)
            {
                System.out.println("Substring error in IsContentStart : " + curRow);
            }

            return output;
        }

        /* IS CONTENT END */
        public boolean IsContentEnd(String curRow)
        {
            boolean output = false;

            try
            {
                if (curRow.equals("]")) output = true;
            }
            catch (Exception ex)
            {
                System.out.println("Equals error in IsContentEnd : " + curRow);
            }

            return output;
        }

        /* GET ELEMENT END */
        public int GetElementEnd(List<String> src, int startRow)
        {
            int endRow = startRow;
            String srcRow = "";
            int indent = 0;

            try
            {
                for (int ix = startRow; ix < src.size(); ix++)
                {
                    srcRow = src.get(ix).trim();
                    if (ix == startRow && IsOneLiner(srcRow)) return ix;
                    else
                    {
                        if (ix > startRow)
                        {
                            if (!IsOneLiner(srcRow))
                            {
                                if (IsElementStart(srcRow)) indent++;
                                if (IsElementEnd(srcRow))
                                {
                                    if (indent > 0) indent--;
                                    else return ix;
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println("Start / End Error in GetElementEnd - Start Row " + startRow);
            }

            return endRow;
        }

        /* GET CONTENT END */
        public int GetContentEnd(List<String> src, int startRow)
        {
            int endRow = startRow;
            String srcRow = "";

            try
            {
                srcRow = src.get(startRow).trim();
                if (srcRow.substring(srcRow.length() - 1).equals("]"))
                {
                    return startRow;
                }

                for (int ix = startRow + 1; ix < src.size(); ix++)
                {
                    srcRow = StringEncode(src.get(ix).trim());
                    if (IsContentEnd(srcRow))
                    {
                        return ix;
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println("Start / End Error in GetContentEnd - Start Row " + startRow);
            }

            return endRow;
        }

        /* ONE LINER CHECK */
        public boolean IsOneLiner(String line)
        {
            int openCount = 0;
            int closeCount = 0;

            try
            {
                if (!line.trim().substring(line.length()-1).equals("}")) return false;

                for(char c : line.toCharArray()){
                    if (c == '{') openCount++;
                    if (c == '}') closeCount++;
                }
            }
            catch (Exception ex)
            {
                System.out.println("Substring, Equals, CharArray Error in IsOneLiner : " + line);
            }

            return (openCount == closeCount);
        }

        /* FILE METHODS */
        public List<String> ReadFile(String file)
        {
            List<String> inp = new ArrayList<String>();
            String line;

            try
            {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        // # at the start of the line is a comment, don't read
                        if (!line.trim().equals(""))
                        {
                            if (!line.trim().substring(0, 1).equals("#"))
                            {
                                if (line.length() < 2 ||
                                    !line.trim().substring(0, 2).equals("//"))
                                {
                                    inp.add(line);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return inp;
        };

        // Cleanup Row
        public String StringEncode(String value)
        {
            try
            {
                value = value.replace("\\n", "@nl@");
                value = value.replace("\\,", "@cma@");
                value = value.replace("*", "@ast@");
                value = value.replace("!", "@exc@");
                value = value.replace("\\{", "@crl@");
                value = value.replace("\\[", "@sqr@");
                value = value.replace("\\}", "@ecrl@");
                value = value.replace("\\]", "@esqr@");
            }
            catch (Exception ex)
            {
                System.out.println("Error in StringEncode - replace : " + value);
            }
            return value;
        }
        public String StringDecode(String value)
        {
            try
            {
                value = value.replace("@nl@", "\\n");
                value = value.replace("@cma@", ",");
                value = value.replace("@ast@", "*");
                value = value.replace("@exc@", "!");
                value = value.replace("@esqr@", "]");
                value = value.replace("@crl@", "{");
                value = value.replace("@sqr@", "[");
                value = value.replace("@ecrl@", "}");
                value = value.replace("\\]", "]");
            }
            catch (Exception ex)
            {
                System.out.println("Error in StringDecode - replace : " + value);
            }
            return value;
        }


        // Clean up objects
        public void Clear()
        {
            elements.clear();
            elements = null;
            System.gc();
        }

        public Element NewElement()
        {
            return new Element();
        }




    /* CLASSES */
    public class Element
    {
        private String filePath = "";
        public String GetFilePath() { return filePath; }
        public void SetFilePath(String val) { filePath = val; }

        private String elementName = "";
        public String GetElementName() { return elementName; }
        public void SetElementName(String val) { elementName = val; }

        private List<List<String>> properties = new ArrayList<List<String>>();
        public List<List<String>> GetProperties() { return properties; }
        public String GetProperty(String name)
        {
            String output = "";

            for (List<String> p : GetProperties())
            {
                if (p.get(0).trim().equals(name))
                {
                    output = p.get(1).trim();
                }
            }

            return output;
        }
        public void SetProperties(List<List<String>> val) { properties = val; }
        public void AddProperty(List<String> val) { properties.add(val); }

        private List<Element> elements = new ArrayList<Element>();
        public List<Element> GetElements() { return elements; }
        public void SetElements(List<Element> val) { elements = val; }
        public void AddElement(Element val) { elements.add(val); }

        private String content = "";
        public String GetContent() { return content; }
        public void SetContent(String val) { content = val; }

        public Element() {}
        public Element(String elementNameIn)
        {
            SetElementName(elementNameIn);
        }
    }
}
