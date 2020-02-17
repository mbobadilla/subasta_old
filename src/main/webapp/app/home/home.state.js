(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('redirect', {
        	parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            	$state.go('evento');
            }]
        }).state('home', {
        	parent: 'app',
        	url: '/home',
        	cache: false,
            data: {
                authorities: []
            },
            params: {
            	images: 'carouselImages',
            	imagesFolder: 'content/images/presentacion/',
            	urlSegundaImagen: 'Condiciones_2.jpg',
            	stateSegundaImagen: 'condiciones',
            	exportImage: 'catalogo-pdf.jpg',
            	exportPdf: '../eventos/activo/catalogo.pdf',
            	mobileExportPath: 'downloadCatalogoFile',
            	mobileExportName: 'catalogo'
			},
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html?images=carouselImages?release=4.1',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	carouselImages: ['$http', 'ServerURL', '$stateParams', function ($http, ServerURL, $stateParams) {
            		return $http({method: 'GET', url: ServerURL + '/api/' + $stateParams.images});
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('home');
                    return $translate.refresh();
                }]
            }
        }).state('condiciones', {
        	parent: 'app',
        	url: '/condiciones',
        	cache: false,
        	data: {
        		authorities: []
        	},
        	params: {
				images: 'carouselImagesCondiciones',
				imagesFolder: 'content/images/condiciones/',
            	urlSegundaImagen: 'catalogo.jpg',
            	stateSegundaImagen: 'home',
            	exportImage: 'condiciones-pdf.jpg',
            	exportPdf: '../eventos/activo/condiciones.pdf',
            	mobileExportPath: 'downloadTermsFile',
            	mobileExportName: 'condiciones'
			},
        	views: {
        		'content@': {
        			templateUrl: 'app/home/home.html?images=carouselImagesCondiciones?release=4.1',
        			controller: 'HomeController',
        			controllerAs: 'vm'
        		}
        	},
        	resolve: {
        		carouselImages: ['$http', 'ServerURL', '$stateParams', function ($http, ServerURL, $stateParams) {
            		return $http({method: 'GET', url: ServerURL + '/api/' + $stateParams.images});
                }],
        		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
        			$translatePartialLoader.addPart('home');
        			return $translate.refresh();
        		}]
        	}
        });
    }
})();
