package org.example.frontend.views.member;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;

import org.example.entity.member.Member;
import org.example.frontend.components.ErrorAlert;
import org.example.services.MemberService;

public class AddMemberDialog extends Dialog<Boolean> {
    private final SimpleObjectProperty<Member> member;
    private final MemberForm form;
    private final Window window;

    public AddMemberDialog(Window window) {
        super();
        this.member = new SimpleObjectProperty<>();
        this.form = new MemberForm(this.member);
        this.window = window;

        createDialog();
    }

    private void createDialog() {
        Label formTitle = new Label("Ny medlem");
        formTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold");

        VBox root = new VBox();
        root.getChildren().addAll(formTitle, form);
        root.setSpacing(20);
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        ButtonType saveBtn = new ButtonType("Lägg till", ButtonBar.ButtonData.OK_DONE);

        this.setTitle("Skapa ny medlem.");
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
                            if (!form.validateForm()) {
                                ev.consume();
                                return;
                            }

                            try {
                                MemberService.addNewMember(this.member.get());
                            } catch (Exception ex) {
                                ev.consume();

                                new ErrorAlert(
                                                this.window,
                                                "Kunde inte skapa medlem.",
                                                "Ett fel inträffade och medlemmen kunde inte skapas.",
                                                ex)
                                        .showAndWait();
                            }
                        });
        this.setResultConverter((pressedBtn) -> pressedBtn == saveBtn);
    }
}
