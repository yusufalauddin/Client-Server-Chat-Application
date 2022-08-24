
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1, recipient;
	Text receipt;
	Button serverChoice,clientChoice,b1,clear;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	String clientNumber = new String();
	ArrayList<Integer> msgRecipients = new ArrayList<Integer>();
	
	ListView<String> listItems, listItems2,names,names2;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						if (data.toString().contains("dropped")) {
							for (Iterator<String> i = names.getItems().iterator();i.hasNext();) {
								if (i.next().equals(data.toString().substring(9))) {
									i.remove();
								}
							}

						} else {
						listItems.getItems().add(data.toString());
						if (data.toString().contains("new client on server")) {
							names.getItems().add(data.toString().substring(22));
						} 
						}
					});

				});
											
		});
		
		
		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
											
											clientConnection = new Client(data->{
							Platform.runLater(()->{
								StorageInfo x = (StorageInfo) data;
								if (clientNumber.isBlank()) {
									clientNumber = x.clientName;
								}

								if (x.isUpdate) {
									primaryStage.setTitle("You are " + x.clientName);

									names2.getItems().clear();
									for (int i =0; i <x.names.size();i++) {
										names2.getItems().add(x.names.get(i));

									}
								}
								else {
								listItems2.getItems().add(x.clientName + " sent: " + x.message);
								}
							
							});
							});
							
											clientConnection.start();
		});
		
		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		
		startScene = new Scene(startPane, 800,800);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		names = new ListView<String>();
		names2 = new ListView<String>();
		c1 = new TextField();
		b1 = new Button("Send");
		clear = new Button("Clear");
		
		recipient = new TextField();
		receipt = new Text("Sending to:");
		receipt.setFill(Color.WHITE);
		receipt.setFont(Font.font("Arial",FontWeight.BOLD,20));
		recipient.setEditable(false);
		


		
		names2.setOnMouseClicked(e->{
			String check = names2.getSelectionModel().getSelectedItem();
			int index; 
			
			if (check.equals("@everyone")) {
				index = 0;
			} else {
				index = Integer.valueOf(check.substring(8));
			}
			
			
			if (recipient.getText().contains(check)) {
				;
			} else {
			if (check.equals("@everyone")) {
					msgRecipients.clear();
					msgRecipients.add(index);
					recipient.setText(check);
			}
			else if (recipient.getText().isBlank()) {
				msgRecipients.add(index);
				recipient.setText(check);
			} 
			else {
				if (recipient.getText().equals("@everyone")) {
					recipient.clear();
					msgRecipients.clear();
					msgRecipients.add(index);
					recipient.setText(check);
				} else {
					msgRecipients.add(index);
					recipient.setText(recipient.getText() + " + " + check);
				}
				
			}
		
		}});
		
		b1.setOnAction(e->{
			
			if (!recipient.getText().isBlank()) {
			StorageInfo information = new StorageInfo();
			information.message = c1.getText();
			information.clientName = clientNumber;
			information.msgRecipients = msgRecipients;
			information.isUpdate = false;
			clientConnection.send(information);
			c1.clear();
			recipient.clear();
			msgRecipients.clear();
			}
			});
		
		clear.setOnAction(e->{
			recipient.clear();
			msgRecipients.clear();
		});
		
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
			
		primaryStage.setScene(startScene);
		primaryStage.show();
	}
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		
		HBox lists = new HBox(listItems,names);
		VBox centerList = new VBox(lists);
		
		lists.setAlignment(Pos.CENTER);
		centerList.setAlignment(Pos.CENTER);
		
		lists.setSpacing(25);
		
		pane.setCenter(centerList);
	
		return new Scene(pane, 500, 400);
		
		
	}
	
	public Scene createClientGui() {
		HBox nameBoxes = new HBox(listItems2,names2);
		nameBoxes.setAlignment(Pos.CENTER);
		
		VBox recieptBox = new VBox(10,receipt,recipient);
		HBox hRecieptBox = new HBox(10,recieptBox);
		hRecieptBox.setAlignment(Pos.CENTER);
		
		VBox recieptAndButton = new VBox(10,hRecieptBox,clear);
		recieptAndButton.setAlignment(Pos.CENTER);
		
		clientBox = new VBox(10, c1,b1,recieptAndButton,nameBoxes);
		
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 400, 300);
		
	}

}
