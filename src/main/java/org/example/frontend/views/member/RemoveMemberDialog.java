package org.example.frontend.views.member;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Window;

import org.example.entity.member.Member;
import org.example.frontend.components.ErrorAlert;
import org.example.services.MemberService;

import java.io.IOException;

public class RemoveMemberDialog extends Dialog<Boolean> {
    private final Window window;
    private final Member member;

    public RemoveMemberDialog(Window window, Member member) {
        super();
        this.member = member.copy();
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Radera medlem");
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

        ButtonType deleteBtn = new ButtonType("Radera", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Radera medlem.");
        this.setHeaderText(null);
        this.initOwner(this.window);
        this.setResizable(true);
        this.getDialogPane().setContent(scrollPane);
        this.getDialogPane().getButtonTypes().addAll(deleteBtn, ButtonType.CANCEL);
        this.getDialogPane()
                .lookupButton(deleteBtn)
                .addEventFilter(
                        ActionEvent.ACTION,
                        (ev) -> {
                            try {
                                MemberService.removeMember(this.member);
                            } catch (IOException ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte radera medlem.",
                                                "Ett fel inträffade och medlemmen kunde inte raderas.",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == deleteBtn);
    }

    private Pane createForm() {
        Label idLbl = new Label("ID");
        Text idTxt = new Text(String.valueOf(this.member.getId()));

        Label firstnameLbl = new Label("Förnamn");
        Text firstnameTxt = new Text(this.member.getFirstname());

        Label lastnameLbl = new Label("Efternamn");
        Text lastnameTxt = new Text(this.member.getLastname());

        Label ageLbl = new Label("Ålder");
        Text ageTxt = new Text(String.valueOf(this.member.getAge()));

        Label emailLbl = new Label("Epost");
        Text emailTxt = new Text(this.member.getEmail());

        Label phoneLbl = new Label("Telefon");
        Text phoneTxt = new Text(this.member.getPhone());

        VBox root = new VBox();
        GridPane theOneGrid = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(60);
        theOneGrid.getColumnConstraints().add(0, cc);

        theOneGrid.setHgap(10);
        theOneGrid.setVgap(5);

        theOneGrid.add(idLbl, 0, 0);
        theOneGrid.add(idTxt, 1, 0);

        theOneGrid.add(firstnameLbl, 0, 1);
        theOneGrid.add(firstnameTxt, 1, 1);

        theOneGrid.add(lastnameLbl, 0, 2);
        theOneGrid.add(lastnameTxt, 1, 2);

        theOneGrid.add(ageLbl, 0, 3);
        theOneGrid.add(ageTxt, 1, 3);

        theOneGrid.add(phoneLbl, 0, 4);
        theOneGrid.add(phoneTxt, 1, 4);

        theOneGrid.add(emailLbl, 0, 5);
        theOneGrid.add(emailTxt, 1, 5);

        root.getChildren().add(theOneGrid);

        return root;
    }
}
