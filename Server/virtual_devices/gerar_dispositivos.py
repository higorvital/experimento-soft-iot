arquivo_entrada = open('ufbaino01.xml', 'r')

n_dispositivos = 21

for i in range(2, n_dispositivos):
	if i <= 9:
		arquivo_saida = open('ufbaino0'+str(i)+'.xml', 'w')
		for linha in arquivo_entrada:
			arquivo_saida.write(linha.replace('ufbaino01', 'ufbaino0'+str(i)))
	else:
		arquivo_saida = open('ufbaino'+str(i)+'.xml', 'w')
		for linha in arquivo_entrada:
			arquivo_saida.write(linha.replace('ufbaino01', 'ufbaino'+str(i)))
	arquivo_entrada.close()
	arquivo_saida.close()
	arquivo_entrada = open('ufbaino01.xml', 'r')
	

	

arquivo_entrada.close()
