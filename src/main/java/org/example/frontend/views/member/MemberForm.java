package org.example.frontend.views.member;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import org.example.entity.member.Member;
import org.example.entity.member.MemberStatus;

import java.util.Arrays;

public class MemberForm extends VBox {
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
    private final ObjectBinding<Member> member;

    public MemberForm(SimpleObjectProperty<Member> member) {
        this.firstname = new SimpleStringProperty("");
        this.lastname = new SimpleStringProperty("");
        this.age = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.memberStatus = new SimpleObjectProperty<>(null);
        this.firstnameError = new SimpleStringProperty("");
        this.lastnameError = new SimpleStringProperty("");
        this.ageError = new SimpleStringProperty("");
        this.emailError = new SimpleStringProperty("");
        this.phoneError = new SimpleStringProperty("");
        this.memberStatusError = new SimpleStringProperty("");
        this.member =
                new ObjectBinding<Member>() {
                    {
                        super.bind(firstname, lastname, age, email, phone, memberStatus);
                    }

                    @Override
                    protected Member computeValue() {
                        return toMember();
                    }
                };

        this.member.addListener(
                (m, o, n) -> {
                    System.out.println("Change");
                    member.set(n);
                });

        createForm();
    }

    public Member toMember() {
        if (!isFormValid()) {
            return null;
        }

        Member member = new Member();
        member.setFirstname(firstname.get().replaceAll("\\s", ""));
        member.setLastname(lastname.get().replaceAll("\\s", ""));
        member.setPhone(phone.get().replaceAll("\\s", ""));
        member.setEmail(email.get().replaceAll("\\s", ""));
        member.setAge(Integer.parseInt(age.get()));
        member.setStatus(memberStatus.get());

        return member;
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

        this.getChildren().add(theOneGrid);

        return this;
    }

    private Label createErrorLabel(SimpleStringProperty stringProp) {
        Label label = new Label();
        label.setStyle("-fx-text-fill: red; -fx-padding: 0 0 5 0;");
        label.textProperty().bind(stringProp);
        label.managedProperty().bind(stringProp.isNotEmpty());
        label.setWrapText(true);

        return label;
    }

    private boolean isFirstnameNotEmpty() {
        return !this.firstname.get().isEmpty();
    }

    private boolean doesFirstnameMatch() {
        return this.firstname.get().matches("^\\p{L}+(-\\p{L}+)?$");
    }

    private boolean isFirstnameValid() {
        return this.isFirstnameNotEmpty() && doesFirstnameMatch();
    }

    private boolean validateFirstname() {
        if (!isFirstnameNotEmpty()) {
            this.firstnameError.set("Vänligen fyll i ett förnamn.");
            return false;
        }

        if (!doesFirstnameMatch()) {
            this.firstnameError.set("Inga siffror, mellanslag eller specialtecken är tillåtna.");
            return false;
        }

        this.firstnameError.set("");

        return true;
    }

    private boolean isLastnameNotEmpty() {
        return !this.lastname.get().isEmpty();
    }

    private boolean doesLastnameMatch() {
        return this.lastname.get().matches("^\\p{L}+(-\\p{L}+)?$");
    }

    private boolean isLastnameValid() {
        return isLastnameNotEmpty() && doesLastnameMatch();
    }

    private boolean validateLastname() {
        if (!isLastnameNotEmpty()) {
            this.lastnameError.set("Vänligen fyll i ett efternamn.");
            return false;
        }

        if (!doesLastnameMatch()) {
            this.lastnameError.set("Inga siffror, mellanslag eller specialtecken är tillåtna.");
            return false;
        }

        this.lastnameError.set("");

        return true;
    }

    private boolean isAgeNotEmpty() {
        return !this.age.get().isEmpty();
    }

    private boolean doesAgeMatch() {
        try {
            return Integer.parseInt(this.age.get()) >= 18;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isAgeValid() {
        return isAgeNotEmpty() && doesAgeMatch();
    }

    private boolean validateAge() {
        String age = this.age.get();

        try {
            if (!isAgeValid()) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.ageError.set("Du måste var minst 18 år gammal.");
            return false;
        }

        this.ageError.set("");

        return true;
    }

    private boolean isEmailNotEmpty() {
        return !this.email.get().isEmpty();
    }

    private boolean doesEmailMatch() {
        return this.email.get().matches("[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+\\.\\w+");
    }

    private boolean isEmailValid() {
        return isAgeNotEmpty() && doesEmailMatch();
    }

    private boolean validateEmail() {
        if (!isEmailNotEmpty()) {
            this.emailError.set("Vänligen fyll i en mejladdress.");
            return false;
        }

        if (!doesEmailMatch()) {
            this.emailError.set("Ogiltigt e-post format. Exempel: karin.johansson@gmail.com");
            return false;
        }

        this.emailError.set("");

        return true;
    }

    private boolean isPhoneNotEmpty() {
        return !this.phone.get().isEmpty();
    }

    private boolean doesPhoneMatch() {
        return this.phone.get().replaceAll("\\s", "").matches("\\d{9}|\\d{10}");
    }

    private boolean isPhoneValid() {
        return isPhoneNotEmpty() && doesPhoneMatch();
    }

    private boolean validatePhone() {
        if (!isPhoneNotEmpty()) {
            this.phoneError.set("Vänligen fyll i ett telefonnummer.");
            return false;
        }

        if (!doesPhoneMatch()) {
            this.phoneError.set("Ogiltigt telefonnummer.");
            return false;
        }

        this.phoneError.set("");

        return true;
    }

    private boolean isMemberStatusNotEmpty() {
        return this.memberStatus.get() != null;
    }

    private boolean isMemberStatusValid() {
        return isMemberStatusNotEmpty();
    }

    private boolean validateMemberStatus() {
        if (!isMemberStatusNotEmpty()) {
            this.memberStatusError.set("Vänligen välj en status för medlemmen.");
            return false;
        }

        this.memberStatusError.set("");

        return true;
    }

    private boolean isFormValid() {
        boolean fname = isFirstnameValid();
        boolean lname = isLastnameValid();
        boolean age = isAgeValid();
        boolean phone = isPhoneValid();
        boolean email = isEmailValid();
        boolean memberStatus = isMemberStatusValid();

        return fname && lname && age && phone && email && memberStatus;
    }

    public boolean validateForm() {
        boolean fname = validateFirstname();
        boolean lname = validateLastname();
        boolean age = validateAge();
        boolean phone = validatePhone();
        boolean email = validateEmail();
        boolean memberStatus = validateMemberStatus();

        return fname && lname && age && phone && email && memberStatus;
    }
}
