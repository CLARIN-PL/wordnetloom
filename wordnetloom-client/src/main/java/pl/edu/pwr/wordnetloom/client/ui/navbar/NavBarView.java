package pl.edu.pwr.wordnetloom.client.ui.navbar;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;

import javafx.fxml.FXML;
import javafx.scene.Parent;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.dictionarydialog.DictionaryDialogView;
import pl.edu.pwr.wordnetloom.client.ui.dictionarydialog.DictionaryDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.lexicondialog.LexiconDialogView;
import pl.edu.pwr.wordnetloom.client.ui.lexicondialog.LexiconDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.profiledialog.ProfileDialogView;
import pl.edu.pwr.wordnetloom.client.ui.profiledialog.ProfileDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.relationtypesdialog.RelationTypesDialogView;
import pl.edu.pwr.wordnetloom.client.ui.relationtypesdialog.RelationTypesDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.users.UsersDialogView;
import pl.edu.pwr.wordnetloom.client.ui.users.UsersDialogViewModel;


import javax.inject.Inject;

public class NavBarView implements FxmlView<NavBarViewModel> {

	@InjectViewModel
	private NavBarViewModel viewModel;

	@FXML
	MenuButton usernameMenuButton;

	@FXML
	MenuItem  logout, relationTypes, lexicons, profile, subMenuSettings, dictionaries, users;

	@InjectContext
	Context context;

	@Inject
	private Stage primaryStage;


	public void initialize() {
		initIcons();
		usernameMenuButton.textProperty().bindBidirectional(viewModel.usernameProperty());
	}

	private void initIcons(){
		AwesomeDude.setIcon(subMenuSettings, AwesomeIcon.COGS,"11");
		AwesomeDude.setIcon(usernameMenuButton, AwesomeIcon.USER, "11");
		AwesomeDude.setIcon(profile, AwesomeIcon.USER,"11");
		AwesomeDude.setIcon(lexicons, AwesomeIcon.BOOKMARK,"11");
		AwesomeDude.setIcon(users, AwesomeIcon.USERS,"11");
		AwesomeDude.setIcon(dictionaries, AwesomeIcon.BOOK,"11");
		AwesomeDude.setIcon(relationTypes, AwesomeIcon.EXCHANGE,"11");
		AwesomeDude.setIcon(logout, AwesomeIcon.SIGN_OUT,"11");

		viewModel.subscribe(NavBarViewModel.OPEN_RELATION_TYPES_DIALOG, (key, payload) -> {

			ViewTuple<RelationTypesDialogView, RelationTypesDialogViewModel> load = FluentViewLoader
					.fxmlView(RelationTypesDialogView.class)
					.context(context)
					.load();

			Parent view = load.getView();
			Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
			load.getCodeBehind().setDisplayingStage(showDialog);
			showDialog.toFront();
		});

		viewModel.subscribe(NavBarViewModel.OPEN_LEXICON_DIALOG, (key, payload) -> {

			ViewTuple<LexiconDialogView, LexiconDialogViewModel> load = FluentViewLoader
					.fxmlView(LexiconDialogView.class)
					.context(context)
					.load();

			Parent view = load.getView();
			Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
			load.getCodeBehind().setDisplayingStage(showDialog);
			showDialog.toFront();
		});

		viewModel.subscribe(NavBarViewModel.OPEN_PROFILE_DIALOG, (key, payload) -> {

			ViewTuple<ProfileDialogView, ProfileDialogViewModel> load = FluentViewLoader
					.fxmlView(ProfileDialogView.class)
					.context(context)
					.load();

			Parent view = load.getView();
			Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
			load.getCodeBehind().setDisplayingStage(showDialog);
			showDialog.toFront();
		});

		viewModel.subscribe(NavBarViewModel.OPEN_DICTIONARIES_DIALOG, (key, payload) -> {

			ViewTuple<DictionaryDialogView, DictionaryDialogViewModel> load = FluentViewLoader
					.fxmlView(DictionaryDialogView.class)
					.context(context)
					.load();

			Parent view = load.getView();
			Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
			load.getCodeBehind().setDisplayingStage(showDialog);
			showDialog.toFront();
		});

		viewModel.subscribe(NavBarViewModel.OPEN_USERS_DIALOG, (key, payload) -> {

			ViewTuple<UsersDialogView, UsersDialogViewModel> load = FluentViewLoader
					.fxmlView(UsersDialogView.class)
					.context(context)
					.load();

			Parent view = load.getView();
			Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
			load.getCodeBehind().setDisplayingStage(showDialog);
			showDialog.toFront();
		});
	}

	@FXML
	private void logout() {
		viewModel.logoutAction();
	}

	@FXML
	public void showRelationTypes() {
		viewModel.showRelationTypes();
	}

	@FXML
	public void showLexicons() {
		viewModel.showLexiconDialog();
	}

	@FXML
	public void showUsers() {
		viewModel.showUsersDialog();
	}

	@FXML
	public void showDictionaries() {
		viewModel.showDictionariesDialog();
	}

	@FXML
	public void showProfile() {
		viewModel.showProfileDialog();
	}
}
