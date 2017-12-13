# Bateria
Avaliamos a bateria utilizando o emulador do Android Studio como nosso dispositivo.

Primeiramente desativamos o modo de carregamento do emulador, desconectando-o da fonte de energia, simulada por suas configurações, a fim de obter alguns resultados mais significativos. Após isso, redefinimos os dados sobre a bateria do aparelho, utilizando o Android Debug Bridge (adb)
```sh
adb shell dumpsys batterystats --reset
```

O aplicativo foi utilizado durante aproximadamente 10 minutos, sendo feito o download de 5 episódios, enquanto reproduzia alguns, e navegava pela ListView do aplicativo. Após essa utilização, fizemos uso da ferramente Battery Historian para analisar os dados extraídos pelo adb
```sh
adb bugreport > bugreport.zip
```

A primeira visualização segue abaixo:

![battery1](https://raw.githubusercontent.com/msb55/projeto-android-if710/master/imagens_relatorio/battery1.PNG)

Nela podemos observar alguns pontos em particular, como por exemplo o uso do sinal de rádio móvel (Mobile radio active) correspondendo aos downloads dos episódios de podcast durante o uso, assim como a utilização dos autofalantes do aparelho (Audio) ao reproduzir alguns episódios. 

A força de sinal do aparelho se manteve estável, uma vez que era o emulador. Em todo o momento a tela do aparelhos estava ligada, devido ao usuário ativo com o aplicativo, não solicitamos a plataforma essa configuração (wakelock).

Por ter sido pouco tempo de utilização, o consumo de bateria ao todo, considerando o sistema, foi de ```0.04%```, mas mesmo assim é possível observar em quais aspectos o aplicativo consome energia, tendo como os principais o uso da internet (seja móvel ou não) para o download dos episódio ou caso seja necessário, a atualização do feed de podcasts; e o uso do recurso de áudio do aparelho para a reprodução dos podcasts.

![battery4](https://raw.githubusercontent.com/msb55/projeto-android-if710/master/imagens_relatorio/battery4.PNG)

Na próxima imagem segue alguns detalhes a mais sobre a análise com a ferramenta Battery Historian, sob o aspecto geral do sistema (System Stats), trazendo algumas informações sobre o tempo de utilização de tela, banda e interação, por exemplo, correspondente ao uso do aplicativo no tempo discutido.

![battery2](https://raw.githubusercontent.com/msb55/projeto-android-if710/master/imagens_relatorio/battery2.PNG)

A última imagem mostram detalhes sobre o status do aplicativo (App Stats), a respeito ao processo, informando em unidades de tempo gastos em diversas configurações, e percebe-se que a todo momento estava sendo utilizado em Foreground, activity e service, e o processo se permanecendo no "topo".

![battery3](https://raw.githubusercontent.com/msb55/projeto-android-if710/master/imagens_relatorio/battery3.PNG)
