//var initController = function($scope, $http, $filter, $cookies){
var initController = function($scope, $http, $filter){
	console.log("initController");
	$scope.param = parameters;
	$scope.inputType = "text";
	$scope.isRoleRuh = false;
//	initDocCookie($scope);
	
	var isRole = function(role){
		console.log($scope.moveTodayPatients);
		angular.forEach($scope.moveTodayPatients.principal.authorities, function(authoritie, key) {
			if(authoritie.authority.indexOf(role) >= 0){
				$scope.isRoleRuh = true;
			}
		});
	}
	initDateVariables = function(){
		var today = new Date($scope.moveTodayPatients.today);
//		$cookies.put('year', today.getFullYear());
		$scope.excelFileName = "pyx-" +today.getFullYear() + "-v.2.xls";
	}
	
	$scope.readMoveTodayPatients = function(){
		var url = "/readMoveTodayPatients"
		if(parameters.date){
			var url = "/readMove-"+parameters.date+"-Patients"
		}
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.moveTodayPatients = data;
			initDateVariables();
			isRole("ruh");
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
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();
	

	if(parameters.date){
		$scope.paramDate = parameters.date;
	}else{
		$scope.paramDate = $filter('date')(new Date(), "yyyy-MM-dd");
	}
	$scope.paramDateDate = new Date(Date.parse($scope.paramDate));
	$scope.addDay = function(date, day){
		var dateOffset = (24*60*60*1000) * day; //5 days
		var myDate = new Date();
		myDate.setTime(date.getTime() + dateOffset);
		return myDate;
	}
}

var initCtrl = function($scope, $http){
	$scope.viewTab = viewTab;
	$scope.error = [];
	$scope.param = parameters;
	if(!$scope.param.ix){
		if(window['eixId'] != undefined){
			$scope.param.ix = eixId; 
		}
	}
	$scope.model = {};

	$scope.errorHandle = function(response){
		$scope.error.push(response);
		console.log($scope.error);
	}

	$http({ method : 'GET', url : "/v/show-tables-columns"
	}).success(function(model, status, headers, config) {
		$scope.showTablesColumns = model;
		if($scope.showTablesColumns.principal != null){
			$scope.showTablesColumns.notNulls = {};
			angular.forEach($scope.showTablesColumns.tables, function(tableColumns, keyTableName) {
				$scope.showTablesColumns.notNulls[keyTableName] = {};
				angular.forEach(tableColumns, function(column) {
					if(column.Null=="NO"){
						$scope.showTablesColumns.notNulls[keyTableName][column.Field] = column;
					}
				});
			});
		}
	}).error(function(model, status, headers, config) {
		$scope.error.push(model);
	});
	
	$scope.model = {};

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

	$scope.setDurationHHMM = function(oh, min){
		var hour = (min - min%60)/60;
		var hourStr = (hour<10?"0":"")+hour;
		var minRest = min - hour*60;
		var minStr = (minRest<10?"0":"")+minRest;
		oh.durationHHMM = hourStr+":"+minStr; 
		oh.operationDurationMin = min;
	}
	$scope.initOperation = function(operation){
		$scope.setDurationHHMM(operation, operation.operation_history_duration/60);
	}

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
// ------------IxCtrl
hol2eih3App.controller('IxCtrl', function ($scope, $http, $filter, $sce, $interval) {
	console.log("IxCtrl");
	initCtrl($scope, $http);
	//$interval(frameCtrl, 3000);
	
});
// ------------IxCtrl END
// ------------OperationCodeCtrl
hol2eih3App.controller('OperationCodeCtrl', ['$scope', '$http', '$filter', '$sce', '$interval'
		, function ($scope, $http, $filter, $sce, $interval) {
	console.log("OperationCodeCtrl");
	$scope.fieldsOperationNotNull = ["procedure_moz_id","icd_id","anestesia_id","operation_result_id"];
	$scope.fieldsOperation = {
		beginOp: "Дата/почато - закінчено/тривалість"
		,surgery: "Хірург"
		,operation : "Операція"
		,icd : "Діагноз при операції"
		,department : "Відділення"
		,anesthetist: "Анестезіолог"
		,anestesia: "Анестезія"
		,result: "Результат"
		,complication: "Ускладнення"
	};

	initCtrl($scope, $http);

	$scope.openToEditPrevious = function(){
		var list = Object.keys($scope.fieldsOperation);
		var currentPosition = list.indexOf($scope.openedToEdit);
		if(currentPosition == 0){
			currentPosition = list.length;
		}
		var previous = list[currentPosition - 1];
		console.log("openToEditPrevious"+$scope.openedToEdit+"/"+list.length+"/"+currentPosition);
		$scope.openToEdit(previous);
	}
	
	$scope.openToEditNext = function(){
		var list = Object.keys($scope.fieldsOperation);
		var currentPosition = list.indexOf($scope.openedToEdit);
		var next = list[currentPosition + 1];
		console.log("openToEditNext "+$scope.openedToEdit+"/"+list.length+"/"+currentPosition);
		$scope.openToEdit(next);
	}

	$scope.openToEdit = function(fieldName){
		$scope.openedToEdit = fieldName;
		console.log($scope.openedToEdit);
		if($scope.openedToEdit == 'operation'){
			$scope.openChildProcedureDb($scope.procedureOperation);
		}else
		if($scope.openedToEdit == 'icd'){
			if($scope.operationCodeCtrl.seekIcd == null){
				if($scope.operationCodeCtrl.seekOperation != null){
					$scope.operationCodeCtrl.seekIcd =
						$scope.operationCodeCtrl.seekOperation;
				}
			}
		}
	}
	//------procedure------Operation-----------
	$scope.changeAnesthetist = function(newValue){
		console.log(newValue);
		$scope.operationHistoryToEdit.anesthetist_id = newValue.personal_id;
		if(newValue.anesthetist_id)
			$scope.operationHistoryToEdit.anesthetist_id = newValue.anesthetist_id;
		$scope.operationHistoryToEdit.anesthetist_name = newValue.personal_username;
		console.log($scope.operationHistoryToEdit);
		updateOperationHistory(null, "anesthetist_id", "int");
	}
	$scope.changeAnesthesia = function(newValue){
		$scope.operationHistoryToEdit.anestesia_id = newValue.anestesia_id;
		$scope.operationHistoryToEdit.anestesia_name = newValue.anestesia_name;
		updateOperationHistory(null, "anestesia_id", "int");
	}
	$scope.changeComplication = function(newValue){
		console.log("-----------");
		console.log(newValue);
		$scope.openedToEdit = "complication";
		$scope.operationHistoryToEdit.operation_complication_id = newValue.operation_complication_id;
//		$scope.operationHistoryToEdit.operation_complication_id = newValue.operation_complication_result_id;
		$scope.operationHistoryToEdit.operation_complication_name = newValue.operation_complication_name;
		console.log($scope.operationHistoryToEdit);
		updateOperationHistory(null, "operation_complication_id", "int");
	}
	$scope.changeResult = function(newValue){
		$scope.operationHistoryToEdit.operation_result_id = newValue.result_id;
		$scope.operationHistoryToEdit.operation_result_name = newValue.result_name;
		updateOperationHistory(null, "operation_result_id", "int");
	}
	$scope.changeDepartment = function(newValue){
		$scope.operationHistoryToEdit.department_id = newValue.department_id;
		$scope.operationHistoryToEdit.department_name = newValue.department_name;
		updateOperationHistory(null, "department_id", "int");
	}
	$scope.changeSurgery = function(newValue){
		if($scope.operationHistoryToEdit){
			$scope.operationHistoryToEdit.personal_id = newValue.personal_id;
			$scope.operationHistoryToEdit.surgery_name = newValue.personal_username;
			updateOperationHistory(null, "personal_id", "int");
		}
	}
	//-------------OperationSaveTimer-------------------------
	$scope.OperationSaveTimer = null;
	$scope.StartOperationSaveTimer = function () {
		$scope.OperationSaveTimerMessage = "Введення нової операції розпочато. ";
		var stopwatch = new Date();
		var fixFirstSave = null;
		stopwatch.setHours(stopwatch.getHours() - stopwatch.getTimezoneOffset()/60);

		$scope.OperationSaveTimer = $interval(function () {
			$scope.myFieldsOperationNotNull = [];
			$scope.OperationSaveTimerMessage = "" ;
			for (var i = 0; i < $scope.fieldsOperationNotNull.length; i++){
				if($scope.operationHistoryToEdit[$scope.fieldsOperationNotNull[i]] == null){
					$scope.myFieldsOperationNotNull.push($scope.fieldsOperationNotNull[i]);
				}
			}
			if($scope.myFieldsOperationNotNull.length > 0){
				$scope.OperationSaveTimerMessage += "Залишилось заповнити " + $scope.myFieldsOperationNotNull.length + " невідмінних поля. ";
			}else if(!$scope.operationHistoryToEdit.operation_history_id){
				if(!fixFirstSave)
					fixFirstSave = $filter('date')(new Date() - stopwatch, 'HH:mm:ss')
					$scope.OperationSaveTimerMessage += " Пішов запис після (" + fixFirstSave + ").";
				if(!$scope.operationHistoryToEdit.sendetToSave){
					$scope.operationHistoryToEdit.sendetToSave = true;
					$http.post('/insertOperationHistory', $scope.operationHistoryToEdit).
					then(function(response) {
						console.log("---insertOperationHistory------success--------");
						$scope.operationHistoryToEdit = response.data;
						$scope.operationHistoryToEdit.operationHistoryStart = new Date($scope.operationHistoryToEdit.operation_history_start);
						console.log($scope.operationHistoryToEdit);
					}, function(response) {
						console.log("---insertOperationHistory-------erros-------");
					});
				}else{
					$scope.OperationSaveTimerMessage += " Очікування."  ;
				}
			}else{
				$scope.OperationSaveTimerMessage += " Операція записана (" + fixFirstSave + ").";
			}
			var time = $filter('date')(new Date() - stopwatch, 'HH:mm:ss');
			$scope.OperationSaveTimerMessage += " Витрачено часу. " + time;
		}, 2700);
	};

	$scope.StopOperationSaveTimer = function () {
		$scope.OperationSaveTimerMessage = "Timer stopped.";
		if (angular.isDefined($scope.OperationSaveTimer)) {
			$interval.cancel($scope.OperationSaveTimer);
		}
	};
	//-------------OperationSaveTimer-------------------------END
	$scope.isProcedureGroup = function (code){
		return !isNaN(code.substring(0,1));
	}
	$scope.procedureOperation = {procedure_moz_id:3552};// group 7 -- хірургічні операції
	$scope.openChild = function (procedure){
		procedure.open = !procedure.open;
	}
	checkToSaveProcedure = function (procedure){
		if((procedure.PROCEDURE_CODE && procedure.PROCEDURE_CODE.split(".").length == 2)
				|| (procedure.procedure_moz_code && procedure.procedure_moz_code.split(".").length == 2)){
			$scope.toSaveProcedure(procedure)
		}
	}
	$scope.openChildProcedureDb = function (procedure){
		$scope.openChild(procedure);
		if(procedure.procedure == null){
			var siblingLevel = procedure.procedure_moz_id;
			$http.get("/v/siblingProcedureOperation/"+siblingLevel).success(function(response) {
				procedure.procedure = response;
				if(response.length == 0){
					$scope.toSaveProcedure(procedure);
					//checkToSaveProcedure(procedure);
				}
			}).error($scope.errorHandle);
		}else{
			checkToSaveProcedure(procedure);
		}
	}

	$scope.procedureViewType = "navigation";
	$scope.gotoNavigation = function (){
		console.log($scope.procedureViewType);
		console.log("----gotoNavigation---------------");
		$scope.procedureViewType = "navigation";
		console.log($scope.procedureViewType);
	}
	$scope.seekUpdate = function (){
		console.log($scope.procedureViewType);
		console.log("-----------seekUpdate--------");
		$scope.procedureViewType = "seek";
		console.log($scope.procedureViewType);
	}
	var openProcedureTreeTo = function (code, level, procedureList){
		angular.forEach(procedureList, function(procedure) {
			if(procedure.procedure_moz_code == code.substring(0,level)){
				$scope.openChild(procedure);
				if(procedure.procedure == null){
					$http.get("/v/siblingProcedureOperation/"+procedure.procedure_moz_id).success(function(response) {
						procedure.procedure = response;
						if(procedure.procedure.length != 0){
							if(code.length > level){
								openProcedureTreeTo(code, level+1, procedure.procedure);
							}
						}
					})
				}
			}
		});
	}
	$scope.gotoNavigationGroup = function (procedure){
		console.log("----gotoNavigationGroup---------------");
		console.log($scope.procedureOperation.procedure);
		openProcedureTreeTo(procedure.PROCEDURE_CODE, 2, $scope.procedureOperation.procedure);
		console.log($scope.procedureOperation.procedure);
		$scope.procedureViewType = "navigation";
	}
	
	//------procedure------Operation-----------END
	$scope.toSaveIcd = function (icd){
		console.log(icd);
		$scope.icdToSave = icd;
		$scope.operationHistoryToEdit.icd_id = icd.icd_id;
		$scope.operationHistoryToEdit.icd_start = icd.icd_start;
		$scope.operationHistoryToEdit.icd_end = icd.icd_end;
		$scope.operationHistoryToEdit.icd_code = icd.icd_code;
		$scope.operationHistoryToEdit.icd_name = icd.icd_name;
		updateOperationHistory("icd", "icd_id");
	}
	$scope.toSaveProcedure = function (newValue){
		if(newValue.procedure_moz_id){
			$scope.operationHistoryToEdit.procedure_moz_id = newValue.procedure_moz_id;
			$scope.operationHistoryToEdit.procedure_moz_name = newValue.procedure_moz_name;
			$scope.operationHistoryToEdit.procedure_moz_code = newValue.procedure_moz_code;
		}else
		if(newValue.PROCEDURE_ID){
			$scope.operationHistoryToEdit.procedure_moz_id = newValue.PROCEDURE_ID;
			$scope.operationHistoryToEdit.procedure_moz_name = newValue.PROCEDURE_NAME;
			$scope.operationHistoryToEdit.procedure_moz_code = newValue.PROCEDURE_CODE;
		}
		updateOperationHistory("procedure", "procedure_moz_id");
	}

	//--------update---operation_history--db----------------------
	var updateOperationHistory = function(groupName, fieldName, fieldType){
		if($scope.operationHistoryToEdit && $scope.operationHistoryToEdit.operation_history_id){
			var putUrl = "/operation-history";
			if(fieldType == "int"){
				putUrl += "-int-set-"+fieldName + "-"
				+ $scope.operationHistoryToEdit[fieldName] +
				"-where-" + $scope.operationHistoryToEdit.operation_history_id;
			}else{
				putUrl += "-set-"+ groupName + "-" 
				+ $scope.operationHistoryToEdit[fieldName] +
				"-where-" + $scope.operationHistoryToEdit.operation_history_id;
			}
			console.log(putUrl);
			$http.put(putUrl).then(function(response) {
				console.log(putUrl + "--------success--------");
				console.log(response);
			}, function(response) {
				console.log(putUrl + "--------erros-------");
				console.log(response);
			});
		}
	}

	$scope.$watch("operationHistoryToEdit.p.rocedure_moz_id", function handleChange( newValue, oldValue ) {
		if(oldValue != null){
			console.log("operationHistoryToEdit.procedure_moz_id");
			updateOperationHistory("procedure", "procedure_moz_id");
		}
	});

	$scope.$watch("operationHistoryToEdit.i.cd_id", function handleChange( newValue, oldValue ) {
		if(oldValue != null){
			console.log("operationHistoryToEdit.icd_id");
			updateOperationHistory("icd", "icd_id");
		}
	});
	var operationHistoryIntGroup = [
	                            	"operationHistoryToEdit.operation_complication_id"
	                            	,"operationHistoryToEdit.anestesia_id"
	                            	]; 
	$scope.$watchGroup(operationHistoryIntGroup, function handleChange( newValues, oldValues ) {
		console.log("--watch--operationHistoryToEdit---int----");
		if(newValues != null){
			for (var i = 0; i < newValues.length; i++){
				if(oldValues[i] != null){
					if(newValues[i] != oldValues[i]){
						console.log(i+"/"+newValues[i]+"/"+oldValues[i]);
					}
				}
			}
		}
	});
	$scope.$watch("operationHistoryToEdit.o.peration_complication_id", function handleChange( newValue, oldValue ) {
		console.log("--watch-----operation_complication_id----");
		if(oldValue != null){
			console.log("operationHistoryToEdit.operation_complication_id");
			updateOperationHistory(null, "operation_complication_id", "int");
		}
	});
	//--------update---operation_history--db----------------------END

	var setEndOperation = function(min){
		$scope.setDurationHHMM($scope.operationHistoryToEdit, min);
		$scope.operationHistoryToEdit.operation_history_end = 
			$scope.operationHistoryToEdit.operation_history_start + min*60*1000;
		$scope.operationHistoryToEdit.operation_history_duration = min*60;
	}
	$scope.$watch("operationHistoryToEdit.operationHistoryStart", function handleChange( newValue, oldValue ) {
		if($scope.operationHistoryToEdit != null)
			if($scope.operationHistoryToEdit.operation_history_start != null){
				$scope.operationHistoryToEdit.operation_history_start = $scope.operationHistoryToEdit.operationHistoryStart.getTime();
				setEndOperation($scope.operationHistoryToEdit.operationDurationMin);
			}
	});
	$scope.$watch("operationHistoryToEdit.operationDurationMin", function handleChange( newValue, oldValue ) {
		if(newValue != null)
			setEndOperation(newValue);
	});

	$scope.addOperation = function(){
		console.log("----------------");
		$scope.openedToEdit = "beginOp";
		//перевірка на наявність відкритої і не збереженої операції
		for (var i = 0; i < $scope.ix.operationHistoryList.length; i++)
			if(!$scope.ix.operationHistoryList[i].operation_history_id)
				return;
		console.log("----------------");
		$scope.operationHistoryToEdit = {};
		$scope.ix.operationHistoryList.push($scope.operationHistoryToEdit);
		//створити запис операції з інформації про колонки таблиці operation_history
		angular.forEach($scope.showTablesColumns.tables.operation_history, function(oh, key) {
			$scope.operationHistoryToEdit[oh.Field] = null;
		});
		//додати дату операції як поточна дата
		$scope.operationHistoryToEdit.history_id = $scope.param.ix;
		$scope.operationHistoryToEdit.operationHistoryStart = new Date();
		$scope.operationHistoryToEdit.operation_history_start = $scope.operationHistoryToEdit.operationHistoryStart.getTime();
		$scope.operationHistoryToEdit.operation_history_duration = 60; //1m=60s
		console.log($scope.operationHistoryToEdit.operation_history_end);
		$scope.operationHistoryToEdit.personal_id = $scope.model.authority.personal.personal_id;
		$scope.operationHistoryToEdit.surgery_name = $scope.model.authority.personal.personal_username;
		$scope.operationHistoryToEdit.department_id = $scope.model.authority.department.department_id;
		$scope.operationHistoryToEdit.department_name = $scope.model.authority.department.department_name;
		$scope.StartOperationSaveTimer();
		$scope.initOperation($scope.operationHistoryToEdit);
	}
	
	$scope.$watch("operationCodeCtrl.seekIcd", function handleChange( newValue, oldValue ) {
		if(newValue != null && newValue.length > 0){
			var seekText = newValue.replace(".","-");
			var getUrl = "/v/seekIcd/"+seekText;
			console.log(getUrl);
			$http.get(getUrl).success(function(response) {
				$scope.seekIcd = response;
				console.log($scope.seekIcd);
			});
		}
	})
	$scope.$watch("operationCodeCtrl.seekOperation", function handleChange( newValue, oldValue ) {
		if(newValue != null && newValue.length > 0){
			$scope.procedureViewType = 'seek';
			var seekText = newValue.replace(".","-");
			var getUrl = "/v/seekProcedureOperation/"+seekText;
			console.log(getUrl);
			$http.get(getUrl).success(function(response) {
				$scope.seekProcedure = response;
				console.log($scope.seekProcedure);
			});
		}
	})

	$scope.useOperation = function(operationHistory){
		$scope.operationHistoryToEdit = operationHistory;
		console.log("useOperation");
	}

	var initDurationOp = function(operation){
		if(!operation.durationOpMin){
			operation.durationOpMin =
				(operation.operation_history_end - operation.operation_history_start)/1000/60;
		}
	}
	$scope.loginToThisPage = function(){
		return "/throughlogin/gotopage/h/operation-code.html"+window.location.search;
	};

}]);
// ------------OperationCodeCtrl END
hol2eih3App.controller('HomeCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("HomeCtrl");
	$http({ method : 'GET', url : "/v/readHome"+window.location.search
	}).success(function(model, status, headers, config) {
		$scope.model = model;
		console.log($scope.model);
	}).error(function(model, status, headers, config) {
		$scope.error = model;
	});
}]);
//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
//hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//, function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	initController($scope, $http, $filter);
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	$scope.saveMoveTodayPatients = function(){
		var url = "/save-"+$scope.paramDate+"-Patients";
		var today = new Date();
		console.log(today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate());
		if(!$scope.paramDate){
			url = "/save-"+today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate()+"-Patients";
		}
		console.log(url);
		console.log($scope.moveTodayPatients);
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
var fileUploadApp = angular.module('fileUploadApp', ['ngFileUpload']);
fileUploadApp.controller('fileUploadCtrl', ['$scope', 'Upload', '$timeout', '$http', function ($scope, Upload, $timeout, $http) {
	console.log("UploadExcelCtrl");

	$scope.setFile = function(e){
		if(e.picFile){
			console.log("---------------");
			console.log(e.picFile.name);
			console.log("---------------");
		}
	}

	$scope.uploadPic = function(file) {
		console.log("---------------");
		console.log(file);
		console.log(file.name);
		console.log("---------------");
		file.upload = Upload.upload({
//			url: 'https://angular-file-upload-cors-srv.appspot.com/upload',
			url: '/upload',
//			url: '/upload2',
			data: {file: file, fileName: file.name},
		});

		file.upload.then(function (response) {
			console.log("file.upload.then 1 param");
			console.log(response);
			$timeout(function () {
				file.result = response.data;
			});
		}, function (response) {
			console.log("file.upload.then 2 param");
			console.log(response);
			if (response.status > 0)
				$scope.errorMsg = response.status + ': ' + response.data;
		}, function (evt) {
			console.log("file.upload.then 3 param");
			console.log(evt);
			console.log("------------------------");
//			Math.min is to fix IE which reports 200% sometimes
			file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
		});
	}

}]);

//hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                         , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementH2Ctrl");
	var url = "/r/readBedDayH2-2-2";
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		console.log($scope.bedDay);
//			initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}]);

var initQuartal = function($scope, $http){
	$scope.eqMonth = function(){
		console.log(" - "+$scope.minMonth+" - "+$scope.maxMonth);
		var url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&department_id=" + $scope.param.department_id+"&type="+$scope.eqMonthType;
		window.location.href = url;
	}
eqMonth = function(){
	var url1 = $scope.url1;
	var url = url1 + "-" + $scope.minMonth + "-" + $scope.maxMonth + "-" + $scope.param.department_id;
	console.log(url);
	console.log($scope.showExpandIcd10);
//	alert(url)
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		console.log($scope.bedDay);
		if($scope.param.viddilennja){
			$scope.bedDayOfMonthMySql 
			= $scope.bedDay.bedDayOfMonthMySql[$scope.param.viddilennja]
			console.log($scope.bedDayOfMonthMySql);
			mmArray();
		}
//		initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}
}

var initReport = function($scope){
	$scope.param = parameters;
	console.log($scope.param);
	$scope.eqMonthType = "month";
	if(parameters.type){
		$scope.eqMonthType = parameters.type;
	}
	var today = new Date();
	var mm = today.getMonth();
	$scope.yyyy = today.getFullYear();
	if(mm==0) mm=1;
	$scope.minMonth = mm;
	$scope.maxMonth = mm;
	if(parameters.m1){
		$scope.minMonth = parameters.m1;
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = parameters.m1;
		}else if(parameters.m2){
			$scope.maxMonth = parameters.m2;
		}
	}
	
	$scope.error = [];
	
	mmArray = function(){
		if($scope.param.department_id > 0){
			$scope.mmArray =[];
			for (var i = $scope.minMonth*1; i <= $scope.maxMonth; i++) {
				$scope.mmArray.push(i);
			}
			console.log($scope.mmArray);
			if($scope.bedDay){
				$scope.mmNewArray =[];
				if($scope.bedDay.bedDayDepartmentMySql){
					if($scope.mmArray.length > $scope.bedDay.bedDayDepartmentMySql.length){
						console.log($scope.bedDay.bedDayDepartmentMySql.length);
						var mmNew = $scope.mmArray.length - $scope.bedDay.bedDayDepartmentMySql.length;
						for (var i = 1; i <= mmNew; i++) {
							$scope.mmNewArray.push(i);
						}
						console.log($scope.mmNewArray);
					}
				}
			}
		}
	}
	
	$scope.isWaleMonth = function(month){
		return month >= $scope.minMonth && month <= $scope.maxMonth;
	}
	$scope.setMonth = function(month){
		console.log($scope.eqMonthType);
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = month;
			$scope.minMonth = month;
		}else{
			if($scope.minMonth == null){
				$scope.minMonth = month;
			}
			if($scope.maxMonth == null){
				$scope.maxMonth = month;
			}
			if(month < $scope.minMonth){
				$scope.minMonth = month;
			}else if(month > $scope.maxMonth){
				$scope.maxMonth = month;
			}else if((month - $scope.minMonth) < ($scope.maxMonth - month)){
				$scope.minMonth = month;
			}else if((month - $scope.minMonth) > ($scope.maxMonth - month)){
				$scope.maxMonth = month;
			}else{
				$scope.maxMonth = month;
				$scope.minMonth = month;
			}
		}
		console.log(month+" - "+$scope.minMonth+" - "+$scope.maxMonth);
		mmArray();
	}
	
	$scope.eqMonth = function(){
		console.log(" - "+$scope.minMonth+" - "+$scope.maxMonth);
//	eqMonth();
		var url = "?department_id="+parameters.department_id + "&m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&type="+$scope.eqMonthType;
		if(!(parameters.department_id > 0))
			url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&type="+$scope.eqMonthType;
//		window.open();
		window.location.href = url;
	}
	
	$scope.months = {
		'nominative': '_січень_лютий_березень_квітень_травень_червень_липень_серпень_вересень_жовтень_листопад_грудень'.split('_'),
		'accusative': '_січня_лютого_березня_квітня_травня_червня_липня_серпня_вересня_жовтня_листопада_грудня'.split('_')
	};
}

