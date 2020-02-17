(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventoController', EventoController);
    
    EventoController.$inject = ['$scope', '$rootScope','$stateParams', 'Principal', 'LoginService', '$state', 'EventoService', '$sce', '$mdDialog', 
                                'GeneralConfigurationService', 'EventTrackerService', '$window', '$translate',
                                '$http', 'ServerURL', '$q', '$interval', '$timeout', 'GlobalService', 'ReloadEventService', 'FinishLoteService'];

    function EventoController ($scope, $rootScope, $stateParams, Principal, LoginService, $state, EventoService, $sce, $mdDialog, 
    		GeneralConfigurationService, EventTrackerService, $window, $translate, $http, ServerURL, $q, $interval, $timeout, GlobalService, ReloadEventService, FinishLoteService) {
        
    	var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.video = null;
        vm.oferta = null;
        vm.ofertaMinima = [];
        vm.loteMessage = [];
        vm.loteEndTime = [];
        vm.loteExpired = [];
        vm.waiting = [];
        vm.followLote = [];
        vm.eventoMap = {};
        vm.promises = [];
        vm.porcentajeOfertaPropuesta = null;
        vm.porcentajeMinimoMenor10000 = null;
        vm.montoMinimo1000020000 = null;
        vm.montonMinimoMayor20000 = null;
        vm.viewUserInOffer = null;
        
        var reload = function() {
    		location.replace("/#/event/last");
    		location.reload();
    	};
        
        // reload every 30 minutes //1800000
    	$interval(reload, 1800000);
        
    	    	
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'PORCENTAJE_OFERTA_PROPUESTA'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'PORCENTAJE_MINIMO_MENOR_10000'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'MONTO_MINIMO_10000_20000'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'MONTO_MINIMO_MAYOR_20000'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'VIEW_USER_IN_OFFER'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'URL_CABALLO'
        }).$promise);
        vm.promises.push(GeneralConfigurationService.get({
        	key: 'URL_FOTO_CABALLO'
        }).$promise);
        
        $q.all(vm.promises).then(function(results){
        	vm.porcentajeOfertaPropuesta = results[0].value;
        	vm.porcentajeMinimoMenor10000 = results[1].value;
        	vm.montoMinimo1000020000 = results[2].value;
        	vm.montonMinimoMayor20000 = results[3].value;
        	vm.viewUserInOffer = results[4].value;
        	vm.urlDetalleCaballo = results[5].value;
        	vm.urlFotoCaballo = results[6].value;
        	
        	vm.evento = EventoService.get({}, function() {
        		vm.initDate = new Date(vm.evento.initDate);
        		vm.endDate = new Date(vm.evento.endDate);
        		var now = new Date();
        		vm.isStarted = now >= vm.initDate; // puede estar finalizado en su totalidad o solo algunos lotes
        		
        		var milisToStart = vm.initDate.getTime() - now.getTime();
        		if(!vm.isStarted && now < vm.initDate && milisToStart < 1800000) { // promise para iniciar el evento
        			$timeout(reload, milisToStart);
        		}
        		
            	for(var i=0; i < vm.evento.lotes.length; i++) {
            		var orden = vm.evento.lotes[i].product.$orden;
            		
            		vm.evento.lotes[i].product.$nombre = 'Lote : ' + orden + ' - ' + vm.evento.lotes[i].product.$nombre;
            		
            		vm.eventoMap[vm.evento.lotes[i].id] = i;
            		
            		var loteEndTimeCard= new Object();
            		loteEndTimeCard.time = $scope.toDateMillis(vm.evento.lotes[i].endDate);
            		loteEndTimeCard.color = "#fff";
            		vm.loteEndTime[vm.evento.lotes[i].id] = loteEndTimeCard;
            		
            		vm.waiting[vm.evento.lotes[i].id] = $scope.isEndDateExpired(loteEndTimeCard.time) && !vm.evento.lotes[i].finished;
            		vm.loteExpired[vm.evento.lotes[i].id] = vm.evento.lotes[i].finished; //$scope.isEndDateExpired(loteEndTimeCard.time);
            		
            		var messageCard = new Object();
            		if(!vm.loteExpired[vm.evento.lotes[i].id]){
	            		if (vm.account != null && vm.evento.lotes[i].lastBid!=null && vm.evento.lotes[i].lastBid.userLogin == vm.account.login){
	            			messageCard.message = $translate.instant("global.event.messageBid2");
	            			messageCard.color = "#088A08";
	        			} else {
	        				var userNameCountry = "";
	        				if(vm.evento.lotes[i].lastBid!=null){
	        					userNameCountry = getCountryName(vm.evento.lotes[i].lastBid.userCountry);
	        					messageCard.message = $translate.instant("global.event.messageBid3", { 
	        						country: userNameCountry,
	        						user: vm.viewUserInOffer == 'true' && vm.account != null ? vm.evento.lotes[i].lastBid.userLogin : "Alguien"
	        					});
	            				messageCard.color = "#FF0000";
	        				}
	        				
	        			}
            		}else{
	            			if(vm.evento.lotes[i].lastBid!=null){
	        					userNameCountry = getCountryName(vm.evento.lotes[i].lastBid.userCountry);
	        					messageCard.message = $translate.instant("global.event.messageBid4", { 
	        						country: userNameCountry
	        					});
	        				}else{
	        					userNameCountry = getCountryName('AR');
	        					messageCard.message = $translate.instant("global.event.messageBid4", { 
	        						country: userNameCountry
	        					});
	        				}
	            			
	            			if (vm.account != null && vm.evento.lotes[i].lastBid!=null && vm.evento.lotes[i].lastBid.userLogin == vm.account.login){
	            				messageCard.message = "";
	            			}
            		}
            		vm.loteMessage[vm.evento.lotes[i].id] = messageCard;
            		
            		if(vm.account !== null) {
            			var followers = new Set(vm.evento.lotes[i].followers);
            			vm.followLote[vm.evento.lotes[i].id] = followers.has(vm.account.login);
            		}
            	}
            	
            	// Volver a la posicion anterior
            	if($rootScope.position !== 0) {
            		$timeout(function() {
            			jQuery("body, html").scrollTop($rootScope.position);
            		}, 500);
            	}
            	
            });
        });
        
        EventTrackerService.receive().then(null, null, function(refreshLoteDTO) {
            console.log("Solicitud de refrescar un lote: ", refreshLoteDTO.loteId)

            // Si el lote esta en espera
            var waiting = vm.waiting[refreshLoteDTO.loteId];
            
            if(!!refreshLoteDTO.lastBid) {	// Si es una oferta real
            	refreshLote(refreshLoteDTO);
            } else { // Solicitud de posponer
            	// Refresco el endTime
    			
            	var newEndDatetime = $scope.toDateMillis(refreshLoteDTO.endDate);			
				vm.loteEndTime[refreshLoteDTO.loteId].time = newEndDatetime;
    			// vm.loteExpired[refreshLoteDTO.loteId] = $scope.isEndDateExpired(newEndDatetime);

				if(!!waiting) {
					reload();
				}
            }
            
        });
        
        ReloadEventService.receive().then(null, null, function(fecha) {
        	console.log("Solicitud de recargar " + fecha)

        	reload();
        });
        
        FinishLoteService.receive().then(null, null, function(loteId) {
        	console.log("Lote finalizado: " + loteId)
        	
        	vm.loteExpired[loteId] = true;
        });
        
        
        var refreshLote = function(refreshLoteDTO) {
			var idx = vm.eventoMap[refreshLoteDTO.lastBid.loteId];
			
			//Si no habia oferta previa
			if(vm.evento.lotes[idx].lastBid == null){
				var firstBid = new Object();
				firstBid.price = refreshLoteDTO.lastBid.price;
				vm.evento.lotes[idx].lastBid = firstBid;
			}else{
				vm.evento.lotes[idx].lastBid.price = refreshLoteDTO.lastBid.price;
			}
			
			vm.oferta[refreshLoteDTO.lastBid.loteId] = $scope.nuevaOferta(refreshLoteDTO.lastBid.price, refreshLoteDTO.lastBid.loteId)
			
			if(vm.account != null && refreshLoteDTO.lastBid.userLogin == vm.account.login){
				vm.loteMessage[refreshLoteDTO.lastBid.loteId].message = $translate.instant("global.event.messageBid2");
				vm.loteMessage[refreshLoteDTO.lastBid.loteId].color = "#088A08";
				
				vm.followLote[refreshLoteDTO.lastBid.loteId] = true;
			} else {
				var userNameCountry = getCountryName(refreshLoteDTO.lastBid.userCountry);
				vm.loteMessage[refreshLoteDTO.lastBid.loteId].message = $translate.instant("global.event.messageBid3", { 
					country: userNameCountry,
					user: vm.viewUserInOffer == 'true' && vm.account != null ? refreshLoteDTO.lastBid.userLogin : "Alguien"
				});
				vm.loteMessage[refreshLoteDTO.lastBid.loteId].color = "#FF0000";
			}
			
			//Refresco el endTime para el caso de extensión de 5 minutos
			var newEndDatetime = $scope.toDateMillis(refreshLoteDTO.endDate);			
			if(newEndDatetime != vm.loteEndTime[refreshLoteDTO.lastBid.loteId].time){
				var loteEndTimeCard= new Object();
	    		loteEndTimeCard.time = newEndDatetime;
	    		loteEndTimeCard.color = "red";
				vm.loteEndTime[refreshLoteDTO.lastBid.loteId] = loteEndTimeCard;
			}
			
			//vm.loteExpired[refreshLoteDTO.lastBid.loteId] = $scope.isEndDateExpired(newEndDatetime);
    	};
        
        $scope.toDateMillis = function(d) {
        	var date = new Date(d);
        	return date.getTime();
        };
        
        $scope.isEndDateExpired = function(date) {
        	var now = new Date().getTime();
            return now >= date;
        };
        
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        
        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        
        function register () {
            $state.go('register');
        }
            	
    	$scope.nuevaOferta = function(valor, loteId) {
    		var porcentajeOferta = 1+ (vm.porcentajeOfertaPropuesta/100);
    		var nextBid = valor * porcentajeOferta;
    		var minOferta = null;
    		
    		if(valor < 10000){
    			var minPorcentaje = 1+ (vm.porcentajeMinimoMenor10000/100);
    			minOferta = valor * minPorcentaje
    		}else if(valor >= 10000 && valor < 20000){
    			minOferta = parseFloat(valor) + parseFloat(vm.montoMinimo1000020000);
    		} else {
    			minOferta = parseFloat(valor) + parseFloat(vm.montonMinimoMayor20000);
    		}
    		
    		//Seteo el valor mínimo que siempre es por porcentaje
    		vm.ofertaMinima[loteId] = roundValue(minOferta);
    		
    		//La sugerencia siempre es el valor mayor
    		return roundValue(nextBid);
    	};
    	
    	/**
    	 * Funcion que redondea el número a la siguiente centena
    	 * 
    	 */
    	function roundValue(value) {
    		 value = Math.trunc(value);
    		 var valueDiv100 = value/100;
    		 
			 var integerPart = Math.trunc(valueDiv100);
			 var centesimaParte = integerPart * 100;
			 if(centesimaParte == value){
				 return value;
			 }
			 var finalValue = centesimaParte + 100;
			 return finalValue;
    	 }
    	
    	$scope.showDetail = function(caballoId, loteId) {

	      	$http({
  				method: 'GET',
  				url: ServerURL + 'api/activityEvent/productView/' + loteId,
  				params: {
  					
  				}
			  	}).then(function() {
  				console.log("Registro de lote visto exitoso");
  		  	}, function(data) {
  				console.log("Error al lote producto visto");
  				console.log(data);
  		  	});
    		
    		var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
    		if ( app ) {
    			// PhoneGap application Android
          		if(device.platform === 'Android'){
                	$state.go('evento.productDetail', {idCaballo: caballoId, url: vm.urlDetalleCaballo});
          		}else {
          			//PhoneGap iOS
	    			let position = Math.floor(window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0);
	    			$state.go('product-detail', {idCaballo: caballoId, position: position, url: vm.urlDetalleCaballo});
          		}
    			
    		}else {
    			//Browser iOS
        		if(navigator.userAgent.match(/(iPod|iPhone|iPad)/)){
	    			let position = Math.floor(window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0);
	    			$state.go('product-detail', {idCaballo: caballoId, position: position, url: vm.urlDetalleCaballo});
	            }else{
	    			// browser android o pc
	            	$state.go('evento.productDetail', {idCaballo: caballoId, url: vm.urlDetalleCaballo});
	            }
    			
    		}

    	};
    	
    	$scope.shareOnFacebook = function(video) {
    		var urlVideo = GlobalService.getYoutubeUrl(video);
    		$window.open('http://www.facebook.com/sharer.php?u='+encodeURIComponent(urlVideo),'_system');
    	};
    	
    	$scope.shareOnTwitter = function(video) {
    		var urlVideo = GlobalService.getYoutubeUrl(video);
    		$window.open('https://twitter.com/share?text=' +encodeURIComponent(urlVideo), "_system");
    	};
    	
    	$scope.shareOnWhatsapp = function(video) {
    		var urlVideo = GlobalService.getYoutubeUrl(video);
    		var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
    		if ( app ) {
    		    // PhoneGap application
        		var urlWhatsapp = "whatsapp://send?text=";

    		} else {
    		    // Web page
        		var urlWhatsapp = 'https://web.whatsapp.com/send?text= ' 
    		}      		
    		var urlCaballo = 'http://www.solaguayre.com.ar/';
    		var urlFinal = urlWhatsapp + encodeURIComponent(urlVideo);
    		$window.open(urlFinal, "_system");
    	};
    	
    	$scope.onFollowClick = function(loteIdFollow, followLote) {
    		var url = undefined;
    		if(followLote) {
    			url = ServerURL + 'api/lote/' + loteIdFollow + '/follower/add';
    		} else {
    			url = ServerURL + 'api/lote/' + loteIdFollow + '/follower/remove';
    		}
    		
    		$http({
				method: 'GET',
				url: url
			}).then(function() {
				console.log("Cambio en el seguimiento del lote " + loteIdFollow + ": " + followLote);
			}, function(data) {
		        // Handle error here
				console.log("Error al clickear Follow, " + data);
				vm.followLote[loteIdFollow] = !followLote;
		    });
    	};
    	
    	function getCountryName (countryCode){
    		
    		switch (countryCode) {
	    	    case "AR":
	    	    	name = "Argentina";
	    	        break;
	    	    case "CL":
	    	    	name = "Chile";
	    	        break;
	    	    case "UY":
	    	    	name = "Uruguay";
	    	        break;
	    	    case "PY":
	    	    	name = "Paraguay";
	    	        break;
	    	    case "BO":
	    	    	name = "Bolivia";
	    	        break;
	    	    case "PE":
	    	    	name = "Perú";
	    	        break;
	    	    case "CO":
	    	    	name = "Colombia";
	    	    	break;
	    	    case "EC":
	    	    	name = "Ecuador";
	    	    	break;
	    	    case "MX":
	    	    	name = "México";
	    	    	break;
	    	    case "BR":
	    	    	name = "Brasil";
	    	    	break;
	    	    case "US":
	    	    	name = "Estados Unidos";
	    	    	break;
	    	    case "ES":
	    	    	name = "España";
	    	    	break;
	    	    default: 
	    	    	name = "Argentina";
	    	}
    		
    		return name;
    	}
    	
    	$scope.postponseLote = function(loteId) {
    		
    		var nombre = vm.evento.lotes[vm.eventoMap[loteId]].product.$nombre;
    		
    		var confirm = $mdDialog.prompt()
				.title('Desea posponer ' + nombre + ' (' + loteId + ')?')
				.textContent('Cantidad de minutos a posponer')
				.placeholder('Minutos')
				.ariaLabel('Minutos')
				.initialValue('3')
				.required(true)
				.ok('Aceptar')
				.cancel('Cancelar');

    		$mdDialog.show(confirm).then(function(result) {
    	    	console.log('Solicitud de posponer por ' + result + ' minutos el lote: ' + loteId);
    	    	
    	      	$http({
	  				method: 'GET',
	  				url: ServerURL + 'api/lote/' + loteId + '/postpone',
	  				params: {
	  					minutes: result
	  				}
  			  	}).then(function() {
	  				console.log("Se pospuso exitosamente");
	  		  	}, function(data) {
	  				console.log("Error al intentar posponer");
	  				console.log(data);
  		  		});
    	    }, function() {
    	    	console.log('Cancelada la solicitud de posponer el lote: ' + loteId);
    	    });
    	};
    	
    	$scope.participar = function() {
    		
    		var confirm = $mdDialog.confirm()
				.title('Desea participar del evento '+ vm.account.lastEvent + '?')
				.textContent('Con dicha participación, usted también acepta las condiciones del evento')
				.ariaLabel('Lucky day')
				.ok('Aceptar')
				.cancel('Cancelar');

    		$mdDialog.show(confirm).then(function(result) {
    	    	console.log('El usuario ' + vm.account.login + ' confirma participacion');
    	    	
    	      	$http({
	  				method: 'GET',
	  				url: ServerURL + 'api/participate/' + vm.account.login,
	  				params: {}
  			  	}).then(function() {
  			  		vm.account.lastEventParticipant = true;
	  				console.log("Participacion exitosa");
	  		  	}, function(data) {
	  				console.log("Error al registrar participacion");
	  				console.log(data);
  		  		});
    	    }, function() {
    	    	console.log('Cancelada la solicitud de participacion: ' + vm.account.login);
    	    });
    	};
    	
    	$scope.deleteLastBid = function(loteId) {
    		
    		var nombre = vm.evento.lotes[vm.eventoMap[loteId]].product.$nombre;
    		var lastBidPrice = vm.evento.lotes[vm.eventoMap[loteId]].lastBid.price;
    		
    		var confirm = $mdDialog.confirm()
				.title('Desea eliminar la última oferta del lote: ' + nombre + ', con el monto: ' + lastBidPrice + '?')
				.ok('Aceptar')
				.cancel('Cancelar');

    		$mdDialog.show(confirm).then(function(result) {
    	    	console.log('Solicitud de borrar ultima oferta del lote: ' + loteId);
    	    	
    	      	$http({
	  				method: 'DELETE',
	  				url: ServerURL + 'api/bid/' + loteId + '/deleteLastBid',
  			  	}).then(function() {
	  				console.log("Se borro exitosamente");
	  		  	}, function(data) {
	  				console.log("Error al intentar borrar la ultima oferta");
	  				console.log(data);
	  				if(!!data.data.title)
	  					alert(data.data.title);
  		  		});
    	    }, function() {
    	    	console.log('Cancelada la solicitud de borrar la ultima oferta del lote: ' + loteId);
    	    });
    	};
    	
    	$scope.timerFinishCallback = function(loteId) {
    		vm.waiting[loteId] = true; //LOTE EN ESPERA
    		//$timeout(function() {
    		//	vm.loteExpired[loteId]=true;
    		//}, 1000);
        };
    }
})();
