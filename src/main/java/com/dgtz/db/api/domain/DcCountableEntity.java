package com.dgtz.db.api.domain;


/**
 * Created by sardor on 1/9/14.
 */

public class DcCountableEntity {
    private long idMedia;
    private Long amountType;
    private Long amount;

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }


    public Long getAmountType() {
        return amountType;
    }

    public void setAmountType(Long amountType) {
        this.amountType = amountType;
    }


    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcCountableEntity that = (DcCountableEntity) o;

        if (idMedia != that.idMedia) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (amountType != null ? !amountType.equals(that.amountType) : that.amountType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idMedia ^ (idMedia >>> 32));
        result = 31 * result + (amountType != null ? amountType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
