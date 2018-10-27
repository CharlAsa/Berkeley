# Berkeley
Simulador do algoritmo de Berkeley utilizando socket e threads em Java para a matéria de Sistemas Distribuidos com o Prof.Eliezio.<br />
Sobre o algoritmo:<br />
-O servidor envia o tempo dele (horas, minutos e segundos).<br />
-O(s) cliente(s) responde(m) com a diferença de tempo.<br />
-O servidor calcula a média de tempo somando-os e dividindo pela quantidade de cliente + o servidor.<br />
-O servidor envia esse tempo para cada cliente. Obs: Envia de acordo com o tempo do cliente, como mostrado no exemplo abaixo.<br />
-O cliente soma esse tempo com o tempo dele.<br />
-O servidor pode conter um limite de tempo que é aceito do cliente, ex: servidor 4:05, limite 1:00,cliente 7:00, o calculo do cliente é descartado.<br /><br />
Exemplo:<br /><br />
Servidor - 4:10<br />
Cliente - 4:30<br /><br />
-Servidor envia 4:10<br />
-Cliente responde 20<br />
-Servidor calcula 20/2 = 10<br />
-Servidor ajusta seu tempo 4:10 + 0:10 = 4:20<br />
-Servidor envia ao cliente -10<br />
-Cliente ajusta seu tempo 4:30 - 0:10 = 4:20<br /><br />
Outro exemplo:<br />
Ser - 3:00<br />
Cli1 - 3:10<br />
Cli2 - 2:56<br /><br />
-Ser envia 3:00<br />
-Cli1 responde 10<br />
-Cli2 responde -4<br />
-Ser calcula (10+(-4))/3 = 2<br />
-Ser ajusta seu tempo 3:00 + 0:02 = 3:02<br />
-Ser envia -8 ao Cli1<br />
-Ser envia 6 ao Cli2<br />
-Cli1 ajusta seu tempo 3:10 - 0:08 = 3:02<br />
-Cli2 ajusta seu tempo 2:56 + 0:06 = 3:02<br />
