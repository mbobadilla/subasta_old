(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventosAnterioresController', EventosAnterioresController);

    EventosAnterioresController.$inject = ['Principal', 'EventosAnteriores', 'AlertService', '$state', 'JhiLanguageService', 'ServerURL', '$http'];

    function EventosAnterioresController(Principal, EventosAnteriores, AlertService, $state, JhiLanguageService, ServerURL, $http) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.languages = null;
        vm.loadAll = loadAll;
        vm.eventos = [];
        vm.appType = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
        vm.images = [];
        
        vm.loadAll();
        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });


        function loadAll () {
        	EventosAnteriores.query().$promise.then(function(data) {
        		vm.eventos = data;
        		for(var i=0; i < vm.eventos.length; i++) {
        			var evento = vm.eventos[i];
        			vm.getImage(evento.id, evento.name);
        		}
        	});
        }

        
        vm.getPath = function (eventId) {
        	return vm.images[eventId];
        };
        
        vm.getImage = function (eventId, eventName) {
        	var url = ServerURL + '/api/imageFile?eventName=' + encodeURIComponent(eventName);

        	if(vm.appType) {
	        	var path = "";
	      		if(device.platform === 'Android'){
	      			path = cordova.file.externalDataDirectory;
	      		}else{
	      			path = cordova.file.syncedDataDirectory;
	      		}
	      		
	    		window.resolveLocalFileSystemURL(path, function (directoryEntry) {
					// Parameters passed to getFile create a new file or return the file if it already exists.
					directoryEntry.getFile( 'image.JPG' , { create: true, exclusive: false }, function (fileEntry) {
						var fileTransfer = new FileTransfer();
						var fileURL = fileEntry.toURL();
	
						fileTransfer.download(
							url,
							fileURL,
							function (entry) {
								vm.images[eventId] = entry.toURL();
							},
							function (error) {
			       				 alert("download error code" + error.code);
							},
							null,{}
						);
	
					}, function(){
						alert("Error en la descarga");
					});
	
				},
				 function(){
				 	alert("Error en la descarga");
				});
        	} else {
        		$http({
    		        method: 'GET',
    		        url: url,
    		        responseType: 'arraybuffer'
    		    }).success(function (data, status, headers) {
    		        headers = headers();
    		        var contentType = headers['content-type'];
    		 
    		        var linkElement = document.createElement('a');
    		        try {
    		            var blob = new Blob([data], { type: contentType });
    		            var url = window.URL.createObjectURL(blob);
    		            vm.images[eventId] = url;
    		        } catch (ex) {
    		            console.log(ex);
    		        }
    		    }).error(function (data) {
    		        console.log(data);
    		    });
        	}
        };
        
    }
})();