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
        List<Member> memberList;

        try {
            memberList = MemberService.readAsList();
        } catch (Exception e) {
            memberList = new ArrayList<>();
        }

        this.membersTable = new MembersTable(memberList);
        this.root = createRoot();
    }

    private Pane createRoot() {
        Text viewTitle = new Text("Medlemmar");
        viewTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        VBox root = new VBox(viewTitle, this.membersTable.getRoot());
        root.setSpacing(20);

        return root;
    }

    public Pane render() {
        return root;
    }
}
