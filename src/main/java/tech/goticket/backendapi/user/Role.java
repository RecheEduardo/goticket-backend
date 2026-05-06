package tech.goticket.backendapi.user;

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
    private Long roleId;

    private String name;

    public enum Values {
        ADMIN(1L),
        ORGANIZER(2L),
        CLIENT(3L);

        long roleId;

        Values(long roleId){
            this.roleId = roleId;
        }

        public long getRoleId() {
            return roleId;
        }
    }
}
