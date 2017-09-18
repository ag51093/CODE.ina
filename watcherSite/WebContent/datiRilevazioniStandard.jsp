<%--
  --   Description: Questa pagina contiene l'interfaccia dei dati delle rilevazioni per gli utenti Standard
  --   Developer :  Antonio Garofalo, Matteo Forina, Graziano Accogli, Ivan Lamparelli
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Dati Rilevazione</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/home.css">
    </head>
    <body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeStandard.jsp">${utenteloggato}</a></li>
                <li><a href="homeStandard.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar active" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="trasferimento">Trasferimento Automatico</a></li>
            </ul>
        </div>
        <div class="card">
 			<form id="filtriDatiRilevazioni" action="datiRilevazioni" method="post">
        	<input type="hidden" name="idform" value="filtradatirilevazioni"/>
        	<%-- Mostra i filtri dei dati delle rilevazioni disponibili per l'utente selezionato --%>
        	${filtridatirilevazioni}
        	</form>
        	<br><br>
        	<%-- Stampa il risultato dell'elaborazione se questo non è nullo --%>
        	${datirilevazioni}	
 		</div>
    </body>
</html>