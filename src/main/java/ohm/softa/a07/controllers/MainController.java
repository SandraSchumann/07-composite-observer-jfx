package ohm.softa.a07.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ohm.softa.a07.VegetarianFilter;
import ohm.softa.a07.api.OpenMensaAPI;
import ohm.softa.a07.model.Meal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	private static final Logger logger = LogManager.getLogger(MainController.class);

	// use annotation to tie to component in XML
	@FXML
	private Button btnRefresh;

	@FXML
	private ListView<Meal> mealsList;

	@FXML
	private Button btnClose;

	@FXML
	private CheckBox chkVegetarian;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private OpenMensaAPI openMensaAPI;

	public MainController() {
		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://openmensa.org/api/v2/")
			.build();

		openMensaAPI = retrofit.create(OpenMensaAPI.class);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the event handler (callback)
		logger.debug("Initializing the MainController");
		loadMensaData();

		chkVegetarian.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				logger.debug("Handling interaction of vegetarian checkbox");
				loadMensaData();
			}
		});

		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				logger.debug("Handling refresh menu item...");
				loadMensaData();
			}
		});

		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void loadMensaData() {
		logger.debug("Starting call to fetch data from API");
		openMensaAPI.getMeals(sdf.format(new Date())).enqueue(new Callback<List<Meal>>() {

			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
				if (!response.isSuccessful()) return;
				logger.debug("Handling positive response from API...");
				if (response.body() == null) return;
				Platform.runLater(() -> {

					mealsList.getItems().clear();
					mealsList.getItems().addAll((chkVegetarian.isSelected() ? VegetarianFilter.filterForVegetarian(response.body()) : response.body()));
				});
			}

			@Override
			public void onFailure(Call<List<Meal>> call, Throwable t) {
				logger.error("Error occured while fetching data from API", t);
				/* Show an alert if loading of mealsProperty fails */
				Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed to get mealsProperty", ButtonType.OK).showAndWait());
			}
		});
	}
}
