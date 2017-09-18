package logicaBusiness.elaboratori;

/**
 * Classe utilizzata per i controlli sulle stringhe
 * @author Antonio Garofalo
 *
 */
public class Validatore {
	
	/**
	 * Sanitizza la stringa data in input rimuovendo caratteri sospetti
	 * @param string stringa da sanitizzare
	 * @return stringa sanitizzata da caratteri sospetti
	 */
	public static String sanitize(String string) {	
		  return string
		     .replaceAll("(?i)<script.*?>.*?</script.*?>", "")   
		     .replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") 
		     .replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");     
		}
}