//hol2eih3App.controller('K1Icd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                       , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('K1Icd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("K1Icd10Ctrl");
	initReport($scope);
	$scope.icd10Head = [
		{"title":"","name":"МКХ-10","key":"ICD_CODE"}
		,{"title":"","name":"Діагноз","key":"ICD_NAME"}
		,{"title":"","name":"Кількість","key":"CNT"}
	];

	eqMonth = function(){
				var url1 = "/r/readIcd10K1-";
		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.k1 = data;
			console.log($scope.k1);
		}).error(function(data, status, headers, config) {
			$scope.error.push(data);
			console.log($scope.error);
		});
	}

	eqMonth();
	
}]);

//hol2eih3App.controller('DepartmentMotionCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMotionCtrl', [ '$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMotionCtrl");
	$scope.url1 = "/r/readDepartmentMotion";
	initReport($scope);
	initQuartal($scope, $http);

	eqMonth();

}]);

//hol2eih3App.controller('DepartmentAdressCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentAdressCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentAdressCtrl");
	$scope.url1 = "/r/readDepartmentAdress";
	initReport($scope);
	initQuartal($scope, $http);
	$scope.bedDayHead = [
 {"title":"","name":"Райони","key":""}
 ,{"title":"Поступило з направленням","name":"з напр.","key":"with"}
 ,{"title":"Поступило без направлення","name":"без напр.","key":"without"}
 ,{"title":"Переведений з іншого відділення","name":"пер. з","key":"from_dep"}
 ,{"title":"Переведений в інше відділення","name":"пер. в","key":"to_dep"}
 ,{"title":"Виписаний","name":"вип.","key":"remove"}
 ,{"title":"Померлі","name":"пом.","key":"dead"}
 ];

