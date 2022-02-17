package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command D0_NOTHING = new DoNothingCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        directionList.add(-1);
        directionList.add(1);
    }

    public Command run() {
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
        if (myCar.damage >= 5) {
            return new FixCommand();
        }
        if (blocks.contains(Terrain.MUD)) {
            int i = random.nextInt(directionList.size());
            return new ChangeLaneCommand(directionList.get(i));
        }
        return new AccelerateCommand();
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    private boolean isOpponentBehind(){
        //Check whether opponent is behind myCar or not
        if(opponent.position.block < myCar.position.block){
            return true;
        }
        return false;
    }

    private int opponentLanePosition(){
        //Check opponent car's lane
        return(opponent.position.lane);
    }

    private int opponentBlockPosition(){
        //Check opponent car's block
        return(opponent.position.block);
    }

    private int getMyLane(){
        return(myCar.position.lane);
    }

    private boolean isTurnValid(int direction, int currLane){
        if(currLane == 1 && direction == 0){
            return true;
        }
        else if(currLane == 4 && direction == 1){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isPowerUpAvailable(PowerUps tocheck, PowerUps[] available){
        for (PowerUps powerUp: available) {
            if (powerUp.equals(tocheck)) {
                return true;
            }
        }
        return false;
    }
}
