package de.l3s.liwa.assessment;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents a User with username.
 * 
 * @author daniel, aszabo
 * 
 */
public class User {

    private int id;
    private String name;
    private DataSource dataSource; // delegate data-related calls to this
    private String password;
    private String language;
    
    /**
     * Constructs a new User with a DataSource and an  ID.
     */
    public User(DataSource dataSource, int id, String name, String password) {
        this.dataSource = dataSource; 
	this.name = name;
        this.id = id;
        this.password = password;
    }
    
    /**
     * Returns the name of the user.
     * 
     * @return The name of the user
     */
    public String getName() {
	return name;
    }
    
    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public List<Label> getLabels() {
        return dataSource.getLabelsOfUser(this);
    }

    public List<Host> getLabelledHosts() {
        return dataSource.getHostsEvaluatedByUser(this);
    }

    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String l) {
        language = l;
    }

}
