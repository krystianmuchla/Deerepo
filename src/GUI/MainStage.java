package GUI;

import Structure.StructureTaskOffline;
import Structure.ToDisplay;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;


public class MainStage extends Application{

    Stage window;
    Scene scene1, scene2;

    public static BorderPane border = new BorderPane(); //Okno z plansza
    public static GridPane gridPane =new GridPane();
    public static GridPane markings = new GridPane();
    public static Label[][] board = new Label[8][8];
    public static BackgroundImage scene1_background = new BackgroundImage(new Image(MainStage.class.getResourceAsStream("img/scene1_background.png")),BackgroundRepeat.REPEAT,
            BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, new BackgroundSize(400, 400, false, false, false, false));

    private final ArrayBlockingQueue<int[]> clickCommand = new ArrayBlockingQueue<>(1); //do synchronizacji z mechanizmem
    private final ArrayBlockingQueue<ToDisplay> display = new ArrayBlockingQueue<>(1);
    private static ToDisplay toDisplay;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage){

        window=primaryStage;
        Label label1 = new Label("Zagrajmy w szachy!");
        window.setTitle("Chess_Project");
        window.getIcons().add(new Image(MainStage.class.getResourceAsStream("img/chess_icon.png")));
        window.setResizable(false);


        //Button 1 Rozpoczynanie gry
        Button button1=new Button("Rozpocznij gre");
        Move move=new Move(clickCommand, display);
        button1.setOnAction(e-> {
            window.setScene(scene2);
            StructureTaskOffline t = new StructureTaskOffline(clickCommand, display);
            move.execute_move(t);
        });
        button1.setStyle("-fx-background-color: white;-fx-border-color: black;");
        // wyrażenie lambda - znacznie skraca kod, dostępne od Java 8


        //Button3 AlertBox test
        Button button3=new Button("Czy można oszukiwać w tej grze?");
        button3.setOnAction(e -> AlertBox.display("Oszukiwanie","Nie można oszukiwać!"));
        button3.setStyle("-fx-background-color: black;-fx-border-color: white;-fx-text-fill:white;");


        //Button4 Zamykanie programu
        Button button4=new Button( "Opuść grę");
        button4.setOnAction(e -> closeProgram());
        button4.setStyle("-fx-background-color: white;-fx-border-color: black;");
        window.setOnCloseRequest(e->
        {
            e.consume(); // sama metoda setOnCloseRequest zamyka program niezależnie jakiej odpowiedzi użytkownik udzieli
            closeProgram(); // uzywajac e.consume() przejmujemy kontrole nad tym co sie stanie np. uzywajac okreslonej metody
        });


        //Layout 1
        VBox layout1 = new VBox(20); // układa obiekty "w kolumnie"
        layout1.getChildren().addAll(label1, button1, button3,button4);
        layout1.setAlignment(Pos.CENTER);
        layout1.setBackground(new Background(scene1_background));
        scene1 = new Scene(layout1, 400,400);



        //Button2 Przycisk powrotu do glownego menu
        Button button2=new Button("Menu");
        button2.setStyle("-fx-background-color: black;-fx-border-color: white;-fx-text-fill:white;");
        button2.setOnAction(e -> window.setScene(scene1));

        //Button5 Przycisk do restartu szachownicy
        Button button5=new Button("Restart");
        button5.setStyle("-fx-background-color: white;-fx-border-color: black;");
        button5.setOnAction(e->
        {
            ChessBoard.BlankSpace(8);
            ChessBoard.InitChessBoard();
            ChessBoardMarkings.Add_Fields_Markings();
            window.setScene(scene2);
        });



        //Layout 2
         // dzieli scenę na funkcjonalne części
        HBox layout2=new HBox(20);
        layout2.getChildren().addAll(button2,button5);
        layout2.setSpacing(0);
        border.setTop(layout2);


        //Plansza
        ChessBoard.BlankSpace(8);
        ChessBoard.InitChessBoard();
        ChessBoardMarkings.Add_Fields_Markings();
        markings.add(gridPane,1,1,8,8);
        border.setCenter(markings);
        BorderPane.setMargin(markings,new Insets(20,25,20,25));

        //Move.execute_move(); //Wykonywanie ruchu
        //Move.execute_move_mouse(); //Wykonywanie ruchow po kliknieciu na plansze losowo
        //move.execute_move_console(); //Wykonywanie ruchu na planszy po podaniu wczesniejszych wartosci w konsoli
        //move.execute_move_on_data_changes(3,1,3,3); //Wykonywanie ruchu na planszy po wprowadzeniu wartosci do funkcji

        scene2 = new Scene(border,495,520);


        //Uruchomienie gry w konsoli
        //Game game = new Game();
        //Thread watek_gry = new Thread(game);
        //watek_gry.start();


        window.setScene(scene1);
        window.show();
    }


    private void closeProgram()
    {
        Boolean answer;
        answer=ConfirmBox.display("Wyjscie z gry", "Czy na pewno chcesz opuscic gre?\nPrzecież jest super!");
        if(answer==true) window.close();
    }

}
