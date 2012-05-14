const proyecto = 'caronte';
var zoomMax = 17;

/**
 * Devuelve los valores iniciales del centro del mapa y su zoom
 * 
 * @return Centro y zoom
 */
function mapInit(){
	var center = new google.maps.LatLng(38.523390, -0.170110); // Villajoyosa, Alicante
	var zoom = 19;
	return {'center':center, 'zoom':zoom};
}

/**
 * Ejecucion adicional a initialize()
 */
function initialize2(){
	mostrarMensajes(); // en mensajes.js

	if(nivelMsg > 1){
		if(document.getElementById('opcionesMapa').verSanos.checked){
			verSanos = true;
		}
	}

	if(userName != '' && nivelMsg > 1){
		plantaResidencia = 0;
		var url = 'markers/residencia/planta'+ plantaResidencia + '.png';
		var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(38.522780, -0.170930), new google.maps.LatLng(38.524040, -0.169530)); // SW, NE
		residencia = new google.maps.GroundOverlay(url, bounds, {clickable: false});
		
		google.maps.event.addListener(map, 'zoom_changed', function(){
			residencia.setMap(null);
			var tipoMapa = map.getMapTypeId();
			var newZoom = map.getZoom();
			if(newZoom >= zoomMax && tipoMapa == roadmap && plantaResidencia >= 0){
				residencia.setMap(map);
			}else if(newZoom < zoomMax && plantaResidencia != -2){
				cambiarPlanta(-2);
			}
		});

		google.maps.event.addListener(map, 'maptypeid_changed', function(){
			residencia.setMap(null);
			var tipoMapa = map.getMapTypeId();
			if(map.getZoom() >= zoomMax && tipoMapa == roadmap && plantaResidencia >= 0){
				residencia.setMap(map);
			}
		});
		
		google.maps.event.addListener(residencia, 'click', function(){
			if(infoWindow != null){
				infoWindow.close();
				infoWindow = null;
				if(infoWinMarker != 'building'){
					limpiarLateral(infoWinMarker);
				}
				infoWinMarker = null;
			}
		});

		residencia.setMap(map);

		if(localizacion == true){ // si el navegador soporta geolocalizacion (valor inicial)
			$.getJSON('getpost/getLatLong.jsp',{
				'nombre':userName
			}, function(data){
				localizacion = data[0].localizacion; // entonces valor por defecto del usuario
				document.getElementById('form-posicion').localizacion.checked = localizacion;
			});
		}else{
			document.getElementById('form-posicion').localizacion.style.display = 'none';
		}

		$.getJSON('getpost/getAsociaciones.jsp', {
			'tipo':'emergencias',
			'nivel':nivelMsg
		}, function(data){
			$.each(data, function(entryIndex, entry) {
				document.getElementById('checkboxAsoc').innerHTML += '<li><input type="checkbox" name="assigned' + entry.id +
					'" onclick="asociarEmergencia(' + entry.id + ', assigned' + entry.id + '.checked)"/>' +
					entry.id +' - ' + entry.nombre + '</li>';
				emergenciasAsociadas.push({'id':entry.id, 'valor':false, 'valorBD':false});
			});
			if(document.getElementById('checkboxAsoc').innerHTML == ''){
				document.getElementById('textoAsoc').innerHTML = '&thinsp;No hay emergencias para asociar';
			}else{
				document.getElementById('textoAsoc').innerHTML = '&emsp;V';
			}
		});

		numeroMarcadores(plantaResidencia);
	}

	if(localizacion == true){
		navigator.geolocation.getCurrentPosition(coordenadasUsuario); // coordenadasUsuario();
	}
	
	if(userName != '' && usuario_actual_tipo != 'citizen'){
		numeroMarcadores(0, true);
	}
}

/**
 * Ejecucion adicional a actualizar()
 */
