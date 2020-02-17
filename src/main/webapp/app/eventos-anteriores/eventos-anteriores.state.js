(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
		        $stateProvider
				.state(
						'eventos-anteriores',
						{
							parent : 'app',
							url : '/eventos-anteriores',
							data : {
								authorities : []
							},
							views : {
								'content@' : {
									templateUrl : 'app/eventos-anteriores/eventos-anteriores.html?release=4.1',
									controller : 'EventosAnterioresController',
									controllerAs : 'vm'
								}
							},
							params : {
								page : {
									value : '1',
									squash : true
								},
								sort : {
									value : 'id,asc',
									squash : true
								}
							},
							resolve : {
								pagingParams : [
										'$stateParams',
										'PaginationUtil',
										function($stateParams, PaginationUtil) {
											return {
												page : PaginationUtil
														.parsePage($stateParams.page),
												sort : $stateParams.sort,
												predicate : PaginationUtil
														.parsePredicate($stateParams.sort),
												ascending : PaginationUtil
														.parseAscending($stateParams.sort)
											};
										} ],
								translatePartialLoader : [
										'$translate',
										'$translatePartialLoader',
										function($translate,
												$translatePartialLoader) {
											$translatePartialLoader
													.addPart('global');
											return $translate.refresh();
										} ]

							}
						})
				.state('eventos-anteriores-detail', {
					parent : 'eventos-anteriores',
					url : '/eventoAnterior/{idEvento}',
					cache : false,
					data : {
						authorities : []
					},
					params : {
						position : 0
					},
					views : {
						'content@' : {
							templateUrl: 'app/eventos-anteriores/evento-anterior.html?release=4.1',
			                controller: 'EventoAnteriorController',
							controllerAs : 'vm'
						}
					},
		            resolve: {
		                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
		                    $translatePartialLoader.addPart('global');
		                    return $translate.refresh();
		                }]
		            }
				}).state('eventos-anteriores-detail.youtube', {
		            url: '/youtube/{video}',
		            cache: false,
		            data: {
		                authorities: []
		            },
		            onEnter: ['$stateParams', '$state', '$uibModal', '$sce', 'GlobalService', function($stateParams, $state, $uibModal, $sce, GlobalService) {
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
		                    $state.go('eventos-anteriores-detail', null, { reload: false });
		                }, function() {
		                    $state.go('^');
		                });
		            }]
		        }).state('eventos-anteriores-detail.productDetail', {
		            url: '/productDetail/{idCaballo}',
		            cache: false,
		            data: {
		                authorities: []
		            },
		            params : {
						url: null
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
		                    $state.go('eventos-anteriores-detail', null, { reload: false });
		                }, function() {
		                    $state.go('^');
		                });
		            }]
		        }).state('eventos-anteriores-detail.product-detail', {
		            parent: 'eventos-anteriores',
		            url: '/product-detail/{idCaballo}',
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
		                    templateUrl: 'app/evento/product-detail.html',
		                    controller: 'ProductDetailController',
		                    controllerAs: 'vm'
		                }
		            }
		        });

    }
})();