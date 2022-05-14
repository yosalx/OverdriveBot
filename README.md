#  Game "Overdrive" Bot implemented using Greedy Algorithm

##### Safiq Faray / 13519145
##### Kevin Roni / 13520114
##### Yoseph A.S. / 13520141

## Deskripsi Program
This bot program is implemented using Greedy by Speed Algorithm. Greedy by speed works by ranking the possible speed of each command for the next round. This method will analyze the player current situation and also analyze command(s) that could give the player the maximum speed possible for the next round.

## Requirements

#### Java (minimum : Java 8)
#### Apache Maven / IntelIiJ IDEA

## Directory

```sh
├── src                     # Program Source Code
├── bin                     # Executable
├── doc                     # Report
```

## How to Run
#### 1. Download the starter-pack : https://github.com/EntelectChallenge/2020-Overdrive/releases/tag/2020.3.4
#### 2. Fork / Download this repo and replace the java folder in the starter-bots with the src folder from this repo
#### 3. Build starter-bot with maven dan replace file.jar with dependencies at target folder that created by the build process with .jar file located at the bin folder of this repo
#### 4. Change the bot.json configuration to "botFileName": Hop On Valorant.jar",

#### Change the game-runner-config.json at the starter-pack folder to
  "player-a": "./starter-bots/java",
  "player-b": "./reference-bot/java",
#### 5. Run the run.bat file
