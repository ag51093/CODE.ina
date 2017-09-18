<%--
  --   Description: Questa pagina contiene l'interfaccia dei dati delle rilevazioni per gli utenti Amministratore
  --   Developer :  Antonio Garofalo, Matteo Forina, Graziano Accogli, Ivan Lamparelli
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Dati Rilevazioni</title>
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
		hr {
			border-top: 1px solid #4662ab;
			border-bottom: none;
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
                <li><a class="sidebar active" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="gestioneClienti">Gestione Clienti&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </ul>
        </div>
        <div class="card">
        	<form id="formSelezionaCliente" action="datiRilevazioni" method="post">
        		<input type="hidden" name="idform" value="sceglicliente"  />
        		<%-- Mostra i clienti del sistema e permette di selezionarne uno --%>
        		${formsceglicliente}
        		<button type="submit" name="ricerca">Seleziona Cliente</button><br>
        		<hr>
        	</form>
        	<br>
        	<form id="filtriDatiRilevazioni" action="datiRilevazioni" method="post">
        		<input type="hidden" name="idform" value="filtradatirilevazioni"/>
        		<%-- Mostra i filtri dei dati delle rilevazioni disponibili per l'utente selezionato --%>
        		${filtridatirilevazioni}
        	</form>
        	<br>
        	<%-- Stampa il risultato dell'elaborazione se questo non è nullo --%>
	        ${datirilevazioni}	
        </div>
	</body>
</html>