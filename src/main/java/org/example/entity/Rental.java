package org.example.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import org.example.entity.member.MemberStatus;
import org.example.registry.table.Entity;

import java.time.LocalDate;

public class Rental extends Entity<Rental> {
    private long memberId;
    private long itemId;
    private MemberStatus memberStatus;
    private boolean active;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate stopDate;

    public Rental() {}

    public Rental(
            long id,
            long memberId,
            long itemId,
            MemberStatus memberStatus,
            boolean active,
            LocalDate startDate,
            LocalDate stopDate) {
        super(id);
        this.memberId = memberId;
        this.itemId = itemId;
        this.memberStatus = memberStatus;
        this.active = active;
        this.startDate = startDate;
        this.stopDate = stopDate;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate stopDate) {
        this.stopDate = stopDate;
    }

    @Override
    public Rental copy() {
        return new Rental(
                getId(),
                getMemberId(),
                getItemId(),
                getMemberStatus(),
                isActive(),
                getStartDate(),
                getStopDate());
    }
}
