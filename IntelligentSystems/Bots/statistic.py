import subprocess, glob, os.path
player1 = 0
player2 = 0
draws = 0
number_of_planets = 0
map_number = 0
bot_player_1 = 0
bot_player_2 = 0

while number_of_planets == 0 or not os.path.isdir('tools/maps/'+number_of_planets+'planets'):
	number_of_planets = input("Number of planets: ")

while map_number == 0 or not os.path.isfile('tools/maps/'+number_of_planets+'planets/map'+map_number+'.txt'):
	map_number = input("Enter the map number: ")

while bot_player_1 == 0 or not os.path.isfile(str(bot_player_1)+'.class'):
	bot_player_1 = input("Enter the name of the bot for player 1: ")

while bot_player_1 == 0 or not os.path.isfile(str(bot_player_2)+'.class'):
	bot_player_2 = input("Enter the name of the bot for player 2: ")


path = glob.glob('C:/Program Files/Java/jdk*/bin/javac.exe')[0]
subprocess.Popen([path,bot_player_1+'.java'])
subprocess.Popen([path,bot_player_2+'.java'])


for i in range(0,10):
	print("turn "+str(i))
	game = subprocess.Popen(['java','-jar','tools/PlayGame.jar','tools/maps/'+str(number_of_planets)+'planets/map'+str(map_number)+'.txt','java '+str(bot_player_1),'java '+str(bot_player_2)],stdout=subprocess.PIPE, stderr=subprocess.PIPE)
	lines = game.communicate();
	output = lines[len(lines)-1].decode("utf-8")
	all_output = output.split('\n')
	result = all_output[len(all_output)-2]
	if(result.find("draw") != -1):
		draws += 1
	elif(result.find("1") != -1):
		player1 += 1
	elif(result.find("2") != -1):
		player2 += 1


print("Draws: "+ str(draws))
print(bot_player_1+" has won: "+ str(player1) + " times")
print(bot_player_2+" has won: "+ str(player2) + " times")
