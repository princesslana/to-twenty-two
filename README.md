# to-twenty-two

A counting game for Discord.

Each round players count until they reach 22.

For each round a player may win, lose, or push.

* If a player wins, they gain points equal to the sum of all numbers they counted.
* If a player loses, they lose points equal to the sum of all numbers they counted.
* If a player pushes, they score zero.

If a player counts 22, they win.
The player who counted 21 pushes (unless there were only two players).
All others lose.

A player can not count 22 unless they have counted previously (an attempt to do so is ignored).

If a player makes an error in the count (wrong number, playing twice in a row) they lose, and all others push.

