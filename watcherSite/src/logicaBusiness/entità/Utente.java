package logicaBusiness.entit‡;

/**
 * Classe per raccogliere le informazioni su un utente del sistema
 * @author Ivan Lamparelli
 *
 */
public class Utente {
	private String email;
	private boolean flagAdmin;
	
	public Utente(String unaMail, boolean flagAmministratore) {
		this.email = unaMail;
		this.flagAdmin = flagAmministratore;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public boolean ËAmministratore() {
		return this.flagAdmin;
	}
}
