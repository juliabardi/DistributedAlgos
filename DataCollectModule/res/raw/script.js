function() {
 
	var result = dataProvider.getAllData('AccelerationData', 1);
	var json = JSON.parse(result);
	document.write('json.id: ' + json.id + '<br/>');
	document.write('json.name: ' + json.name + '<br/>');

	for (var i in json.params){
		
		document.write('json.params['+i+']: ' + json.params[i] + ' ');	
	}
	
	var sum = 0;
	
	//summing up values
	for (var j in json.values){
		sum += parseInt(json.values[j][1]);
	}
	
	document.write('values.id sum:' + sum);
	
	//calling jsinterface to set the sum
	dataProvider.setSum(sum);
	
	//calling jsinterface to set the data in json (string)
	dataProvider.setJSON(JSON.stringify(json));

}