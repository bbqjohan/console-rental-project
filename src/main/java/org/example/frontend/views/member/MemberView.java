package org.example.frontend.views.member;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.entity.member.Member;
import org.example.services.MemberService;

import java.util.ArrayList;
import java.util.List;

public class MemberView {
    private final Pane root;
    private final MembersTable membersTable;

    public MemberView() {
        this.membersTable = new MembersTable(new ArrayList<>());
        this.root = createRoot();
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Medlemmar");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        VBox root = new VBox(viewTitle, this.membersTable.getRoot());
        root.setSpacing(20);

        return root;
    }

    private List<Member> loadMembers() {
        List<Member> items;

        try {
            items = MemberService.readAsList();
        } catch (Exception e) {
            e.printStackTrace();
            items = new ArrayList<>();
        }

        return items;
    }

    public Pane render() {
        this.membersTable.setItems(loadMembers());

        return root;
    }
}
