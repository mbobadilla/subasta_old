(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig)
        .run(runConfig);

    stateConfig.$inject = ['$stateProvider'];
    runConfig.$inject = ['$rootScope'];

    function stateConfig($stateProvider) {
        $stateProvider.state('evento', {
        	parent: 'app',
            url: '/event/last',
            cache: false,
            data: {
                authorities: []
            },
            params: {
            	position: 0
            },
            views: {
                'content@': {
                    templateUrl: 'app/evento/evento.html?release=4.1',
                    controller: 'EventoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            },
            onEnter: ['EventTrackerService', 'ReloadEventService', 'FinishLoteService', function(EventTrackerService, ReloadEventService, FinishLoteService) {
            	EventTrackerService.subscribe();
            	ReloadEventService.subscribe();
            	FinishLoteService.subscribe();
            }],
            onExit: ['EventTrackerService', 'ReloadEventService', 'FinishLoteService', function(EventTrackerService, ReloadEventService, FinishLoteService) {
            	EventTrackerService.unsubscribe();
            	ReloadEventService.unsubscribe();
            	FinishLoteService.unsubscribe();
            }]
        }).state('evento.youtube', {
            url: '/{video}/youtube',
            cache: false,
            data: {
                authorities: []
            },
            params: {
            	video: null,
            	loteId: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', '$sce', '$http', 'ServerURL', 'GlobalService', function($stateParams, $state, $uibModal, $sce, $http, ServerURL, GlobalService) {
            	
    	      	$http({
      				method: 'GET',
      				url: ServerURL + 'api/activityEvent/productView/' + $stateParams.loteId,
      				params: {
      					
      				}
    			  	}).then(function() {
      				console.log("Registro de lote visto exitoso");
      		  	}, function(data) {
      				console.log("Error al lote producto visto");
      				console.log(data);
      		  	});
            	
            	var url = GlobalService.getYoutubeUrl($stateParams.video);
            	
            	$sce.trustAsResourceUrl(url);
     
            	var iframe = '<iframe src="' +url+ '" style="zoom:0.60;" width="99.6%" height="800px" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>';
            	var close = '<button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="vm.clear()">&times;</button>';
            	var content = '<div class="modal-header">'+ close + '</div>' + iframe;
            	
            	$uibModal.open({
            		animation: true,
                    template: content,
                    keyboard: true,
                    controller: 'DetalleController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                    	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {
                    $state.go('evento', null, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('evento.bid', {
            cache: false,
            // sin url para que no quede en el historial
            data: {
                authorities: ['ROLE_USER']
            },
            params: {
            	loteId: null,
            	userLogin: null,
            	price: null,
            	nombre: null,
            	loteNro: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/evento/product-bid-dialog.html?release=4.1',
                    controller: 'ProductBidDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {
                	$state.go('evento', null, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('evento.lastestOffers', {
            cache: false,
            // sin url para que no quede en el historial
            data: {
                authorities: ['ROLE_ADMIN']
            },
            params: {
            	loteId: null,
            	nombreCaballo: null
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/evento/lastest-offers.html?release=4.1',
                    controller: 'LastestOffersController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {
                	$state.go('evento', null, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('evento.productDetail', {
            url: '/{idCaballo}/productDetail',
            cache: false,
            params: {
            	url: null
            },
            data: {
                authorities: []
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal, $sce) {
            	var frameStyle = '';
            	if(navigator.userAgent.match(/(iPod|iPhone|iPad)/)){
            		frameStyle = ' scrolling="no" height="1800px ';
                }else{
                	frameStyle = ' scrolling="yes" height="530px ';
                }
            	
            	var url = $stateParams.url;
            	var src = url.replace("##ID_CABALLO##", $stateParams.idCaballo);
            	
            	var iframe = '<iframe src="' + src + '" width="100%"  ' + frameStyle + ' frameborder="0"></iframe>';
            	
             	var close = '<button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="vm.clear()">&times;</button>';
            	var content = '<div class="modal-header">'+ close + '</div> ' + iframe ;
            	
            	console.log(iframe);
            	
            	$uibModal.open({
            		animation: true,
                    template: content,
                    keyboard: true,
                    controller: 'DetalleController',
                    controllerAs: 'vm',
                    size: 'lg',
                    resolve: {
                    	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {
                    $state.go('evento', null, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('product-detail', {
            parent: 'evento',
            url: '/productDetail/{idCaballo}',
            cache: false,
            data: {
                authorities: []
            },
            params: {
            	position: 0,
            	url: null
            },
            views: {
                'content@': {
                    templateUrl: 'app/evento/product-detail.html?release=4.1',
                    controller: 'ProductDetailController',
                    controllerAs: 'vm'
                }
            }
        });
    }
    
    function runConfig($rootScope) {
        $rootScope.$on("$stateChangeSuccess",  function(event, toState, toParams, fromState, fromParams) {
        	$rootScope.position = 0;
            if(fromState.name == 'product-detail' && toState.name == 'evento') {
            	console.log("To position: ", toParams.position)
            	$rootScope.position = toParams.position;
            }
        });
    }
})();
