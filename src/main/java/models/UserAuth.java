package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserAuth {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    public String username;
    public String password;
    public String fullname;
    public boolean isAdmin;
    
    public UserAuth() {}
    
    public UserAuth(String username, String password, String fullname) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.isAdmin = false;
    }
 
}
