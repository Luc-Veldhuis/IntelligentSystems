import subprocess, glob, os.path
player1 = 0
player2 = 0
draws = 0
results = {'draw':0, 'player1':0, 'player2':0}
map_results={}
bot_player_1 = 0
bot_player_2 = 0

while bot_player_1 == 0 or not os.path.isfile(str(bot_player_1)+'.class'):
	bot_player_1 = input("Enter the name of the bot for player 1: ")

while bot_player_1 == 0 or not os.path.isfile(str(bot_player_2)+'.class'):
	bot_player_2 = input("Enter the name of the bot for player 2: ")


path = glob.glob('C:/Program Files/Java/jdk*/bin/javac.exe')[0]
subprocess.Popen([path,bot_player_1+'.java'])
subprocess.Popen([path,bot_player_2+'.java'])

list_of_range = list(range(3,9))
list_of_range.append('larger')
for number_of_planets in list_of_range:
	print(str(number_of_planets)+" planets")

	#not so nice solution on finding all maps in the folders
	range_of_maps = list(range(1,4))
	if number_of_planets == "larger":
		range_of_maps.extend(list(range(4,13)))
	for map_number in range_of_maps:
		print("Map "+str(map_number))
		game = subprocess.Popen(['java','-jar','tools/PlayGame.jar','tools/maps/'+str(number_of_planets)+'planets/map'+str(map_number)+'.txt','java '+str(bot_player_1),'java '+str(bot_player_2)],stdout=subprocess.PIPE, stderr=subprocess.PIPE)
		lines = game.communicate();
		output = lines[len(lines)-1].decode("utf-8")
		all_output = output.split('\n')
		result = all_output[len(all_output)-2]
		if(result.find("Draw") != -1):
			results['draw'] += 1
			map_results[str(number_of_planets)+ " "+str(map_number)] = "draw"
		elif(result.find("1") != -1):
			results['player1'] += 1
			map_results[str(number_of_planets)+ " "+str(map_number)] = "player1"
		elif(result.find("2") != -1):
			results['player2'] += 1
			map_results[str(number_of_planets)+ " "+str(map_number)] = "player2"


print(results)
print(map_results)
