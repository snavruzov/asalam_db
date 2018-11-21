package com.dgtz.db.api.domain;


public class DcAuthoritiesEntity {
    private String email;
    private String authority;
    private long idAuthority;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }


    public long getIdAuthority() {
        return idAuthority;
    }

    public void setIdAuthority(long idAuthority) {
        this.idAuthority = idAuthority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcAuthoritiesEntity that = (DcAuthoritiesEntity) o;

        if (idAuthority != that.idAuthority) return false;
        if (authority != null ? !authority.equals(that.authority) : that.authority != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        result = 31 * result + (int) (idAuthority ^ (idAuthority >>> 32));
        return result;
    }
}
