var definitionScope = function($scope){
	
	// порахувати вік на день поступлення
	$scope.ageToHospitalization = function() {
		if(!$scope.eix)
			return 0;
		var dob = new Date($scope.eix.historyPatient.patient_dob);
		var currentDate = new Date($scope.eix.historyPatient.history_in);
		var currentYear = currentDate.getFullYear();
		var birthdayThisYear = new Date(currentYear, dob.getMonth(), dob.getDate());
		var age = currentYear - dob.getFullYear();

		if(birthdayThisYear > currentDate) {
			age--;
		}

		return age;
	}

	// порахувати ліжко-день
	$scope.bedDay= function(){
	}

	//ключі об'єкта
	$scope.keys = function(obj){
		return Object.keys(obj);
	}
	
	// порахувати тривалість операції в годинах і хвилинах
	$scope.opDuration = function(op){
		var diffMs = op.operation_history_end - op.operation_history_start;
//		console.log(diffMs);
		var hh = Math.floor(diffMs / 1000 / 60 / 60);
		var hhInMs = hh * 1000 * 60 * 60;
		var mm = Math.floor((diffMs-hhInMs)/ 1000 / 60);
//		console.log(hh+':'+mm);
		var opDuration = '';
		if(hh>0)
			opDuration += hh + 'г.';
		opDuration += mm + 'хв.';
		return opDuration;
	}

	hol1HtmlToJson = function(){
		console.log($scope.eix.historyTreatmentAnalysis);
		angular.forEach($scope.eix.historyTreatmentAnalysis, function(historyTreatmentAnalysis, key) {
//			console.log(historyTreatmentAnalysis.history_treatment_analysis_id);
			historyTreatmentAnalysis.history_treatment_analysis_json = {};
			try {
				convertTableToJson(historyTreatmentAnalysis.history_treatment_analysis_json, historyTreatmentAnalysis.history_treatment_analysis_text);
			}catch(err) {
				console.log("--err-- "
				+ historyTreatmentAnalysis.history_treatment_analysis_id+"/\n"
				+ historyTreatmentAnalysis.history_treatment_analysis_text+"/\n"
				+ historyTreatmentAnalysis.history_treatment_analysis_json+"/\n"
				+ "--err--END");
			}
//			console.log($scope.keys(historyTreatmentAnalysis.history_treatment_analysis_json).length);
		});
	}

	// перетворити таблицю аналізів в JSON
	var convertTableToJson = function(jsonObj, tableAsLaborAnalis){
		var element = angular.element(tableAsLaborAnalis);
		var trs = element.find("td.name");
		if(trs.length > 0){
			var laborValues = {};
			for(i = 0; i < trs.length; i++){
				var nameTd = angular.element(trs[i]);
				var valueTd = nameTd.next();
				var unitTd = valueTd.next();
				if(valueTd.text().trim().length > 0){
					laborValues[nameTd.text()] = {value:valueTd.text(), unit:unitTd.text()};
				}
			}
			jsonObj.laborValues = laborValues;
			/*
		}else{
			return;
			if(!jsonObj.textHtml || jsonObj.textHtml.trim().length == 0){
				jsonObj.textHtml = tableAsLaborAnalis;
				if(tableAsLaborAnalis == "") {
					jsonObj.textHtml = " &nbsp; ";
				}
			}
			jsonObj.textHtml1 = tableAsLaborAnalis;
			*/
		}
	}

	// діалог дати jquery
	$scope.dateOptions = {
		dateFormat: 'dd/mm/yy'
	}

	$scope.openBlockToEdit = function(historyTreatmentAnalysis){
		$scope.editBlockById = 
			$scope.editBlockById == historyTreatmentAnalysis.history_treatment_analysis_id
				? 0 : historyTreatmentAnalysis.history_treatment_analysis_id;

		historyTreatmentAnalysis.date = new Date(historyTreatmentAnalysis.history_treatment_analysis_datetime);
		historyTreatmentAnalysis.hh = historyTreatmentAnalysis.date.getHours();
		historyTreatmentAnalysis.mm = historyTreatmentAnalysis.date.getMinutes();
	}

	$scope.ddmmyyChange = function(ta){
		ta.history_treatment_analysis_datetime = ta.date.getTime();
	}

	$scope.hhChange = function(ta){
		if(ta.hh>=0||ta.hh<24){
			ta.date.setHours(ta.hh)
			$scope.ddmmyyChange(ta);
		}
	}

	$scope.mmChange = function(ta){
		if(ta.mm>=0||ta.mm<60){
			ta.date.setMinutes(ta.mm)
			$scope.ddmmyyChange(ta);
		}
	}

};

angular.module('ix3App', ['ngSanitize','textAngular','ui.date'])
.controller('Ix3Ctrl', function($scope, $http, $sce) {
	console.log("Ix2Ctrl");
	//$interval(frameCtrl, 3000);

	definitionScope($scope);
	$scope.pageTitle = 'EІХ ' + eixId;
	var url = '/r/eix-'+eixId;

	console.log(url);

	$http.get(url)
	.then(function(response) {
		$scope.eix = response.data;
		console.log($scope.eix);
		initEix();
	}, function errorCallback(response) {
		$scope.error.push(response.data);
	});

	function initEix(){
		hol1HtmlToJson();
		//індекс клінічного/при поступлені діагнозу
		angular.forEach($scope.eix.historyDiagnose, function(historyDiagnose, key) {
			if(historyDiagnose.diagnos_id == 2){
				$scope.eix.hospitalizationDiagnosIndex = key;
			}else
			if(historyDiagnose.diagnos_id == 4){
				$scope.eix.clinDiagnosIndex = key;
			}
		});
	};
	
	$scope.pageDesign = {
		'elements':[
			{'bs_row':[
				{'bs_col_nr':5
				,'elements':[
					{'type':'fieldset','inc':'/v/eix1/fieldset-info_person.html'}
					,{'type':'fieldset','inc':'/v/eix1/fieldset-info_med.html'}
				]
				}
				,{'bs_col_nr':7
				,'elements':[
					{'type':'fieldset','inc':'/v/eix1/fieldset-hospitalization.html'}
					,{'type':'fieldset','inc':'/v/eix1/fieldset-ruh.html'}
				]
				}
			]}
			,{'type':'div'}
		]
	};
	
	$scope.eixField = {
		'Address':'Адреса'
		,'patient':'Хворий'
		,'born':'народився'
		,'direct':'Хворий поступив по направленю'
		,'history_urgent_yes':'в ургентному порядку'
		,'history_urgent_no':'в плановому порядку'
		,'hospitalized_to_department':'Госпіталізований в відділеня'
		,'with_diagnose':'з діагнозом'
		,'fieldset':{
			'info_person':'Особиста інформація'
			,'info_med':'Медична інформація'
			,'hospitalization':'Госпіталізація'
			,'ruh':'Рух'
			,'diagnoses':'Діагнози'
			,'operations':'Операції'
		}
		,'blood_group':'Група крові'
		,'blood_group_symbol':{
			'I':'(0)'
			,'II':'(A)'
			,'III':'(B)'
			,'IV':'(AB)'
		}
		,'history_in':'Поступив'
		,'history_out':'Виписаний'
		,'op_in':'Початок'
		,'op_out':'Кінець'
		,'operazii':'Операції'
		,'operazia':'Операція'
		,'clin_dz':'Клін.Д/З'
		,'patient_dob_narodjenya':'н.'
		,'patient_dob_rn':'р.н.'
		,'patient_street':'вул.'
		,'patient_house':'буд.'
	}

});
