string_saida = 'DevicesConnected=['

n_dispositivos = 21

arquivo_saida = open('br.ufba.dcc.wiser.soft_iot.gateway.mapping_devices.cfg', 'w')

for i in range(1, n_dispositivos):
	if i <= 9:
		string_saida+='{"id":"ufbaino0'+str(i)+'", "latitude":53.290411, "longitude":-9.074406, "sensors":[{"id":"temperatureSensor", "type":"Thermometer", "collection_time":30000, "publishing_time": 60000}, {"id":"humiditySensor", "type":"HumiditySensor", "collection_time":30000, "publishing_time": 60000}]}'
	else:
		string_saida+='{"id":"ufbaino'+str(i)+'", "latitude":53.290411, "longitude":-9.074406, "sensors":[{"id":"temperatureSensor", "type":"Thermometer", "collection_time":30000, "publishing_time": 60000}, {"id":"humiditySensor", "type":"HumiditySensor", "collection_time":30000, "publishing_time": 60000}]}'
	
	if i < n_dispositivos-1:
		string_saida+=','
	else:
		string_saida+=']\n\n\n'



arquivo_saida.write(string_saida)
arquivo_saida.write('debugMode=false')

arquivo_saida.close()
