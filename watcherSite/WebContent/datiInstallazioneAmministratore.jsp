<%--
  --   Description: Questa pagina contiene l'interfaccia dei dati d'installazione per gli utenti amministratori
  --   Developer :  Antonio Garofalo, Matteo Forina
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Dati Installazione</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
   	</head>
   	<style>
   		input {
    		color: #4662ab;
    		border: 0px;
    		width: 120px;
    		height: auto;
    		border-bottom: 1px solid;
    		text-align: center;
		}
		
		button.danger{
    		background: #ffffff;
   			color: #d53232;
    		border-color: #d53232;
		}

		button.danger:hover{
			background : #d53232;
  			color : white;
  			border-color: #d53232;
		}
		
		hr {
			border-top: 1px solid #4662ab;
			border-bottom: none;
		}
		
		div.error {
			color: #d53232;
			font-size: 12px;			
		} 
	</style>
	<body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeAmministratore.jsp">${utenteloggato}</a></li>
                <li><a href="homeAmministratore.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar active" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="gestioneClienti">Gestione Clienti&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </ul>
        </div>
        <div class="card">
        	<form id="formSelezionaCliente" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="sceglicliente"  />
        		<%-- Mostra i clienti del sistema e permette di selezionarne uno --%>
        		${selezioneclienti}
        		<button type="submit" name="ricerca">Seleziona Cliente</button><br>
        		<hr>
        	</form>
        	<br>
        	<form id="filtriDatiInstallazione" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="filtradatiinstallazione"/>
        		<%-- Mostra i filtri dei dati d'installazione disponibili per l'utente selezionato --%>
        		${filtridatiinstallazione}
        	</form>
        	<br>
        	<div class="error">
        		<%-- Stampa un messaggio di errore se questo è presente --%>
        		${erroreinserimento}
        	</div>
        	<br>
        	<%-- Stampa il risultato dell'elaborazione se questo non è nullo --%>
        	${risultato}    	
        	<br>
        	<br>
        	<br>
        	<br>
        	<a id="gestoredatiinstallazione"></a>
        	<form id="formInserisciDatoInstallazione" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="inseriscidatoinstallazione"/>
        		<%-- Stampa un form per l'inserimento di un dato d'installazione --%>
        		${forminseriscidatoinstallazione}
        	</form>
        	<br>
        	<form id="formInserisciNuovoTipoSensore" action="datiInstallazione" method="post">
				<input type="hidden" name="idform" value="inseriscinuovotiposensore"/>
				<%-- Stampa un form per l'inserimento di un nuovo tipo di sensore --%>
				${forminseriscinuovotiposensore}	
        	</form>
        	<br>
        	<form id="formModificaDatoInstallazione" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="modificadatoinstallazione"/>
        		<%-- Stampa un form per la modifica di un dato d'installazione --%>
        		${formmodificadatoinstallazione}
        	</form>
        	<br>
        	<form id="formEliminaAmbiente" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="eliminaambiente"/>
        		<%-- Stampa un form per l'eliminazione di un ambiente --%>
        		${formeliminaambiente}
        	</form>
        	<br>
        	<form id="formEliminaSensore" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="eliminasensore"/>
        		<%-- Stampa un form per l'eliminazione di un sensore --%>
        		${formeliminasensore}
        	</form>
        </div>
	</body>
</html>