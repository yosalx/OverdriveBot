package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private int maxSpeed;
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command D0_NOTHING = new DoNothingCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private Command TWEET(int lane, int block){
        return new TweetCommand(lane, block);
    }


    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        if (myCar.damage == 0){
            this.maxSpeed = 15;
        }
        else if (myCar.damage == 1){
            this.maxSpeed = 9;
        }
        else if (myCar.damage == 2){
            this.maxSpeed = 8;
        }
        else if (myCar.damage == 3){
            this.maxSpeed = 6;
        }
        else if (myCar.damage == 4){
            this.maxSpeed = 3;
        }
        else if (myCar.damage == 5){
            this.maxSpeed = 0;
        }
        directionList.add(-1);
        directionList.add(1);
    }

    public Command run() {
        int LaneNow = myCar.position.lane;
        int BlockNow = myCar.position.block;
        PowerUps[] ready2 = myCar.powerups;
        if (myCar.damage >= 4) {
            return FIX;
        }
        if (myCar.speed == this.maxSpeed){
            if (isLaneSafe(LaneNow, BlockNow)){
                usePowerUps();
            }
            else if (isTurnValid(-1, LaneNow) && isLaneSafe(LaneNow-1, BlockNow-1)){
                return TURN_LEFT;
            }
            else if (isTurnValid(1, LaneNow) && isLaneSafe(LaneNow+1, BlockNow-1)){
                return TURN_RIGHT;
            }
            else{
                if (isPowerUpAvailable(PowerUps.LIZARD)){
                    return LIZARD;
                }
                else{
                    if (!isTurnValid(-1, LaneNow) || !isTurnValid(1, LaneNow)){
                        if (ForwardDamageComparison(LaneNow, BlockNow, !isTurnValid(-1, LaneNow), !isTurnValid(1, LaneNow), false)){
                            usePowerUps();
                        }
                        else{
                            if (!isTurnValid(-1, LaneNow)){
                                return TURN_RIGHT;
                            }
                            else{
                                return TURN_LEFT;
                            }
                        }
                    }
                    else{
                        if (ForwardDamageComparison(LaneNow, BlockNow, true, true, false)){
                            usePowerUps();
                        }
                        else if (ForwardDamageComparison(LaneNow, BlockNow, true, true, true)){
                            return TURN_LEFT;
                        }
                        else{
                            return TURN_RIGHT;
                        }
                    }
                }
            }
        }
        else{
            if (isPowerUpAvailable(PowerUps.BOOST) && isLaneSafeBoosted(LaneNow, BlockNow)){
                return BOOST;
            }
            else{
                if (isLaneSafe(LaneNow, BlockNow)){
                    usePowerUps();
                }
                else if (isTurnValid(-1, LaneNow) && isLaneSafe(LaneNow-1, BlockNow-1)){
                    return TURN_LEFT;
                }
                else if (isTurnValid(1, LaneNow) && isLaneSafe(LaneNow+1, BlockNow-1)){
                    return TURN_RIGHT;
                }
                else{
                    if (isPowerUpAvailable(PowerUps.LIZARD)){
                        return LIZARD;
                    }
                    else{
                        if (!isTurnValid(-1, LaneNow) || !isTurnValid(1, LaneNow)){
                            if (ForwardDamageComparison(LaneNow, BlockNow, !isTurnValid(-1, LaneNow), !isTurnValid(1, LaneNow), false)){
                                usePowerUps();
                            }
                            else{
                                if (!isTurnValid(-1, LaneNow)){
                                    return TURN_RIGHT;
                                }
                                else{
                                    return TURN_LEFT;
                                }
                            }
                        }
                        else{
                            if (ForwardDamageComparison(LaneNow, BlockNow, true, true, false)){
                                usePowerUps();
                            }
                            else if (ForwardDamageComparison(LaneNow, BlockNow, true, true, true)){
                                return TURN_LEFT;
                            }
                            else{
                                return TURN_RIGHT;
                            }
                        }
                    }
                }
            }
        }
        return D0_NOTHING;
    }

    /**
     * Return the damage of each lane based on the terrain
     **/
    private int getLaneDamage(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        int damage = 0;
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + myCar.speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            else if (laneList[i].terrain == Terrain.OIL_SPILL){
                damage += 2;
            }
            else if (laneList[i].terrain == Terrain.MUD){
                damage += 2;
            }
            else if (laneList[i].terrain == Terrain.WALL){
                damage += 4;
            }
        }
        return damage;
    }

    private boolean ForwardDamageComparison(int lane, int block, boolean left, boolean right, boolean leftPriority){
        //idk what this is, tapi aku taruh di function biar lebih gampang nge read nya
        //lane dan block adalah posisinya
        //boolean left/right adalah mau membandingkan damage bagian mana terhadap forward
        //leftPriority artinya left damage yg dibandingkan thd forward nya, bukan sebaliknya
        int forwardDamage = getLaneDamage(lane, block);
        int rightOrLeftDamage = left? getLaneDamage(lane+1, block-1) : getLaneDamage(lane-1, block-1);
        if (left && right){
            int rightDamage = getLaneDamage(lane+1, block-1);
            int leftDamage = getLaneDamage(lane-1, block-1);
            if (leftPriority){
                return leftDamage >= forwardDamage && leftDamage >= rightDamage;
            }
            else{
                return forwardDamage >= leftDamage && forwardDamage >= rightDamage;
            }
        }
        return forwardDamage >= rightOrLeftDamage;

    }

    private int getLaneDamageBoosted(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        int damage = 0;
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + this.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            else if (laneList[i].terrain == Terrain.OIL_SPILL){
                damage += 2;
            }
            else if (laneList[i].terrain == Terrain.MUD){
                damage += 2;
            }
            else if (laneList[i].terrain == Terrain.WALL){
                damage += 4;
            }
        }
        return damage;
    }

    private boolean isLaneSafe(int lane, int block){
        return(getLaneDamage(lane, block) == 0);
    }

    private boolean isLaneSafeBoosted(int lane, int block){
        return(getLaneDamageBoosted(lane, block) == 0);
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
        if(currLane == 1 && direction == -1){
            return false;
        }
        else if(currLane == 4 && direction == 1){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean isPowerUpAvailable(PowerUps tocheck){
        for (PowerUps powerUp: myCar.powerups) {
            if (powerUp.equals(tocheck)) {
                return true;
            }
        }
        return false;
    }

    private Command usePowerUps(){
        if (isPowerUpAvailable(PowerUps.EMP) && (getMyLane() == opponentBlockPosition())){
            return EMP;
        }
        if (isPowerUpAvailable(PowerUps.TWEET)){
            return TWEET(opponentLanePosition(), opponentBlockPosition());
        }
        if (isPowerUpAvailable(PowerUps.OIL) && isOpponentBehind()){
            return OIL;
        }
        else{
            return ACCELERATE;
        }
    }


}
