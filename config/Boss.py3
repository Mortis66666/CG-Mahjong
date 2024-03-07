from operator import add
from random import choice, seed
import sys

seed(2311604759057582817)

wind_seat = int(input())
tileString = input()

tiles = [*map(lambda a: add(*a), zip([*tileString[::2]], [*tileString[1::2]]))]

filter(lambda tile: tile[0] not in "fs", tiles)

while True:
	round_type = input()

	actions = []
	for i in range(int(input())):
		inputs = input().split()
		committer = int(inputs[0])
		action = inputs[1]
		target_tiles = inputs[2]

		if committer == wind_seat and action == "DRAW":
			tiles.append(target_tiles)

		actions.append(inputs)

	for tile in tiles.copy():
		if tile[0] in "fs":
			tiles.remove(tile)

	print(tiles, file=sys.stderr, flush=True)

	discard = choice(tiles)

	if round_type == "normal":
		tiles.remove(discard)

		print(f"DISCARD {discard}")
	else:
		last_tile = actions[-1][-1]

		count = tiles.count(last_tile)

		if count == 3:
			for i in range(3):
				tiles.remove(last_tile)

			print("GONG")
		elif count == 2:
			for i in range(2):
				tiles.remove(last_tile)

			discard = choice(tiles)
			tiles.remove(discard)

			print(f"PONG {discard}")

		else:
			print("PASS")