function actualizar2(){
	mostrarMensajes2(); // en mensajes.js

	if(userName != ''){
		numeroMarcadores(plantaResidencia);
	}

	contador++;
	if(contador >= 3){
		contador = 0;
		if(localizacion == true && nivelMsg > 0){
			navigator.geolocation.getCurrentPosition(coordenadasUsuario); // coordenadasUsuario();
		}
	}
}

/**
 * Genera los edificios que se muestran en el mapa al inicio
 */
function buildingInit(){
	showBuilding('hospital');
	showBuilding('firemenStation');
	showBuilding('policeStation');
	showBuilding('geriatricCenter');
}

/**
 * Muestra en el mapa un tipo de edificio
 * 
 * @param type Tipo de edificio a mostrar
 */
function showBuilding(type){
	// Villajoyosa, Alicante
	if(type == 'hospital'){
		generateBuilding('hospital', 'Centro de salud', 38.506900, -0.231120); // Villajoyosa, Alicante
		generateBuilding('hospital', 'Cruz Roja', 38.505760, -0.229290); // Villajoyosa, Alicante
		//generateBuilding('hospital', 'Centro de salud', 38.228138, -1.706449); // Calasparra, Murcia
	}else if(type == 'firemenStation'){
		generateBuilding('firemenStation', 'Parque de bomberos', 38.505610, -0.233230); // Villajoyosa, Alicante
		//generateBuilding('firemenStation', 'Parque de bomberos', 38.111020, -1.865018); // Caravaca de la Cruz, Murcia
	}else if(type == 'policeStation'){
		generateBuilding('policeStation', 'Polic&iacute;a municipal', 38.510400,-0.231850); // Villajoyosa, Alicante
		generateBuilding('policeStation', 'Guardia Civil', 38.504640, -0.237360); // Villajoyosa, Alicante
		//generateBuilding('policeStation', 'Ayuntamiento y Polic&iacute;a municipal', 38.231125, -1.697560); // Calasparra, Murcia
	}else if(type == 'geriatricCenter'){
		generateBuilding('geriatricCenter', 'Ballesol Costa Blanca Senior Resort', 38.523850, -0.170180); // Villajoyosa, Alicante
		//generateBuilding('geriatricCenter', 'Residencia Virgen de la Esperanza', 38.232272, -1.698925); // Calasparra, Murcia
	}
}

function esquinasResidencia(lat, lng){
	var esquinas;
	if(lat == 38.523850 && lng == -0.170180){
		var esquina1 = new google.maps.LatLng(38.523950, -0.169880); // -0.000040, 0.000010
		var esquina2 = new google.maps.LatLng(38.523480, -0.169650);
		var esquina3 = new google.maps.LatLng(38.523420, -0.169840);
		var esquina4 = new google.maps.LatLng(38.523220, -0.169760);
		var esquina5 = new google.maps.LatLng(38.522940, -0.170070);
		var esquina6 = new google.maps.LatLng(38.522860, -0.170500);
		var esquina7 = new google.maps.LatLng(38.523180, -0.170650);
		var esquina8 = new google.maps.LatLng(38.523390, -0.170530);
		var esquina9 = new google.maps.LatLng(38.523700, -0.170680);
		esquinas = [esquina1, esquina2, esquina3, esquina4, esquina5, esquina6, esquina7, esquina8, esquina9];
	}/*else if(lat == 38.232272 && lng == -1.698925){
		var esquina1 = new google.maps.LatLng(38.232440, -1.699160); // -0.000095, 0.000120
		var esquina2 = new google.maps.LatLng(38.232380, -1.698640);
		var esquina3 = new google.maps.LatLng(38.232105, -1.698690);
		var esquina4 = new google.maps.LatLng(38.232165, -1.699210);
		esquinas = [esquina1, esquina2, esquina3, esquina4];
	}*/
   return esquinas;
}

/**
 * Define las opciones de un tipo de marcador segun sea el evento
 * 
 * @param evento Objeto marcador del que se quieren las opciones
 * @return Opciones del marcador
 */
