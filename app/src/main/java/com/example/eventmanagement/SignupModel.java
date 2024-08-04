package com.example.eventmanagement;

public class SignupModel {

    public SignupModel(String name, String number, String email, String address, String cnic, String company) {
        this.owner_name = name;
        this.number = number;
        this.email = email;
        this.owner_cnic = cnic;
        this.company_address = address;
        this.company_name = company;
    }

    public SignupModel() {
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String name) {
        this.owner_name = owner_name;
    }

    public String getNumber() {
        return number;
    }
    public String getEmail() {
        return email;
    }

    public String getCompany_address() {
        return company_address;
    }

    public String getOwner_cnic() {
        return owner_cnic;
    }

    public String getCompany_name() {
        return company_name;
    }



    String owner_name, number, email, company_address, owner_cnic, company_name;
}
