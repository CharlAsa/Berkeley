# Berkeley
Simulador do algoritmo de Berkeley utilizando socket e threads em Java.

Sobre o algoritmo:
-O servidor envia o tempo dele (horas, minutos e segundos).
-O(s) cliente(s) responde(m) com a diferença de tempo.
-O servidor calcula a média de tempo somando-os e dividindo pela quantidade de cliente + o servidor.
-O servidor envia esse tempo para cada cliente. Obs: Envia de acordo com o tempo do cliente, como mostrado no exemplo abaixo.
-O cliente soma esse tempo com o tempo dele.

Exemplo:
Servidor - 4:10
Cliente - 4:30

-Servidor envia 4:10
-Cliente responde 20
-Servidor calcula 20/2 = 10
-Servidor ajusta seu tempo 4:10 + 0:10 = 4:20
-Servidor envia ao cliente -10
-Cliente ajusta seu tempo 4:30 - 0:10 = 4:20

Outro exemplo:
Ser - 3:00
Cli1 - 3:10
Cli2 - 2:56

-Ser envia 3:00
-Cli1 responde 10
-Cli2 responde -4
-Ser calcula (10+(-4))/3 = 2
-Ser ajusta seu tempo 3:00 + 0:02 = 3:02
-Ser envia -8 ao Cli1
-Ser envia 6 ao Cli2
-Cli1 ajusta seu tempo 3:10 - 0:08 = 3:02
-Cli2 ajusta seu tempo 2:56 + 0:06 = 3:02
