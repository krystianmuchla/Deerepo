package Structure;

import MutableVariables.MutableBoolean;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class StructureTaskOffline extends Thread{
    private final ArrayBlockingQueue<int[]> clickCommand;
    private final ArrayBlockingQueue<ToDisplay> display;
    private final ArrayBlockingQueue<GameState> gameStates;
    private final Player white;
    private final Player black;
    private final Semaphore clickSemaphore;
    private final MutableBoolean workCommand;
    private Player currentPlayer;
    private int storedX;
    private int storedY;

    public StructureTaskOffline(ArrayBlockingQueue<int[]> clickCommand, ArrayBlockingQueue<ToDisplay> display, ArrayBlockingQueue<GameState> gameStates, Semaphore clickSemaphore, MutableBoolean workCommand){
        this.clickCommand = clickCommand;
        this.display = display;
        this.gameStates = gameStates;
        this.clickSemaphore = clickSemaphore;
        this.workCommand = workCommand;
        white = new Player(true);
        black = new Player(false);
        currentPlayer = white;
        this.setDaemon(true);
    }

    private void sendGUIGameState(){

    }

    private void sendGUIDisplayData(ToDisplay toDisplay){
        try {
            display.put(toDisplay);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //game flow
    public void run(){
        Board.setupBoard();
        while (true) {
            Board.addCurrentBoardState();
            GameState gameState = Board.checkGameState(currentPlayer.getColour());
            if (gameState != GameState.active) {
                try {
                    gameStates.put(gameState);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            Board.display();
            ClickResult clickResult;
            do {
                int[] coordinates = null;
                clickSemaphore.release();
                try {
                    coordinates = clickCommand.take();
                    clickSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clickResult = currentPlayer.performOnClick(Objects.requireNonNull(coordinates)[0], coordinates[1]);
                switch (clickResult) {
                    case nothing -> {
                    }
                    case pick -> {
                        ToDisplay toDisplay = new ToDisplay(TypeOfAction.pick);
                        storedX = currentPlayer.getPickedPiece().getX();
                        storedY = currentPlayer.getPickedPiece().getY();
                        toDisplay.addCoordinates(new int[]{storedX, storedY});
                        sendGUIDisplayData(toDisplay);
                    }
                    case repick -> {
                        ToDisplay toDisplay = new ToDisplay(TypeOfAction.repick);
                        toDisplay.addCoordinates(new int[]{storedX, storedY});
                        toDisplay.addCoordinates(new int[]{currentPlayer.getPickedPiece().getX(), currentPlayer.getPickedPiece().getY()});
                        storedX = currentPlayer.getPickedPiece().getX();
                        storedY = currentPlayer.getPickedPiece().getY();
                        sendGUIDisplayData(toDisplay);
                    }
                    case clear -> {
                        ToDisplay toDisplay = new ToDisplay(TypeOfAction.clear);
                        toDisplay.addCoordinates(new int[]{storedX, storedY});
                        sendGUIDisplayData(toDisplay);
                    }
                    case move -> {
                        ToDisplay toDisplay = new ToDisplay();
                        DataChanges dataChanges = new DataChanges();
                        currentPlayer.makeChanges(coordinates[0], coordinates[1], dataChanges, toDisplay);
                        Board.executeDataChanges(dataChanges);
                        sendGUIDisplayData(toDisplay);
                    }
                }
            } while (clickResult != ClickResult.move);
            if (currentPlayer == white) currentPlayer = black;
            else currentPlayer = white;
        }
        System.out.println("koniec watku struktury");
    }
    
}