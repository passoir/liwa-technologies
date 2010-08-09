package de.l3s.liwa.assessment;

public interface NextHostProvider {

    /** Returns a Host to evaluate for the given User. 
	Any host should be evaluated by a user only once - except if 
	there are conflicting labels attached to the Host: in this case 
	the user might be asked to re-evaluate the Host.
    */
    Host getNextHost(User user);

}
