#!/bin/bash

#BASH SCRIPT FOR RUNNING GAME WITH 
#Argument total=4 [1,2,3 and 4], all are optional (see comments above code for what each one reprisents)
#Default is "java RandomBot","java MyBot" 8(planets), (map)1. 
#If want to change e.g. 4, you have to give 1,2 and 3 and to change 2 you have to five 1, etc..

#ALSO if you wish to only specify oponent that your bot has to face, just type ./runGame.sh "oponent bot"
#where oponent bot is e.g. "java RandomBot"


#1 player 2 e.g. java RandomBot
PLAYER2=""

if [ -z "$1" ];
 then
  PLAYER2="\"java RandomBot\""
 else
  PLAYER2="\"$1\"";
fi

#2 player 2 e.g. MyBot
PLAYER1=""

if [ -z "$2" ];
 then
  PLAYER1="\"java MyBot\""
 else
  PLAYER1="\"$2\"";
fi

#3 number of planets [1-8] e.g. 2 or 4
NUM=""

if [ -z "$3" ]; 
 then
  NUM="8planets" 
 else
  NUM="$3planets";
fi

#4 map e.g. map1 || map2
MAP=""

if [ -z "$4" ];
 then
  MAP="map1"
 else
  MAP="map$4";
fi

command="java -jar tools/PlayGame.jar tools/maps/$NUM/$MAP.txt $PLAYER1 $PLAYER2 | python tools/visualizer/visualize_locally.py"

eval $command

#java -jar tools/PlayGame.jar tools/maps/8planets/map1.txt "java RandomBot" "java RandomBot" | python tools/visualizer/visualize_locally.py
