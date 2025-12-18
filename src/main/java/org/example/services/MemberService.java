package org.example.services;

import org.example.entity.member.Member;
import org.example.registry.MemberRegistry;

import java.io.IOException;
import java.util.List;

public final class MemberService {
    public static void addNewMember(Member newMember) throws IOException {
        MemberRegistry reg = new MemberRegistry();
        reg.read();
        reg.add(newMember);
        reg.write();
    }

    public static void updateMember(Member member) throws IOException {
        MemberRegistry reg = new MemberRegistry();
        reg.read();
        reg.set(member);
        reg.write();
    }

    public static void removeMember(Member member) throws IOException {
        MemberRegistry reg = new MemberRegistry();
        reg.read();
        reg.remove(member.getId());
        reg.write();
    }

    public static List<Member> readAsList() throws IOException {
        MemberRegistry reg = new MemberRegistry();
        reg.read();

        return reg.getAll();
    }

    public static Member getMember(long id) throws IOException {
        MemberRegistry reg = new MemberRegistry();
        reg.read();

        return reg.get(id);
    }
}
