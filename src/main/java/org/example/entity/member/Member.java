package org.example.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.example.entity.price.CasualPrice;
import org.example.entity.price.GamerPrice;
import org.example.entity.price.PricePolicy;
import org.example.registry.table.Entity;

public class Member extends Entity<Member> {
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private MemberStatus status;
    private int age;

    public Member() {}

    public Member(
            long id,
            String firstname,
            String lastname,
            String phone,
            String email,
            MemberStatus status,
            int age) {
        super(id);
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.age = age;
    }

    @Override
    public Member copy() {
        return new Member(
                getId(),
                getFirstname(),
                getLastname(),
                getPhone(),
                getEmail(),
                getStatus(),
                getAge());
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public PricePolicy getPricePolicy() {
        return this.status.equals(MemberStatus.CASUAL) ? new CasualPrice() : new GamerPrice();
    }

    @JsonIgnore
    public String getFullName() {
        return this.firstname + " " + this.lastname;
    }
}
