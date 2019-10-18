import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.prism.paint.ImagePattern;
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
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

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
	TextArea result = new TextArea();

	// Right VBox
	TextField currWinnings;
	TextField bankerCard1;
	TextField bankerCard2;
	TextField bankerCard3;
	TextField playerCard1;
	TextField playerCard2;
	TextField playerCard3;
//	ImageView bankerCard1;
//	ImageView bankerCard2;
//	ImageView bankerCard3;
//	ImageView playerCard1;
//	ImageView playerCard2;
//	ImageView playerCard3;

	// evaluateWinnings calculate the player's amount of totalWinnings after the end of a game
	public double evaluateWinnings() {
		double resultWinnings = 0.0;
		if (playerWin) {
			if (choice.equals("Player")) {
				resultWinnings = totalWinnings + currentBet * 2;
			} else if (choice.equals("Banker")) {
				resultWinnings = totalWinnings + currentBet * 1.95;
			} else {
				resultWinnings = totalWinnings + currentBet * 8;
			}
		} else {
			resultWinnings = totalWinnings - currentBet;
		}
		totalWinnings = resultWinnings;
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
		totalWinnings = 0.0;
		result.clear();
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

		VBox selection = initLeftVBox();
		BorderPane game = initRightVBox();
		pane.setLeft(selection);
		pane.setCenter(game);
		pane.setStyle("-fx-background-color: #43a047;");

		return new Scene(pane, 950, 600);
	}

	private VBox initLeftVBox() {
		ImageView logo = new ImageView(new Image("logo.png"));
		logo.setFitWidth(255);
		logo.setPreserveRatio(true);

		DropShadow dropShadow = new DropShadow();
		dropShadow.setBlurType(BlurType.GAUSSIAN);
		dropShadow.setRadius(5);

		Text dollar = new Text("$");
		dollar.setStyle("-fx-font-size: 20;");
		// Textfield for bet
		betMoney = new TextField();
		betMoney.setPromptText("Enter your bid here!");
		betMoney.setDisable(true);
		dollar.setTextAlignment(TextAlignment.CENTER);
		HBox betRow = new HBox(dollar, betMoney);
		HBox.setMargin(dollar, new Insets(5));
		betRow.setAlignment(Pos.CENTER);

		toggleGrp = new ToggleGroup();
		// Select Banker
		BankerButt = new ToggleButton("Bet Banker");
		BankerButt.setPrefSize(100, 20);
		BankerButt.setToggleGroup(toggleGrp);
		BankerButt.setId("Banker");
		BankerButt.setEffect(dropShadow);
		BankerButt.setStyle("-fx-background-radius:15em;");
		// Select Player
		PlayerButt = new ToggleButton("Bet Player");
		PlayerButt.setPrefSize(100, 20);
		PlayerButt.setToggleGroup(toggleGrp);
		PlayerButt.setId("Player");
		PlayerButt.setEffect(dropShadow);
		PlayerButt.setStyle("-fx-background-radius:15em;");
		// Select Tie
		TieButt = new ToggleButton("Bet Tie");
		TieButt.setPrefSize(100, 20);
		TieButt.setToggleGroup(toggleGrp);
		TieButt.setId("Draw");
		TieButt.setEffect(dropShadow);
		TieButt.setStyle("-fx-background-radius:15em;");
		betChoices = new HBox(8.5, BankerButt, PlayerButt, TieButt);
		betChoices.setDisable(true);

		//force the textfield to be Numeric, EX: 1234.56
		betMoney.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*([\\.]\\d{0,2})?")) {
					betMoney.setText(newValue.replaceAll("\\D", ""));
				}
			}
		});

		//After Player, Banker, or Tie butt are pressed
		bpdButt = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent pressed) {
				ToggleButton butt = (ToggleButton) pressed.getSource();
				choice = butt.getId();
				//primaryStage.setScene(sceneMap.get("gameScene")); //switches to the game scene
			}
		};

		BankerButt.setOnAction(bpdButt);
		PlayerButt.setOnAction(bpdButt);
		TieButt.setOnAction(bpdButt);

		// Button to submit the player's bet and start the game
		startBtn = new Button("Confirm Bet");
		startBtn.setOnAction(e -> {
			if (!betMoney.getText().equals("")) {
				currentBet = Integer.parseInt(betMoney.getText());
				betMoney.setDisable(true);
				betChoices.setDisable(true);
				startBtn.setDisable(true);
				gamePlay();
			}
		});
		startBtn.setEffect(dropShadow);
		startBtn.setStyle("-fx-background-radius:15em;");
		startBtn.setDisable(true);
		HBox startHBox = new HBox(startBtn);
		startHBox.setAlignment(Pos.CENTER_RIGHT);

		playBtn = new Button("PLAY");
		playBtn.setStyle("-fx-font-size: 40px;");
		playBtn.setPrefSize(296.7, 75);
		playBtn.setOnAction(e -> {
			betMoney.setDisable(false);
			betChoices.setDisable(false);
			startBtn.setDisable(false);
			playBtn.setDisable(true);
		});
		playBtn.setEffect(dropShadow);
		HBox playHBox = new HBox(playBtn);
		playHBox.setAlignment(Pos.BOTTOM_CENTER);

		result.setEditable(false);

		VBox selection = new VBox(10, logo, betRow, betChoices, startHBox, result, playHBox);
		selection.setMaxWidth(316.7);
		selection.setStyle("-fx-background-color: #ff8a65;");
		DropShadow vBoxDS = new DropShadow();
		vBoxDS.setHeight(0);
		selection.setEffect(vBoxDS);
		VBox.setMargin(logo, new Insets(20, 30, 0, 30));
		VBox.setMargin(betRow, new Insets(50,50,0,50));
		VBox.setMargin(betChoices, new Insets(0,10,0,10));
		VBox.setMargin(startHBox, new Insets(0,10,0,10));
		VBox.setMargin(playHBox, new Insets(10,10,10,10));
		return selection;
	}

	private BorderPane initRightVBox() {
		BorderPane board = new BorderPane();
		Text displayWinnings = new Text("Total Winnings: ");
		currWinnings = new TextField();
		currWinnings.setEditable(false);
		currWinnings.setText(Double.toString(totalWinnings));
		HBox winningBar = new HBox(displayWinnings, currWinnings);
		board.setTop(winningBar);

		bankerCard1 = new TextField();
		bankerCard1.setEditable(false);
		bankerCard2 = new TextField();
		bankerCard2.setEditable(false);
		bankerCard3 = new TextField();
		bankerCard3.setEditable(false);

//        bankerCard1 = new ImageView();
//        bankerCard2 = new ImageView();
//        bankerCard3 = new ImageView();

//        GridPane imagePane = new GridPane();
//        imagePane.add(playerCard1, 0,0);
//        imagePane.add(playerCard2,0,1);
//        imagePane.add(playerCard3,0,2);
//        imagePane.add(bankerCard1,1,0);
//        imagePane.add(bankerCard2,1,1);
//        imagePane.add(bankerCard3,1,2);
		HBox bankerPos = new HBox(bankerCard1, bankerCard2, bankerCard3);

		playerCard1 = new TextField();
		playerCard1.setEditable(false);
		playerCard2 = new TextField();
		playerCard2.setEditable(false);
		playerCard3 = new TextField();
		playerCard3.setEditable(false);

//        playerCard1 = new ImageView();
//        playerCard2 = new ImageView();
//        playerCard3 = new ImageView();

		HBox playerPos = new HBox(playerCard1, playerCard2, playerCard3);
		VBox bankerNPlayer = new VBox(bankerPos, playerPos);
		board.setCenter(bankerNPlayer);

		return board;
	}

	private void gamePlay() {
//		theDealer.shuffleDeck();
//
//		playerHand = theDealer.dealHand();
//		playerCard1 = new ImageView(CardImage(playerHand.get(0).getSuite(), playerHand.get(0).getValue()));
//		playerCard2 = new ImageView(CardImage(playerHand.get(1).getSuite(), playerHand.get(1).getValue()));
//
//		bankerHand = theDealer.dealHand();
//		bankerCard1 = new ImageView(CardImage(bankerHand.get(0).getSuite(), bankerHand.get(0).getValue()));
//		bankerCard2 = new ImageView(CardImage(bankerHand.get(1).getSuite(), bankerHand.get(1).getValue()));

		theDealer.shuffleDeck();

		playerHand = theDealer.dealHand();
		playerCard1.setText(Integer.toString(playerHand.get(0).getValue()));
		playerCard2.setText(Integer.toString(playerHand.get(1).getValue()));

		bankerHand = theDealer.dealHand();
		bankerCard1.setText(Integer.toString(bankerHand.get(0).getValue()));
		bankerCard2.setText(Integer.toString(bankerHand.get(1).getValue()));

		Card player3rdC = null;
		if (gameLogic.whoWon(playerHand, bankerHand).equals("None")) {
			if (gameLogic.evaluatePlayerDraw(playerHand)) {
				player3rdC = theDealer.drawOne();
				playerHand.add(player3rdC);
//			playerCard3 = new ImageView(CardImage(playerHand.get(2).getSuite(), playerHand.get(2).getValue()));
				playerCard3.setText(Integer.toString(playerHand.get(2).getValue()));
			}
			if (gameLogic.evaluateBankerDraw(bankerHand, player3rdC)) {
				bankerHand.add(theDealer.drawOne());
//			bankerCard3 = new ImageView(CardImage(bankerHand.get(2).getSuite(), bankerHand.get(2).getValue()));
				bankerCard3.setText(Integer.toString(bankerHand.get(2).getValue()));
			}
		}
		gameEnd();
	}

	// gameEnd contains the logic for the end of the game
	private void gameEnd() { // text representation of end results, prefer a popup window
		String winner = gameLogic.whoWon(playerHand, bankerHand);
		result.setText(gameEndMsg(winner));
		if (winner.equals(choice))
			playerWin = true;
		else
			playerWin = false;
		currWinnings.setText(Double.toString(evaluateWinnings()));
		playBtn.setDisable(false);
	}

	private String gameEndMsg(String winner) {
		String playerMsg = "Player Total: " + gameLogic.handTotal(playerHand);
		String bankerMsg = " Banker Total: " + gameLogic.handTotal(bankerHand) + "\n";
		String winnerMsg = winner + " wins!\n";
		String msg = "";
		if (choice.equals(winner)) {
			msg = "Congratulations! You bet " + choice + "! You win!";
		} else {
			msg = "Sorry, you bet " + choice + "! You lost your bet!";
		}
		return playerMsg + bankerMsg + winnerMsg + msg;
	}

	private Image CardImage(String suite, int value) {
		if (suite == "Hearts") { // use .equals not ==
			return (new Image(value + "H.png"));
		}else if (suite == "Diamonds") {
			return (new Image(value + "D.png"));
		}else if (suite == "Spades") {
			return (new Image(value + "S.png"));
		}else{
			return (new Image("resources/" + value + "C.png"));
		}
}

//	return (new Image(getClass().getResource("resources/" + value + "C.png").toExternalForm()));
//  return (new Image(getClass().getResource(value + "H.png").toExternalForm()));
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
