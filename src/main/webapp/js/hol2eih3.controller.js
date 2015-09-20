var initController = function($scope, $http, $filter){
	console.log("initController");
	console.log(parameters);
	$scope.inputType = "text";
	console.log($scope.inputType);
//	initDocCookie($scope);
	
	$scope.setDocCookie = function(cname, cvalue){
		$scope.docCooke[cname] = cvalue;
		console.log($scope.docCooke);
		var ckeys = Object.keys($scope.docCooke);
		var dc = "";
		for(var i=0; i<ckeys.length; i++) {
			dc += (dc.length>0?"; ":"") + ckeys[i] + "=" + $scope.docCooke[ckeys[i]];
		}
		document.cookie = dc;
		console.log(document.cookie);
	}
//	$scope.setDocCookie("inputType", $scope.inputType);
	
	$scope.readMoveTodayPatients = function(){
		var url = "/readMoveTodayPatients"
		if(parameters.date){
			var url = "/readMove-"+parameters.date+"-Patients"
		}
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			console.log(data);
			$scope.moveTodayPatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	$scope.formatDateyyyyMMdd = function(d){
		return $filter('date')( d, "yyyy-MM-dd")
	}

	$scope.isMyDate = function(d){
		return parameters.date == $scope.formatDateyyyyMMdd(d);
	}

	$scope.summ = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				summ += department[fieldName]/1;
			});
		return summ;
	}

	$scope.summMinusDR = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				if(department.DEPARTMENT_ID != 5
				&& department.DEPARTMENT_ID != 22
				){
					summ += department[fieldName];
				}
			});
		return summ;
	}

	$scope.today = new Date();
	console.log( $filter('date')( $scope.today, "yyyy-MM-dd"));
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();
	
	console.log(parameters);
	console.log(parameters.date);

	if(parameters.date){
		$scope.paramDate = parameters.date;
	}
	
}
//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
hol2eih3App.controller('SaveCopeTodayPatientsCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	initController($scope, $http, $filter);
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	$scope.saveMoveTodayPatients = function(){
		var url = "/save-"+$scope.paramDate+"-Patients";
		var today = new Date();
		console.log(today.getYear()+"-"+today.getMonth()+"-"+today.getDate());
		if(!$scope.paramDate){
			url = "/save-"+today.getYear()+"-"+today.getMonth()+"-"+today.getDate()+"-Patients";
		}
		console.log(url);
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : url
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.moveTodayPatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.calc = function(departmentHol){
		console.log(departmentHol);
		var sum = 0;
		if(departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY)
			sum = departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_IN)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_IN/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_DEAD)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_DEAD/1;
		departmentHol.MOVEDEPARTMENTPATIENT_PATIENT2DAY = sum;
	}

}]);
hol2eih3App.controller('MvPatientInWeekDayCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	console.log("/readMove-day-Patients");
	initController($scope, $http, $filter);
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
}]);

// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.
hol2eih3App.controller('MovePatientsCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	console.log("/readMovePatients");
	$scope.today = new Date();
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();

	$scope.yyyyMMdd = function(d){
		return moment(d).format("YYYY-MM-DD");
	}

	// 2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	$scope.readMovePatients = function(){
		$http({ method : 'GET', url : "/readMovePatients"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
			console.log($scope.movePatients);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readMovePatients();
	
}]);
// 3  План операцій – operationsPlan.html
hol2eih3App.controller('OperationsPlanCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 3.2  Зчитування плану операцій на сьогодні – readTodayOperationsPlan
	$scope.readTodayOperationsPlan = function(){
		$http({ method : 'GET', url : "/readTodayOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readTodayOperationsPlan();
	// 3.1  Запис плану операцій на завтра – saveTomorrowOperationsPlan
	$scope.saveTomorrowOperationsPlan = function(){
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : "/saveTomorrowOperationsPlan"
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.tomorrowOperationsPlan = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

}]);
//4  Список операцій відділення по датах –  departmentOperationsList.html
hol2eih3App.controller('DepartmentOperationsListCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 4.1  Зчитування списку операцій за останні 7 днів –  readListOperationsPlan
	$scope.readListOperationsPlan = function(){
		$http({ method : 'GET', url : "/readListOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readListOperationsPlan();
}]);
