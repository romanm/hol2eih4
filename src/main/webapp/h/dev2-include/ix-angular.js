(function(angular) {
	'use strict';
	angular.module('includeExample', [])
	.controller('ExampleController', ['$scope', '$http', function($scope, $http) {
		console.log("---------------");
		declareFunctions($scope);
		$scope.param = parameters;
		$scope.model = {};
		declareFileCtrl($scope);
		initCtrl($scope, $http);
		$scope.number = 123;
		$scope.hello = "Hello World!";
	}]);
})(window.angular);

var initCtrl = function($scope, $http){
	console.log("-------------------");
	
	
	$http({ method : 'GET', url : "/v/readAuthorityUser"
	}).success(function(model, status, headers, config) {
		$scope.model.authority = model;
		if($scope.model.authority.departmentId){
			$http({ method : 'GET', url : "/v/department-patient/"+$scope.model.authority.departmentId
			}).success(function(model, status, headers, config) {
				$scope.model.departmentPatient = model;
			}).error(function(model, status, headers, config) {
				$scope.error.push(model);
			});
		}
	}).error(function(model, status, headers, config) {
		$scope.error.push(model);
	});
	
	$scope.$watch("model.authority", function handleChange( newValue, oldValue ) {
		if(newValue != null){
			$http({ method : 'GET', url : "/v/operation/start-lists"
			}).success(function(model, status, headers, config) {
				$scope.model.operationEditLists = model;
				console.log($scope.model.operationEditLists);
				if($scope.model.operationEditLists.principal){
					for (var i = 0; i < $scope.model.operationEditLists.departmentOperation.length; i++) {
						if( $scope.model.operationEditLists.departmentOperation[i].department_id == $scope.model.authority.departmentId ){
							$scope.model.authority.department = $scope.model.operationEditLists.departmentOperation[i];
						}
						
					}
					for (var i = 0; i < $scope.model.operationEditLists.departmentSurgery.length; i++) {
						if( $scope.model.operationEditLists.departmentSurgery[i].personal_id == $scope.model.authority.personalId ){
							$scope.model.authority.personal = $scope.model.operationEditLists.departmentSurgery[i];
						}
					}
				}

			}).error(function(model, status, headers, config) {
				$scope.error.push(model);
			});
		}
	});
	
	if($scope.param.ix){
		console.log("/v/ix/"+$scope.param.ix+window.location.search);
		$http({ method : 'GET', url : "/v/ix/"+$scope.param.ix+window.location.search
		}).success(function(model, status, headers, config) {
			$scope.ix = model;
			console.log($scope.ix);
			angular.forEach($scope.ix.operationHistoryList, function(oh, key) {
				$scope.initOperation(oh);
			});
		}).error(function(model, status, headers, config) {
			$scope.error.push(model);
		});
	}
}

var declareFunctions = function($scope){
	$scope.objectKeys = function(obj){
		return Object.keys(obj);
	}
}

var parameters = {};
if(window.location.search){
//	$.each(window.location.search.split("?")[1].split("&"), function(index, value){
	angular.forEach(window.location.search.split("?")[1].split("&"), function(value, index){
		var par = value.split("=");
		parameters[par[0]] = par[1];
	});
}
