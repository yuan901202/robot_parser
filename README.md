# robot_parser
A simple program that can be used to control simple robots by loading parser files

## Introduction
A variety of applications allow the user to "script" the application, or otherwise specify domain-specific programs to control, modify, or extend the application. Many advanced computer games have this facility, as do sophisticated editors of many kinds. All these applications will provide some kind of domain-specific language for specifying the scripts/programs, and must therefore also have a parser and interpreter to parse and execute the scripts.
In this assignment, your task will be to design and implement a language interpreter for a simple programming language that can be used to control robots for a simple robot game. The RoboGame program is written already; your task is to add the parser and interpreter.
We will provide a set of programs for testing each stage of your language interpreter.
Although it is not part of the assignment, you may wish to publish any robot programs you write on the forum so that other students can try running their robot programs against yours.

### Grammer for this program
### Stage 0
PROG  ::= STMT+

STMT  ::= ACT ; | LOOP 

ACT   ::= move | turnL | turnR | takeFuel | wait

LOOP  ::= loop BLOCK

BLOCK ::= { STMT+ }


### Stage 1
PROG  ::= STMT+

STMT  ::= ACT; | LOOP |IF | WHILE

ACT   ::= move | turnL | turnR | turnAround | shieldOn | shieldOff | takeFuel | wait

LOOP  ::= loop BLOCK

IF    ::= if ( COND ) BLOCK

WHILE ::= while ( COND ) BLOCK

BLOCK ::= { STMT+ }

COND  ::= lt ( SEN, NUM )  | gt ( SEN, NUM )  | eq ( SEN, NUM ) 

SEN   ::= fuelLeft | oppLR | oppFB | numBarrels | barrelLR | barrelFB | wallDist

NUM   ::= "-?[0-9]+"

### Stage 2
PROG  ::= STMT+

STMT  ::= ACT; | LOOP | IF | WHILE

ACT   ::= move [ ( EXP ) ] | turnL | turnR | turnAround | shieldOn | shieldOff | takeFuel | wait [ ( EXP ) ]

LOOP  ::= loop BLOCK

IF    ::= if ( COND ) BLOCK [ else BLOCK ]

WHILE ::= while ( COND ) BLOCK

BLOCK ::= { STMT+ }

EXP   ::= NUM | SEN | OP ( EXP, EXP )  

SEN   ::= fuelLeft | oppLR | oppFB | numBarrels | barrelLR | barrelFB | wallDist

OP    ::= add | sub | mul | div

COND  ::= and ( COND, COND ) | or ( COND, COND ) | not ( COND )  | lt ( EXP, EXP )  | gt ( EXP, EXP )  | eq ( EXP, EXP ) 

NUM   ::= "-?[1-9][0-9]*|0"

### Stage 3
PROG  ::= STMT+

STMT  ::= ACT ; | LOOP | IF | WHILE | ASSGN ; 

LOOP  ::= loop BLOCK

IF    ::= if ( COND ) BLOCK [elif ( COND ) BLOCK]*[else BLOCK]

WHILE ::= while ( COND ) BLOCK

ASSGN ::= VAR = EXP

BLOCK ::= { STMT+ }

ACT   ::= move [( EXP )] | turnL | turnR | turnAround | shieldOn | shieldOff | takeFuel | wait [( EXP )]

EXP   ::= NUM | SEN | VAR | OP ( EXP, EXP )  

SEN   ::= fuelLeft | oppLR | oppFB | numBarrels | barrelLR [( EXP )] | barrelFB [ ( EXP ) ] | wallDist

OP    ::= add | sub | mul | div

COND  ::= lt ( EXP, EXP )  | gt ( EXP, EXP )  | eq ( EXP, EXP ) | and ( COND, COND ) | or ( COND, COND ) | not ( COND )  

NUM   ::= "-?[1-9][0-9]*|0"

VAR   ::= "\\$[A-Za-z][A-Za-z0-9]*"  

### Stage 4
PROG  ::= STMT*

STMT  ::= ACT; | ASSGN; | LOOP | IF | WHILE | DO; | { STMT* }

ASSGN ::= VAR = EXP

VAR   ::= "\\$[A-Za-z][A-Za-z0-9]*"

LOOP  ::= loop STMT

IF    ::= if ( COND ) STMT [elif ( COND ) STMT ]* [else STMT]

WHILE ::= while ( COND ) STMT

DO    ::= do STMT while ( COND )

ACT   ::= move [( EXP )] | turnL | turnR | turnAround | shieldOn ( COND )  | takeFuel | wait [( EXP )]

EXP   ::= NUM | EXP OP EXP |  SEN | VAR | ( EXP )

SEN   ::= fuelLeft | oppLR | oppFB | numBarrels | barrelLR [( EXP )] | barrelFB [( EXP )] | wallDist

OP    ::= + | - | * | /

COND  ::= BOOL | COND LOGIC COND | ! COND | EXP COMP EXP | ( COND )

LOGIC ::= && | || | ^

COMP  ::= < | <= | > | >= | == | !=

NUM   ::= "-?[1-9][0-9]*|0"

BOOL  ::= true | false

