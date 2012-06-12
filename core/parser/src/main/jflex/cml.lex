package eu.compassresearch.core.lexer;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Vector;
import java.util.Stack;
import eu.compassresearch.core.parser.CmlParser;
import eu.compassresearch.core.parser.CmlParser.Lexer;
import eu.compassresearch.core.parser.CmlParser.Location;
import org.overturetool.vdmj.lex.*;
/*
class CmlContext {
  
}
*/

class S {
  public static StringBuilder b;
}

class CommentBlock extends CMLToken {
  static CommentBlock current;
  private int level;
  StringBuilder content;

  public CommentBlock()
  {
    super("--");
    current=this;
    content=new StringBuilder();
    level = 1;
  }

  public void appendLine(String line)
  {
    this.content.append(line);
  }

  public void increaseLevel()
  {
    level ++;
  }

  public boolean decreaseLevel()
  {
    level--;
    return level == 0;
  }
}

%%
// ******************************************
// *** JFLEX SCANNER GENERATOR DIRECTIVES ***
// ******************************************
// The Lexer output class name 
%class CmlLexer
//The Lexer interface generated by the parser
%implements Lexer
//input files will be in unicode
%unicode
 //used when interfacing with bison  
%byaccj
// yylex return CmlLexeme
//%type CmlLexeme 
// all JFLEX internal operations, including yylex, are defined as private
//%apiprivate
%line
%column
%char
 //%debug
%public

