var hol2eih4v1App = angular.module('hol2eih4v1App', ['textAngular', 'ui.bootstrap']);
hol2eih4v1App.factory();
var parameters = {};
if(window.location.search){
	angular.forEach(window.location.search.split("?")[1].split("&"), function(value, index){
		var par = value.split("=");
		parameters[par[0]] = par[1];
	});
}

hol2eih4v1App.controller('MvDepartmentPatientDay', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("/mvDepartmentPatientDay.html");
	initController($scope, $http, $filter);
	console.log($scope.param);
	$scope.viewPatientList = false;
	console.log($scope.viewPatientList);
	$scope.changePatientList = function(){
		console.log($scope.viewPatientList);
		$scope.viewPatientList = !$scope.viewPatientList;
		console.log($scope.viewPatientList);
	}
	$scope.readDepartmentPatientDay = function(){
		if(parameters.date && parameters.d10t){
			var url = '/v/department-'+parameters.d10t+'-PatientsMove-'+parameters.date;
			console.log(url);
			$http({ method : 'GET', url : url
			}).success(function(data, status, headers, config) {
				$scope.departmentPatientDay = data;
				console.log($scope.departmentPatientDay);
				//initDateVariables();
//				isRole("ruh", $scope.departmentPatientDay);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
			// v.02
			var url2 = '/v/hol1F007Day-'+parameters.date+'-department-'+parameters.d10t;
			console.log(url2);
			$http({ method : 'GET', url : url2
			}).success(function(data, status, headers, config) {
				$scope.hol1F007Day = data;
				console.log($scope.hol1F007Day);
				//initDateVariables();
				isRole("ruh", $scope.hol1F007Day);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
		}else{
			console.error('false parameters, use data and d10t');
		}
	};
	
	$scope.readDepartmentPatientDay();
	$scope.isParamDate = function(month, day){
		if($scope.paramDateDate.getDate() == day){
			if(month == $scope.paramDateDate.getMonth() + 1){
				return true;
			}
		}
		return false;
	}
	$scope.monthDayDate = function(month, day){
		var d2 = new Date(2016, month - 1, day);
		return d2;
	}
	$scope.passedDays = function(m,d){
		return new Date() > $scope.monthDayDate(m,d);
	}
	$scope.satSanDay = function(m,d){
		return $scope.monthDayDate(m,d).getDay() == 6 || $scope.monthDayDate(m,d).getDay() == 0;
	}
	$scope.monthDay = function(m,d){
		var dt = new Date(new Date().getFullYear(), m, 0).getDate();
		if(d>dt){
			return;
		}
		return d;
	}
}]);

var initController = function($scope, $http, $filter){
	console.log("initController");
	$scope.param = parameters;
	$scope.isRoleRuh = false;

	isRole = function(role, map){
		angular.forEach(map.principal.authorities, function(authoritie, key) {
			if(authoritie.authority.indexOf(role) >= 0){
				$scope.isRoleRuh = true;
			}
		});
	}

}
