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
                return new ChangeLaneCommand(-1);
            }
            else if (isTurnValid(1, LaneNow) && isLaneSafe(LaneNow+1, BlockNow-1)){
                return new ChangeLaneCommand(1);
            }
            else{
                if (isPowerUpAvailable(PowerUps.LIZARD, ready2)){
                    return LIZARD;
                }
                else{
                    if (!isTurnValid(-1, LaneNow)){
                        int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                        int rightDamage = getLaneDamage(LaneNow+1, BlockNow-1);
                        if (forwardDamage >= rightDamage){
                            usePowerUps();                            
                        }
                        else{
                            return new ChangeLaneCommand(1);
                        }
                    }
                    else if (!isTurnValid(1, LaneNow)){
                        int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                        int leftDamage = getLaneDamage(LaneNow-1, BlockNow-1);
                        if (forwardDamage >= leftDamage){
                            usePowerUps();                            
                        }
                        else{
                            return new ChangeLaneCommand(-1);
                        }
                    }
                    else{
                        int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                        int leftDamage = getLaneDamage(LaneNow-1, BlockNow-1);
                        int rightDamage = getLaneDamage(LaneNow+1, BlockNow-1);
                        if (forwardDamage >= leftDamage && forwardDamage >= rightDamage){
                            usePowerUps();                            
                        }
                        else if (leftDamage >= forwardDamage && leftDamage >= rightDamage){
                            return new ChangeLaneCommand(-1);
                        }
                        else{
                            return new ChangeLaneCommand(1);
                        }
                    }
                }
            }
        }
        else{
            if (isPowerUpAvailable(PowerUps.BOOST, ready2) && isLaneSafeBoosted(LaneNow, BlockNow)){
                return BOOST;
            }
            else{
                if (isLaneSafe(LaneNow, BlockNow)){
                    usePowerUps();
                }
                else if (isTurnValid(-1, LaneNow) && isLaneSafe(LaneNow-1, BlockNow-1)){
                    return new ChangeLaneCommand(-1);
                }
                else if (isTurnValid(1, LaneNow) && isLaneSafe(LaneNow+1, BlockNow-1)){
                    return new ChangeLaneCommand(1);
                }
                else{
                    if (isPowerUpAvailable(PowerUps.LIZARD, ready2)){
                        return LIZARD;
                    }
                    else{
                        if (!isTurnValid(-1, LaneNow)){
                            int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                            int rightDamage = getLaneDamage(LaneNow+1, BlockNow-1);
                            if (forwardDamage >= rightDamage){
                                usePowerUps();                            
                            }
                            else{
                                return new ChangeLaneCommand(1);
                            }
                        }
                        else if (!isTurnValid(1, LaneNow)){
                            int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                            int leftDamage = getLaneDamage(LaneNow-1, BlockNow-1);
                            if (forwardDamage >= leftDamage){
                                usePowerUps();                            
                            }
                            else{
                                return new ChangeLaneCommand(-1);
                            }
                        }
                        else{
                            int forwardDamage = getLaneDamage(LaneNow, BlockNow);
                            int leftDamage = getLaneDamage(LaneNow-1, BlockNow-1);
                            int rightDamage = getLaneDamage(LaneNow+1, BlockNow-1);
                            if (forwardDamage >= leftDamage && forwardDamage >= rightDamage){
                                usePowerUps();                            
                            }
                            else if (leftDamage >= forwardDamage && leftDamage >= rightDamage){
                                return new ChangeLaneCommand(-1);
                            }
                            else{
                                return new ChangeLaneCommand(1);
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

    private boolean isPowerUpAvailable(PowerUps tocheck, PowerUps[] available){
        for (PowerUps powerUp: available) {
            if (powerUp.equals(tocheck)) {
                return true;
            }
        }
        return false;
    }

    /*
    private Command UseOil(){
        if (isOpponentBehind()){
            return OIL;
        }
        else{
            return D0_NOTHING;
        }
    }

    private Command UseEMP(){
        if (getMyLane() == opponentBlockPosition()){
            return EMP;
        }
        else{
            return D0_NOTHING;
        }
    }

    private Command UseTweet(){
        return new TweetCommand(opponentLanePosition(), opponentBlockPosition());
    }
    */
    private Command usePowerUps(){
        PowerUps[] ready = myCar.powerups;
        if (isPowerUpAvailable(PowerUps.EMP,ready) && (getMyLane() == opponentBlockPosition())){
            return EMP;
        }
        if (isPowerUpAvailable(PowerUps.TWEET, ready)){
            return new TweetCommand(opponentLanePosition(), opponentBlockPosition());
        }
        if (isPowerUpAvailable(PowerUps.OIL, ready) && isOpponentBehind()){
            return OIL;
        }
        else{
            return ACCELERATE;
        }
    }
}