%{
  static private Stack<Integer> stateStack = new Stack<Integer>();
  
  // placeholder for the reserved word (keyword) table
  static private HashMap<String,Integer> keywords = null;
  
  // initialize the reserved word table as a static constructor
  static {
    keywords = new HashMap<String,Integer>();
    
    keywords.put("abs", CmlParser.ABS);
    //keywords.put("all", CmlParser.ALL);
    //keywords.put("always", CmlParser.ALWAYS);
    keywords.put("and", CmlParser.AND);
    keywords.put("atomic", CmlParser.ATOMIC);
    //keywords.put("async", CmlParser.ASYNC);
    //keywords.put("be", CmlParser.BE);
    keywords.put("bool", CmlParser.TBOOL);
    //keywords.put("by", CmlParser.BY);
    keywords.put("card", CmlParser.CARD);
    //    keywords.put("cases", CmlParser.CASES);
    keywords.put("char", CmlParser.TCHAR);
    keywords.put("class", CmlParser.CLASS);
    /*
    keywords.put("comp", CmlParser.COMP);
    keywords.put("compose", CmlParser.COMPOSE);
    keywords.put("conc", CmlParser.CONC);
    keywords.put("cycles", CmlParser.CYCLES);
    keywords.put("dcl", CmlParser.DCL);
    keywords.put("def", CmlParser.DEF);
    keywords.put("dinter", CmlParser.DINTER);
    keywords.put("div", CmlParser.ARITHMETIC_INTEGER_DIVISION);
    keywords.put("do", CmlParser.DO);
    keywords.put("dom", CmlParser.DOM);
    keywords.put("dunion", CmlParser.DUNION);
    keywords.put("duration", CmlParser.DURATION);
    keywords.put("elems", CmlParser.ELEMS);
    */
    keywords.put("else", CmlParser.ELSE);
    keywords.put("elseif", CmlParser.ELSEIF);
    keywords.put("end", CmlParser.END);
    /*
    keywords.put("error", CmlParser.ERROR);
    keywords.put("errs", CmlParser.ERRS);
    keywords.put("exists", CmlParser.EXISTS);
    keywords.put("exists1", CmlParser.EXISTS1);
    keywords.put("exit", CmlParser.EXIT);
    keywords.put("ext", CmlParser.EXT);
    */
    //    keywords.put("false", CmlParser.bool_false);
    /*
    keywords.put("floor", CmlParser.FLOOR);
    keywords.put("for", CmlParser.FOR);
    keywords.put("forall", CmlParser.FORALL);
    keywords.put("from", CmlParser.FROM);
    keywords.put("functions", CmlParser.FUNCTIONS);
    keywords.put("hd", CmlParser.HD);
    */   
    keywords.put("if", CmlParser.IF);
   
    keywords.put("in", CmlParser.IN);
    /*
    keywords.put("inds", CmlParser.INDS);
    keywords.put("inmap", CmlParser.INMAP);
    keywords.put("instance", CmlParser.INSTANCE);
    keywords.put("int", CmlParser.INT);
    keywords.put("inter", CmlParser.SET_INTERSECTION);
    keywords.put("inv", CmlParser.INV);
    keywords.put("inverse", CmlParser.INVERSE);
    keywords.put("iota", CmlParser.IOTA);
    keywords.put("is", CmlParser.IS);
    keywords.put("is_", CmlParser.IS_);
    keywords.put("isofbaseclass", CmlParser.ISOFBASECLASS);
    keywords.put("isofclass", CmlParser.ISOFCLASS);
    keywords.put("lambda", CmlParser.LAMBDA);
    keywords.put("len", CmlParser.LEN);
    */
    keywords.put("let", CmlParser.LET);
    /*
    keywords.put("map", CmlParser.MAP);
    keywords.put("merge", CmlParser.DMERGE);
    keywords.put("mk_", CmlParser.MK_);
    keywords.put("mod", CmlParser.MOD);
    keywords.put("mu", CmlParser.MU);
    keywords.put("munion", CmlParser.MAP_MERGE);
    keywords.put("mutex", CmlParser.MUTEX);
    */
    keywords.put("nat", CmlParser.TNAT);
    keywords.put("nat1", CmlParser.TNAT1);
    /*
    keywords.put("new", CmlParser.NEW);
    keywords.put("nil", CmlParser.NIL);
    keywords.put("not", CmlParser.NOT);
    keywords.put("of", CmlParser.OF);
    keywords.put("operations", CmlParser.OPERATIONS);
    keywords.put("or", CmlParser.OR);
    keywords.put("others", CmlParser.OTHERS);
    keywords.put("per", CmlParser.PER);
    keywords.put("periodic", CmlParser.PERIODIC);
    keywords.put("post", CmlParser.POST);
    keywords.put("power", CmlParser.POWER);
    keywords.put("pre", CmlParser.PRE);
    keywords.put("pre_", CmlParser.PRECONDAPPLY);
    */
    //keywords.put("private", CmlParser.PRIVATE);
    keywords.put("process", CmlParser.PROCESS);
    //keywords.put("protected", CmlParser.PROTECTED);
    keywords.put("psubset", CmlParser.PROPER_SUBSET);
    //keywords.put("public", CmlParser.PUBLIC);
    keywords.put("rat", CmlParser.TRAT);
    /*
    keywords.put("rd", CmlParser.VDMRD);
    */
    keywords.put("real", CmlParser.TREAL);
    /*
    keywords.put("rem", CmlParser.REM);
    keywords.put("responsibility", CmlParser.RESPONSIBILITY);
    keywords.put("return", CmlParser.RETURN);
    keywords.put("reverse", CmlParser.REVERSE);
    keywords.put("rng", CmlParser.RNG);
    keywords.put("samebaseclass", CmlParser.SAMEBASECLASS);
    keywords.put("sameclass", CmlParser.SAMECLASS);
    keywords.put("self", CmlParser.SELF);
    keywords.put("seq", CmlParser.SEQ);
    keywords.put("seq1", CmlParser.SEQ1);
    keywords.put("set", CmlParser.SET);
    keywords.put("skip", CmlParser.SKIP);
    keywords.put("specified", CmlParser.SPECIFIED);
    keywords.put("st", CmlParser.ST);
    keywords.put("start", CmlParser.START);
    keywords.put("startlist", CmlParser.STARTLIST);
    keywords.put("static", CmlParser.STATIC);
    keywords.put("subclass", CmlParser.SUBCLASS);
    keywords.put("subset", CmlParser.SUBSET);
    keywords.put("sync", CmlParser.SYNC);
    keywords.put("system", CmlParser.SYSTEM);
    */
    keywords.put("then", CmlParser.THEN);
    /*
    keywords.put("thread", CmlParser.THREAD);
    keywords.put("threadid", CmlParser.THREADID);
    keywords.put("time", CmlParser.TIME);
    keywords.put("tixe", CmlParser.TIXE);
    keywords.put("tl", CmlParser.TL);
    keywords.put("to", CmlParser.TO);
    */
    keywords.put("token", CmlParser.TTOKEN);
    /*
    keywords.put("trap", CmlParser.TRAP);
    */
    //    keywords.put("true", CmlParser.bool_true);
    keywords.put("types", CmlParser.TYPES);
    //keywords.put("undefined", CmlParser.UNDEFINED);
    keywords.put("union", CmlParser.UNION);
    keywords.put("values", CmlParser.VALUES);
    //keywords.put("variables", CmlParser.INSTANCEVARS);
    /*
    keywords.put("while", CmlParser.WHILE);
    keywords.put("with", CmlParser.WITH);
    keywords.put("wr", CmlParser.VDMWR);
    keywords.put("yet", CmlParser.YET);
    */
    
  }


  private CmlLexeme yylvalue;
  
  public int errors = 0;
public List<ParserError> parseErrors = new Vector<ParserError>();
  
    /**
     * Method to retrieve the beginning position of the last scanned token.
     * @return the position at which the last scanned token starts.  */
  public Position getStartPos () { return yylvalue.getStartPos(); }

    /**
     * Method to retrieve the ending position of the last scanned token.
     * @return the first position beyond the last scanned token.  */
  public Position getEndPos () { return yylvalue.getEndPos(); }

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.  */
  public Object getLVal () { return yylvalue; }
  
    /**
     * Entry point for error reporting.  Emits an error
     * referring to the given location in a user-defined way.
     *
     * @param loc The location of the element to which the
     *                error message is related
     * @param s The string for the error message.  */
  public void yyerror (Location loc, String err) { 
    
    //    System.err.println("Error : " + err + " at " + loc.begin.toString()); 
    
      String msg = new String(err);
	    if (yylvalue != null) {
	    	CmlLexeme currentToken = (CmlLexeme) yylvalue;
	    	if (currentToken.getValue().length() > 0) {
	    		// add position info iff the scanner found a lexem
	    	    msg += " at (" + currentToken.getStartPos().line + ", " + currentToken.getStartPos().column + ")";
	    		msg += " after reading token \"" + currentToken.getValue() + "\"";
	    		
	    		parseErrors.add(new ParserError(err+" after reading token \"" + currentToken.getValue() + "\"", 
	    		currentToken.getStartPos().line, currentToken.getStartPos().column));
	    	}
	    }
		System.out.println(msg);
		errors++;
    
  }
   

  // ************************************
  // *** AUXILIARY PRIVATE OPERATIONS ***
  // ************************************

  // helper function for checking reserved words and identifiers
  private int checkIdentifier(String id) {
      
      int line = yyline + 1;
      int column = yycolumn;
      String value = yytext();
      
      try {
	  if (keywords.containsKey(value)) {
	      //return new OmlLexem(line, column, new Long(keywords.get(id)), id, IOmlLexem.ILEXEMKEYWORD);
	      //return new CmlLexeme(line, column,IDENTIFIER, id);
	      yylvalue = new CmlLexeme(new Position(line,column),new Position(line,column + value.length()),keywords.get(value),value);
	      int v = keywords.get(value);				   
	      return v;
	      
	  } else {
	      //DEBUG String theText = yytext();
	      //DEBUG System.out.print(theText + " = ");
	      //DEBUG for (int idx=0; idx< theText.length(); idx++) System.out.format("%04x ", (int) theText.charAt(idx));
	      //DEBUG System.out.println();
	      //return new OmlLexem(line, column, new Long(LEX_identifier), id, IOmlLexem.ILEXEMIDENTIFIER);
	      //return new CmlLexeme(line, column, IDENTIFIER, id);
	      yylvalue = new CmlLexeme(new Position(line,column),new Position(line,column + value.length()),CmlParser.IDENTIFIER,value);
	      return CmlParser.IDENTIFIER;
	  }
      }
      catch (Exception cge) {
	  cge.printStackTrace();
	  return -1;
      }
  }
  
  // helper function for default token creation
  private int defaultToken()
  {
    return createToken(yytext().charAt(0));
  }
  
  private int createToken(int lex, String value)
  {
    int line = yyline + 1;
    int column = yycolumn;

    try {
      yylvalue = new CmlLexeme(new Position(line,column),new Position(line,column + value.length()),lex,value);
      //return new CmlLexeme(line, column, lex, yytext());
      return lex;
    }
    catch (Exception cge) {
      cge.printStackTrace();
      return -1;
    }
  }

  /* Helper function to return the correct integer for the parser and create the correct Lexeme (semantic) values for the parser and beyond */
  private int createToken(int lex)
  {
    String value = yytext();
    return createToken(lex, value);
  }

  private int createToken(int lex, VDMToken vdmToken)
  {
      int r = createToken(lex);
      CmlLexeme cmlLexeme = (CmlLexeme)yylvalue;
      
      return r;
  }
  
%}

