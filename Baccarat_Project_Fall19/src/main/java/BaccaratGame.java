import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BaccaratGame extends Application {
	ArrayList<Card> playerHand;
	ArrayList<Card> bankerHand;
	ArrayList<Card> deck;

	BaccaratDealer theDealer = new BaccaratDealer();
	BaccaratGameLogic gameLogic = new BaccaratGameLogic();

	String choice = "";
	double currentBet;
	double totalWinnings;
	boolean playerWin;

	MenuBar menuBar = new MenuBar();
	HashMap<String, Scene> sceneMap = new HashMap<String, Scene>();

	// Left VBox
	TextField betMoney;
	Button startBtn;
	ToggleButton PlayerButt, BankerButt, TieButt;
	ToggleGroup toggleGrp;
	EventHandler<ActionEvent> bpdButt;
	HBox betChoices;
	Button playBtn;
	TextField result = new TextField();

	// Right VBox
	TextField currWinnings;
	TextField bankerCard1;
	TextField bankerCard2;
	TextField bankerCard3;
	TextField playerCard1;
	TextField playerCard2;
	TextField playerCard3;


	// evaluateWinnings calculate the player's amount of totalWinnings after the end of a game
	public double evaluateWinnings() {
		double resultWinnings = 0.0;
		if (playerWin) {
			if (choice.equals("Player"))
				resultWinnings = totalWinnings + currentBet;
			 else if (choice.equals("Banker"))
				resultWinnings = totalWinnings + currentBet*1.95;
			else
				resultWinnings = totalWinnings + currentBet*1.8;
		} else
			resultWinnings = totalWinnings - currentBet;
		return resultWinnings;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Let's Play Baccarat!!!");		
		initMenu(primaryStage);
		startGame(primaryStage);
	}

	private void startGame(Stage primaryStage) {
		sceneMap.put("scene", mainScene());
		//sceneMap.put("gameScene", gameBoardScene());
		primaryStage.setScene(sceneMap.get("scene"));
		primaryStage.show();
	}

	// initMenu initializes a menu with two menu items: freshstart and exit, and add into menuBar
	public void initMenu(Stage primaryStage) {
		Menu mainMenu = new Menu();
		mainMenu.setText("Options");
		MenuItem frshStart = new MenuItem();
		MenuItem exitItm = new MenuItem();

		frshStart.setText("Fresh Start");
		frshStart.setOnAction(e -> {
			// Reset game
			startGame(primaryStage);
		});

		exitItm.setText("Exit");
		exitItm.setOnAction(e -> {
			Platform.exit();
		});

		mainMenu.getItems().addAll(frshStart, exitItm);
		menuBar.getMenus().add(mainMenu);
	}

	public Scene mainScene() {
		BorderPane pane = new BorderPane();
		pane.setTop(menuBar);
		//pane.setPadding(new Insets(70));

		VBox selection = initLeftVBox();
		BorderPane game = initRightVBox();
		pane.setLeft(selection);
		pane.setCenter(game);
		pane.setStyle("-fx-background-color: Green;");

		return new Scene(pane, 950, 600);
	}

	private VBox initLeftVBox() {
		// Textfield for bet
		betMoney = new TextField();
		betMoney.setPromptText("Enter your bid here!");
		betMoney.setDisable(true);

		toggleGrp = new ToggleGroup();
		// Select Banker
		BankerButt = new ToggleButton("Bet On Banker");
		BankerButt.setToggleGroup(toggleGrp);
		BankerButt.setId("Banker");
		// Select Player
		PlayerButt = new ToggleButton("Bet on Player");
		PlayerButt.setToggleGroup(toggleGrp);
		PlayerButt.setId("Player");
		// Select Tie
		TieButt = new ToggleButton("Bet On Tie");
		TieButt.setToggleGroup(toggleGrp);
		TieButt.setId("Draw");
		betChoices = new HBox(BankerButt, PlayerButt, TieButt);
		betChoices.setDisable(true);

		//force the textfield to be Numeric, EX: 1234.56
		betMoney.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.matches("\\d*([\\.]\\d{0,2})?")){
					betMoney.setText(newValue.replaceAll("\\D", ""));
				}
			}
		});

		//After Player, Banker, or Tie butt are pressed
		bpdButt = new EventHandler<ActionEvent>(){
			public void handle(ActionEvent pressed){
				ToggleButton butt = (ToggleButton)pressed.getSource();
				choice = butt.getId();
				//primaryStage.setScene(sceneMap.get("gameScene")); //switches to the game scene
			}
		};

		BankerButt.setOnAction(bpdButt);
		PlayerButt.setOnAction(bpdButt);
		TieButt.setOnAction(bpdButt);

		// Button to submit the player's bet and start the game
		startBtn = new Button("Start Game!");
		startBtn.setOnAction(e->{
			currentBet = Integer.parseInt(betMoney.getText());
			betMoney.setDisable(true);
			betChoices.setDisable(true);
			startBtn.setDisable(true);
			gamePlay();
		});
		startBtn.setDisable(true);

		playBtn = new Button("PLAY");
		playBtn.setOnAction(e -> {
			betMoney.setDisable(false);
			betChoices.setDisable(false);
			startBtn.setDisable(false);
			playBtn.setDisable(true);
		});
		result.setEditable(false);
		return new VBox(10, betMoney, betChoices, startBtn, result, playBtn);
	}

	private BorderPane initRightVBox() {
		BorderPane board = new BorderPane();
		Text displayWinnings = new Text("Total Winnings: ");
		currWinnings = new TextField();
		currWinnings.setEditable(false);
		currWinnings.setText(Double.toString(totalWinnings));
		HBox winningBar  = new HBox(displayWinnings, currWinnings);
		board.setTop(winningBar);

		bankerCard1 = new TextField();
		bankerCard1.setEditable(false);
		bankerCard2 = new TextField();
		bankerCard2.setEditable(false);
		bankerCard3 = new TextField();
		bankerCard3.setEditable(false);
		HBox bankerPos = new HBox(bankerCard1, bankerCard2, bankerCard3);

		playerCard1 = new TextField();
		//playerCard1.setEditable(false);
		playerCard2 = new TextField();
		//playerCard2.setEditable(false);
		playerCard3 = new TextField();
		//playerCard3.setEditable(false);
		HBox playerPos = new HBox(playerCard1, playerCard2, playerCard3);
		VBox bankerNPlayer = new VBox(bankerPos, playerPos);
		board.setCenter(bankerNPlayer);

		return board;
	}

	public Scene gameBoardScene() {
		//Temporary Holder
		BorderPane pane = new BorderPane();
		return new Scene(pane, 950,600);
	}

	private void gamePlay() {
		theDealer.shuffleDeck();

		playerHand = theDealer.dealHand();
		playerCard1.setText(Integer.toString(playerHand.get(0).getValue()));
		playerCard2.setText(Integer.toString(playerHand.get(1).getValue()));

		bankerHand = theDealer.dealHand();
		bankerCard1.setText(Integer.toString(bankerHand.get(0).getValue()));
		bankerCard2.setText(Integer.toString(bankerHand.get(1).getValue()));

		Card player3rdC = null;
		if (gameLogic.evaluatePlayerDraw(playerHand)) {
			player3rdC = theDealer.drawOne();
			playerHand.add(player3rdC);
			playerCard3.setText(Integer.toString(playerHand.get(2).getValue()));
		}
		if (gameLogic.evaluateBankerDraw(bankerHand, player3rdC)) {
			bankerHand.add(theDealer.drawOne());
			bankerCard3.setText(Integer.toString(bankerHand.get(2).getValue()));
		}
		gameEnd();
	}

	// gameEnd contains the logic for the end of the game
	private void gameEnd() { // text representation of end results, prefer a popup window
		result.setText(gameEndMsg());
		currWinnings.setText(Double.toString(evaluateWinnings()));
		playBtn.setDisable(false);
	}

	private String gameEndMsg() {
		String playerMsg = "Player Total: " + gameLogic.handTotal(playerHand);
		String bankerMsg = "Banker Total: " + gameLogic.handTotal(bankerHand) + "\n";
		String winner = gameLogic.whoWon(playerHand, bankerHand);
		String winnerMsg = winner + "wins!\n";
		String msg = "";
		if (choice.equals(winner)) {
			msg = "Congratuations! You bet " + choice + "! You win!";
		} else {
			msg = "Sorry, you bet " + choice + "! You lost your bet!";
		}
		return playerMsg + bankerMsg + winnerMsg + msg;
	}




	/* Optional to implement the Welcome Scene
		StackPane root = new StackPane();

		//Welcome Scene
		welcome = new Text("Welcome to a game of Baccarat\n\n");
		startButt = new Button("Let's Begin!");
		startButt.setOnAction(e->stage.setScene(sceneMap.get("scene")));
		sceneMap.put("scene", SceneController());
		welcome.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
		welcome.setFill(Color.SKYBLUE);
		welcome.setTextAlignment(TextAlignment.CENTER);
		StackPane.setAlignment(welcome, Pos.CENTER);
		Image image = new Image(welcomePage.jpg);
		root.setStyle("-fx-background-image: url('"+image+"');" +
				"-fx-background-position: center center;");
		root.getChildren().addAll(welcome, startButt);
		*/
//	public Scene SceneController(){
//		vb = new VBox(new Label("To Be Added"));
//		return new Scene(vb, 950,600);
//	}

}