eqMonth();

}]);

//hol2eih3App.controller('DepartmentIcd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                               , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentIcd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentIcd10Ctrl");
	initReport($scope);
	console.log($scope.param);
	console.log($scope.param.showExpandIcd10==1);
	$scope.url1 = $scope.param.showExpandIcd10==1?"/r/readDepartmentIcd10":"/r/readDepartmentIcd10Group";
	initQuartal($scope, $http);

$scope.bedDayHead = [
	{"title":"","name":"МКХ10","key":""}
	,{"title":"","name":"Місто","key":"misto"}
	,{"title":"","name":"Село","key":"selo"}
	,{"title":"Поступило з направленням","name":"з напр.","key":"referral_has"}
	,{"title":"Поступило без направлення","name":"без напр.","key":"referral_self"}
	,{"title":"Переведений з іншого відділення","name":"пер. з","key":"referral_hol"}
	,{"title":"Переведений в інше відділення","name":"пер. в","key":"remove_hol"}
	,{"title":"Виписаний","name":"вип.","key":"remove_true"}
	,{"title":"Померлі","name":"пом.","key":"remove_dead"}
];

$scope.clickShowAllIcd10 = function(){
	console.log("showAllIcd10 "+$scope.showAllIcd10+"; showExpandIcd10 "+$scope.showExpandIcd10);
}