function definirOpciones(evento){
	var opciones;
	var icono = new google.maps.MarkerImage(null);

	// MAXIMO DE RECURSOS POR MARCADOR ES 10
	var cantidad;
	if(evento.cantidad > 10){
		cantidad = 10;
	}else{
		cantidad = evento.cantidad;
	}

	if(evento.marcador == 'event'){ // Es un evento
		if(evento.tipo == 'fire'){ // Incendio
			icono.url = 'markers/events/fuego.png';
			if(evento.estado == 'controlled'){
				icono.url ='markers/events/fuego_control.png';
			}
		}else if(evento.tipo == 'flood'){// Inundacion
			icono.url = 'markers/events/agua.png';
			if(evento.estado == 'controlled'){
				icono.url ='markers/events/agua_control.png';
			}
		}else if(evento.tipo == 'collapse'){ // derrumbamiento
			icono.url = 'markers/events/casa.png';
			if(evento.estado == 'controlled'){
				icono.url ='markers/events/casa_control.png';
			}
		}else if(evento.tipo == 'lostPerson'){ // anciano perdido
			icono.url = 'markers/events/personaPerdida.png';
			if(evento.estado == 'controlled'){
				icono.url ='markers/events/personaPerdida_control.png';
			}
		}else if(evento.tipo == 'injuredPerson'){ // anciano herido
			icono.url = 'markers/events/personaHerida.png';
			if(evento.estado == 'controlled'){
				icono.url ='markers/events/personaHerida_control.png';
			}
		}
		opciones = {
			icon: icono,
			draggable: true // Para que se pueda arrastrar
		};
	}else if(evento.marcador == 'resource'){ // es un recurso
		var actuando = '';
		if(evento.estado == 'active'){
			actuando = '_no';
		}

		if(evento.nombre == userName){
			icono.url = 'markers/resources/user' + actuando + '.png';
		}else if(evento.tipo == 'police'){ // es un policia
			icono.url = 'markers/resources/policia' + actuando + '.png';
		}else if(evento.tipo == 'firemen'){ // es un bombero
			icono.url = 'markers/resources/bombero' + actuando + '.png';
		}else if(evento.tipo == 'ambulance' || evento.tipo == 'ambulancia'){ // es una ambulancia
			icono.url = 'markers/resources/ambulancia' + actuando + '.png';
		}else if(evento.tipo == 'nurse'){ // es un enfermero
			icono.url = 'markers/resources/enfermero' + actuando + '.png';
		}else if(evento.tipo == 'gerocultor'){ // es un gerocultor
			icono.url = 'markers/resources/gerocultor' + actuando + '.png';
		}else if(evento.tipo == 'assistant'){ // es un auxiliar
			icono.url = 'markers/resources/auxiliar' + actuando + '.png';
		}else if(evento.tipo == 'coordinator'){ // es un coordinador
			icono.url = 'markers/resources/coordinador' + actuando + '.png';
		}else if(evento.tipo == 'orderly'){ // es un celador
			icono.url = 'markers/resources/celador' + actuando + '.png';
		}else if(evento.tipo == 'receptionist'){ // es un recepcionista
			icono.url = 'markers/resources/recepcionista' + actuando + '.png';
		}else if(evento.tipo == 'otherStaff'){ // otro
			icono.url = 'markers/resources/otroPersonal' + actuando + '.png';
		}
		opciones = {
			icon: icono,
			draggable: (evento.nombre == userName) // Arrastrar si soy yo
		};
	}else if(evento.marcador == 'people'){ // es una victima
		if(evento.tipo == 'trapped'){ // personas atrapadas
			icono.url = 'markers/people/trapped' + cantidad + '.png';
			if(evento.estado == 'controlled'){
				icono.url = 'markers/people/trapped_control.png';
			}
		}else if(evento.tipo=='healthy'){ // sanos
			icono.url = 'markers/people/sano' + cantidad + '.png';
			if(evento.estado=='controlled'){
				icono.url = 'markers/people/sano_control.png';
			}
		}else if(evento.tipo == 'slight'){ // heridos leves
			icono.url = 'markers/people/leve' + cantidad + '.png';
			if(evento.estado == 'controlled'){
				icono.url = 'markers/people/leve_control.png';
			}
		}else if(evento.tipo == 'serious'){ // heridos graves
			icono.url = 'markers/people/grave' + cantidad + '.png';
			if(evento.estado == 'controlled'){
				icono.url = 'markers/people/grave_control.png';
			}
		}else if(evento.tipo == 'dead'){ // muertos
			icono.url = 'markers/people/muerto' + cantidad + '.png';
			if(evento.estado == 'controlled'){
				icono.url = 'markers/people/muerto_control.png';
			}
		}
		icono.size = new google.maps.Size(28, 43);
		icono.anchor = new google.maps.Point(13, 43);
		opciones = {
			icon: icono,
			draggable: true // Se pueden arrastrar para asociarlo
		};
	}

	return opciones;
}

