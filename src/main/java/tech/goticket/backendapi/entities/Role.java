package tech.goticket.backendapi.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleID;

    private String name;

    public Long getRoleID() {
        return roleID;
    }

    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Values {
        ADMIN(1L),
        ORGANIZER(2L),
        CLIENT(3L);

        long roleID;

        Values(long roleID){
            this.roleID = roleID;
        }

        public long getRoleID() {
            return roleID;
        }
    }
}
