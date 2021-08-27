grammar FormulaRubrica;

parse
 : expr
 ;

expr
 : MINUS expr                            #unaryMinusExpr
 | expr op=(MULT | DIV) expr             #multiplicationExpr
 | expr op=(PLUS | MINUS) expr           #additiveExpr
 | expr op=(LTEQ | GTEQ | LT | GT) expr  #relationalExpr
 | expr op=(EQ | NEQ) expr               #equalityExpr
 | expr AND expr                         #andExpr
 | expr OR expr                          #orExpr
 | <assoc=right>expr THEN expr ELSE expr #ternary
 | atom                                  #atomExpr
 ;

atom
 : OPAR expr CPAR #parExpr
 | (INT | FLOAT)  #numberAtom
 | ID             #idAtom
 ;

OR : '||';
AND : '&&';
EQ : '==';
NEQ : '!=';
GT : '>';
LT : '<';
GTEQ : '>=';
LTEQ : '<=';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';

OPAR : '(';
CPAR : ')';
THEN: '?';
ELSE: ':';

VARIAVELD: 'VARD';
VARIAVEL: 'VAR';
PARAMETRO: 'PAR';
BASECALCULO: 'BAS';

PREFIXO: (VARIAVELD | VARIAVEL | PARAMETRO | BASECALCULO);
SUFIXO: [a-zA-Z_]+ [a-zA-Z_0-9]* ;

ID
 : PREFIXO '@' SUFIXO
 ;

INT
 : [0-9]+
 ;

FLOAT
 : [0-9]+ '.' [0-9]* 
 | '.' [0-9]+
 ;

SPACE
 : [ \t\r\n] -> skip
 ;