/**
 * Crea un marcador y le dota de diferentes eventos
 * 
 * @param evento Objeto marcador con el que se generara el marcador
 * @param caracter Si el marcador es definitivo o temporal
 * @param opciones Opcones del marcador
 * @retunr Marcador
 */
function comportamientoMarcador(evento, caracter, opciones){
	var opts = {
		position: new google.maps.LatLng(evento.latitud, evento.longitud),
		icon: opciones.icon,
		draggable: opciones.draggable
	};
	var marker = new google.maps.Marker(opts);
	// Annadimos el comportamiento
	google.maps.event.addListener(marker, 'click', function(){
		var small = evento.nombre + '<br/>' + evento.descripcion;
		var links = '<form id="form_acciones" name="form_acciones" action="#">';
		if(nivelMsg > 1){
			if(evento.marcador != 'resource'){
				links += cargarMenuAcciones(marcadores_definitivos[evento.id]); // en mapa_caronte2.js
			}else{
				links += cargarListaActividades(marcadores_definitivos[evento.id]); // en mapa_caronte2.js
			}
		}
		links += '</form>';
		
		if(infoWindow != null){
			infoWindow.close();
			if(infoWinMarker != 'building' && infoWinMarker != evento.marcador){
				limpiarLateral(infoWinMarker, true);
			}
		}
		infoWindow = new google.maps.InfoWindow({content:'<div id="bocadillo">' + small + '<div id="bocadillo_links">' + links + '</div></div>'});
		infoWinMarker = evento.marcador;
		infoWindow.open(map, marker);
		
		google.maps.event.addListener(infoWindow, 'closeclick', function(){
			limpiarLateral(evento.marcador);
		});
		
		cargarLateral(evento); // en mapa_caronte2.js
	});

	google.maps.event.addListener(marker, 'dragstart', function(){
		noActualizar = evento.id;
		if(infoWindow != null){
			infoWindow.close();
		}
	});

	google.maps.event.addListener(marker, 'dragend', function(punto){
		var nuevaLat = punto.latLng.lat().toFixed(6);
		var nuevaLong = punto.latLng.lng().toFixed(6);
		
		infoWindow = new google.maps.InfoWindow({content:'<div id="bocadillo">&iquest;Confirmar cambio de posici&oacute;n?<br/><br/>' +
			'<span id="confirmar" class="pulsable azul" onclick="infoWindow.close(); guardar_posicion(' + evento.id +
			',' + nuevaLat + ',' + nuevaLong + ')" >Confirmar</span>'+ ' - ' +
			'<span id="cancelar" class="pulsable azul" onclick="infoWindow.close(); cancelar_asignacion(' + evento.id + ');">Cancelar</span></div>'});
		infoWindow.open(map, marker);
		
		google.maps.event.addListener(infoWindow, 'closeclick', function(){
			cancelar_asignacion(evento.id);
			limpiarLateral(evento.marcador);
		});
	});

	if((evento.planta == plantaResidencia || evento.planta == -2 || plantaResidencia == -2) && (evento.tipo != 'healthy' || verSanos == true)){
		marker.setMap(map);
	}

	return marker;
}