// *****************************
// *** SHORTHAND DEFINITIONS ***
// *****************************

ucode					= [\u0100-\ufff0] 
hexdigit 				= [0-9ABCDEF]|[0-9abcdef] 
hexquad 				= {hexdigit}{hexdigit}{hexdigit}{hexdigit} 
hexliteral                              = 0[x X]{hexdigit}+ 
universalcharactername  = (\\u{hexquad})|(\\U{hexquad}) 
letter 					= [A-Za-z]|#[A-Za-z]|{universalcharactername}|{ucode} 
digit 					= [0-9] 
/* rtfuniversalcharacter	= \\u{hexquad}[A-Za-z] */
/* identifierorkeyword		= {letter}([0-9\'_]|{letter})* */

numeral 			= {digit}+ 
/* realliteral				= [0-9]+(("."[0-9]+)|([Ee]("+"|"-")?[0-9]+)|("."[0-9]+[Ee]("+"|"-")?[0-9]+)) */

/* embeddedctrlchar 		= [\000-\037] */
/* backslashed				= \\c.|\\x..|\\[\\nrabtvef\'\"]|\\[0-3][0-7][0-7] */
/* highbitchar				= [\200-\377] */
/* deletechar				= \177 */
/* characterliteral		= "'"([\040-\133\135-\176]|{embeddedctrlchar}|{backslashed}|{deletechar}|{highbitchar}|{universalcharactername}|{rtfuniversalcharacter}|{ucode})"'" */

