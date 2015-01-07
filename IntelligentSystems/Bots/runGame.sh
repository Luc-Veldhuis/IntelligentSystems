#!/bin/bash

#BASH SCRIPT FOR RUNNING GAME WITH 
#Argument total=4 [1,2,3 and 4], all are optional
#Default is 8planets, map1, MyBot and MyBot. 
#If want to change e.g. 4, you have to give 1,2 and 3 and to change 2 you have to five 1, etc..

#1 number of planets [1-8] e.g. 2 or 4
NUM=""

if [ -z "$1" ]; 
 then
  NUM="8planets" 
 else
  NUM="$1planets";
fi

#2 map e.g. map1 || map2
MAP=""

if [ -z "$2" ];
 then
  MAP="map1"
 else
  MAP="map$2";
fi

#3 player 1 e.g. MyBot
PLAYER1=""

if [ -z "$3" ];
 then
  PLAYER1="java MyBot"
 else
  PLAYER1="java $3";
fi
#4 player 2 e.g. MyBot
PLAYER2=""

if [ -z "$4" ];
 then
  PLAYER2="java MyBot"
 else
  PLAYER2="java $4";
fi

command="java -jar tools/PlayGame.jar tools/maps/$NUM/$MAP.txt \"$PLAYER1\" \"$PLAYER2\" | python tools/visualizer/visualize_locally.py"

eval $command

#java -jar tools/PlayGame.jar tools/maps/8planets/map1.txt "java RandomBot" "java RandomBot" | python tools/visualizer/visualize_locally.py
