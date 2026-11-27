package com.presyotrack;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Main extends Application {
    private BorderPane shell;
    private boolean pricesOpen, expensesOpen, settingsOpen;
    private StackPane appBackdrop;
    private String accountName = "Your Name", accountEmail = "";
    private String period = "Month";
    // Backend services
    private final com.presyotrack.service.FileManager fileManager =
            new com.presyotrack.service.FileManager();
    private com.presyotrack.service.BudgetManager budgetManager;
    private com.presyotrack.service.BudgetReport budgetReport;
    private com.presyotrack.service.ProductManager productManager;
    private com.presyotrack.service.PriceTracker priceTracker;
    private final java.util.List<Product> products = new java.util.ArrayList<>();
    private record Product(String name, String category, double price, double prior) {}
    private record Expense(String name, String category, double amount, LocalDate date, String payment) { Expense(String name, String category, double amount, LocalDate date) { this(name, category, amount, date, "Unspecified"); } }
    private final java.util.List<Expense> expenses = new java.util.ArrayList<>();
    private final VBox activityList = new VBox(10);
    private Label balanceValue;
    private Label spentValue;
    private int transactionCount = 0;
    private double spent = 0.0;
    private double budget = 0.0;
    private java.time.YearMonth budgetMonth = java.time.YearMonth.now();

    @Override
    public void start(Stage stage) {

        // Initialize backend services
        com.presyotrack.model.Budget savedBudget = fileManager.loadBudget();

        com.presyotrack.model.Budget initialBudget =
                savedBudget != null
                ? savedBudget
                : new com.presyotrack.model.Budget(
                        1.0, java.time.YearMonth.now());

        budgetManager =
                new com.presyotrack.service.BudgetManager(initialBudget);
        budgetManager.loadData();
        budgetReport = new com.presyotrack.service.BudgetReport(budgetManager);

        // Restore the saved budget for the JavaFX interface
        budget = initialBudget.getAmount();
        budgetMonth = initialBudget.getMonth();

        // Restore saved expenses into the UI list
        for (com.presyotrack.model.Expense savedExpense : budgetManager.getExpenses()) {
            expenses.add(new Expense(
                    savedExpense.getName(),
                    savedExpense.getCategory(),
                    savedExpense.getAmount(),
                    savedExpense.getDate(),
                    savedExpense.getPayment()
            ));
        }

        productManager = new com.presyotrack.service.ProductManager();
        productManager.loadData();

        // Restore saved backend products into the JavaFX display list
        products.clear();

        for (com.presyotrack.model.Product savedProduct
                : productManager.getProducts()) {
            products.add(new Product(
                    savedProduct.getName(),
                    savedProduct.getCategory(),
                    savedProduct.getCurrentPrice(),
                    savedProduct.getPreviousPrice()
            ));
        }

        priceTracker = new com.presyotrack.service.PriceTracker();
        priceTracker.loadData();
        StackPane backdrop = new StackPane();
        appBackdrop = backdrop;
        backdrop.getStyleClass().add("backdrop");

        shell = new BorderPane();
        shell.getStyleClass().add("app-shell");
        shell.setTop(buildHeader());
        shell.setCenter(buildDashboard());
        shell.setBottom(buildBottomBar());
        backdrop.getChildren().add(buildWelcome(backdrop));

        Scene scene = new Scene(backdrop, 390, 844);
        scene.getStylesheets().add(getClass().getResource("/com/presyotrack/css/style.css").toExternalForm());
        stage.setTitle("PresyoTrack Lite");
        stage.setMinWidth(390);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildWelcome(StackPane backdrop) {
        VBox page = new VBox();
        page.getStyleClass().add("welcome-page");
        page.setMaxWidth(390);
        page.setAlignment(Pos.TOP_CENTER);
        Label brand = welcomeLabel("PresyoTrack Lite", "welcome-brand");
        Region topSpace = new Region();
        VBox.setVgrow(topSpace, Priority.ALWAYS);
        VBox hero = new VBox(0);
        hero.setAlignment(Pos.CENTER_LEFT);
        Label first = welcomeLabel("Your Money", "welcome-first");
        Label second = welcomeLabel("Better Days", "welcome-second");
        Label description = welcomeLabel("Monitor food commodity prices, track grocery expenses, and stay within your monthly household budget", "welcome-description");
        description.setWrapText(true);
        VBox.setMargin(description, new Insets(6, 10, 0, 10));
        hero.getChildren().addAll(first, second, description);
        Region middleSpace = new Region();
        VBox.setVgrow(middleSpace, Priority.ALWAYS);
        VBox features = new VBox(14,
            welcomeFeature("M 1 12 L 12 1 L 22 3 L 24 12 L 12 24 Z", "Commodity Price Tracker", "Monitor current vs. previous food prices"),
            welcomeFeature("M 6 2 H 21 V 19 Q 21 23 17 23 H 5 Q 1 23 1 19 V 16 H 6 Z M 10 7 H 17 M 10 11 H 17 M 6 16 H 13 V 19 Q 13 23 17 23", "Daily Expense Logger", "Record grocery purchases & calculate totals"),
            welcomeFeature("M 12 2 L 22 5 L 20 17 Q 18 22 12 24 Q 6 22 4 17 L 2 5 Z M 7 12 L 11 16 L 17 9", "Monthly Budget Guard", "Track remaining balance & overspending alerts"));
        VBox.setMargin(features, new Insets(0, 10, 0, 10));
        Region bottomSpace = new Region();
        VBox.setVgrow(bottomSpace, Priority.ALWAYS);
        Button start = new Button("Get Started");
        start.getStyleClass().add("welcome-start");
        start.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(start, new Insets(0, 30, 0, 30));
        start.setOnAction(e -> backdrop.getChildren().setAll(buildBudgetSetup(backdrop)));
        page.getChildren().addAll(brand, topSpace, hero, middleSpace, features, bottomSpace, start);
        return page;
    }

    private VBox buildBudgetSetup(StackPane backdrop) {
        VBox page = new VBox(0); page.getStyleClass().add("setup-page"); page.setMaxWidth(390); page.setAlignment(Pos.TOP_CENTER);
        Label brand = welcomeLabel("PresyoTrack Lite", "welcome-brand");
        Region top = new Region(); VBox.setVgrow(top, Priority.ALWAYS);
        Label title = welcomeLabel("Set Up Your\nBudget", "setup-title");
        Label description = welcomeLabel("Enter your initial monthly grocery budget and choose the active month to begin tracking.", "welcome-description"); description.setWrapText(true);
        VBox hero = new VBox(4,title,description); hero.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(8); card.getStyleClass().add("setup-card");
        TextField amount = new TextField(String.format(Locale.US,"%,.2f",budget)); amount.getStyleClass().add("setup-amount"); HBox.setHgrow(amount,Priority.ALWAYS);
        Label currency = welcomeLabel("PHP","setup-currency"); HBox amountBox = new HBox(8,currency,amount); amountBox.setAlignment(Pos.CENTER_LEFT); amountBox.getStyleClass().add("setup-amount-box");
        HBox presets = new HBox(5); for(int n : new int[]{3000,5000,8000,10000}) { Button b = new Button(String.format(Locale.US,"PHP %,d",n)); b.getStyleClass().add("setup-preset"); b.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(b,Priority.ALWAYS); b.setOnAction(e -> amount.setText(String.format(Locale.US,"%,.2f",(double)n))); presets.getChildren().add(b); }
        ComboBox<java.time.YearMonth> month = new ComboBox<>(); month.getStyleClass().add("setup-month"); month.setMaxWidth(Double.MAX_VALUE);
        for(int i=-24;i<=24;i++) month.getItems().add(budgetMonth.plusMonths(i));
        month.setConverter(new javafx.util.StringConverter<java.time.YearMonth>() { public String toString(java.time.YearMonth m){return m==null?"":m.format(DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH));} public java.time.YearMonth fromString(String v){return java.time.YearMonth.parse(v,DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH));} }); month.setValue(budgetMonth);
        Label note=welcomeLabel("ⓘ You can update this budget anytime in Settings.","setup-note"); note.setWrapText(true);
        card.getChildren().addAll(welcomeLabel("Monthly Grocery Budget","setup-label"),amountBox,presets,welcomeLabel("Select Budget Month","setup-label"),month,note);
        Region middle = new Region(); VBox.setVgrow(middle,Priority.ALWAYS); Region bottom = new Region(); VBox.setVgrow(bottom,Priority.ALWAYS);
        Button save = new Button("Save & Continue"); save.getStyleClass().add("welcome-start"); save.setMaxWidth(Double.MAX_VALUE); VBox.setMargin(save,new Insets(0,22,0,22));
        save.setOnAction(e->{try {double value=Double.parseDouble(amount.getText().replace(",", "").trim()); if(!Double.isFinite(value)||value<=0||month.getValue()==null) throw new IllegalArgumentException(); com.presyotrack.model.Budget newBudget = new com.presyotrack.model.Budget(value, month.getValue()); budgetManager.updateBudget(newBudget); budgetManager.saveData(); budget=value; budgetMonth=month.getValue(); pricesOpen=false; expensesOpen=false; settingsOpen=false; refreshScreen(); backdrop.getChildren().setAll(shell); } catch(IllegalArgumentException ex){alert("Invalid budget","Enter a positive budget and select a month.");}});
        page.getChildren().addAll(brand,top,hero,middle,card,bottom,save); return page;
    }

    private Label welcomeLabel(String value, String style) {
        Label label = new Label(value);
        label.getStyleClass().add(style);
        return label;
    }

    private HBox welcomeFeature(String path, String title, String description) {
        javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
        icon.setContent(path);
        icon.setFill(Color.TRANSPARENT);
        icon.setStroke(Color.WHITE);
        icon.setStrokeWidth(1.5);
        StackPane iconBox = new StackPane(icon);
        iconBox.setMinWidth(30);
        Label subtitle = welcomeLabel(description, "welcome-feature-description");
        subtitle.setWrapText(true);
        VBox words = new VBox(1, welcomeLabel(title, "welcome-feature-title"), subtitle);
        HBox.setHgrow(words, Priority.ALWAYS);
        HBox card = new HBox(12, iconBox, words);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("welcome-feature");
        return card;
    }

    private HBox buildHeader() {
        Label avatar = new Label(accountName.equals("Your Name") ? "P" : accountName.substring(0,1).toUpperCase(Locale.ROOT));
        avatar.getStyleClass().add("avatar");
        Label brand = new Label("PresyoTrack Lite");
        brand.getStyleClass().add("brand");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button notification = new Button("♟");
        notification.getStyleClass().add("round-icon");
        notification.setOnAction(e -> alert("Notifications", "You're all caught up!"));
        HBox header = new HBox(12, avatar, brand, spacer, notification);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 18, 8, 18));
        return header;
    }

    private VBox buildDashboard() {
        VBox content = new VBox(22);
        content.setPadding(new Insets(42, 18, 12, 18));
        content.setFillWidth(true);

        VBox balance = new VBox(3);
        Label eyebrow = new Label("REMAINING BALANCE");
        eyebrow.getStyleClass().add("eyebrow");
        balanceValue = new Label(money(budgetReport.getRemainingBudget()));
        balanceValue.getStyleClass().add("balance");
        Label sub = new Label("of " + money(budget) + " Monthly Budget");
        sub.getStyleClass().add("subtext");
        balance.getChildren().addAll(eyebrow, balanceValue, sub);

        HBox stats = new HBox(12,
            statCard("▤", "Budget:", money(budget)),
            statCard("▰", "Spent:", money(spent)),
            statCard("◇", "Products:", String.valueOf(products.size()))
        );
        stats.setAlignment(Pos.CENTER);

        VBox recent = new VBox(12);
        recent.getStyleClass().add("activity-panel");
        Label title = new Label("Recent Activity");
        title.getStyleClass().add("section-title");
        Region line = new Region();
        line.getStyleClass().add("separator");
        activityList.getChildren().clear();
        spent = budgetReport.getTotalExpenses();
        balanceValue.setText(money(budgetReport.getRemainingBudget())); spentValue.setText(money(spent));
        for (Expense x : expenses) if (java.time.YearMonth.from(x.date()).equals(budgetMonth)) addActivityRow(x);
        recent.getChildren().addAll(title, line, activityList);
        VBox.setVgrow(recent, Priority.ALWAYS);

        content.getChildren().addAll(balance, stats, recent);
        VBox.setVgrow(recent, Priority.ALWAYS);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("transparent-scroll");
        VBox wrapper = new VBox(scroll); VBox.setVgrow(scroll, Priority.ALWAYS); return wrapper;
    }

    private VBox statCard(String icon, String title, String value) {
        Label iconLabel = new Label(icon + " " + title);
        iconLabel.getStyleClass().add("stat-label");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        if (title.equals("Spent:")) spentValue = valueLabel;
        VBox card = new VBox(5, iconLabel, valueLabel);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void addActivityRow(Expense expense) {
        Circle marker = new Circle(22);
        marker.getStyleClass().add("activity-marker");

        Label category = new Label(expense.name());
        category.getStyleClass().add("activity-name");

        Label dateLabel = new Label(expense.date().format(
                DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));
        dateLabel.getStyleClass().add("activity-date");

        VBox details = new VBox(0, category, dateLabel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amountLabel = new Label("- PHP " +
                String.format(Locale.US, "%,.2f", expense.amount()));
        amountLabel.getStyleClass().add("activity-amount");

        Button edit = new Button("Edit");
        edit.getStyleClass().add("plain-action");

        Button delete = new Button("Delete");
        delete.getStyleClass().add("plain-action");

        edit.setOnAction(event -> {
            com.presyotrack.model.Expense oldExpense =
                    findBackendExpense(expense);

            if (oldExpense == null) {
                alert("Expense not found", "Could not find this transaction.");
                return;
            }

            Dialog<ButtonType> dialog =
                    new Dialog<>();
            dialog.setTitle("Edit Expense");
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL);

            TextField name = new TextField(expense.name());
            TextField amount = new TextField(String.valueOf(expense.amount()));

            ComboBox<String> categoryBox = new ComboBox<>();
            categoryBox.getItems().addAll(
                    "Pantry", "Dairy & Eggs", "Beans",
                    "Others / Uncategorized");
            if (!categoryBox.getItems().contains(expense.category())) {
                categoryBox.getItems().add(expense.category());
            }
            categoryBox.setValue(expense.category());

            DatePicker date = new DatePicker(expense.date());

            ComboBox<String> payment = new ComboBox<>();
            payment.getItems().addAll(
                    "Card", "Cash", "Bank", "Digital Wallet");
            if (!payment.getItems().contains(expense.payment())) {
                payment.getItems().add(expense.payment());
            }
            payment.setValue(expense.payment());

            dialog.getDialogPane().setContent(new VBox(8,
                    new Label("Expense name"), name,
                    new Label("Amount (PHP)"), amount,
                    new Label("Category"), categoryBox,
                    new Label("Date"), date,
                    new Label("Payment method"), payment));

            dialog.showAndWait()
                    .filter(result -> result ==
                            ButtonType.OK)
                    .ifPresent(result -> {
                        try {
                            double value = Double.parseDouble(
                                    amount.getText().replace(",", "").trim());

                            if (name.getText().isBlank()
                                    || !Double.isFinite(value) || value <= 0
                                    || categoryBox.getValue() == null
                                    || date.getValue() == null
                                    || payment.getValue() == null) {
                                throw new IllegalArgumentException();
                            }

                            com.presyotrack.model.Expense updatedExpense =
                                    new com.presyotrack.model.Expense(
                                            name.getText().trim(),
                                            categoryBox.getValue(),
                                            value,
                                            date.getValue(),
                                            payment.getValue());

                            if (!budgetManager.updateExpense(
                                    oldExpense, updatedExpense)) {
                                alert("Update failed",
                                        "The expense could not be updated.");
                                return;
                            }

                            budgetManager.saveData();

                            int index = expenses.indexOf(expense);
                            if (index >= 0) {
                                expenses.set(index, new Expense(
                                        updatedExpense.getName(),
                                        updatedExpense.getCategory(),
                                        updatedExpense.getAmount(),
                                        updatedExpense.getDate(),
                                        updatedExpense.getPayment()));
                            }

                            refreshScreen();

                        } catch (IllegalArgumentException ex) {
                            alert("Invalid expense",
                                    "Enter a valid name, positive amount, category, date, and payment method.");
                        }
                    });
        });

        delete.setOnAction(event -> {
            Alert confirm =
                    new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Delete " + expense.name() + "?",
                            ButtonType.YES,
                            ButtonType.NO);

            confirm.setHeaderText("Delete Expense");

            if (confirm.showAndWait().orElse(
                    ButtonType.NO)
                    == ButtonType.YES) {

                com.presyotrack.model.Expense oldExpense =
                        findBackendExpense(expense);

                if (oldExpense == null) {
                    alert("Expense not found",
                            "Could not find this transaction.");
                    return;
                }

                budgetManager.removeExpense(oldExpense);
                budgetManager.saveData();
                expenses.remove(expense);
                refreshScreen();
            }
        });

        HBox row = new HBox(
                14, marker, details, spacer, amountLabel, edit, delete);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("activity-row");
        activityList.getChildren().add(row);
    }

    private com.presyotrack.model.Expense findBackendExpense(Expense expense) {
        return budgetManager.getExpenses().stream()
                .filter(item ->
                        item.getName().equals(expense.name())
                        && item.getCategory().equals(expense.category())
                        && Double.compare(item.getAmount(), expense.amount()) == 0
                        && item.getDate().equals(expense.date())
                        && item.getPayment().equals(expense.payment()))
                .findFirst()
                .orElse(null);
    }
    private HBox buildBottomBar() {
        Button dashboard = navButton("▣", "Dashboard", !pricesOpen && !expensesOpen && !settingsOpen);
        Button prices = navButton("◇", "Prices", pricesOpen);
        Button expenses = navButton("▤", "Expenses", expensesOpen);
        Button settings = navButton("⚙", "Settings", settingsOpen);
        HBox nav = new HBox(12, dashboard, prices, expenses, settings);
        nav.getStyleClass().add("nav-pill");
        nav.setAlignment(Pos.CENTER);
        HBox.setHgrow(dashboard, Priority.ALWAYS);
        HBox.setHgrow(prices, Priority.ALWAYS);
        HBox.setHgrow(expenses, Priority.ALWAYS);
        HBox.setHgrow(settings, Priority.ALWAYS);

        Button add = new Button("+");
        add.getStyleClass().add("add-button");
        add.setOnAction(e -> { showAddTransaction(); });

        HBox bottom = new HBox(18, nav, add);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 22, 20, 22));
        return bottom;
    }

    private Button navButton(String icon, String text, boolean active) {
        Button button = new Button(icon + "\n" + text);
        button.getStyleClass().add("nav-item"); if(active) button.getStyleClass().add("active");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            closeExpense(); pricesOpen = text.equals("Prices"); expensesOpen = text.equals("Expenses"); settingsOpen = text.equals("Settings"); refreshScreen();
        });
        return button;
    }

    private javafx.scene.Node previousPage;
    private void showAddTransaction() {
        if (previousPage != null) { closeExpense(); return; }
        previousPage = shell.getCenter();
        VBox form = new VBox(10); form.getStyleClass().add("new-expense"); form.setAlignment(Pos.TOP_CENTER);
        Label heading = welcomeLabel("New Expense", "new-heading");
        TextField title = new TextField(); title.setPromptText("Title"); title.getStyleClass().add("new-title");
        TextField amount = new TextField(); amount.setPromptText("Amount"); amount.getStyleClass().add("new-amount"); HBox.setHgrow(amount, Priority.ALWAYS);
        HBox moneyRow = new HBox(8,welcomeLabel("PHP","new-heading"),amount); moneyRow.setAlignment(Pos.CENTER);
        ComboBox<String> group = new ComboBox<>(); group.getItems().addAll("Pantry","Dairy & Eggs","Beans","Others / Uncategorized"); group.setPromptText("Category"); group.setMaxWidth(Double.MAX_VALUE); group.getStyleClass().add("new-field");
        DatePicker date = new DatePicker(); date.setPromptText("Date"); date.setMaxWidth(Double.MAX_VALUE); date.getStyleClass().add("new-field"); date.setEditable(false);
        Label payment = welcomeLabel("Payment Method","subtext"); payment.setMaxWidth(Double.MAX_VALUE);
        ToggleGroup methods = new ToggleGroup();
        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(10);
        String[] names={"Card","Cash","Bank","Digital Wallet"}; String[] icons={"▰","▣","♜","▣"};
        for(int i=0;i<4;i++){ ToggleButton t=new ToggleButton(icons[i]+"\n"+names[i]); t.setUserData(names[i]); t.setToggleGroup(methods); t.getStyleClass().addAll("payment-tile","payment-"+i); t.setMaxWidth(Double.MAX_VALUE); GridPane.setHgrow(t,Priority.ALWAYS); grid.add(t,i%2,i/2); }
        for(int i=0;i<2;i++){ColumnConstraints c=new ColumnConstraints();c.setPercentWidth(50);grid.getColumnConstraints().add(c);}
        Label error=welcomeLabel("","new-error"); error.setWrapText(true); error.setVisible(false); error.setManaged(false);
        Button save=new Button("SAVE"); save.getStyleClass().add("new-save"); save.setMaxWidth(Double.MAX_VALUE);
        save.setOnAction(e->{try{double value=Double.parseDouble(amount.getText().replace(",", "").trim()); if(!Double.isFinite(value)||value<=0||title.getText().isBlank()||group.getValue()==null||date.getValue()==null||methods.getSelectedToggle()==null)throw new IllegalArgumentException(); com.presyotrack.model.Expense newExpense =
                new com.presyotrack.model.Expense(
                        title.getText().trim(),
                        group.getValue(),
                        value,
                        date.getValue(),
                        methods.getSelectedToggle().getUserData().toString()
                );
                budgetManager.addExpense(newExpense);
                budgetManager.saveData();
                expenses.add(new Expense(
                        newExpense.getName(),
                        newExpense.getCategory(),
                        newExpense.getAmount(),
                        newExpense.getDate(),
                        newExpense.getPayment()
                )); previousPage=null; refreshScreen();}catch(IllegalArgumentException ex){error.setText("Enter a title, positive amount, category, date and payment method.");error.setManaged(true);error.setVisible(true);}});
        form.getChildren().addAll(heading,title,moneyRow,group,date,payment,grid,error,save);
        ScrollPane scroll=new ScrollPane(form);scroll.setFitToWidth(true);scroll.setFitToHeight(true);scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);scroll.getStyleClass().add("transparent-scroll");
        shell.setCenter(null); StackPane overlay=new StackPane(previousPage,scroll); previousPage.setEffect(new javafx.scene.effect.GaussianBlur(18)); previousPage.setDisable(true); StackPane.setMargin(scroll,new Insets(65,0,0,0)); shell.setCenter(overlay);
    }
    private void closeExpense(){if(previousPage==null)return; javafx.scene.Node old=previousPage; previousPage=null; old.setEffect(null);old.setDisable(false); if(shell.getCenter() instanceof StackPane stack)stack.getChildren().remove(old);shell.setCenter(old);}

    private String money(double n) { return String.format(Locale.US,"PHP %,.2f",n); }
    private void refreshScreen() {
        spent = budgetReport.getTotalExpenses();
        shell.setCenter(settingsOpen ? buildSettings() : pricesOpen ? buildPrices() : expensesOpen ? buildExpenses() : buildDashboard());
        shell.setBottom(buildBottomBar());
    }
    private VBox buildSettings() {
        VBox content = new VBox(14); content.getStyleClass().add("settings-page");
        Label heading = welcomeLabel("Settings", "settings-heading");
        Label subtitle = welcomeLabel("Manage your account and budget preferences.", "subtext"); subtitle.setWrapText(true);
        VBox account = settingsCard();
        Label avatar = welcomeLabel(accountName.equals("Your Name") ? "P" : accountName.substring(0,1).toUpperCase(Locale.ROOT), "avatar");
        Label name = welcomeLabel(accountName,"settings-name"); name.setWrapText(true);
        Label email = welcomeLabel(accountEmail.isBlank()?"No email added":accountEmail,"subtext"); email.setWrapText(true);
        VBox identity = new VBox(3,name,email,welcomeLabel("Personal Account","subtext")); HBox.setHgrow(identity,Priority.ALWAYS);
        HBox profile = new HBox(12,avatar,identity); profile.setAlignment(Pos.CENTER_LEFT);
        Button editProfile = settingsButton("Edit Profile   ›"); editProfile.setOnAction(e -> editAccount());
        account.getChildren().addAll(profile,settingsLine(),editProfile);
        VBox budgetCard = settingsCard();
        Button editBudget = settingsButton("Edit Budget ✎"); editBudget.setOnAction(e -> editSettingsBudget());
        HBox amount = new HBox(8,welcomeLabel(money(budget),"settings-money"),editBudget); amount.setAlignment(Pos.CENTER_LEFT);
        Button month = settingsButton("Budget Month   ›"); month.setOnAction(e -> editSettingsBudget());
        double left = budgetReport.getRemainingBudget();
        ProgressBar progress = new ProgressBar(Math.max(0,Math.min(1,budget > 0 ? spent / budget : 0))); progress.setMaxWidth(Double.MAX_VALUE); progress.getStyleClass().add("settings-progress");
        budgetCard.getChildren().addAll(welcomeLabel("Monthly Grocery Budget","subtext"),amount,settingsLine(),month,welcomeLabel(budgetMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH)),"settings-name"),settingsLine(),welcomeLabel("Remaining Balance","subtext"),welcomeLabel(money(left),"settings-money"),welcomeLabel(Math.round(budget > 0 ? left / budget * 100 : 0)+"% left","subtext"),progress);
        VBox about = settingsCard(); Button aboutButton = settingsButton("ⓘ  About PresyoTrack Lite   ›"); aboutButton.setOnAction(e -> alert("About PresyoTrack Lite","Version 1.0.0\nYour Money. Better Days.\nMonitor commodity prices, record grocery expenses and manage your monthly budget.")); about.getChildren().addAll(aboutButton,welcomeLabel("Version 1.0.0","subtext"));
        Button logout = settingsButton("Log Out"); logout.getStyleClass().add("settings-logout"); logout.setOnAction(e -> { Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,"Return to the welcome screen? Your current data stays in memory.",ButtonType.CANCEL,ButtonType.OK); confirm.setHeaderText("Log Out"); if(confirm.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK){closeExpense(); settingsOpen=false; pricesOpen=false; expensesOpen=false; refreshScreen(); appBackdrop.getChildren().setAll(buildWelcome(appBackdrop));} });
        Label slogan = welcomeLabel("Your Money. Better Days.","subtext"); slogan.setMaxWidth(Double.MAX_VALUE); slogan.setAlignment(Pos.CENTER);
        content.getChildren().addAll(heading,subtitle,welcomeLabel("Manage Your Account","settings-section"),account,welcomeLabel("Budget Management","settings-section"),budgetCard,about,logout,slogan);
        ScrollPane scroll = new ScrollPane(content); scroll.setFitToWidth(true); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll.getStyleClass().add("transparent-scroll"); VBox wrapper = new VBox(scroll); VBox.setVgrow(scroll,Priority.ALWAYS); return wrapper;
    }
    private VBox settingsCard(){VBox card=new VBox(9);card.getStyleClass().add("settings-card");return card;}
    private Region settingsLine(){Region line=new Region();line.getStyleClass().add("separator");return line;}
    private Button settingsButton(String text){Button b=new Button(text);b.getStyleClass().add("settings-action");b.setMaxWidth(Double.MAX_VALUE);return b;}
    private void editAccount(){
        Dialog<ButtonType> d=new Dialog<>();d.setTitle("Edit Profile");d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        TextField name=new TextField(accountName);TextField email=new TextField(accountEmail);email.setPromptText("Email address");
        d.getDialogPane().setContent(new VBox(8,new Label("Name"),name,new Label("Email"),email));
        d.showAndWait().filter(v->v==ButtonType.OK).ifPresent(v->{if(name.getText().isBlank()||(!email.getText().isBlank()&&!email.getText().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))){alert("Invalid profile","Enter a name and a valid email (or leave email blank).");return;}accountName=name.getText().trim();accountEmail=email.getText().trim();shell.setTop(buildHeader());refreshScreen();});
    }
    private void editSettingsBudget(){
        Dialog<ButtonType> d=new Dialog<>();d.setTitle("Edit Budget");d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        TextField amount=new TextField(String.format(Locale.US,"%,.2f",budget)); ComboBox<java.time.YearMonth> month=new ComboBox<>(); for(int i=-24;i<=24;i++)month.getItems().add(budgetMonth.plusMonths(i));month.setValue(budgetMonth);
        d.getDialogPane().setContent(new VBox(8,new Label("Monthly grocery budget (PHP)"),amount,new Label("Budget month"),month));
        d.showAndWait().filter(v->v==ButtonType.OK).ifPresent(v->{try{double n=Double.parseDouble(amount.getText().replace(",", "").trim());if(!Double.isFinite(n)||n<=0||month.getValue()==null)throw new IllegalArgumentException();com.presyotrack.model.Budget updatedBudget = new com.presyotrack.model.Budget(n, month.getValue()); budgetManager.updateBudget(updatedBudget); budgetManager.saveData(); budget=n; budgetMonth=month.getValue(); refreshScreen();}catch(IllegalArgumentException ex){alert("Invalid budget","Enter a positive amount and select a month.");}});
    }
    private VBox buildExpenses() {
        VBox content = new VBox(12); content.setPadding(new Insets(65, 23, 8, 23));
        Region line = new Region(); line.getStyleClass().add("separator");
        Label remaining = welcomeLabel(money(budgetReport.getRemainingBudget()), "balance");
        Label cap = welcomeLabel("of " + money(budget) + " cap (" + Math.round(budget > 0 ? spent / budget * 100 : 0) + "% used)", "subtext");
        Button edit = new Button("Edit Budget ✎"); edit.getStyleClass().add("setup-preset");
        edit.setOnAction(e -> editSettingsBudget());
        Region space = new Region(); HBox.setHgrow(space,Priority.ALWAYS);
        HBox capRow = new HBox(5,cap,space,edit); capRow.setAlignment(Pos.CENTER_LEFT);
        Region line2 = new Region(); line2.getStyleClass().add("separator");
        HBox filters = new HBox(4); filters.getStyleClass().add("expense-filters");
        for(String name : new String[]{"Today","Week","Month"}) {Button b=new Button(name); b.getStyleClass().addAll("expense-filter",name.equals(period)?"selected-filter":"unselected-filter"); b.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(b,Priority.ALWAYS); b.setOnAction(e->{period=name;refreshScreen();}); filters.getChildren().add(b);}
        String[] categories={"Pantry","Dairy & Eggs","Beans","Others / Uncategorized"};
        String[] colors={"#c5d9c5","#d4d13e","#456482","#d6a12d"};
        double[] totals=new double[4]; LocalDate now=LocalDate.now();
        for(Expense x:expenses) { if(!java.time.YearMonth.from(x.date()).equals(budgetMonth)) continue; if(period.equals("Today")&&!x.date().equals(now))continue; if(period.equals("Week")&&(x.date().isBefore(now.with(java.time.DayOfWeek.MONDAY))||x.date().isAfter(now.with(java.time.DayOfWeek.SUNDAY))))continue; int i=java.util.Arrays.asList(categories).indexOf(x.category()); totals[i<0?3:i]+=x.amount(); }
        double total=java.util.Arrays.stream(totals).sum();
        javafx.scene.canvas.Canvas chart=new javafx.scene.canvas.Canvas(344,180); chart.widthProperty().bind(content.widthProperty().subtract(46));
        Runnable draw=()->{var g=chart.getGraphicsContext2D(); double w=chart.getWidth(); g.clearRect(0,0,w,180); if(total==0){g.setFill(Color.WHITE);g.fillText("No expenses in this period",20,90);return;} double x=0; double end=36; for(int i=0;i<4;i++){if(totals[i]==0)continue; double len=(w-end)*totals[i]/total; Color c=Color.web(colors[i]); g.setFill(c.deriveColor(0,1,1,.66)); g.fillRoundRect(x,16,len+end,148,end,148); g.setStroke(c.deriveColor(0,1,1,.85));g.setLineWidth(1.5);g.strokeOval(x,16,end,148); x+=len;}}; chart.widthProperty().addListener((o,a,b)->draw.run()); draw.run();
        VBox legend=new VBox(13); legend.getStyleClass().add("expense-glass");
        for(int i=0;i<4;i++){Label swatch=new Label(" "); swatch.setStyle("-fx-background-color: "+colors[i]+"; -fx-background-radius: 7; -fx-min-width: 16; -fx-min-height: 22;"); Label name=welcomeLabel(categories[i],"subtext"); Label value=welcomeLabel(money(totals[i])+" • "+String.format(Locale.US,"%.1f%%",total==0?0:totals[i]/total*100),"expense-legend-value"); HBox row=new HBox(7,swatch,new VBox(name,value));row.setAlignment(Pos.CENTER_LEFT);legend.getChildren().add(row);}
        Region gap=new Region(); VBox.setVgrow(gap,Priority.ALWAYS);
        boolean safe=budgetReport.getRemainingBudget()>=0; Label icon=new Label(safe?"✓":"!"); icon.setStyle("-fx-font-size: 23; -fx-text-fill: "+(safe?"#42db61":"#ffba68")+";");
        HBox status=new HBox(8,icon,new VBox(welcomeLabel(safe?"Within Safe Budget Cap":"Budget Cap Exceeded","activity-name"),welcomeLabel("Remaining: "+money(budgetReport.getRemainingBudget())+" / "+Math.round(budget > 0 ? (budget - spent) / budget * 100 : 0)+"% left","activity-date")));status.setAlignment(Pos.CENTER_LEFT);status.getStyleClass().add("expense-glass");
        content.getChildren().addAll(line,remaining,capRow,line2,filters,chart,legend,gap,status);
        ScrollPane scroll=new ScrollPane(content);scroll.setFitToWidth(true);scroll.setFitToHeight(true);scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);scroll.getStyleClass().add("transparent-scroll"); VBox wrapper=new VBox(scroll);VBox.setVgrow(scroll,Priority.ALWAYS);return wrapper;
    }
    private boolean marketDeck = true;
    private VBox buildPrices() {
        Button personal = new Button("My Products");
        Button market = new Button("Albay Market Prices");
        personal.getStyleClass().addAll("deck-tab", marketDeck ? "deck-inactive" : "deck-active");
        market.getStyleClass().addAll("deck-tab", marketDeck ? "deck-active" : "deck-inactive");
        personal.setMaxWidth(Double.MAX_VALUE); market.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(personal, Priority.ALWAYS); HBox.setHgrow(market, Priority.ALWAYS);
        HBox tabs = new HBox(8, personal, market); tabs.setPadding(new Insets(12,18,0,18));
        StackPane deck = new StackPane(marketDeck ? buildMarketPrices() : buildPersonalPrices());
        personal.setOnAction(e -> { marketDeck=false; refreshScreen(); });
        market.setOnAction(e -> { marketDeck=true; refreshScreen(); });
        VBox wrapper = new VBox(6,tabs,deck); VBox.setVgrow(deck,Priority.ALWAYS); return wrapper;
    }
    private VBox buildMarketPrices() {
        VBox content = new VBox(8); content.setPadding(new Insets(12,0,0,0));
        VBox summary = new VBox(5); summary.getStyleClass().add("tracker-summary");
        Label title=welcomeLabel("COMMODITY TRACKER", "eyebrow");
        Label count=welcomeLabel("0 Market Records", "tracker-count");
        Label subtitle=welcomeLabel("Local price vs. Bicol benchmark", "subtext");
        HBox stats=new HBox(8);
        summary.getChildren().addAll(title,count,subtitle,stats); VBox.setMargin(summary,new Insets(0,18,0,18));
        TextField search=new TextField(); search.setPromptText("Search Products..."); search.getStyleClass().add("product-search");
        ComboBox<String> market=new ComboBox<>(); market.getItems().addAll("All markets","Legazpi City","Ligao City"); market.setValue("All markets");
        ComboBox<String> filter=new ComboBox<>(); filter.getItems().addAll("All prices","Above benchmark","Below benchmark","At benchmark"); filter.setValue("All prices");
        HBox controls=new HBox(6,market,filter); market.setMaxWidth(Double.MAX_VALUE); filter.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(market,Priority.ALWAYS); HBox.setHgrow(filter,Priority.ALWAYS);
        VBox tools=new VBox(6,search,controls); VBox.setMargin(tools,new Insets(0,18,4,18));
        VBox panel=new VBox(10); panel.getStyleClass().add("activity-panel");
        Label heading=welcomeLabel("Commodity Prices", "section-title");
        Button all=new Button("View All"); all.getStyleClass().add("plain-action"); all.setOnAction(e->{search.clear();market.setValue("All markets");filter.setValue("All prices");});
        Region spacer=new Region(); HBox.setHgrow(spacer,Priority.ALWAYS); HBox head=new HBox(heading,spacer,all); head.setAlignment(Pos.CENTER_LEFT);
        VBox rows=new VBox(12); Label note=welcomeLabel("Snapshot: Nov 9, 2026 • Not live prices", "subtext"); note.setWrapText(true);
        panel.getChildren().addAll(head,note,rows); VBox.setVgrow(panel,Priority.ALWAYS);
        content.getChildren().addAll(summary,tools,panel);
        try {
            var quotes=new com.presyotrack.service.CommodityDAO().load();
            Runnable render=()->{
                var selected=quotes.stream().filter(q->market.getValue().equals("All markets")||q.market().startsWith(market.getValue())).toList();
                long up=selected.stream().filter(q->q.difference()>0).count(), down=selected.stream().filter(q->q.difference()<0).count();
                count.setText(selected.size()+" Market Records");
                stats.getChildren().setAll(statCard("⊙","Tracked:",""+selected.size()),statCard("↗","Above:",up+" ▴"),statCard("↘","Below:",down+" ▾"));
                rows.getChildren().clear(); String query=search.getText().toLowerCase(Locale.ROOT);
                for(var q:selected) {
                    if(!(q.name()+" "+q.category()).toLowerCase(Locale.ROOT).contains(query)) continue;
                    if(!switch(filter.getValue()){case "Above benchmark"->q.difference()>0;case "Below benchmark"->q.difference()<0;case "At benchmark"->q.difference()==0;default->true;}) continue;
                    Label icon=welcomeLabel("◇","product-icon"); Label name=welcomeLabel(q.name(),"activity-name"); name.setWrapText(true);
                    Label meta=welcomeLabel(q.market().split(" Public")[0]+" • "+q.unit(),"activity-date");
                    Label ref=welcomeLabel("Ref: "+money(q.benchmark()),"activity-date");
                    VBox details=new VBox(name,meta,ref); HBox.setHgrow(details,Priority.ALWAYS); details.setMinWidth(0);
                    double d=q.difference(); Label price=welcomeLabel(money(q.price()),"activity-amount");
                    Label change=welcomeLabel((d>0?"+":d<0?"-":"")+money(Math.abs(d))+(d>0?" ▴":d<0?" ▾":""),"price-change"); change.getStyleClass().add(d>0?"market-above":d<0?"market-below":"unchanged");
                    VBox amounts=new VBox(price,change); amounts.setAlignment(Pos.CENTER_RIGHT); HBox row=new HBox(8,icon,details,amounts); row.setAlignment(Pos.CENTER_LEFT); rows.getChildren().add(row);
                }
                if(rows.getChildren().isEmpty()) rows.getChildren().add(welcomeLabel("No matching market prices","subtext"));
            };
            search.textProperty().addListener((o,a,b)->render.run()); market.valueProperty().addListener((o,a,b)->render.run()); filter.valueProperty().addListener((o,a,b)->render.run()); render.run();
        } catch(Exception ex) { Label error=welcomeLabel("Unable to load market prices: "+ex.getMessage(),"subtext"); error.setWrapText(true); rows.getChildren().add(error); }
        ScrollPane scroll=new ScrollPane(content); scroll.setFitToWidth(true); scroll.setFitToHeight(true); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll.getStyleClass().add("transparent-scroll");
        VBox wrapper=new VBox(scroll); VBox.setVgrow(scroll,Priority.ALWAYS); return wrapper;
    }
    private VBox buildPersonalPrices() {
        VBox content = new VBox(6); content.setPadding(new Insets(42,0,0,0));
        VBox summary = new VBox(5); summary.getStyleClass().add("tracker-summary");
        Label title = new Label("COMMODITY TRACKER"); title.getStyleClass().add("eyebrow");
        Label count = new Label((products.size())+" Tracked Items"); count.getStyleClass().add("tracker-count");
        Label subtitle = new Label("Benchmark vs Prior Market Price"); subtitle.getStyleClass().add("subtext");
        long up=products.stream().filter(x->x.price()>x.prior()).count();
        long down=products.stream().filter(x->x.price()<x.prior()).count();
        HBox stats = new HBox(8,statCard("⊙","Tracked:",String.valueOf(products.size())),statCard("↗","Price Increase:",up+" ▴"),statCard("↘","Price Decrease:",down+" ▾"));
        summary.getChildren().addAll(title,count,subtitle,stats); VBox.setMargin(summary,new Insets(0,18,0,18));
        TextField search = new TextField(); search.setPromptText("Search Products..."); search.getStyleClass().add("product-search"); VBox.setMargin(search,new Insets(0,18,19,18));
        VBox panel = new VBox(12); panel.getStyleClass().add("activity-panel");
        Label heading=new Label("Commodity Prices"); heading.getStyleClass().add("section-title");
        Region space=new Region(); HBox.setHgrow(space,Priority.ALWAYS);
        Button all=new Button("View All"); all.getStyleClass().add("plain-action"); all.setOnAction(e->search.clear());
        Button addProduct=new Button("Add product"); addProduct.getStyleClass().add("plain-action"); addProduct.setOnAction(e->editProduct(null)); HBox head=new HBox(heading,space,addProduct,all); head.setAlignment(Pos.CENTER_LEFT);
        Region line=new Region(); line.getStyleClass().add("separator");
        VBox rows=new VBox(12);
        Runnable render=()->{ rows.getChildren().clear(); String q=search.getText().toLowerCase(Locale.ROOT); for(Product x:products) if((x.name()+" "+x.category()).toLowerCase(Locale.ROOT).contains(q)) rows.getChildren().add(productRow(x)); if(rows.getChildren().isEmpty()){Label empty=new Label("No matching products");empty.getStyleClass().add("subtext");rows.getChildren().add(empty);} };
        search.textProperty().addListener((o,a,b)->render.run()); render.run();
        panel.getChildren().addAll(head,line,rows); VBox.setVgrow(panel,Priority.ALWAYS);
        content.getChildren().addAll(summary,search,panel);
        ScrollPane scroll=new ScrollPane(content); scroll.setFitToWidth(true); scroll.setFitToHeight(true); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll.getStyleClass().add("transparent-scroll");
        VBox wrapper=new VBox(scroll); VBox.setVgrow(scroll,Priority.ALWAYS); return wrapper;
    }
    private HBox productRow(Product x) {
        Label icon=new Label(switch(x.name()){case "Coffee Beans"->"♨";case "Dozen of Eggs"->"◯";case "Cooking Oil (1L)"->"◩";case "Sliced Bread"->"▣";default->"◇";}); icon.getStyleClass().add("product-icon");
        Label name=new Label(x.name()); name.getStyleClass().add("activity-name"); Label cat=new Label(x.category()); cat.getStyleClass().add("activity-date"); VBox details=new VBox(name,cat); HBox.setHgrow(details,Priority.ALWAYS);
        Label price=new Label(money(x.price())); price.getStyleClass().add("activity-amount"); double delta=x.price()-x.prior();
        Label change=new Label((delta>0?"+":delta<0?"-":"")+money(Math.abs(delta))+(delta>0?" ▴":delta<0?" ▾":"")); change.getStyleClass().addAll("price-change",delta>0?"increase":delta<0?"decrease":"unchanged");
        VBox amounts=new VBox(price,change); amounts.setAlignment(Pos.CENTER_RIGHT);
        Button edit=new Button("✎"); edit.getStyleClass().add("plain-action"); edit.setAccessibleText("Edit "+x.name()); edit.setOnAction(e->editProduct(x));
        Button delete=new Button("×"); delete.getStyleClass().add("plain-action"); delete.setAccessibleText("Delete "+x.name()); delete.setOnAction(e->{Alert confirm=new Alert(Alert.AlertType.CONFIRMATION,"Delete "+x.name()+"?",ButtonType.YES,ButtonType.NO); if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
    productManager.removeProduct(x.name());
    productManager.saveData();

    products.remove(x);
    refreshScreen();
}});
        HBox row=new HBox(8,icon,details,amounts,edit,delete); row.setAlignment(Pos.CENTER_LEFT); return row;
    }
    private void editProduct(Product old) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(old == null ? "Add product" : "Edit product");
        d.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        TextField name = new TextField(old == null ? "" : old.name());
        TextField category = new TextField(old == null ? "" : old.category());
        TextField price = new TextField(
                old == null ? "" : String.valueOf(old.price())
        );
        TextField prior = new TextField(
                old == null ? "" : String.valueOf(old.prior())
        );

        d.getDialogPane().setContent(new VBox(
                8,
                new Label("Product name"), name,
                new Label("Category"), category,
                new Label("Current price (PHP)"), price,
                new Label("Prior price (PHP)"), prior
        ));

        d.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                try {
                    double a = Double.parseDouble(price.getText().trim());
                    double c = Double.parseDouble(prior.getText().trim());

                    if (name.getText().isBlank()
                            || !Double.isFinite(a)
                            || !Double.isFinite(c)
                            || a < 0 || c < 0) {
                        throw new IllegalArgumentException();
                    }

                    String categoryValue = category.getText().isBlank()
                            ? "Others / Uncategorized"
                            : category.getText().trim();

                    if (old == null) {
                        com.presyotrack.model.FoodProduct newProduct =
                                new com.presyotrack.model.FoodProduct(
                                        name.getText().trim(),
                                        categoryValue,
                                        a,
                                        c
                                );

                        productManager.addProduct(newProduct);
                        productManager.saveData();

                        products.add(new Product(
                                newProduct.getName(),
                                newProduct.getCategory(),
                                newProduct.getCurrentPrice(),
                                newProduct.getPreviousPrice()
                        ));

                    } else {
                        com.presyotrack.model.Product backendProduct =
                                productManager.searchProduct(old.name());

                        if (backendProduct == null) {
                            throw new IllegalArgumentException(
                                    "Could not find this product in saved data."
                            );
                        }

                        double oldCurrentPrice =
                                backendProduct.getCurrentPrice();

                        boolean priceChanged =
                                Double.compare(oldCurrentPrice, a) != 0;

                        backendProduct.setName(name.getText().trim());
                        backendProduct.setCategory(categoryValue);

                        if (priceChanged) {
                            backendProduct.setPreviousPrice(oldCurrentPrice);
                            backendProduct.setCurrentPrice(a);

                            priceTracker.recordPrice(backendProduct);
                            priceTracker.saveData();
                        } else {
                            backendProduct.setPreviousPrice(c);
                            backendProduct.setCurrentPrice(a);
                        }

                        productManager.saveData();

                        products.set(products.indexOf(old), new Product(
                                backendProduct.getName(),
                                backendProduct.getCategory(),
                                backendProduct.getCurrentPrice(),
                                backendProduct.getPreviousPrice()
                        ));
                    }

                    refreshScreen();

                } catch (IllegalArgumentException ex) {
                    alert(
                            "Invalid product",
                            ex.getMessage() == null
                                    ? "Enter a name and valid nonnegative prices."
                                    : ex.getMessage()
                    );
                }
            }
        });
    }
    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
