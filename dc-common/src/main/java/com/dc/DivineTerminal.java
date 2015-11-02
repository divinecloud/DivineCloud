package com.dc;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DivineTerminal extends Application {
//	private Scene	          scene;
//	private ProgressIndicator	progress;

	private Label	          statusLabel;

	Button	                  openBtn;
	Button	                  stopBtn;
	Button	                  startBtn;

	FileWriter writer = null;
	
	public DivineTerminal() {
		initLogs();
		statusLabel = new Label();
		openBtn = generateOpenNewButton();
		stopBtn = generateStopButton();
		stopBtn.setVisible(false);
		stopBtn.setDisable(true);
		startBtn = generateStartButton();
		startBtn.setVisible(false);
		startBtn.setDisable(true);
	}

	private void initLogs() {
//		File file = new File("c:/Temp/dt.log");
//		try {
//			writer = new FileWriter(file, true);
//			file.createNewFile();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	private void log(String entry) {
//		try {
//	        writer.write(entry);
//	        writer.flush();
//        } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//        }
	}
	
	@Override
	public void start(Stage stage) {
		log("Init DT stage");
		try {
//			stage.getIcons().add(new Image("/images/dt_logo_two.png"));
			stage.setTitle("Divine Terminal");

			GridPane grid = new GridPane();
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);

			Hyperlink link = new Hyperlink();
			link.setText(getTerminalUrl());
			link.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					openTerminal();
				}
			});

			final Separator sepVert = new Separator();
			sepVert.setOrientation(Orientation.VERTICAL);
			sepVert.setValignment(VPos.CENTER);

			grid.add(openBtn, 1, 0);
			grid.add(stopBtn, 2, 0);
			grid.add(startBtn, 2, 0);
			grid.add(sepVert, 3, 0, 1, 2);
			grid.add(statusLabel, 4, 0);
			grid.add(link, 1, 1, 2, 1);

			int width = 600;
			int height = 175;

			Scene scene = new Scene(grid, width, height);
			stage.setScene(scene);

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.out.println("Stage is closing");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							statusLabel.setText("Closing");
						}
					});
//					server.shutdown();
				}
			});

			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

			// set Stage boundaries to the lower right corner of the visible bounds of the main screen
			stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - width);
			stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - height);
			stage.setWidth(width);
			stage.setHeight(height);

			stage.show();
//			startServer();
		}
		catch(Exception e) {
			log(e.toString());
		}
	}

	private void startServer() {
//		TerminalStartTracker tracker = new TerminalStartTracker(progress);
//		tracker.start();
		try {
//			server = new Server();
//			server.start();
		} catch (Exception e) {
			// TODO: add message in UI for user
		}
	}

	private void stopServer() {
//		TerminalStopTracker tracker = new TerminalStopTracker(progress);
//		tracker.start();
		try {
//			server.shutdown();
		} catch (Exception e) {
			// TODO: add message in UI for user
		}
	}

	private void openTerminal() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(getTerminalUrl()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			// TODO: Handle condition
		}
	}

	private String getTerminalUrl() {
		return "http://www.divinecloud.com";
	}

	private Button generateOpenNewButton() {
		Button openBtn = new Button("Open in Browser");
		openBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				openTerminal();
			}
		});
		return openBtn;
	}

	private Button generateStopButton() {
		Button stopBtn = new Button("Stop");
		stopBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stopServer();
			}
		});
		return stopBtn;
	}

	private Button generateStartButton() {
		Button stopBtn = new Button("Start");
		stopBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				startServer();
			}
		});
		return stopBtn;
	}

//	private static String getDtPath(String currentPath) {
//		String dtPath = "";
//
//		File file = new File(currentPath + "/divinecloud.check");
//		if (file.exists()) {
//			try {
//				dtPath = readToList(file).get(2);
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new RuntimeException("Unable to read divinecloud.check file", e);
//			}
//		} else {
//			System.out.println("Invalid startup directory.");
//			System.exit(10);
//		}
//
//		return dtPath;
//	}
//
	private static List<String> readToList(File file) throws Exception {
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			for (String line; (line = br.readLine()) != null;) {
				list.add(line);
			}
		}
		return list;
	}

	public static void main(String[] args) {
//		File file = new File("c:/Temp/dt.log");
//		FileWriter writer = null;
//		
//		try {
//			writer = new FileWriter(file);
//			file.createNewFile();
//			writer.write("Starting DT...");
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		finally {
//			try {
//	            writer.close();
//            } catch (IOException e) {
//	            e.printStackTrace();
//            }		
//		}
		 
		launch(args);
	}

}