/* textliteral				= \"([\040-\041\043-\133\135-\176]|{embeddedctrlchar}|{backslashed}|{deletechar}|{highbitchar}|{universalcharactername}|{ucode})*\" */

/* quoteliteral 			= \<{identifierorkeyword}\> */

/* range					= ","({separator}*)"..."({separator}*)"," */

prime		= \` 
hook 		= \~ 

//identifier      = [A-Za-z0-9]
actions         = [Aa][Cc][Tt][Ii][Oo][Nn][Ss]
begin           = [Bb][Ee][Gg][Ii][Nn]
channels        = [Cc][Hh][Aa][Nn][Nn][Ee][Ll][Ss]
chansets        = [Cc][Hh][Aa][Nn][Ss][Ee][Tt][Ss]
class           = [Cc][Ll][Aa][Ss][Ss]
end             = [Ee][Nn][Dd]
functions       = [Ff][Uu][Nn][Cc][Tt][Ii][Oo][Nn][Ss]
//global          = [Gg][Ll][Oo][Bb][Aa][Ll]
identifier      = {letter}([0-9\'_]|{letter})*
initial         = [Ii][Nn][Ii][Tt][Ii][Aa][Ll]
operations      = [Oo][Pp][Ee][Rr][Aa][Tt][Ii][Oo][Nn][Ss]
process         = [Pp][Rr][Oo][Cc][Ee][Ss][Ss]
state           = [Ss][Tt][Aa][Tt][Ee]
types           = [Tt][Yy][Pp][Ee][Ss]
mk_name         = "mk_"{identifier}
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

%states CLASS PROCESS STATE TYPES STATE FUNCTIONS OPERATIONS CHANNELS CHANSETS ACTIONS VDM_CASES 
%xstates COMMENT STRING LCOMMENT

%%
									  
// ********************************
// *** SCANNER PRODUCTION RULES ***
// ********************************

"//".*                                { /* match comment; do nothing */ }
"/*"                                  { stateStack.push(yystate()); yybegin(COMMENT); }
<COMMENT>"*/"                         { yybegin(stateStack.pop()); }
<COMMENT>[^*]                         { /* match comment text; do nothing */ }
<COMMENT>\**[^/]                      { /* match comment text; do nothing */ }
"--"                                  { stateStack.push(yystate()); yybegin(LCOMMENT); }
<LCOMMENT>.*                          { }
<LCOMMENT>\n                          { yybegin(stateStack.pop()); }

<CLASS,PROCESS,TYPES,STATE,FUNCTIONS,OPERATIONS,CHANNELS,CHANSETS,ACTIONS,YYINITIAL> {
  {actions}                           { yybegin(ACTIONS); return createToken(CmlParser.CSP_ACTIONS); }
  {channels}                          { yybegin(CHANNELS); return createToken(CmlParser.CHANNELS); }
  {chansets}                          { yybegin(CHANSETS); return createToken(CmlParser.CHANSETS); }
  {types}                             { yybegin(TYPES); return createToken(CmlParser.TYPES); }
  {state}                             { yybegin(STATE); return createToken(CmlParser.STATE); }
  {functions}                         { yybegin(FUNCTIONS); return createToken(CmlParser.FUNCTIONS); }
  {operations}                        { yybegin(OPERATIONS); return createToken(CmlParser.OPERATIONS); }
  {class}                             { stateStack.push(yystate());yybegin(CLASS); return createToken(CmlParser.CLASS); }
  {process}                           { stateStack.push(yystate());yybegin(PROCESS); return createToken(CmlParser.PROCESS); }
}

//Jump to the action state if "@" occurs
<TYPES,STATE,FUNCTIONS,OPERATIONS> {
  "@"                                 { yybegin(ACTIONS); return createToken(CmlParser.AT); }
 }


/* //common state for CLASS PROCESS and GLOBAL definitions */
/* <CLASS,PROCESS,GLOBAL,TYPES,STATE,FUNCTIONS,OPERATIONS,> { */
/*   {types}                             { yybegin(TYPES); return createToken(CmlParser.TYPES); } */
/*   {state}                             { yybegin(STATE); return createToken(CmlParser.STATE); } */
/*   {functions}                         { yybegin(FUNCTIONS); return createToken(CmlParser.FUNCTIONS); } */
/*   {operations}                        { yybegin(OPERATIONS); return createToken(CmlParser.OPERATIONS); } */
/* } */


<CLASS,PROCESS> {
  "="                                 { return createToken(CmlParser.EQUALS); }
  {begin}                             { return createToken(CmlParser.BEGIN); }
  {end}                               { yybegin(stateStack.pop()); return createToken(CmlParser.END); }
}

<TYPES> {
  ";"                                 { return createToken(CmlParser.SEMI); }
  ":-"                                { return createToken(CmlParser.VDMTYPENCMP); }
  "::"                                { return createToken(CmlParser.VDMRECORDDEF); }
  "=="                                { return createToken(CmlParser.DEQUALS); }
 }

/* <STATE> { */
/*   //"of"                                { /\*return createToken(CmlParser.VDMOF); *\/} */
/* } */

<FUNCTIONS> {
  "+>"				      { return createToken(CmlParser.VDMPFUNCARROW); }
  /*"->"				      { return createToken(CmlParser.VDMTFUNCARROW); }*/
  "->"				      { return createToken(CmlParser.RARROW); }
}

<OPERATIONS> {
  "==>"				      { return createToken(CmlParser.OPERATIONARROW); }
  "rd"				      { return createToken(CmlParser.VDM_RD); }
  "wr"				      { return createToken(CmlParser.VDM_WR); }
  "frame"			      { return createToken(CmlParser.VDM_FRAME); }
  "return"			      { return createToken(CmlParser.RETURN); }
}

<FUNCTIONS, OPERATIONS> {
  "post"                              { return createToken(CmlParser.POST); }
  "pre"                               { return createToken(CmlParser.PRE); }
  "measure"                           { return createToken(CmlParser.MEASURE); }
  "is subclass responsibility"        { return createToken(CmlParser.VDM_SUBCLASSRESP); }
  "is not yet specified"              { return createToken(CmlParser.VDM_NOTYETSPEC); }
}

// <CHANNELS> {

// }

<CHANSETS> {
  "{|"				      { return createToken(CmlParser.CHANSET_SETEXP_BEGIN); } //TODO: CHANGE this into something else
  "|}"				      { return createToken(CmlParser.CHANSET_SETEXP_END); } //TODO: CHANGE this into something else
  "="                                 { return createToken(CmlParser.EQUALS); }
}

<ACTIONS,PROCESS,CHANSETS> {
  ";"                                 { return createToken(CmlParser.CSPSEQ); }
  //  "|"                                 { return createToken(CmlParser.BAR); }
  "["                                 { return createToken(CmlParser.CSP_LSQUARE); }
  "]"                                 { return createToken(CmlParser.CSP_RSQUARE); }
  ">"                                 { return createToken(CmlParser.CSP_GT); }
  "[]"                                { return createToken(CmlParser.CSPEXTCH); }
  "[["                                { return createToken(CmlParser.DLSQUARE); }
  "]]"                                { return createToken(CmlParser.DRSQUARE); }
  "[|"                                { return createToken(CmlParser.CSPLSQUAREBAR); }
  "|]"                                { return createToken(CmlParser.CSPBARRSQUARE); }
  "<-"                                { return createToken(CmlParser.CSPSAMEAS); }
  "|~|"                               { return createToken(CmlParser.CSPINTCH); }
  "|||"                               { return createToken(CmlParser.CSPINTERLEAVE); }
  "||"                               { return createToken(CmlParser.CSPDBAR); }
  "[>"                                { return createToken(CmlParser.CSPLSQUAREGT); }
  "/"                                 { return createToken(CmlParser.CSP_SLASH); }
  "\\"                                { return createToken(CmlParser.CSP_BACKSLASH); }
  "&"				      { return createToken(CmlParser.AMP); }
  "?"				      { return createToken(CmlParser.CSP_CHANNEL_READ); }
  "!"				      { return createToken(CmlParser.CSP_CHANNEL_WRITE); }
  "."				      { return createToken(CmlParser.CSP_CHANNEL_DOT); }
  "dcl"                               { yybegin(TYPES);return createToken(CmlParser.DCL); }
  "@"                                 { return createToken(CmlParser.AT); }
  "=>"                                { return createToken(CmlParser.CSP_OPS_COM); }
  "Skip"                              { return createToken(CmlParser.CSPSKIP); }
  "Stop"                              { return createToken(CmlParser.CSPSTOP); }
  "Chaos"                             { return createToken(CmlParser.CSPCHAOS); }
  "startby"                           { return createToken(CmlParser.CSP_STARTBY); }
  "endsby"                            { return createToken(CmlParser.CSP_ENDBY); }
  "Wait"                              { return createToken(CmlParser.CSPWAIT); }
  {mk_name}                           { return createToken(CmlParser.MKUNDERNAME); }
}

<CHANNELS,CHANSETS,ACTIONS> {
  ","				      { return createToken(CmlParser.COMMA); }
  ":"				      { return createToken(CmlParser.COLON); } //TODO: CHANGE this into something else
  ";"				      { return createToken(CmlParser.SEMI); }
}

<VDM_CASES> {
  ":"                            { yybegin(stateStack.pop()); return createToken(CmlParser.COLON); }

 }

<VDM_CASES,CLASS,TYPES,STATE,FUNCTIONS,OPERATIONS, ACTIONS, CHANSETS,PROCESS> 
{
  "->"                                { return createToken(CmlParser.RARROW); } 
  //vdm expressions
  "<=>"				      { return createToken(CmlParser.BIMPLY); }
  //"|->"			      { return createToken(CmlParser.BAR_ARROW); }
  "<-:"				      { return createToken(CmlParser.VDM_MAP_DOMAIN_RESTRICT_BY); }
  ":->"				      { return createToken(CmlParser.RNGSUB /*MAP_RANGE_RESTRICT_BY*/); }
  "cases"                             { stateStack.push(yystate());yybegin(VDM_CASES); return createToken(CmlParser.CASES); }
  "@"                                 { return createToken(CmlParser.AT); }
  "..."                               { return createToken(CmlParser.ELLIPSIS); }
  "forall"                            { return createToken(CmlParser.FORALL); }
  "exists"                            { return createToken(CmlParser.EXISTS); }
  "exists1"                           { return createToken(CmlParser.EXISTS1); }
  "div"                               { return createToken(CmlParser.DIVIDE); }
  "mod"                               { return createToken(CmlParser.MOD); }
  "others"                            { return createToken(CmlParser.OTHERS); }
  "abs"                               { return createToken(CmlParser.ABS); } 
  "floor"                             { return createToken(CmlParser.FLOOR); } 
  "not"                               { return createToken(CmlParser.NOT); }
  "card"                              { return createToken(CmlParser.CARD); }  
  "power"                             { return createToken(CmlParser.POWER); } 
  "dunion"                            { return createToken(CmlParser.DUNION); } 
  "dinter"                            { return createToken(CmlParser.DINTER); } 
  "hd"                                { return createToken(CmlParser.HD); } 
  "tl"                                { return createToken(CmlParser.TL); } 
  "len"                               { return createToken(CmlParser.LEN); } 
  "elems"                             { return createToken(CmlParser.ELEMS); } 
  "inds"                              { return createToken(CmlParser.INDS); } 
  "reverse"                           { return createToken(CmlParser.REVERSE); } 
  "conc"                              { return createToken(CmlParser.DCONC); } /*should this be dconc*/
  "dom"                               { return createToken(CmlParser.DOM); } 
  "rng"                               { return createToken(CmlParser.RNG); } 
  "merge"                             { return createToken(CmlParser.MERGE); } 
  "inverse"                           { return createToken(CmlParser.INVERSE); } 
  ":-"				      { return createToken(CmlParser.VDMDONTCARE /*DONTCARE*/); }
  ".#"				      { return createToken(CmlParser.DOTHASH); }
  "<="				      { return createToken(CmlParser.LTE); }
  ">="				      { return createToken(CmlParser.GTE); }
  "<>"				      { return createToken(CmlParser.NEQ); }
  "=>"				      { return createToken(CmlParser.IMPLY); }
  "=="				      { return createToken(CmlParser.DEQUALS /*IS_DEFINED_AS*/); }
  //"||"			      { return createToken(CmlParser.NONDET); }
  ":="				      { return createToken(CmlParser.ASSIGN); }
  "::"				      { return createToken(CmlParser.VDMRECORDDEF); }
  //"**"			      { return createToken(CmlParser.EXP_OR_ITERATE); }
  "++"				      { return createToken(CmlParser.OVERWRITE /*MODIFY_BY*/); }
  "<:"				      { return createToken(CmlParser.DOMRES/*MAP_DOMAIN_RESTRICT_TO*/); }
  ":>"				      { return createToken(CmlParser.RNGRES/*MAP_RANGE_RESTRICT_TO*/); }
  {hook}			      { return createToken(CmlParser.TILDE /*HOOK*/); }
  {prime}			      { return createToken(CmlParser.BACKTICK); }
  ","				      { return createToken(CmlParser.COMMA); }
  "!"				      {  }
  ":"				      { return createToken(CmlParser.COLON); }
  ";"				      { return createToken(CmlParser.SEMI); }
  "="				      { return createToken(CmlParser.EQUALS); }
  ")"				      { return createToken(CmlParser.RPAREN); }
  "("				      { return createToken(CmlParser.LPAREN); }
  "|"				      { return createToken(CmlParser.BAR); }
  "-"				      { return createToken(CmlParser.MINUS); }
  "["				      { return createToken(CmlParser.LSQUARE); }
  "]"				      { return createToken(CmlParser.RSQUARE); }
  "{"				      { return createToken(CmlParser.LCURLY); }
  "}"				      { return createToken(CmlParser.RCURLY); }
  "+"				      { return createToken(CmlParser.PLUS); }
  "/"				      { return createToken(CmlParser.DIV);  }
  "<"				      { return createToken(CmlParser.LT); }
  ">"				      { return createToken(CmlParser.GT); }
  "."				      { return createToken(CmlParser.DOT); }
  "&"				      { return createToken(CmlParser.AMP); }
  "*"				      { return createToken(CmlParser.STAR); }
  "^"				      { return createToken(CmlParser.CONC); }
  //logical operators
  "and"				      { return createToken(CmlParser.AND); }
  "or"				      { return createToken(CmlParser.OR); }
  "not"				      { return createToken(CmlParser.NOT); }
  //set operators
  "\\"				      { return createToken(CmlParser.SETDIFF); }
  "in set"			      { return createToken(CmlParser.INSET); }
  "union"			      { return createToken(CmlParser.UNION); }
  "inter"			      { return createToken(CmlParser.INTER); }
  "not in set"                        { return createToken(CmlParser.NOTINSET); }

  //patterns
  "-"                                 { return createToken(CmlParser.VDMDONTCARE); }
  "mk_"                               { return createToken(CmlParser.MKUNDER); }
    
  //basic types
  "bool"                              { return createToken(CmlParser.TBOOL); }
  "nat"                               { return createToken(CmlParser.TNAT); }
  "nat1"                              { return createToken(CmlParser.TNAT1); }
  "int"                               { return createToken(CmlParser.TINT); }
  "rat"                               { return createToken(CmlParser.TRAT); }
  "real"                              { return createToken(CmlParser.TREAL); }
  "char"                              { return createToken(CmlParser.TCHAR); }
  "token"                             { return createToken(CmlParser.TTOKEN); }
  "lambda"                            { return createToken(CmlParser.LAMBDA); }

  //set type
  "set of"                            { return createToken(CmlParser.VDMSETOF); }
  "subset"                            { return createToken(CmlParser.SUBSET); }


  // seq type
  "seq of"                            { return createToken(CmlParser.VDMSEQOF); }

  // map type
  "map of"                            { return createToken(CmlParser.VDMMAPOF); }
  "inmap of"                             { return createToken(CmlParser.VDMINMAPOF); }

  "to"                                { return createToken(CmlParser.TO); }
  "|->"                               { return createToken(CmlParser.MAPLETARROW); }
   "inv"                               { return createToken(CmlParser.VDMINV); }
  "private"                           { return createToken(CmlParser.PRIVATE); }
  "protected"                         { return createToken(CmlParser.PROTECTED); }
  "public"                            { return createToken(CmlParser.PUBLIC); }
  "logical"                           { return createToken(CmlParser.LOGICAL); }

}

"\""                                  { stateStack.push(yystate());yybegin(STRING); S.b = new StringBuilder(); }
<STRING>"\""                          { yybegin( stateStack.pop() ); return createToken(CmlParser.STRING, S.b.toString()); }
<STRING>[^\"]                         { S.b.append(yytext()); }

//[:whitespace:]                        { /* match whitespace; do nothing */ }
{WhiteSpace}                            { /* match whitespace; do nothing */ }
{identifier}                          {  return checkIdentifier(""); /* return createToken(CmlParser.IDENTIFIER); */}
{numeral}                             { return createToken(CmlParser.NUMERAL); }
{hexliteral}                          { return createToken(CmlParser.HEX_LITERAL); }
// default catch-all production rule is to return the current character
/* .								{ return defaultToken(); } */
// production rule to handle end-of-file
<<EOF>>		  		      { stateStack.clear(); return 0; } 
.                                     { throw new IllegalArgumentException("Syntax at line "+yyline+" position "+yycolumn+" \"" + yytext() + "\" was unexpected at this time."); }
