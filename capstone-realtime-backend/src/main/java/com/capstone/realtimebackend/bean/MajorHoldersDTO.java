package com.capstone.realtimebackend.bean;

import java.util.List;

public class MajorHoldersDTO {
    private MajorHolders majorHolders;
    private List<InstHolder> instHolders;
    private List<MtlfdHolder> mtlfdHolders;

    public MajorHolders getMajorHolders() {
        return majorHolders;
    }

    public void setMajorHolders(MajorHolders majorHolders) {
        this.majorHolders = majorHolders;
    }

    public List<InstHolder> getInstHolders() {
        return instHolders;
    }

    public void setInstHolders(List<InstHolder> instHolders) {
        this.instHolders = instHolders;
    }

    public List<MtlfdHolder> getMtlfdHolders() {
        return mtlfdHolders;
    }

    public void setMtlfdHolders(List<MtlfdHolder> mtlfdHolders) {
        this.mtlfdHolders = mtlfdHolders;
    }
}
