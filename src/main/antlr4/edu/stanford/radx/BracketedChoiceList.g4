grammar BracketedChoiceList;

choices : '[' choice (COMMA choice)* ']' ;

choice : DOUBLE_DOUBLE_QUOTED_STRING | DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING | ID | 'nan' ;

ID: ([a-zA-Z0-9_])+ ;

DOUBLE_DOUBLE_QUOTED_STRING : '"' '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"' '"';
DOUBLE_QUOTED_STRING : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"';
SINGLE_QUOTED_STRING : '\'' (~('\'' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '\'';
COMMA : ',' ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines