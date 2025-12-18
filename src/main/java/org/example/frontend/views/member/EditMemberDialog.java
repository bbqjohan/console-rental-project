package org.example.frontend.views.member;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import org.example.entity.member.Member;
import org.example.entity.member.MemberStatus;
import org.example.frontend.components.ErrorAlert;
import org.example.services.MemberService;

import java.io.IOException;
import java.util.Arrays;

public class EditMemberDialog extends Dialog<Boolean> {
    private final SimpleStringProperty firstname;
    private final SimpleStringProperty lastname;
    private final SimpleStringProperty age;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phone;
    private final SimpleObjectProperty<MemberStatus> memberStatus;
    private final SimpleStringProperty firstnameError;
    private final SimpleStringProperty lastnameError;
    private final SimpleStringProperty ageError;
    private final SimpleStringProperty emailError;
    private final SimpleStringProperty phoneError;
    private final SimpleStringProperty memberStatusError;
    private final Window window;
    private final Member member;

    public EditMemberDialog(Window window, Member member) {
        super();
        this.member = member.copy();
        this.firstname = new SimpleStringProperty(this.member.getFirstname());
        this.lastname = new SimpleStringProperty(this.member.getLastname());
        this.age = new SimpleStringProperty(String.valueOf(this.member.getAge()));
        this.email = new SimpleStringProperty(this.member.getEmail());
        this.phone = new SimpleStringProperty(this.member.getPhone());
        this.memberStatus = new SimpleObjectProperty<>(this.member.getStatus());
        this.firstnameError = new SimpleStringProperty("");
        this.lastnameError = new SimpleStringProperty("");
        this.ageError = new SimpleStringProperty("");
        this.emailError = new SimpleStringProperty("");
        this.phoneError = new SimpleStringProperty("");
        this.memberStatusError = new SimpleStringProperty("");
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Redigera medlem");
        formTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold");

        VBox root = new VBox();
        root.getChildren().addAll(formTitle, createForm());
        root.setSpacing(20);
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        ButtonType saveBtn = new ButtonType("Spara", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Redigera medlem.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(saveBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (ev) -> {
                            if (!validateForm()) {
                                ev.consume();
                                return;
                            }

                            this.member.setFirstname(firstname.get().replaceAll("\\s", ""));
                            this.member.setLastname(lastname.get().replaceAll("\\s", ""));
                            this.member.setPhone(phone.get().replaceAll("\\s", ""));
                            this.member.setEmail(email.get().replaceAll("\\s", ""));
                            this.member.setAge(Integer.parseInt(age.get()));
                            this.member.setStatus(memberStatus.get());

                            try {
                                MemberService.updateMember(this.member);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte redigera medlem.",
                                                "Ett fel inträffade och medlemmen kunde inte sparas.",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == saveBtn);
    }

    private Pane createForm() {
        Label firstnameLbl = new Label("Förnamn");
        TextField firstnameFld = new TextField();
        firstnameFld.textProperty().bindBidirectional(firstname);
        firstnameFld.setPrefWidth(300);
        Label firstnameErrorLbl = createErrorLabel(firstnameError);

        Label lastnameLbl = new Label("Efternamn");
        TextField lastnameFld = new TextField();
        lastnameFld.textProperty().bindBidirectional(lastname);
        Label lastnameErrorLbl = createErrorLabel(lastnameError);

        Label ageLbl = new Label("Ålder");
        TextField ageFld = new TextField();
        ageFld.textProperty().bindBidirectional(age);
        Label ageErrorLbl = createErrorLabel(ageError);

        Label emailLbl = new Label("Epost");
        TextField emailFld = new TextField();
        emailFld.textProperty().bindBidirectional(email);
        Label emailErrorLbl = createErrorLabel(emailError);

        Label phoneLbl = new Label("Telefon");
        TextField phoneFld = new TextField();
        phoneFld.textProperty().bindBidirectional(phone);
        Label phoneErrorLbl = createErrorLabel(phoneError);

        Label memberStatusLbl = new Label("Status");
        ComboBox<MemberStatus> memberStatusFld =
                new ComboBox<>(FXCollections.observableList(Arrays.asList(MemberStatus.values())));
        memberStatusFld.setConverter(
                new StringConverter<MemberStatus>() {
                    @Override
                    public String toString(MemberStatus status) {
                        return status.toLabel();
                    }

                    @Override
                    public MemberStatus fromString(String s) {
                        switch (s) {
                            case "Casual":
                                return MemberStatus.CASUAL;
                            case "Gamer":
                                return MemberStatus.GAMER;
                            default:
                                return null;
                        }
                    }
                });
        memberStatusFld.valueProperty().bindBidirectional(memberStatus);
        memberStatusFld.setMaxWidth(Double.MAX_VALUE);
        Label memberStatusError = createErrorLabel(this.memberStatusError);

        VBox root = new VBox();
        GridPane theOneGrid = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);
        theOneGrid.getColumnConstraints().add(0, cc);

        theOneGrid.setHgap(10);
        theOneGrid.setVgap(5);
        theOneGrid.add(firstnameLbl, 0, 0);
        theOneGrid.add(firstnameFld, 1, 0);
        theOneGrid.add(firstnameErrorLbl, 1, 1);

        theOneGrid.add(lastnameLbl, 0, 2);
        theOneGrid.add(lastnameFld, 1, 2);
        theOneGrid.add(lastnameErrorLbl, 1, 3);

        theOneGrid.add(ageLbl, 0, 4);
        theOneGrid.add(ageFld, 1, 4);
        theOneGrid.add(ageErrorLbl, 1, 5);

        theOneGrid.add(phoneLbl, 0, 6);
        theOneGrid.add(phoneFld, 1, 6);
        theOneGrid.add(phoneErrorLbl, 1, 7);

        theOneGrid.add(emailLbl, 0, 8);
        theOneGrid.add(emailFld, 1, 8);
        theOneGrid.add(emailErrorLbl, 1, 9);

        theOneGrid.add(memberStatusLbl, 0, 10);
        theOneGrid.add(memberStatusFld, 1, 10);
        theOneGrid.add(memberStatusError, 1, 11);

        root.getChildren().add(theOneGrid);

        return root;
    }

    private Label createErrorLabel(SimpleStringProperty stringProp) {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-padding: 0 0 5 0;");
        label.textProperty().bind(stringProp);
        label.managedProperty().bind(stringProp.isNotEmpty());
        label.setWrapText(true);

        return label;
    }

    private boolean validateFirstname() {
        if (this.firstname.get().isEmpty()) {
            this.firstnameError.set("Vänligen fyll i ett förnamn.");
            return false;
        }

        if (!this.firstname.get().matches("^\\p{L}+(-\\p{L}+)?$")) {
            this.firstnameError.set("Inga siffror, mellanslag eller specialtecken är tillåtna.");
            return false;
        }

        this.firstnameError.set("");

        return true;
    }

    private boolean validateLastname() {
        if (this.lastname.get().isEmpty()) {
            this.lastnameError.set("Vänligen fyll i ett efternamn.");
            return false;
        }

        if (!this.lastname.get().matches("^\\p{L}+(-\\p{L}+)?$")) {
            this.lastnameError.set("Inga siffror, mellanslag eller specialtecken är tillåtna.");
            return false;
        }

        this.lastnameError.set("");

        return true;
    }

    private boolean validateAge() {
        String age = this.age.get();

        try {
            if (age.isEmpty() || Integer.parseInt(age) < 18) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.ageError.set("Du måste var minst 18 år gammal.");
            return false;
        }

        this.ageError.set("");

        return true;
    }

    private boolean validateEmail() {
        if (this.email.get().isEmpty()) {
            this.emailError.set("Vänligen fyll i en mejladdress.");
            return false;
        }

        if (!this.email.get().matches("[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+\\.\\w+")) {
            this.emailError.set("Ogiltigt e-post format. Exempel: karin.johansson@gmail.com");
            return false;
        }

        this.emailError.set("");

        return true;
    }

    private boolean validatePhone() {
        if (this.phone.get().isEmpty()) {
            this.phoneError.set("Vänligen fyll i ett telefonnummer.");
            return false;
        }

        if (!this.phone.get().replaceAll("\\s", "").matches("\\d{9}|\\d{10}")) {
            this.phoneError.set("Ogiltigt telefonnummer.");
            return false;
        }

        this.phoneError.set("");

        return true;
    }

    private boolean validateMemberStatus() {
        if (this.memberStatus.get() == null) {
            this.memberStatusError.set("Vänligen välj en status för medlemmen.");
            return false;
        }

        this.memberStatusError.set("");

        return true;
    }

    private boolean validateForm() {
        boolean fname = validateFirstname();
        boolean lname = validateLastname();
        boolean age = validateAge();
        boolean phone = validatePhone();
        boolean email = validateEmail();
        boolean memberStatus = validateMemberStatus();

        return fname && lname && age && phone && email && memberStatus;
    }
}
