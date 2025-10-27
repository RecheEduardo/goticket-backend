package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleID;

    private String name;

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
