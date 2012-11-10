package eu.compassresearch.core.parser;

public enum ParserErrorMessage
{
//  COULD_NOT_DETERMINE_TYPE("Could not determine type for %s."),
//  
//  INCOMPATIBLE_TYPE("Expected an entity of type %s but %s was found."),
//  
//  EXPECTED_SUBTYPE_RELATION(
//      "This code assumes that %s is a sub type of %s, which is not true."),
//  
//  NAMED_TYPE_UNDEFINED("The type %s is undefined.")
	
	OPERATION_NAMES_ARE_NOT_CONSISTENT("The operation definition name %s does not match the body name %s"),
	FUNCTION_NAMES_ARE_NOT_CONSISTENT("The function definition name %s does not match the body name %s"),
	MALFORMED_CHANNEL_EXPRESSION("The channel expresison %s is malformed"),
	MALFORMED_CHANNEL_SET_EXPRESSION("A channel set expresison can only contain identifiers, you gave %s"),
	ACTIONS_INSIDE_CLASS_OPERATION("You cannot have actions inside class operations only statements"),
	SEMI_BETWEEN_CLASS_OPERATIONS("You must not separate class operations with ';'"),
	VARINFORMATION_MULTIPLETYPES_DEFINED("Only one type is allowed")
  ;
  
  private String template;
  
  private ParserErrorMessage(String templateString)
    {
      this.template = templateString;
    }
  
  public String customizeMessage(String... strs)
    {
      // Check Arity
      int arity = 0;
      for (Character c : template.toCharArray())
        if ('%' == c)
          arity++;
      if (arity != strs.length)
        throw new RuntimeException(
            "Error in the error-message. Template requires exactly " + arity
                + " arguments but " + strs.length + " were given.");
      
      // Format String
      return String.format(template, (Object[]) strs);
    }
}