$scope.showIcd10 = function(bedDayOfMonthMySql){
	if($scope.showAllIcd10)
		return true;
	var showIcd10 = bedDayOfMonthMySql.misto_cnt + bedDayOfMonthMySql.selo_cnt > 0;
	return showIcd10;
}

eqMonth();

}]);

//hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                            , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementCtrl");
	initReport($scope);
	
	$scope.bedDayHead = [
		{"title":"","name":"Відділення","key":""}
		,{"title":" (ліжка)","name":"Ліжок на штаті","key":"department_bed"}
		,{"title":"Поступило (люди)","name":"Поступило","key":"in_clinic"}
		,{"title":"Виписано (люди)","name":"Виписано","key":"out_clinic"}
		,{"title":"Переведено в інші відділення (люди)"
			,"name":"П. в інші від.","key":"in_dep"}
		,{"title":"Переведено з інші відділення (люди)"
			,"name":"П. з інші від.","key":"out_dep"}
		,{"title":"Померло (люди)","name":"Померло","key":"dead"}
		,{"title":"Лікарняна летальність (%)","name":"Л-на летальність","key":"mortality"}
		,{"title":"Лікувалось хворих (люди)","name":"Лік. хворих","key":"TREAT"}
		,{"title":"Проведено ліжкоднів (дні)","name":"Пр-но ліжкоднів","key":"bed_day"}
		,{"title":"План ліжкоднів (дні)","name":"Пл-н ліжкоднів","key":"bed_day_plan"}
		,{"title":"Процент виконання плана ліжкоднів (%)","name":"% вик. плана","key":"bed_day_fulfil"}
		,{"title":"Зайнятість ліжка (днів)","name":"З-ть ліжка","key":"bed_occupancy"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка","key":"bed_turnover"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка (альтернатів)","key":"bed_turnover2"}
		,{"title":"Ср. трив. лікуання (днів)","name":"Ср. трив. лікуання","key":"treat_avg"}
	];

	eqMonth = function(){
		var url1 = "/r/readBedDayMySql-";
		if($scope.param.department_id)
			url1 = "/r/readBedDayDepartmentMySql-";
		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.bedDay = data;
			console.log($scope.bedDay);
			if($scope.param.department_id){
				for (var i = 0; i < $scope.bedDay.bedDayOfMonthMySql.length; i++) {
					if($scope.bedDay.bedDayOfMonthMySql[i].dp_id == $scope.param.department_id){
						$scope.bedDayOfMonthMySql = $scope.bedDay.bedDayOfMonthMySql[i];
						if($scope.bedDay.bedDayDepartmentMySql){
							$scope.bedDayOfMonthMySql.bedDayDepartmentMySql = [];
							for (var j = 0; j < $scope.bedDay.bedDayDepartmentMySql.length; j++) {
								var x = $scope.bedDay.bedDayDepartmentMySql[j][i];
								$scope.bedDayOfMonthMySql.bedDayDepartmentMySql.push(x);
							}
						}
						break;
					}
				}
				console.log($scope.bedDayOfMonthMySql);
				mmArray();
			}
//			initDateVariables();
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	eqMonth();


	mmArray();
}]);

//hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                  , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//	console.log("/readMove-day-Patients");
//	initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("/readMove-day-Patients");
	initController($scope, $http, $filter);
	// 1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	$scope.isParamDate = function(month, day){
		if($scope.paramDateDate.getDate() == day){
			if(month == $scope.paramDateDate.getMonth() + 1){
				return true;
			}
		}
		return false;
	}
	$scope.monthDayDate = function(month, day){
//		var d2 = new Date($cookies.get('year'), month - 1, day);
		var d2 = new Date(2016, month - 1, day);
		return d2;
//		return new Date(new Date().getFullYear(), month - 1, day);
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

