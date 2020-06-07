package GUI;

import Structure.GameState;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import java.util.concurrent.ArrayBlockingQueue;

import static GUI.AdditionsToSecondScene.statusinfo;

public class CheckState extends Service<GameState> {

    private final ArrayBlockingQueue<GameState> gameState;

    private void reuse(){
        reset();
        start();
    }

    //Serwis do sprawdzania stanu gry oraz obsluzenie zmiany jej stanu
    public CheckState(ArrayBlockingQueue<GameState> gameState) {
        this.gameState = gameState;
/*
        setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {

                    GameState check = getValue();


                    if(check==GameState.active) {
                        statusinfo.setText("W trakcie");
                        reuse();
                    }

                    else if(check==GameState.disconnected) statusinfo.setText("Utracono połączenie");

                    else if(check==GameState.connected) {
                        statusinfo.setText("Połączono");
                        reuse();
                    }

                    else if(check==GameState.hostSetupFail) statusinfo.setText("Nieudana próba ustawienia serwera");

                    else if(check==GameState.tryConnectToHost) {
                        statusinfo.setText("Próba nawiązania połączenia");
                        reuse();
                    }

                    else if(check==GameState.waitingForClient) {
                        statusinfo.setText("Oczekiwanie na klienta");
                        reuse();
                    }

                    else if (check==GameState.whiteWon) {
                        statusinfo.setText("Wygrana białych");
                        AlertBox.display("Koniec gry", "Białe wygrały!");
                        MainStage.endGame();
                    }

                    else if (check==GameState.blackWon){
                        statusinfo.setText("Wygrana czarnych");
                        AlertBox.display("Koniec gry", "Czarne wygraly!");
                        MainStage.endGame();
                    }

                    else if (check==GameState.draw){
                        statusinfo.setText("Remis");
                        AlertBox.display("Koniec gry", "Remis!");
                        MainStage.endGame();
                    }
            }

        });*/
    }

    protected Task<GameState> createTask() {
        return new Task<>() {
            public GameState call() {
                GameState state;

                do {
                    try {
                        state = gameState.take();

                    } catch (InterruptedException e) {
                        return null;
                    }
                } while (state == GameState.active);

                return state;
            }
        };
    }

}
