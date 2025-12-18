package org.example.registry;


import com.fasterxml.jackson.core.type.TypeReference;

import org.example.entity.member.Member;
import org.example.registry.table.EntityTable;

import java.io.IOException;
import java.util.List;

public final class MemberRegistry extends EntityTable<Member> {
    public MemberRegistry() {
        super("members.json");
    }

    @Override
    public void read() throws IOException {
        update(read(new TypeReference<List<Member>>() {}));
    }
}
