# CPU & Performance

## Avaliações

Foram realizadas avaliações do uso de CPU e da performance utilizando o as ferramentas Android Profiler e AndroidDevMetrics.

#### Android Profiler
O android Profiler foi utilizado para avaliar o uso de CPU pelo aplicativo, que mostrou ser bem pequeno, tendo alguns picos em torno de 30% quando alguma tarefa foi executada (abrir activities, rolar a listview, etc...). Segue imagem abaixo:

![Imagem 1](https://github.com/msb55/projeto-android-if710/blob/master/imagens_relatorio/AndroidProfiler_CPU_before.png?raw=true)

#### AndroidDevMetrics

O android Profiler foi utilizado para avaliar o desempenho do aplicativo. Segue imagem abaixo. No relatório, não há nenhuma perda de frames e percebe-se que é gasto mais tempo no método onStart() e na criação do layout.

![Imagem 2](https://github.com/msb55/projeto-android-if710/blob/master/imagens_relatorio/AndroidDevMetrics-2.png?raw=true)


## Boas Práticas

##### Hardware Acceleration 

utilizando aceleração por hardware houve uma melhora significativa no tempo gasto pelo onStart() e um pouco de aumento na criação do layout.

![Imagem 2](https://github.com/msb55/projeto-android-if710/blob/master/imagens_relatorio/AndroidDevMetrics-3.png?raw=true)


#### RecyclerView

Uma vez que executar o garbage collector gera uso de tempo de CPU, reduzindo a quantidade de lixo produzido, consequentemente, reduzirá o uso de CPU gasto pela aplicação. 
A adoção de uma RecyclerView na listView se mostra eficiente, uma vez que há uma menor criação de objetos. O funcionamento da mesma é melhor detalhado no arquivo   **memoria**.**md**
