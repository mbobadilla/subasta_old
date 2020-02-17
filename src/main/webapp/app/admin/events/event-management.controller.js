(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('EventManagementController', EventManagementController);

    EventManagementController.$inject = ['Principal', 'EventManagement', 'ParseLinks', 'AlertService', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService', '$http', 'ServerURL', '$stateParams'];

    function EventManagementController(Principal, EventManagement, ParseLinks, AlertService, $state, pagingParams, paginationConstants, JhiLanguageService, $http, ServerURL, $stateParams) {
        var vm = this;

        vm.loadAll = loadAll;
        vm.events = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;
        vm.downloadExcel = downloadExcel;

        vm.loadAll();
        

        function loadAll () {
        	EventManagement.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.events = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
        
        
        function downloadExcel(id, name) {
        
        	var url = ServerURL + '/api/event/excel/' + id;
        	var filename = name + '.xls';
        	
        	var app = document.URL.indexOf( 'http://' ) === -1 && document.URL.indexOf( 'https://' ) === -1;
    		if ( app ) {
    			
	        	var path = "";
	      		if(device.platform === 'Android'){
	      			path = cordova.file.externalDataDirectory;
	      		}else{
	      			path = cordova.file.syncedDataDirectory;
	      		}
	      		
	    		window.resolveLocalFileSystemURL(path, function (directoryEntry) {
					// Parameters passed to getFile create a new file or return the file if it already exists.
					directoryEntry.getFile( filename , { create: true, exclusive: false }, function (fileEntry) {
						var fileTransfer = new FileTransfer();
						var fileURL = fileEntry.toURL();
	
						fileTransfer.download(
							url,
							fileURL,
							function (entry) {
				          		if(device.platform === 'Android'){
									cordova.InAppBrowser.open(entry.toURL(), '_system');
				          		}else{
									cordova.InAppBrowser.open(entry.toURL(), '_blank');
				          		}
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
    		 
    		            linkElement.setAttribute('href', url);
    		            linkElement.setAttribute("download", filename);
    		 
    		            var clickEvent = new MouseEvent("click", {
    		                "view": window,
    		                "bubbles": true,
    		                "cancelable": false
    		            });
    		            linkElement.dispatchEvent(clickEvent);
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
