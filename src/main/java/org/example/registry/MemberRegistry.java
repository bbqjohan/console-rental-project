package org.example.registry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entity.member.Member;
import org.example.registry.table.EntityTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public final class MemberRegistry extends EntityTable<Member> {
    public MemberRegistry() {
        super("members.json");
    }

    @Override
    public void read() throws IOException {
        List<Member> file;

        try {
            file =
                    new ObjectMapper()
                            .readValue(
                                    new File(getTablePath()), new TypeReference<List<Member>>() {});

        } catch (FileNotFoundException e) {
            throw new IOException("Could not find members registry file: " + getTablePath(), e);
        } catch (IOException e) {
            throw new IOException(
                    "Failed to convert data to Member objects from registry file: "
                            + getTablePath()
                            + "\n\nIllegal JSON structure/data.",
                    e);
        }

        update(file);
    }
}
