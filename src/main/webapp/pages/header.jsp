<%@ page import="control.utente.Login" %>

<%@ page errorPage = "errorpage.jsp" %>

<% Integer tipoUtente = (Integer) request.getSession().getAttribute("tipoUtente"); %>
<% String[] validPagesForSearchBar = { "catalogo.jsp", "catalogoAdmin.jsp", "index.jsp" }; %>

<script type="text/javascript">
	let contextPath = '<%= request.getContextPath() %>'
</script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/system/favicon.ico" type="image/x-icon">
	
	<div class="defNav" id="defNav">
		<div class="logo-container">
			<a href="${pageContext.request.contextPath}/index.jsp" class="a">
				<img src="${pageContext.request.contextPath}/images/system/logo-small.png" class="logo" alt="whitee">
			</a>
		</div>
		<div class="menu-container">
		<button class="btn" onclick="window.location.href='${pageContext.request.contextPath}/Catalogo'; return false;">Catalogo</button>
		<button class="btn" onclick="window.location.href='${pageContext.request.contextPath}/pages/personalizzata.jsp'; return false;">Personalizza</button>
		</div>
		<div class="right-container">
			
			<%
				String currentPage = request.getRequestURI();
				String pageName = currentPage.substring(currentPage.lastIndexOf("/") + 1);
	
				for (String s : validPagesForSearchBar)
					if (pageName.equals(s)) {
			%>
			<form class="searchForm">
				<label><input placeholder="Cerca..." class="inputSearch" name="searchText" id="searchInput"
							  type="text"></label>
			</form>
			<% } %>
			<%-------------- Blocco utente non registrato ----------%>
			<% if (tipoUtente == null) { %>
			<a href="${pageContext.request.contextPath}/pages/login.jsp" id="login-icon-not-registered" class="a"><img src="${pageContext.request.contextPath}/images/system/login.png" class="logo lo" alt="login"></a>
			<% } %>
			<%------------------------------------------------------%>
	
			<%-------------- Blocco utente registrato ----------%>
			<% if (tipoUtente != null && tipoUtente.equals(Login.REGISTRATO)) { %>
			<div class="dropdown b">
			  	<a href="${pageContext.request.contextPath}/pages/profilo.jsp" id="login-icon-profile" class="a"><img src="${pageContext.request.contextPath}/images/system/profilo.png" class="logo lo" alt="profilo"></a>
			    <div class="dropdown-content">
			    	<a href="${pageContext.request.contextPath}/Logout" id="login-icon-logout" class="a"><img src="${pageContext.request.contextPath}/images/system/logout.png" class="esci" alt="Logout"></a>
			    </div>
			</div>
			
			<% } %>
			<%--------------------------------------------------%>
	
			<%-------------- Blocco admin ----------%>
			<% if (tipoUtente != null && tipoUtente.equals(Login.ADMIN)) { %>
			<a href="${pageContext.request.contextPath}/Logout" id="login-icon-admin-logout" class="a"><img src="${pageContext.request.contextPath}/images/system/logout.png" class="logo lo" alt="Log Out"></a>
			<% } %>
			<%--------------------------------------%>
	
			<!-- L'admin è l'unico a non vedere il carrello -->
			<% if (tipoUtente == null || !tipoUtente.equals(Login.ADMIN)) { %>
			<script src="https://cdn.lordicon.com/bhenfmcm.js"
					integrity="sha384-VY539ll5TIagHE4WlmKaJKJ4gKxfKtGxK0MgVqVuFG4RXvATOK4KWfapoPR/PE9K"
					crossorigin="anonymous"></script>
			<lord-icon src="https://cdn.lordicon.com/lpddubrl.json" 
					trigger="hover"
					colors="primary:#e88c30,secondary:#30c9e8" 
					stroke="65" 
					role="link"
					tabindex="0"
					aria-label="Vai al carrello"
					class="cart"
					onclick="location.href = '${pageContext.request.contextPath}/pages/carrello.jsp';"
					onkeydown="if(event.key==='Enter' || event.key===' '){ event.preventDefault(); location.href='${pageContext.request.contextPath}/pages/carrello.jsp'; }">
			</lord-icon>
			<% } %>
		</div>
		<div id="mySidenav" class="sidenav">
			<button
				type="button"
				class="closebtn"
				aria-label="Chiudi menu"
				onclick="closeNav()"
			>
				&times;
			</button>

			<a href="${pageContext.request.contextPath}/Catalogo">Catalogo</a>
			<a href="${pageContext.request.contextPath}/pages/personalizzata.jsp">Personalizza</a>
		</div>

		<div id="main">
			<button
				type="button"
				id="open"
				class="openbtn"
				aria-label="Apri menu"
				aria-controls="mySidenav"
				aria-expanded="false"
				onclick="openNav()"
				style="font-size:30px;cursor:pointer;background:transparent;border:0;padding:0;">
			&#9776;
			</button>
		</div>

		<script>
			function openNav() {
				document.getElementById("mySidenav").style.width = "250px";
				document.getElementById("main").style.marginRight = "250px";
				document.getElementById("open").style.display = "none";
				document.getElementById("open").setAttribute("aria-expanded", "true");
			}

			function closeNav() {
				document.getElementById("mySidenav").style.width = "0";
				document.getElementById("main").style.marginRight = "0";
				document.getElementById("open").style.display = "block";
				document.getElementById("open").setAttribute("aria-expanded", "false");
			}
		</script>
	</div>
<div id="risultati">
	<table>
		<caption hidden>Risultati</caption>
		<tr hidden>
			<th>Risultato</th>
		</tr>
	</table>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"
		integrity="sha384-UG8ao2jwOWB7/oDdObZc6ItJmwUkR/PfMyt9Qs5AwX7PsnYn1CRKCTWyncPTWvaS"
		crossorigin="anonymous"></script>
<script type="text/javascript">
	$(document).ready(function () {
		let searchInput = document.getElementById("searchInput");
		searchInput.addEventListener("input", function () {
			let searchQuery = searchInput.value;
			let divRisultati = document.getElementById("risultati");
			if (searchQuery !== "" && searchQuery !== " ") {
				$(divRisultati).css("display", "block")
				$.ajax({
					url: '${pageContext.request.contextPath}/SearchBar',
					type: 'POST',
					data: {search: searchQuery},
					success: function (data) {
						divRisultati.innerHTML = " "
						let jsonarray = eval(data);
						if (data.length > 0) {
							for (let i = 0; i < jsonarray.length; i++) {
								let obj = jsonarray[i];
								divRisultati.innerHTML += '<td>' + '<a href="DescrizioneMaglietta?id=' + obj.ID + '">' + '<img class ="search-img" src="' + obj.grafica + '" alt="' + obj.nome + '">' + '</a>' + '</td>';
							}
						} else {
							divRisultati.innerHTML = '<p class="nessun-articolo">' + 'Nessun risultato trovato' + '</p>';
						}
					},
					error: function () {
						console.log("errore");
					}
				})
			} else {
				$(divRisultati).css("display", "none")
			}
		})
	})
</script>
